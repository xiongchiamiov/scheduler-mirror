Setup Development Environment
=============================

Get Java EE Eclipse

make sure you have the GWT plugin for eclipse and GWT developer plugin for your respective browser

checkout the project
In /implementation/GWTView/:
- copy the file ".project template" and name it to ".project"
- copy the file ".classpath template" and name it to ".classpath"
import the GWTView project into eclipse (import -> General -> existing projects)
do a clean and a build. you should get a fuckton of errors about various classes not found. this is normal.
right click on the GWTView project, hit properties
under java build path screen's source tab, hit Link Source
for the linked folder location, browse to repo/implementation/model/java
for the folder name, type "modeljava"
hit Finish.
Go to the libraries tab (still in java build path screen)
Hit add external jar. add repo/implementation/model/java/mysql-connector-java-5.0.8-bin.jar.
Hit add external jar. add repo/implementation/GWTView/gwt-dnd-3.1.2.jar.
Hit add external jar. add repo/implementation/GWTView/war/WEB-INF/lib/commons-fileupload-1.2.2.jar
Hit add external jar. add repo/implementation/GWTView/war/WEB-INF/lib/guava-r09.jar
Hit add external jar. add repo/implementation/model/java/javacsv.jar

also add the "dnd" jar by clicking on "Add JARs..." and browsing under GWTView
get out of the properties menu.
Do a clean and a build. there should be no errors. (there may be some warnings) There might be a warning about some server missing.


To run,
Run As -> Web Application



Optional (only if you want to have the ability to compile a faster version, the compile takes longer though):
	right click on project, Google -> GWT Compile. It should take twenty seconds to a minute, and eventually say Compilation succeeded.
	Go to Window -> Show View -> Other.
	Choose Server/Servers.
	Right click in the servers view, hit New -> Server
	Select Apache / Tomcat v7.0 Server, hit next.
	Move GWTView from the available list to the configured list.

	To rebuild and deploy:
	Right click on the project, Google -> GWT Compile.
	If it asks which entry point to use, select edu.calpoly.csc.scheduler.view.web.GWTView
	Once its done, right click on the project, Run As -> Run on Server
	On the dialog that comes up, hit Finish.


If you ever get an error in the application that says "java.lang.ClassNotFoundException: com.mysql.jdbc.Driver" or "Error connecting to the database.":
Right click on the project, Run As -> Run Configurations
In Apache Tomcat -> Tomcat v7.0 Server at localhost, click on classpath tab.
Click on user entries
Click on add external JARs
navigate to repo/implementation/model/java, and select mysql-connector-java-5.0.8-bin.jar, hit open

If you get an error like "javax.servlet.http.HttpServletResponse not found" do the following
1. Right click your GWTView project
2. Go to build path and select Configure Build Path...
3. Go to the Libraries tab
4. Click on Add Library...
5. Select Google Web Toolkit and follow the instructions


To enable assertions:
Right click on GWTView -> Run As -> Run Configurations
Apache Tomcat -> Tomcat v7.0 Server at localhost
Arguments tab
In the VM arguments box, put -ea
Hit apply

Deploy to Server
================

Dependencies
------------

You'll need to install fabric; detailed installation instructions are provided
[on fabfile.org](http://docs.fabfile.org/en/1.3.4/installation.html).

If you have Python and setuptools, you should be able to just do

    $> sudo easy_install fabric

Deploy
------

Fabric is like make, but better.

To display the list of tasks Fabric knows about, run

    $> fab -l

Since building via ant isn't completely working yet, you'll need to build the
project using Eclipse (right-click -> Google -> GWT Compile).  Then, run `fab
deploy` and feed it the names of directories on the server to which you wish to
deploy.  For instance,

    $> fab deploy:dev,CSC,EE

If you deploy to a new directory, you'll have to configure Nginx to proxy
through to Tomcat.  Edit `/etc/nginx/sites-enabled/default` and reload Nginx or
talk to James.

