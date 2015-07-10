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
import bluej.extensions.event.*;
import inginious.api.*;

import java.net.*;

import javax.swing.JOptionPane;

public class INGInious extends Extension implements PackageListener {

    private BlueJ bluej;
    public static API API;
    public static Course lastCourse;
    public static Task lastTask;

    public void startup (BlueJ bluej) {

        this.bluej = bluej;

        // Register a generator for menu items
        this.bluej.setMenuGenerator(new MenuBuilder());
        this.bluej.setPreferenceGenerator(new PreferenceBuilder(bluej));
        this.bluej.addPackageListener(this);

        // Init api
        INGInious.API = new API(bluej.getExtensionPropertyString("url",""), 
                bluej.getExtensionPropertyString("sessionid",""));
    }

    public boolean isCompatible () {
        return true; 
    }

    public String  getVersion () { 
        return ("0.1");  
    }

    public String  getName () { 
        return ("INGInious");  
    }

    public void terminate() {
        // Save sessionId for subsequent submissions
        bluej.setExtensionPropertyString("sessionid", API.getSessionId());
    }

    public String getDescription () {
        return ("The INGInious BlueJ extension enables students to submit their work "
                + "on an INGInious platform directly from BlueJ interface");
    }

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

    public void packageClosing(PackageEvent arg0) {
        // Not used
    }

    public void packageOpened(PackageEvent arg0) {
        // Not used
    }

}

