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

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import javax.swing.*;

import inginious.api.*;

public class AuthenticationGUI {

    private JPanel mainPanel, labelPanel, controlPanel;
    private JLabel authLabel;
    private JComboBox<AuthMethod> authCombo;
    private API api;

    public AuthenticationGUI(API api) throws Exception {
        this.api = api;
        
        this.labelPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        this.authLabel = new JLabel("Auth method", SwingConstants.RIGHT);
        this.authCombo = new JComboBox<AuthMethod>(new Vector<AuthMethod>(AuthMethod.getAllFromAPI(this.api)));
        this.controlPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        this.mainPanel = new JPanel(new BorderLayout(10, 10));

        mainPanel.add(labelPanel, BorderLayout.WEST);
        mainPanel.add(controlPanel, BorderLayout.CENTER);

        authCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                updatePanel((AuthMethod) arg0.getItem());
            }
        });
    }

    /**
     * Display a dialog for authentication
     * @return 0 if auth failed, 1 if auth succeeded, 2 if user cancelled
     * @throws Exception
     */
    public int authenticate() throws Exception {
        updatePanel((AuthMethod) authCombo.getSelectedItem());

        // Open dialog box for authentication
        if(JOptionPane.showConfirmDialog(null, mainPanel, "Authentication", JOptionPane.OK_CANCEL_OPTION) == 0)
            return api.authenticate((AuthMethod) authCombo.getSelectedItem()) ? 1 : 0;
        else
            return 2;
    }


    private void updatePanel(AuthMethod am)
    {
        // Empty panels
        controlPanel.removeAll();
        labelPanel.removeAll();

        // Add authentication method controls
        controlPanel.add(authCombo);
        labelPanel.add(authLabel);

        // For each input, create and link a field, and add it on panel
        for(AuthInput input : am.getInput()) {
            labelPanel.add(new JLabel(input.getName() +" : "));
            JTextField field;

            if(input.getType().equals("password")) 
                field = new JPasswordField();
            else
                field = new JTextField();

            controlPanel.add(field);
            input.setField(field);	
        }

        // Update main panel
        mainPanel.revalidate();
        mainPanel.repaint();

        // Resize dialog box
        Window window = SwingUtilities.getWindowAncestor(mainPanel);
        if (window instanceof Dialog) {
            Dialog dialog = (Dialog) window;
            dialog.pack();
        }
    }

}



