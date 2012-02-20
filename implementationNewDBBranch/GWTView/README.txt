Setup Development Environment
=============================

1.  Download and install [Eclipse IDE for Java EE Developers][eclipse].
2.  Make sure you have the GWT plugin for eclipse and GWT developer plugin for
    your respective browser
3.  Checkout the project
4.  In /implementation/GWTView/:
    1. Copy the file ".project template" and name it to ".project"
    2. Copy the file ".classpath template" and name it to ".classpath"
5.  Import the GWTView project into eclipse (import -> General -> existing
    projects)
6.  Do a clean and a build. You should get a fuckton of errors about various
    classes not found. This is normal.
7.  Right click on the GWTView project, hit properties
8.  Under java build path screen's source tab, hit Link Source
9.  For the linked folder location, browse to repo/implementation/model/java
10. for the folder name, type "modeljava"
11. hit Finish.
12. Go to the libraries tab (still in java build path screen)
13. Hit add external jar. add
    repo/implementation/model/java/mysql-connector-java-5.0.8-bin.jar.
14. Hit add external jar. add repo/implementation/GWTView/gwt-dnd-3.1.2.jar.
15. Hit add external jar. add
    repo/implementation/GWTView/war/WEB-INF/lib/commons-fileupload-1.2.2.jar
16. Hit add external jar. add
    repo/implementation/GWTView/war/WEB-INF/lib/guava-r09.jar
17. Hit add external jar. add repo/implementation/model/java/javacsv.jar
18. Hit add external jar. add repo/implementation/GWTView/test/test-jars/junit-3.8.2.jar
19. also add the "dnd" jar by clicking on "Add JARs..." and browsing under
    GWTView
20. get out of the properties menu.
21. Do a clean and a build. there should be no errors. (there may be some
    warnings) There might be a warning about some server missing.

[eclipse]: http://www.eclipse.org/downloads/

Running
-------

1. Run As -> Web Application

*Optional* (only if you want to have the ability to compile a faster version,
the compile takes longer though):

1. right click on project, Google -> GWT Compile. It should take twenty seconds
   to a minute, and eventually say Compilation succeeded.
2. Go to Window -> Show View -> Other.
3. Choose Server/Servers.
4. Right click in the servers view, hit New -> Server
5. Select Apache / Tomcat v7.0 Server, hit next.
6. Move GWTView from the available list to the configured list.

To rebuild and deploy:

1. Right click on the project, Google -> GWT Compile.
2. If it asks which entry point to use, select
   `edu.calpoly.csc.scheduler.view.web.GWTView`
3. Once its done, right click on the project, Run As -> Run on Server
4. On the dialog that comes up, hit Finish.


If you ever get an error in the application that says
`java.lang.ClassNotFoundException: com.mysql.jdbc.Driver` or `Error connecting
to the database.`:

1. Right click on the project, Run As -> Run Configurations
2. In Apache Tomcat -> Tomcat v7.0 Server at localhost, click on classpath tab.
3. Click on user entries
4. Click on add external JARs
5. navigate to repo/implementation/model/java, and select
   mysql-connector-java-5.0.8-bin.jar, hit open

If you get an error like `javax.servlet.http.HttpServletResponse not found` do
the following:

1. Right click your GWTView project
2. Go to build path and select Configure Build Path...
3. Go to the Libraries tab
4. Click on Add Library...
5. Select Google Web Toolkit and follow the instructions

To enable assertions:

1. Right click on GWTView -> Run As -> Run Configurations
2. Apache Tomcat -> Tomcat v7.0 Server at localhost
3. Arguments tab
4. In the VM arguments box, put -ea
5. Hit apply

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

First, you'll need to build the project; then, run `fab deploy` and feed it the
names of directories on the server to which you wish to deploy.  For instance,

    $> fab build && fab deploy:dev,CSC,EE

If you deploy to a new directory, you'll have to configure Nginx to proxy
through to Tomcat.  Edit `/etc/nginx/sites-enabled/default` and reload Nginx or
talk to James.

