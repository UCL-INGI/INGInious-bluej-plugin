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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

/*
 * Preference panel class
 */
class PreferenceBuilder implements PreferenceGenerator {
    private JPanel mainPanel;
    private JTextField urlField;
    private BlueJ bluej;

    // Construct the panel, and initialize it from any stored values
    public PreferenceBuilder(BlueJ bluej) {
        this.bluej = bluej;

        urlField = new JTextField();

        JPanel labelPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        labelPanel.add(new JLabel(" API URL : ", SwingConstants.RIGHT));

        JPanel fieldPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        fieldPanel.add(urlField);

        JButton testButton = new JButton("Test connection");

        JPanel controlPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        controlPanel.add(testButton);

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(labelPanel, BorderLayout.WEST);
        mainPanel.add(fieldPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.EAST);

        testButton.addActionListener(new TestConnectionListener());

        // Load the default value
        loadValues();
    }

    public JPanel getPanel ()  { 
        return mainPanel; 
    }

    public void saveValues () {
        bluej.setExtensionPropertyString("url", urlField.getText());

        // Change API URL
        INGInious.API.setUrl(urlField.getText());
    }

    public void loadValues () {
        urlField.setText(bluej.getExtensionPropertyString("url",""));
    }
    
    private class TestConnectionListener implements ActionListener {

        public void actionPerformed(ActionEvent anEvent) {
            try {
                // Check for the last "/"
                if(!urlField.getText().endsWith("/"))
                    urlField.setText(urlField.getText() + "/");

                // If method do not send exception, connection must be considered OK
                INGInious.API.checkConnection(urlField.getText());
                JOptionPane.showMessageDialog(null, "Connection OK");

            } catch(FileNotFoundException e) {
                JOptionPane.showMessageDialog(null, "Incorrect path to API !", "Error", JOptionPane.ERROR_MESSAGE);
            } catch(UnknownHostException e) {
                JOptionPane.showMessageDialog(null, "Unknown host !", "Error", JOptionPane.ERROR_MESSAGE);
            } catch(MalformedURLException e) {
                JOptionPane.showMessageDialog(null, "Malformed URL !", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Unexpected error :\n" + e.getClass().getName() + " : " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}


