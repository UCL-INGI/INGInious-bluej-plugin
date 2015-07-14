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

/**
 * Class used to deserialize a Task problem returned by the INGInious API
 */
public class Problem {
    private String id;
    private String header;
    private String type;
    private String name;
    private String language;
    private String[] allowed_exts;

    private Problem() {}
    
    /**
     * Returns the type of the task problem
     * @return String representing the type of a task problem
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns the header of the task problem
     * @return Header of the task problem
     */
    public String getHeader() {
        return this.header;
    }

    /**
     * Returns the task problem id used in POST request to submit
     * @return Task problem id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Returns the displayed name of the task problem
     * @return Displayed name of the task problem
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the programming language of the task problem
     * @return String representing the programming language of the task problem
     */
    public String getLanguage() {
        return this.language;
    }

    /**
     * Returns the allowed extensions for file submissions
     * @return Array of string containing the allowed file extensions
     */
    public String[] getAllowedExtensions() {
        return this.allowed_exts;
    }

}
