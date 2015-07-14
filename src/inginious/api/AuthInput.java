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

package inginious.api;

import javax.swing.JTextField;

/**
 * Class used to deserialize the Authentication Input field returned by the INGInious API
 */
public class AuthInput {
    private String id;
    private String type;
    private String name;
    private transient JTextField tfield;

    private AuthInput() {}
    
    /**
     * Returns the type of the authentication input field instance
     * @return String representing the type (login, password...)
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns the displayed name of the authentication input field
     * @return Name to display for the field
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the id used to form the POST authentication request
     * @return Id of the authentication field
     */
    public String getId() {
        return this.id;
    }

    /**
     * Set the JTextField instance associated to the field
     * @param val JTextField instance to which associate the input field
     */
    public void setField(JTextField val) {
        this.tfield = val;
    }

    /**
     * Returns the JTextField associated to the input field
     * @return JTextField instance
     */
    public JTextField getField() {
        return this.tfield;
    }

    /**
     * Returns the value contained in the associated JTextField instance used for POST request
     * @return String value used for POST request
     */
    public String getValue() {
        return this.tfield.getText();
    }
}
