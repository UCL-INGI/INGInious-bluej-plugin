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
import inginious.api.*;

import java.net.*;

import javax.swing.JOptionPane;

/**
 * Main class, starting point of BlueJ extension
 */
public class INGInious extends Extension {

    private BlueJ bluej;
    private API api;

    /**
     * Called when BlueJ is launched
     */
    public void startup (BlueJ bluej) {

        this.bluej = bluej;
        
        // Init api
        this.api = new API(bluej.getExtensionPropertyString("url",""), 
                bluej.getExtensionPropertyString("sessionid",""));

        // Register a generator for menu items
        this.bluej.setMenuGenerator(new MenuBuilder(this.api));
        this.bluej.setPreferenceGenerator(new PreferenceBuilder(this.api, this.bluej));
        
    }

    /**
     * Determines if plugin is compatible with BlueJ version
     */
    public boolean isCompatible () {
        return true; 
    }

    /**
     * Returns the current version of the plugin
     */
    public String  getVersion () { 
        return this.getClass().getPackage().getImplementationVersion();
    }

    /**
     * Returns the name of the plugin
     */
    public String  getName () { 
        return "INGInious";
    }

    /**
     * Called when BlueJ is terminated
     */
    public void terminate() {
        // Save sessionId for subsequent submissions
        bluej.setExtensionPropertyString("sessionid", api.getSessionId());
    }

    /**
     * Returns the plugin description
     */
    public String getDescription () {
        return ("The INGInious BlueJ extension enables students to submit their work "
                + "on an INGInious platform directly from BlueJ interface");
    }

    /**
     * Returns the plugin information URL
     */
    public URL getURL () {
        try {
            return new URL("https://github.com/UCL-INGI/INGInious-bluej-plugin");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unhandled error\n" + e.getClass().getName() + " : " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public static void main(String[] args) {
        // For build purposes
    }

}

