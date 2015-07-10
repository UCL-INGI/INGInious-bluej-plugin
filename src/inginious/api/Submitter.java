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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.Map;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class Submitter {
    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private OutputStream outputStream;
    private PrintWriter writer;
    private API api;


    public Submitter(API api, String courseId, String taskId) throws Exception {
        this.api = api;

        // Creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "===";

        // Initialize and configure HTTP connection for multipart POST
        httpConn = this.api.getHttpURLConnection("courses/" + courseId + "/tasks/" + taskId + "/submissions");
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        // Configure output stream
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, "utf-8"), true);
    }

    /**
     * Adds a form field to the request
     * @param name field name
     * @param value field value
     */
    public void addFormField(String name, String value) {
        // Add a boundary
        writer.append("--" + boundary).append(LINE_FEED);

        // Specify Content-Disposition, Content-Type and write value
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=utf-8").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a upload file section to the request
     * @param fieldName name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, byte[] uploadFile, String fileName) throws IOException {
        // Add a boundary
        writer.append("--" + boundary).append(LINE_FEED);

        // Specify that data is a binary file
        writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
        writer.append("Content-Type: application/octet-stream").append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        // Write file in the stream
        outputStream.write(uploadFile);
        outputStream.flush();

        // Add final line feed and flush
        writer.append(LINE_FEED);
        writer.flush();
    }

    /**
     * Completes the request and receives response from the server.
     * @throws Exception 
     * @throws JsonSyntaxException 
     */
    public String submit() throws JsonSyntaxException, Exception {
        // Add the final boundary and close the stream
        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();

        String submissionId = null;

        // Take decision based on the response code
        switch(httpConn.getResponseCode())
        {
        case 200:
            Map<String, String> map = api.getGson().fromJson(API.readHTTPContent(httpConn), new TypeToken<Map<String,String>>(){}.getType());
            submissionId = map.get("submissionid");
        case 403:
        default:
        }

        httpConn.disconnect();

        return submissionId;
    }
}