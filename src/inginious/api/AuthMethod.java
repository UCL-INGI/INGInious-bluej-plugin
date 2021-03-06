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

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.List;

import com.google.gson.reflect.TypeToken;

/**
 * Class used to deserialize the authentication method returned by the INGInious API
 */
public class AuthMethod
{
    private String id;
    private AuthInput[] input;
    private String name;

    private AuthMethod() {}
    
    /**
     * Returns the displayed name of the authentication method
     * @return Displayed name of the authentication method
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the POST request id of the authentication method
     * @return ID
     */
    public String getId() {
        return this.id;
    }

    /**
     * Returns the input field array used for authentication
     * @return AuthInput array
     */
    public AuthInput[] getInput() {
        return input;
    }

    /**
     * Returns the displayed name of the authentication method
     */
    public String toString() {
        return name;
    }

    /**
     * Returns all the available authentication methods from the server
     * @param api Instance of the INGInious API
     * @return List of AuthMethod
     * @throws Exception
     */
    public static List<AuthMethod> getAllFromAPI(API api) throws Exception {
        HttpURLConnection conn = api.getHttpURLConnection("auth_methods");

        // Parse JSON response of the server
        Type type = new TypeToken<List<AuthMethod>>(){}.getType();
        List<AuthMethod> result = api.getGson().fromJson(API.readHTTPContent(conn), type);

        conn.disconnect();

        return result;

    }

}

