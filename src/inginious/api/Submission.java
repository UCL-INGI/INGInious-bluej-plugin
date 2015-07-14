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
import java.util.Map;

import com.google.gson.reflect.TypeToken;

/**
 * Class used to deserialize a submission returned by the server 
 */
public class Submission {

    private String id;
    private String status;
    private String feedback;
    private double grade;
    private String submitted_on;
    private Map<String,String> problems_feedback;
    private String result;

    private Submission() {}
    
    /**
     * Returns the submission id
     * @return Submission id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the current status of the submission
     * @return String representing the current status of a submission (waiting, done,...)
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns the main feedback of the task
     * @return String containing the main feedback of the task
     */
    public String getFeedback() {
        return feedback;
    }

    /**
     * Returns the user grade for the current task
     * @return User grade
     */
    public double getGrade() {
        return grade;
    }

    /**
     * Returns the datetime associated to the submission
     * @return String representing the date and time at which the submission was made
     */
    public String getSubmittedOn() {
        return submitted_on;
    }

    /**
     * Returns the specific feedback for a given problem id
     * @param problemId problem id
     * @return String containing the feedback for the specified problem
     */
    public String getProblemFeedback(String problemId) {
        return problems_feedback.get(problemId);
    }

    /**
     * Returns the global result of the submission
     * @return String representing the global result (success, crash, ...)
     */
    public String getResult() {
        return result;
    }


    /**
     * Returns the submission for the given course, task and submission id from the server
     * @param api Instance of the INGInious API
     * @param courseId Course id
     * @param taskId Task id
     * @param submissionId Submission id
     * @return Submission
     * @throws Exception
     */
    public static Submission getFromAPI(API api, String courseId, String taskId, String submissionId) throws Exception {

        HttpURLConnection conn = api.getHttpURLConnection("courses/" + courseId + "/tasks/" + taskId + "/submissions/" + submissionId);

        // Parse JSON response of the server
        Type type = new TypeToken<List<Submission>>(){}.getType();
        List<Submission> result = api.getGson().fromJson(API.readHTTPContent(conn), type);

        conn.disconnect();

        return  result.get(0);
    }

    /**
     * Returns all the available submissions for the given course and task from the server
     * @param api Instance of the INGInious API
     * @param courseId Course id
     * @param taskId Task id
     * @return List of Submission
     * @throws Exception
     */
    public static List<Submission> getAllFromAPI(API api, String courseId, String taskId) throws Exception {
        HttpURLConnection conn = api.getHttpURLConnection("courses/" + courseId + "/tasks/" + taskId + "/submissions");

        // Parse JSON response of the server
        Type type = new TypeToken<List<Submission>>(){}.getType();
        List<Submission> result = api.getGson().fromJson(API.readHTTPContent(conn), type);

        conn.disconnect();

        return result;
    }
}

