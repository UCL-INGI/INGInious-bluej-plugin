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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.*;

import bluej.extensions.BProject;

import inginious.api.*;

public class SubmissionGUI {

    private JPanel mainPanel;
    private JComboBox<Course> courseCombo;
    private JComboBox<Task> taskCombo;
    private JLabel userLabel;

    public SubmissionGUI() throws Exception {
        // Get courses user is registered to and init a combobox
        List<Course> courses = Course.getAllFromAPI(INGInious.API, false);
        courseCombo = new JComboBox<Course>(new Vector<Course>(courses));

        // If last course is set, select good item
        if(INGInious.lastCourse != null)
            courseCombo.setSelectedItem(INGInious.lastCourse);
        else
            INGInious.lastCourse = courses.get(0);

        // Get list of tasks and init a combobox
        taskCombo = new JComboBox<Task>(new Vector<Task>(Task.getAllFromAPI(INGInious.API, INGInious.lastCourse.getId())));
        courseCombo.addItemListener(new ComboListener());

        // If last task is set, select good item
        if(INGInious.lastTask != null)
            taskCombo.setSelectedItem(INGInious.lastTask);

        // Init user label and change user button
        userLabel = new JLabel(INGInious.API.getUsername());
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
     * Display a dialog for choosing task and submit the whole current BlueJ project
     * @param project The BlueJ project
     * @return null if submission failed, submission id if it succeeded, empty string if user cancelled
     * @throws Exception
     */
    public String submitProject(BProject project) throws Exception {
        // Ask user to choose and confirm submit
        if(JOptionPane.showConfirmDialog(null, mainPanel, "Submitting", JOptionPane.OK_CANCEL_OPTION) == 0)
        {
            INGInious.lastCourse = (Course) courseCombo.getSelectedItem();
            INGInious.lastTask = (Task) taskCombo.getSelectedItem();

            // Currently, the BlueJ plugin is only able to submit entire project
            Submitter sub = new Submitter(INGInious.API, INGInious.lastCourse .getId(), INGInious.lastTask.getId());

            // Make a zip file of the whole project and send it as the first problem input
            sub.addFilePart(INGInious.lastTask.getProblems()[0].getId(), makeZipFile(project.getDir()), "test.zip");

            return sub.submit();
        }
        return "";
    }

    private byte[] makeZipFile(File parent) throws IOException {

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

    private void listFiles(File dir, List<File> list) {
        // Recursively add the files contained in directory
        for(File f : dir.listFiles()) {
            if(f.isDirectory())
                listFiles(f, list);
            else
                list.add(f);
        }
    }
    
    private class AuthListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            try {
                int auth = new AuthenticationGUI().authenticate();
                if(auth == 1) {
                    userLabel.setText(INGInious.API.getUsername());

                    courseCombo.removeAllItems();
                    taskCombo.removeAllItems();

                    for(Course course : Course.getAllFromAPI(INGInious.API, false))
                        courseCombo.addItem(course);

                    for(Task task : Task.getAllFromAPI(INGInious.API, ((Course)courseCombo.getSelectedItem()).getId()))
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

    private class ComboListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent i) {
            Course course = (Course) i.getItem();
            try {
                taskCombo.removeAllItems();
                for(Task task : Task.getAllFromAPI(INGInious.API, course.getId()))
                    taskCombo.addItem(task);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Unhandled error\n" + e.getClass().getName() + " : " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        }

    }
}
