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
 * Class used to deserialize a Task returned by the INGInious API
 * @author Anthony
 *
 */
public class Task {

    private String status;
    private String deadline;
    private String name;
    private String context;
    private String[] authors;
    private Problem[] problems;
    private double grade;
    private double grade_weight;
    private String id;

    private Task() {}
    
    /**
     * Returns the task id
     * @return Task id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Returns the student grade for the task
     * @return User grade for the task
     */
    public double getGrade() {
        return grade;
    }

    /**
     * Returns the grade weight of the task
     * @return Grade weight of the task
     */
    public double getGradeWeight() {
        return grade_weight;
    }

    /**
     * Returns the task authors
     * @return Array of String containing the task authors
     */
    public String[] getAuthors() {
        return authors;
    }

    /**
     * Returns the user achievement status for the task
     * @return String representing the achievement status for the task (succeded, failed...)
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns the task deadline
     * @return String representing the date and time of the task deadline
     */
    public String getDeadline() {
        return deadline;
    }

    /**
     * Returns the displayed task name
     * @return Displayed task name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the task context
     * @return Task context
     */
    public String getContext() {
        return context;
    }

    /**
     * Returns the task problems
     * @return Array of Problem
     */
    public Problem[] getProblems() {
        return problems;
    }

    /**
     * Returns the displayed task name
     */
    public String toString() {
        return this.name;
    }

    /**
     * Indicates of the given object is equal to the current instance
     */
    public boolean equals(Object anObject) {
        if(anObject == null)
            return false;
        return this.id.equals(((Task)anObject).getId());
    }

    /**
     * Returns all the available tasks for the given course id from the server
     * @param api Instance of the INGInious API
     * @param courseId Course id
     * @return List of Task
     * @throws Exception
     */
    public static List<Task> getAllFromAPI(API api, String courseId) throws Exception {
        HttpURLConnection conn = api.getHttpURLConnection("courses/" + courseId + "/tasks");

        // Parse JSON response of the server
        Type type = new TypeToken<List<Task>>(){}.getType();
        List<Task> result = api.getGson().fromJson(API.readHTTPContent(conn), type);

        conn.disconnect();

        return result;
    }

    /**
     * Returns the Task for the given course id and task id from the server
     * @param api Instance of the INGInious API
     * @param courseId Course id
     * @param taskId Task id
     * @return Task
     * @throws Exception
     */
    public static Task getFromAPI(API api, String courseId, String taskId) throws Exception {
        HttpURLConnection conn = api.getHttpURLConnection("courses/" + courseId + "/tasks/" + taskId);

        // Parse JSON response of the server
        Type type = new TypeToken<List<Task>>(){}.getType();
        List<Task> result = api.getGson().fromJson(API.readHTTPContent(conn), type);

        conn.disconnect();

        return result.get(0);
    }

}


