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

public class Submission {

    private String id;
    private String status;
    private String feedback;
    private double grade;
    private String submitted_on;
    private Map<String,String> problems_feedback;
    private String result;
    private Map<String,Object> input;

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getFeedback() {
        return feedback;
    }

    public double getGrade() {
        return grade;
    }

    public String getSubmittedOn() {
        return submitted_on;
    }

    public String getProblemFeedback(String problemId) {
        return problems_feedback.get(problemId);
    }

    public String getResult() {
        return result;
    }

    public Object getProblemInput(String problemId) {
        return input.get(problemId);
    }

    public static Submission getFromAPI(API api, String courseId, String taskId, String submissionId) throws Exception {

        HttpURLConnection conn = api.getHttpURLConnection("courses/" + courseId + "/tasks/" + taskId + "/submissions/" + submissionId);

        // Parse JSON response of the server
        Type type = new TypeToken<List<Submission>>(){}.getType();
        List<Submission> result = api.getGson().fromJson(API.readHTTPContent(conn), type);

        conn.disconnect();

        return  result.get(0);
    }

    public static List<Submission> getAllFromAPI(API api, String courseId, String taskId) throws Exception {
        HttpURLConnection conn = api.getHttpURLConnection("courses/" + courseId + "/tasks/" + taskId + "/submissions");

        // Parse JSON response of the server
        Type type = new TypeToken<List<Submission>>(){}.getType();
        List<Submission> result = api.getGson().fromJson(API.readHTTPContent(conn), type);

        conn.disconnect();

        return result;
    }
}

