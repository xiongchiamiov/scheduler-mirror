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
19. Hit add jar. add /GWTView/test/edu/calpoly/csc/scheduler/view/web/shared/Selenium/jar/selenium-server-standalone-2.8.0.jar
20. also add the "dnd" jar by clicking on "Add JARs..." and browsing under
    GWTView
21. get out of the properties menu.
22. Do a clean and a build. there should be no errors. (there may be some
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

Testing 
===============

Setup
--------------------------
If you get an error about missing jUnit 3 jar, Eclipse knows how to find and add it to your build path. 
Select the error in the file -> Resolve -> Add jUnit 3 to Build Path

For Selenium Testing 
Compilation errors with missing jar
Add [selenium-server-standalone-2.8.0.jar] to the build path. The jar is located in 
/implementation/test/edu/calpoly/csc/scheduler/view/web/shared/Selenium/jar

Selenium Testing Strategy
--------------------------
For those who are interested/working on selenium testing, here's some info that might be helpful for how it's setup
with our project.

There are several levels to modify for a successful test. Interact-able elements must have a tag in the GWTView code. 
Testing is broken into two groups: emulating the user by writing testcases that follow the flow of logging in, creating 
a schedule, adding data, and generating it. That requires the other level of testing to be complete: verifying that 
elements were initialized on the page. You can do additional testing at this level, also, such as verifying the 
element's text if it has any, handling errors on erroneous input and any popups/error messages/alerts that may show 
up, etc. Generally, use asserts and check for any expected output. But by the time the user-based testcase is 
written and run, all of that basic verification needs to be complete.

First level to be modified is View code. The best way to access elements on a page (such as buttons, fields etc) is to 
associate a tag to the element in the view code, and then access that tag when creating a selenium webelement 
(the interactable item representation). For example, to log in:Ê

in LoginView.java, GWTView

DOM.setElementAttribute(login.getElement(), "id", "login");
this.add(login);

The second level is basic component testing. Functionality has been grouped into 'pages', ie LoginandSelectSchedule, 
Courses, Instructors, etc. Each page takes in a driver element (firefox driver) and uses that driver to 
access page elements. To test your page, you can create a main or something, pass in a driver to instantiate the page 
(skipping use of SchedulerBot if you want, but feel free to use it), and call the methods you create. (Although you 
need to create an instance of login and select a schedule and toolbar to actually be able to navigate to whatever page 
you're working on, but those should mostly work. Let me know if they don't).

Methods that accomplish something (such as, Login with a given username) should be protected, and called through the 
SchedulerBot class which will have an instance of the page.

Here's an example of how Login works after all of the basic instantiation process, in the LoginPage. As you can see, 
the basic level of testing is evident by making sure the button can be instantiated, has relevant text, etc. If it can't 
be found, note it in the error message so that it is apparent where exactly the error is occurring. Login's given a 
firefoxDriver (fbot), and has WebElements called loginBtn and unamefield (the username input field)

protected String CASLogin(String loginID) {
		String errorMsg = "";
		try {
			loginBtn = fbot.findElement(By.id("login")); 
			assertEquals("Login", loginBtn.getText());
			unameField = fbot.findElement(By.id("uname"));				
			unameField.sendKeys(loginID);
			loginBtn.click();
		} catch (org.openqa.selenium.NotFoundException ex){
			errorMsg += "Selenium Page Elements [intial login] not located, check ID's";}				
		try {
			Alert popup = fbot.switchTo().alert();
			errorMsg += popup.getText();
			popup.accept();		
			} catch (NoAlertPresentException ex) {
			System.out.println("Valid credentials");
			 errorMsg += "success";}
		return errorMsg;
	}

The third modification level is driven through SchedulerBot. SchedulerBot acts as the bot with meaningful methods that 
represent user sequence actions, such as logging in, creating a course, creating an Instructor, etc. It'll be used by 
the testcases for creating a full department schedule. Those test cases extend DefaultSelTestCase, which takes 
care of the initialization. Just pass in a URL that you want to be the target AUT (application under test). It can even 
be localhost if you want. It provides the bot whose methods you can call in your test case. 

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

