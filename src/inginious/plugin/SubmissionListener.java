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
import java.awt.Dimension;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import inginious.api.*;

public class SubmissionListener extends Thread {
    
    private Course course;
    private Task task;
    private String submissionId;
    private API api;
    
    public SubmissionListener(API api, Course course, Task task, String submissionId) {
        this.api = api;
        this.course = course;
        this.task = task;
        this.submissionId = submissionId;
    }
    
    public void run() {
        try {
            // Wait till feedback is available
            Submission sub = Submission.getFromAPI(api, course.getId(), task.getId(), submissionId);
            while(sub.getStatus().equals("waiting")) {
                Thread.sleep(1000);
                sub = Submission.getFromAPI(api, course.getId(), task.getId(), submissionId);
            }
            
            // Produce small HTML code to format feedback
            String html = "<html><body>";
            html += "<h1>" + course.getName() + "</h1>";
            html += "<h2>" + task.getName() + "</h2>";
            html += "<h3> Your grade : " + Math.round(sub.getGrade()) + " %</h3>";
            html += "<hr/>";
            html += sub.getFeedback() + "</body></html>";
            
            // Initialize a scroll pane with an editor pane whose content is set to produced html
            JScrollPane scrollPane = new JScrollPane(new JEditorPane("text/html", html));
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            
            // Initialize a panel to stretch the editor to the border of the window
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            
            // Initialize and open a new frame with the feedback
            JFrame frame = new JFrame("Feedback for submission : " + sub.getId());
            frame.add(mainPanel);
            frame.setMinimumSize(new Dimension(500,300));
            frame.setSize(500, 300);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unhandled error\n" + e.getClass().getName() + " : " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
     
}
