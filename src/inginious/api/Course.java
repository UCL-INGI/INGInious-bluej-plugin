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

/**
 * Class used to deserialize the Course information returned by the INGInious API
 */
public class Course {

    private Map<String,String> tasks;
    private double grade;
    private boolean require_password;
    private String name;
    private boolean is_registered;
    private String id;

    private Course() {}
    
    /**
     * Returns the displayed name of the course
     * @return Displayed name of the course
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the course id
     * @return Course id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Indicates if user is registered to the course
     * @return True if user is registered, else False
     */
    public boolean isRegistered() {
        return this.is_registered;
    }

    /**
     * Indicates if course require password to register
     * @return True if it requires, else False
     */
    public boolean requirePassword() {
        return this.require_password;
    }

    /**
     * Returns the current user grade for the course
     * @return User grade
     */
    public double getGrade() {
        return this.grade;
    }

    /**
     * Returns a map of the tasks id and names
     * @return Map with task id as key and names as values
     */
    public Map<String,String> getTaskNames() {
        return this.tasks;
    }

    /**
     * Returns the displayed name of the course
     */
    public String toString() {
        return this.name;
    }

    /**
     * Indicates if the object given in argument is equal to the current instance
     */
    public boolean equals(Object anObject) {
        if(anObject == null)
            return false;

        return this.id.equals(((Course)anObject).getId());
    }

    /**
     * Returns all the available courses from the server
     * @param api Instance of the INGInious API
     * @return List of Course
     * @throws Exception
     */
    public static List<Course> getAllFromAPI(API api) throws Exception {
        HttpURLConnection conn = api.getHttpURLConnection("courses");

        // Parse JSON response of the server
        Type type = new TypeToken<List<Course>>(){}.getType();
        List<Course> result = api.getGson().fromJson(API.readHTTPContent(conn), type);

        conn.disconnect();

        return result;
    }

    /**
     * Returns all the available courses from the server
     * @param api Instance of the INGInious API
     * @param allCourses True if all courses are asked, False if only courses user is registered to are asked
     * @return List of Course
     * @throws Exception
     */
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

    /**
     * Returns the course from the given course id from the server
     * @param api Instance of the INGInious API
     * @param courseId Course id
     * @return Course
     * @throws Exception
     */
    public static Course getFromAPI(API api, String courseId) throws Exception {
        HttpURLConnection conn = api.getHttpURLConnection("courses/" + courseId);

        // Parse JSON response of the server
        Type type = new TypeToken<List<Course>>(){}.getType();
        List<Course> result = api.getGson().fromJson(API.readHTTPContent(conn), type);

        conn.disconnect();

        return result.get(0);
    }

}
