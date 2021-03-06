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

import bluej.extensions.*;
import inginious.api.API;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Class extending MenuGenerator to add MenuItems in BlueJ menus
 */
class MenuBuilder extends MenuGenerator {

    private BPackage aPackage;
    private API api;

    /**
     * Called when Tool menu items are displayed
     */
    public JMenuItem getToolsMenuItem(BPackage aPackage) {
        this.aPackage = aPackage;

        JMenuItem menu = new JMenuItem("Submit on INGInious");
        menu.addActionListener(new SubmitListener());
        return menu;
    }
    
    /**
     * Initializes a new MenuBuilder
     * @param api Instance of the INGInious API
     */
    public MenuBuilder(API api) {
        super();
        this.api = api;
    }

    /**
     * ActionListener to handle the click on the "Submit on INGInious" menu item
     */
    private class SubmitListener implements ActionListener {

        private String tempUrl;
        
        /**
         * Save default API url and replace it by project one
         */
        private void saveAPIUrl() {
            tempUrl = api.getUrl();
            try {
                ProjectConfig pc = new ProjectConfig(api, aPackage.getProject());
                String url = pc.getUrl();
                if(url != null)
                    api.setUrl(url);
            } catch (ProjectNotOpenException e) {
                JOptionPane.showMessageDialog(null, "Unhandled error\n" + e.getClass().getName() + " : " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        /**
         * Restore default API url
         */
        private void restoreAPIUrl() {
            api.setUrl(tempUrl);
        }
        
        @Override
        public void actionPerformed(ActionEvent arg0) {
            
            saveAPIUrl();
            
            // Check if authentified
            boolean authenticated = false;

            try {
                authenticated = api.isAuthenticated();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Couldn't connect to API.\nCheck your connection settings", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Authenticate if needed
            try {
                if(!authenticated)
                    authenticated = new AuthenticationGUI(api).authenticate() == 1;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Unhandled error\n" + e.getClass().getName() + " : " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Re-check after authentication
            if(!authenticated)
            {
                JOptionPane.showMessageDialog(null, "Wrong credentials !", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Submit project
            // Currently, the plugin only supports submitting a zipfile of the project as a one-problem-task input
            try {
                SubmissionGUI subGui = new SubmissionGUI(api, aPackage.getProject());
                String subid = subGui.submitProject();

                if(subid != null && !subid.equals("")) {
                    JOptionPane.showMessageDialog(null, "Your project has been successfully submitted\nSubmission ID : " + subid);
                    
                    // Start a new thread to display feedback when available
                    // TODO : Clean up thread things...
                    new FeedbackGUI(api.getUrl(), api.getSessionId(), subGui.getSelectedCourse(), subGui.getSelectedTask(), subid).start();
                }
                else if(subid == null)
                    JOptionPane.showMessageDialog(null, "Your project couldn't be submitted\n"
                            + "Maybe task format is not yet supported by the plugin.", "Error", JOptionPane.ERROR_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Unhandled error\n" + e.getClass().getName() + " : " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            restoreAPIUrl();
        }

    }   
}