Architecture Overview
=====================

        CachedService       \
              ^              \  Client
              |              /
      GreetingServiceAsync  /
              ^
              |
           Internet
              ^
              |
       GreetingServiceImpl  \
              ^              \
              |               \
    GreetingServiceImplInner   | Server
              ^               /
              |              /
            Model           /

Model wraps database (ORM).  There are two sets for each class - `Course.java`
and `GWTCourse.java` because `GWTCourse.java` needs to be serializable and
`Course.java` contains database connections and shit that can't be serialized.

`GreetingServiceImplInner` does all the communication between the user and
these classes; everyone calls `GreetingServiceImpl` (a thin wrapper with
logging and sanity checks).

`CachedService` runs on the client (translated to Javascript); it creates the
cached open working-copy documents.  `GreetingServiceAsync` handles the
communication between client-side stuff and `GreetingServiceImpl`.

`CourseDocumentSource` translates between user-viewable working-copy documents
and the underlying `ListGrids`.

We're using both standard GWT and SmartGWT; unfortunately, they don't play
together well.  So, we do some funky stuff to separate them:

     -------------------------------
     | --------------------------- |
     | |                         | |
     | |        The Rest         | |
     | |       (SmartGWT)        | |
     | |                         | |
     |  -------------------------  |
     | --------------------------- |
     | |                         | |
     | |        Calendar         | |
     | |         (GWT)           | |
     | |                         | |
     |  -------------------------  |
     -------------------------------

Sometimes one of these divs is empty (or almost empty), but they're both there.

Setup Development Environment
=============================

1.  Download and install [Eclipse IDE for Java EE Developers][eclipse].
2.  Make sure you have the GWT plugin for eclipse and GWT developer plugin for
    your respective browser
3.  Checkout the project
5.  Import the Scheduler project into eclipse (import -> General -> existing
    projects)
6.  Do a clean and a build. there should be no errors. (there may be some
    warnings)

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

Windows Installation
--------------------

1. Download and install Python 2.7 from
[ActiveState](http://www.activestate.com/activepython/downloads).
2. If on 64-bit, download [a precompiled version of
pycrypto](http://yorickdowne.wordpress.com/2010/12/22/compiling-pycrypto-on-win7-64/).
Put it in `C:\Python27` and extract it into the current folder using 7zip.
3. Open a command prompt and run `pip install fabric`.
4. Install the JDK.
5. Download [ant](http://ant.apache.org/bindownload.cgi) and unzip it to
`C:\ant`.
6. Change a few environment variables (right-click Computer, Advanced system
settings, Environment Variables):
   * `PATH` - add `%ANT_HOME%\bin`
   * `ANT_HOME` - `C:\ant`
   * `JAVA_HOME` - `C:\Program Files\Java\jdk1.7.0_04`

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

