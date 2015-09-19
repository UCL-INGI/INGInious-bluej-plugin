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

package inginious.plugin;

import java.io.*;

import bluej.extensions.BProject;
import bluej.extensions.ProjectNotOpenException;
import inginious.api.API;

/**
 * Class to access specific project preconfiguration (url, course, task)
 */
public class ProjectConfig {
    
    private API api;
    private BProject bProject;
    private Configuration configCache;
    
    /**
     * Create a new ProjectConfig
     * @param api Instance of the INGInious API
     * @param bProject Instance of the BlueJ project
     */
    public ProjectConfig(API api, BProject bProject) {
        this.api = api;
        this.bProject = bProject;
        this.configCache = getConfiguration();
    }
    
    /**
     * Returns the API url to use for the project
     * @return API url to use for the project
     */
    public String getUrl() {
        return this.configCache.url;
    }
    
    /**
     * Returns the course id used for the project
     * @return String representing the course id used for the project
     */
    public String getCourseId() {
        return this.configCache.courseid;
    }
    
    /**
     * Returns the task id used for the project
     * @return String representing the task id used for the project
     */
    public String getTaskId() {
        return this.configCache.taskid;
    }
    
    /**
     * Returns the Configuration instance corresponding to the project configuration file
     * This file must be called .inginious and be placed in root project directory
     * It contains JSON formatted data : {"url":"https://...", "courseid": "theId", "taskid", "theId"}
     * @return Configuration instance
     */
    private Configuration getConfiguration() {
        try {
            BufferedReader bf = new BufferedReader(new FileReader(bProject.getDir().getCanonicalPath() + File.separator + ".inginious"));
            StringBuilder sb = new StringBuilder();
            
            String line;
            while((line = bf.readLine()) != null)
                sb.append(line + "\n");
            
            bf.close();
            
            return api.getGson().fromJson(sb.toString(), Configuration.class);
            
        } catch (Exception e) {
            // Ignore configuration
            return new Configuration();
        }
    }
    
    /**
     * Class used to deserialize the configuration file in the project folder
     */
    private class Configuration {
        public String url;
        public String courseid;
        public String taskid;
    }
    
}
