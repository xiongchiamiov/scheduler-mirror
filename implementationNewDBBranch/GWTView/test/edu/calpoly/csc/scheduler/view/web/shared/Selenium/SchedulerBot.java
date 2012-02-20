package edu.calpoly.csc.scheduler.view.web.shared.Selenium;

import java.util.ArrayList;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.ie.*;
//TODO: had problems with the other browser drivers, pulled multibrowser support out for now
//

/**
 * SchedulerBot provides an intermediary navigator of the Scheduler UI
 * components and functionality
 * 
 * @author Salome Navarrete
 * @version 1.0 Nov 22 2011
 */
public class SchedulerBot {
	
	/** The prototype URL */
	private static final String defaultURL = "http://scheduler.csc.calpoly.edu/test"; 
	/** The provided URL, optional field */
	private String protoURL;
	/** The Firefox Driver. */
	private FirefoxDriver fBot;
	/** The Chrome Driver */
	private ChromeDriver cBot;
	/** The Internet Explorer Driver */
	private InternetExplorerDriver ieBot;
	/** The list of webdrivers */
	private ArrayList<WebDriver> browsers = new ArrayList<WebDriver>();
	/** The login and schedule selection functionality */
	private LoginSelectPage login;	
	/** The location editing functionality. */
	private LocationsPage locations;	
	/** The course editing functionality. */
	private CoursesPage courses;
	/** The instructor preferences functionality */
	private InstructorsPage instructors;	
	/** The basic toolbar functionality */
	private Toolbar toolbar;
	private static final String SUCCESS = "success";
	
	/**
	 * Instantiates a new scheduler bot, page functionality groups,
	 *  and browser drivers. Uses the default prototype url '/test'
	 */
	public SchedulerBot() {
		//hardcoded since removed multibrowser support 
		//fBot = new FirefoxDriver();
		//fBot.get(defaultURL);
		
		System.out.println("Using default bot url: " + defaultURL);
		
		initBots();
		this.fBot.get(defaultURL);
		//for(WebDriver wd : browsers) {
			//wd.get(defaultURL);
		//}
	}
	
	/**
	 * Instantiates a new scheduler bot, page functionality groups,
	 *  and browser drivers. Uses the provided prototype url
	 *  
	 *  @param String prototypeURL the specified URL of the prototype to be tested
	 */
	public SchedulerBot(String prototypeURL) {
		this.protoURL = prototypeURL;
		
		System.out.println("Using provided url: " + protoURL);

		initBots();
		this.fBot.get(protoURL);
		//for(WebDriver wd : browsers) 
			//wd.get(protoURL);
	}
	
	private void initBots() {
		this.fBot = new FirefoxDriver();
		//cBot = new ChromeDriver();
		//ieBot = new InternetExplorerDriver();		
		
		//browsers.add(fBot);
		//browsers.add(cBot);
		//browsers.add(ieBot);
		
		//first thing on starting, load the login page elements
		//and pass a copy of the controller to the various 'pages'
		this.login = new LoginSelectPage(fBot);
		this.toolbar = new Toolbar(fBot);
		this.instructors = new InstructorsPage(fBot);
		this.locations = new LocationsPage(fBot);
		this.courses= new CoursesPage(fBot);
	}
	
	/**
	 * If you're worried an error might be a timing issue, trying to access content
	 * before it loads. Using 3000 for now, feel free to change as needed
	 * 
	 */
	public void pause() {
		try {
			Thread.currentThread().sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//log in to the scheduler and populate schedule
	/**
	 * Login accesses the login page and uses the provided credentials
	 * to log in to the Scheduler Application
	 *
	 * @param String username the provided user name
	 * @return String "success" if successful, else error message
	 */
	public String login(String username) {
		String err;
		if((err = login.CASLogin(username)).equals(SUCCESS)) {
			pause();
			login.initScheduleSelection();
			pause();
			toolbar.initPrimary();
			return SUCCESS;
		}
		return err;
	}
	
	public boolean logout() {
		toolbar.logout();
		
		try {
			WebElement login = fBot.findElement(By.id("login"));
		} catch(org.openqa.selenium.NotFoundException ex) {
			return false;
		}
		return true;
	}
	
	//cleanup and close browser
	/**
	 * Quit and cleanup the active browser session.
	 */
	public void quitSession() {
		fBot.close();		
		//for(WebDriver wd : browsers) 
			//wd.close();
	}
	
	/**
	 * Select a specific schedule associated with the logged-in ID previously provided
	 *
	 * @param String schedule the name of the schedule
	 * @return true, if successful else false
	 */
	public boolean selectSchedule(String schedule) {
		boolean success =  login.selectPreviousSchedule(schedule);	

		if(success) {
			toolbar.init();
			return true;
		}
		return false;
	}
	
	public boolean createNewSchedule() {
		login.addNewSchedule();
		//if it cant init the toolbar it didnt create a schedule
		//and elements'll throw exceptions
		toolbar.init();
		//change to be more meaningful
		return true;
	}
	
	public String getLoggedInUser() {
		return toolbar.checkUsername();
	}
	
	/**
	 * Gets the list of previous schedules.
	 *
	 * @return List<String>  a list of previous schedules available
	 */
	public ArrayList<String> getPreviousSchedules() {	
		return login.getPreviousSchedules();
	}

	/**
	 * Adds an instructor
	 */
	public void addInstructors() {
		toolbar.gotoInstructors();
		pause();
		instructors.init();
		
	}

	/** 
	 */
	public void addInstructors(ArrayList<String> inslist) {
		
	}
	
	/** 
	 * Gets the list of instructors
	 * @return ArrayList<String> a list of instructors
	 * @postcondition: Courses are formatted as follows:
	 * [to be determined]
	 */
	public ArrayList<String> getInstructors() {
		return null;
	}
	
	/**
	 * Removes the instructor.
	 * NOTE: this functionality not yet available in the prototype
	 */
	public void removeInstructor() {
		
	}
	
	/**
	 * Removes all instructors.
	 * NOTE: this functionality not yet available in the prototype
	 */
	public void removeAllInstructors() {
		
	}
	
	/**
	 * Add courses to the course list.
	 * NOTE: this functionality not yet available in the prototype.
	 * 
	 * @param String cname the name of the course
	 * @param int catalogNumber the offical catalog number of the course
	 * @param String dptmt the name of the department
	 * @param int wtu work time units of the course
	 */
	public void addCourse(String cname, int catalogNumber, String dptmt, int wtu) {
		toolbar.gotoCourses();
		//courses.init();
	}
	
	/** 
	 * Add the list of courses to the courselist
	 * @param ArrayList<String> courselist a list of courses
	 * @precondition: Courses are formatted as follows:
	 * [to be determined]
	 */
	public void addCourses(ArrayList<String> courselist) {
		
	}
	
	/**
	 * Removes the course.
	 * NOTE: this functionality not yet available in the prototype
	 */
	public void removeCourse() {
		
	}
	
	/**
	 * Removes all courses.
	 * NOTE: this functionality not yet available in the prototype
	 */
	public void removeAllCourses() {
		
	}
	
	/** 
	 * Gets the list of courses
	 * @return ArrayList<String> a list of instructors
	 * @postcondition: Courses are formatted as follows:
	 * [to be determined]
	 */
	public ArrayList<String> getCourses() {
		return null;
	}
	
	/**
	 * Adds the locations to the locations list.
	 * 
	 * NOTE: Functionality not yet fully available in the prototype
	 * NOTE: Parameter types to be determined
	 */
	public void addLocation() {
		toolbar.gotoLocations();
		//locations.init();
	}
	
	/** 
	 * Add the list of locations
	 * @param ArrayList<String> locationList a list of locations
	 * @precondition: Locations are formatted as follows:
	 * [to be determined]
	 */
	public void addLocations(ArrayList<String> locationList) {
		
	}
	
	/**
	 * Removes the location.
	 * NOTE: this functionality not yet available in the prototype
	 * 
	 * @param String roomID the room number and letter, if applicable. ie 'C303'
	 * or '253', precise ID format to be determined based on prototype
	 * @return true if successful, false otherwise
	 */
	public boolean removeLocation(String roomID) {
		return false;
	}
	
	/**
	 * Removes all locations.
	 * NOTE: this functionality not yet available in the prototype
	 */
	public void removeAllLocations() {
		
	}
	
	/** 
	 * Gets the list of locations
	 * @return ArrayList<String> a list of locations
	 * @postcondition: locations are formatted as follows:
	 * [to be determined]
	 */
	public ArrayList<String> getLocations() {
		return null;
	}
}
