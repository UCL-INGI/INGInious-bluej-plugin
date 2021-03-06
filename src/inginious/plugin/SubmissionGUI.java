//
// This file is part of the INGInious BlueJ plugin.
//
// The INGInious BlueJ plugin is free software: you can redistribute
// it and/or modify it under the terms of the GNU General Public
// License as published by the Free Software Foundation, either
// version 3 of the License, or (at your option) any later version.
//
// The INGInious BlueJ plugin is distributed in the hope that it will
// be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
// of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with the INGInious BlueJ plugin.  If not, see <http://www.gnu.org/licenses/>.
//

package inginious.plugin;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.*;

import bluej.extensions.BProject;

import inginious.api.*;

/**
 * Class to display a submission dialog for user to choose Course and Task
 */
public class SubmissionGUI {

    private JPanel mainPanel;
    private JComboBox<Course> courseCombo;
    private JComboBox<Task> taskCombo;
    private JLabel userLabel;
    private static Course lastCourse;
    private static Task lastTask;
    private API api;
    private BProject bProject;
    private ProjectConfig projectConfig;
    private File pluginFile;

    /**
     * Initalize a new SubmissionGUI
     * @param api Instance of the INGInious API
     * @throws Exception
     */
    public SubmissionGUI(API api, BProject bProject) throws Exception {
        this.bProject = bProject;
        this.api = api;
        this.projectConfig = new ProjectConfig(api, bProject);
        this.pluginFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        
        String courseId = projectConfig.getCourseId();
        
        // Check if course id is overrided
        if(courseId != null) {
            courseCombo = new JComboBox<Course>();
            courseCombo.addItem(Course.getFromAPI(api, courseId));
            courseCombo.setEnabled(false);
            
        } else {
            // Get courses user is registered to and init a combobox
            List<Course> courses = Course.getAllFromAPI(api, false);
            courseCombo = new JComboBox<Course>(new Vector<Course>(courses));
    
            // If last course is set, select good item
            if(lastCourse != null)
                courseCombo.setSelectedItem(lastCourse);
            else
                lastCourse = courses.get(0);
        }

        String taskId = projectConfig.getTaskId();
        
        // Check if task id is overrided
        if(taskId != null) {
           taskCombo = new JComboBox<Task>();
           taskCombo.addItem(Task.getFromAPI(api, courseId, taskId));
           taskCombo.setEnabled(false);
        } else {
            // Get list of tasks and init a combobox
            taskCombo = new JComboBox<Task>(new Vector<Task>(Task.getAllFromAPI(api, lastCourse.getId())));
            courseCombo.addItemListener(new ComboListener());
    
            // If last task is set, select good item
            if(lastTask != null)
                taskCombo.setSelectedItem(lastTask);
        }

        // Init user label and change user button
        userLabel = new JLabel(api.getUsername());
        JButton changeButton = new JButton("Change user");
        changeButton.addActionListener(new AuthListener());

        // Init user panel for username and change user button
        JPanel userPanel = new JPanel(new BorderLayout(10, 10));
        userPanel.add(userLabel, BorderLayout.CENTER);
        userPanel.add(changeButton, BorderLayout.EAST);

        // Add the compononents to controls panel
        JPanel controls = new JPanel(new GridLayout(0, 1, 5, 5));
        controls.add(userPanel);
        controls.add(courseCombo);
        controls.add(taskCombo);

        // Init and add labels to label panel
        JPanel label = new JPanel(new GridLayout(0, 1, 5, 5));
        label.add(new JLabel("Submitting as : ", SwingConstants.RIGHT));
        label.add(new JLabel("Course : ", SwingConstants.RIGHT));
        label.add(new JLabel("Task : ", SwingConstants.RIGHT));

        // Init and add components to main panel
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(label, BorderLayout.WEST);
        mainPanel.add(controls, BorderLayout.CENTER);
    }

    /**
     * Returns the selected course in the submission dialog
     * @return Course
     */
    public Course getSelectedCourse() {
        return (Course) courseCombo.getSelectedItem();
    }
    
    /**
     * Returns the selected task in the submission dialog
     * @return Task
     */
    public Task getSelectedTask() {
        return (Task) taskCombo.getSelectedItem();
    }
    
    /**
     * Display a dialog for choosing task and submit the whole current BlueJ project
     * @param project The BlueJ project
     * @return null if submission failed, submission id if it succeeded, empty string if user cancelled
     * @throws Exception
     */
    public String submitProject() throws Exception {
        // Ask user to choose and confirm submit
        if(JOptionPane.showConfirmDialog(null, mainPanel, "Submitting " + bProject.getName(), JOptionPane.OK_CANCEL_OPTION) == 0)
        {
            lastCourse = (Course) courseCombo.getSelectedItem();
            lastTask = (Task) taskCombo.getSelectedItem();
            
            // Currently, the BlueJ plugin is only able to submit entire project
            Submitter sub = new Submitter(api, lastCourse .getId(), lastTask.getId());

            // Make a zip file of the whole project and send it as the first problem input
            sub.addFilePart(lastTask.getProblems()[0].getId(), makeZipFile(bProject.getDir()), "test.zip");
            
            return sub.submit();
        }
        
        return "";
    }

    /**
     * Returns the byte array of the zipped folder
     * @param parent File object corresponding to the folder to zip
     * @return Byte array of the compressed folder
     * @throws IOException
     * @throws URISyntaxException 
     */
    private byte[] makeZipFile(File parent) throws IOException, URISyntaxException {

        // Prepare list of file in folder
        List<File> files = new ArrayList<File>();
        listFiles(parent, files);

        // Initialize buffer for reading file in memory
        int bufsize = 4096;
        byte data[] = new byte[bufsize];

        // Initialize a ZIP over ByteArray output stream
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        ZipOutputStream os = new ZipOutputStream(ba);

        // Compression level
        os.setMethod(ZipOutputStream.DEFLATED);
        os.setLevel(9);

        for(File file : files)
        {
            FileInputStream fi = new FileInputStream(file);
            BufferedInputStream buffi = new BufferedInputStream(fi);

            // Add filename in ZIP file
            String filename = file.getCanonicalPath().substring(parent.getCanonicalPath().length() + 1);
            ZipEntry entry = new ZipEntry(filename);
            os.putNextEntry(entry);

            // Write the file contents in ZIP stream
            int count;
            while((count = buffi.read(data,0,bufsize)) != -1) {
                os.write(data, 0, count);
            }

            // Finalizes file addition
            os.closeEntry();
            buffi.close();
        }

        // Finalizes ZIP file
        os.close();

        // Flush into heap
        byte[] result = ba.toByteArray();
        ba.close();

        return result;
    }

    /**
     * Returns a list of all the files contained in a directory and its subdirectories
     * @param dir File object corresponding to the main folder
     * @param list File list to which add the files
     * @throws IOException 
     */
    private void listFiles(File dir, List<File> list) throws IOException {
        // Recursively add the files contained in directory
        for(File f : dir.listFiles()) {
            if(f.isDirectory())
                listFiles(f, list);
            else if(!f.equals(pluginFile))
                list.add(f);
        }
    }
    
    /**
     * ActionListener to handle the click on the "Change user" button
     */
    private class AuthListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            try {
                int auth = new AuthenticationGUI(api).authenticate();
                if(auth == 1) {
                    userLabel.setText(api.getUsername());

                    courseCombo.removeAllItems();
                    taskCombo.removeAllItems();

                    for(Course course : Course.getAllFromAPI(api, false))
                        courseCombo.addItem(course);

                    for(Task task : Task.getAllFromAPI(api, ((Course)courseCombo.getSelectedItem()).getId()))
                        taskCombo.addItem(task);

                } else if(auth == 0) {
                    JOptionPane.showMessageDialog(null, "Wrong credentials !", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Unhandled error\n" + e.getClass().getName() + " : " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }   
        }
    }

    /**
     * ItemListener to handle the selection of a course in the course combo box
     * and update the task combo box
     */
    private class ComboListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent i) {
            Course course = (Course) i.getItem();
            try {
                taskCombo.removeAllItems();
                for(Task task : Task.getAllFromAPI(api, course.getId()))
                    taskCombo.addItem(task);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Unhandled error\n" + e.getClass().getName() + " : " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        }

    }
}
