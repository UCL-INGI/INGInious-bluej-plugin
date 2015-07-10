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

    public String getId() {
        return this.id;
    }

    public double getGrade() {
        return grade;
    }

    public double getGradeWeight() {
        return grade_weight;
    }

    public String[] getAuthors() {
        return authors;
    }

    public String getStatus() {
        return status;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getName() {
        return name;
    }

    public String getContext() {
        return context;
    }

    public Problem[] getProblems() {
        return problems;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object anObject) {
        if(anObject == null)
            return false;
        return this.id.equals(((Task)anObject).getId());
    }

    public static List<Task> getAllFromAPI(API api, String courseId) throws Exception {
        HttpURLConnection conn = api.getHttpURLConnection("courses/" + courseId + "/tasks");

        // Parse JSON response of the server
        Type type = new TypeToken<List<Task>>(){}.getType();
        List<Task> result = api.getGson().fromJson(API.readHTTPContent(conn), type);

        conn.disconnect();

        return result;
    }

    public Task getFromAPI(API api, String courseId, String taskId) throws Exception {
        HttpURLConnection conn = api.getHttpURLConnection("courses/" + courseId + "/tasks/" + taskId);

        // Parse JSON response of the server
        Type type = new TypeToken<List<Task>>(){}.getType();
        List<Task> result = api.getGson().fromJson(API.readHTTPContent(conn), type);

        conn.disconnect();

        return result.get(0);
    }

}


