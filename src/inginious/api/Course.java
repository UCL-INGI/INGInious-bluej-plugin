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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;


public class Course {

    private Map<String,String> tasks;
    private double grade;
    private boolean require_password;
    private String name;
    private boolean is_registered;
    private String id;

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    public boolean isRegistered() {
        return this.is_registered;
    }

    public boolean requirePassword() {
        return this.require_password;
    }

    public double getGrade() {
        return this.grade;
    }

    public Map<String,String> getTaskNames() {
        return this.tasks;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object anObject) {
        if(anObject == null)
            return false;

        return this.id.equals(((Course)anObject).getId());
    }

    public static List<Course> getAllFromAPI(API api) throws Exception {
        HttpURLConnection conn = api.getHttpURLConnection("courses");

        // Parse JSON response of the server
        Type type = new TypeToken<List<Course>>(){}.getType();
        List<Course> result = api.getGson().fromJson(API.readHTTPContent(conn), type);

        conn.disconnect();

        return result;
    }

    public static List<Course> getAllFromAPI(API api, boolean allCourses) throws Exception {

        // If all courses needed, return directly
        if(allCourses)
            return Course.getAllFromAPI(api);
        else
        {
            // Otherwise, create a new list with only registered courses
            List<Course> newList = new ArrayList<Course>();
            for(Course course : Course.getAllFromAPI(api)) {
                if(course.isRegistered())
                    newList.add(course);
            }
            return newList;
        }
    }

    public static Course getFromAPI(API api, String courseId) throws Exception {
        HttpURLConnection conn = api.getHttpURLConnection("courses/" + courseId);

        // Parse JSON response of the server
        Type type = new TypeToken<List<Course>>(){}.getType();
        List<Course> result = api.getGson().fromJson(API.readHTTPContent(conn), type);

        conn.disconnect();

        return result.get(0);
    }

}
