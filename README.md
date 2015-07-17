# INGInious BlueJ plugin

INGInious is an intelligent grader that allows secured and automated testing of code made by students. 

This BlueJ plugin is aimed at submitting projects directly from the BlueJ interface without logging 
on the INGInious frontend.

## Setup and configuration

### Download

Download the jar file available for download in [Releases](https://github.com/UCL-INGI/INGInious-bluej-plugin/releases) 
and put in one of the three locations below.

* `<BLUEJ_HOME>/lib/extensions`  (Unix), or `<BLUEJ_HOME>\lib\extensions` (Windows), 
or `<BLUEJ_HOME>/BlueJ.app/Contents/Resources/Java/extensions` (Mac) to make it available to all users on the system.
* `<USER_HOME>/.bluej/extensions` (Unix), or `<USER_HOME>\bluej\extensions` (Windows), or 
`<USER_HOME>/Library/Preferences/org.bluej/extensions` (Mac) to make it available for all projects of a single user
* `<BLUEJ_PROJECT>/extensions` to make it available for a single project

### Configuration

The only mandatory configuration that needs to be done is to specify the INGInious API url. This can be done 
at the plugin level, in the BlueJ Preferences->Extensions panel, or at the project level.

The url has the form : `http[s]://youringiniousserver.org/api/v0/`

## Usage

When the plugin is installed, a new link appears in the Tools menu : *Submit on INGInious*. 

If user has never logged from BlueJ before, he will be asked to before accessing the submission module.
Otherwise, plugin will retain user session to avoid him to log every time. User will still be able to 
switch user if necessary. Preferences are stored in the user home directory so another user on the same computer 
won't benefit of the opened INGInious session of another.

The authentication methods offered to the user are exactly the same as those configured on the server.

When the user is logged, a new dialog asks him for the course and task he's submitting his project for. These lists
are exactly the same as those he would see on the INGInious web frontend. It is possible to set the submission
profile to force submission for a specific course/task (see below).

## Project submission profile

A specific submission profile can be set for each project. Just create a `.inginious` file in your project directory
and put those JSON lines in :

	{
		"url": "http[s]://youringiniousserver.org/api/v0/",
		"courseid": "my_course_id",
		"taskid": "my_task_id"
	}

Whenever this file is detected in the project directory, the default API url will be replaced by the project-specific one
and the submission dialog won't offer any choice to change the course and task the submission is for.

This feature can be used when providing project skeletons to students.