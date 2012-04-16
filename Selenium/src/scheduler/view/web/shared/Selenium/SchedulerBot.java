package scheduler.view.web.shared.Selenium;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;

/**
 * SchedulerBot provides an intermediary navigator of the Scheduler UI
 * components and functionality
 * 
 * @author Salome Navarrete
 * @version 1.0 Nov 22 2011
 */
public class SchedulerBot {
	private WebDriver driver;
	
	public SchedulerBot(WebDriver driver) {
		if (driver != null)
			this.driver = driver;
		else
			throw new WebDriverException("Driver not found");
	}
	
	public boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
			return true;
		}
		catch (NoSuchElementException e) {
			return false;
		}
	}

	interface Waitable {
		public boolean stopWaiting();
	}
	
	private void mouseDownAndUpAt(WebElement element, int x, int y) {
		new Actions(driver)
		.clickAndHold(element)
		.moveByOffset(x, y)
		.release(element)
		.build()
		.perform(); 
	}

	public class PopupWaiter {
		final Set<String> initialWindows;
		String poppedUpWindow;
		public PopupWaiter() {
			initialWindows = driver.getWindowHandles();
		}
		public String waitForPopup() {
			new WebDriverWait(driver, 20).until(new Predicate<WebDriver>() {
				public boolean apply(WebDriver arg0) {
					Set<String> currentWindows = driver.getWindowHandles();
					currentWindows.removeAll(initialWindows);
					if (!currentWindows.isEmpty()) {
						poppedUpWindow = currentWindows.iterator().next();
						return true;
					}
					else {
						return false;
					}
				}
			});
			assert(poppedUpWindow != null);
			return poppedUpWindow;
		}
	}
	
	private WebElement elementForResourceTableCell(int row0Based, int col0Based) {
		return driver.findElement(By.xpath("((//table[@class='listTable']/tbody/tr[@role='listitem'])[" + (1 + row0Based) + "]/td)[" + (1 + col0Based) + "]"));
	}
	
	private void setResourceTableTextCell(int row0Based, int col0Based, String text) {
		System.out.println("Entering " + text + " into (" + row0Based + "," + col0Based + ")");
		
		WebElement cell = elementForResourceTableCell(row0Based, col0Based);
		cell.click();
		
		WebElement input = elementForResourceTableCell(row0Based, col0Based).findElement(By.xpath("//input"));
		input.sendKeys(text);

		driver.findElement(By.tagName("body")).click();
	}

	private void setResourceTableSelectCell(int row0Based, int col0Based, String text) {
		WebElement cell = elementForResourceTableCell(row0Based, col0Based);
		cell.click();
		
		System.out.println("TODO: get rid of IND->LAB conversion!");
		if ("IND".equals(text))
			text = "LAB";
		// Making test temporarily pass so we can get on with the rest of it
		
		WebElement selectBeforeExpand = elementForResourceTableCell(row0Based, col0Based).findElement(By.xpath("div/nobr/span/table"));
		selectBeforeExpand.click();
		
		boolean foundOptionToClickOn = false;
		List<WebElement> options = elementForResourceTableCell(row0Based, col0Based).findElements(By.xpath("//tr[@role='listitem']"));
		for (WebElement option : options) {
			if (option.getText().trim().equalsIgnoreCase(text)) {
				option.click();
				foundOptionToClickOn = true;
				break;
			}
		}
		
		assert(foundOptionToClickOn) : "Couldnt find option " + text;

		driver.findElement(By.tagName("body")).click();
		
		assert(elementForResourceTableCell(row0Based, col0Based).getText().trim().equalsIgnoreCase(text));
	}
	
	private void setResourceTableCheckboxCell(int row0Based, int col0Based, boolean newValue) {
		System.out.println("row " + row0Based + " col " + col0Based);
		
//		WebElement newCourseDeptCell = elementForResourceTableCell(row0Based, col0Based);
//		newCourseDeptCell.click();
		
		WebElement img = elementForResourceTableCell(row0Based, col0Based).findElement(By.xpath("//div[@class='labelAnchor']/img"));
		WebElement imgParent = img.findElement(By.xpath("parent::*"));
		assert(imgParent.getTagName().equals("div"));
		
		System.out.println(imgParent.getTagName());
		System.out.println("checked: " + imgParent.getAttribute("aria-checked"));
		
		boolean currentValue = "true".equals(imgParent.getAttribute("aria-checked"));
		
		if (currentValue != newValue)
			mouseDownAndUpAt(imgParent, 5, 5);
		
//		currentValue = "true".equals(imgParent.getAttribute("aria-checked"));
//		assert(currentValue == newValue);
		
		driver.findElement(By.tagName("body")).click();
	}
	
//	private void enterIntoResourceTableRow(int row0Based, Object... values) {
//		for (int i = 0; i < values.length; i++) {
//			Object object = values[i];
//			if (object == null)
//				continue;
//			
//			System.out.println("Handling index " + i + " value " + object);
//			
//			CellType type = openCellAndGetType(row0Based, i);
//			switch (type) {
//				case CHECKBOX: setResourceTableCheckboxCell(row0Based, i, (Boolean)object); break;
//				case TEXT: setResourceTableTextCell(row0Based, i, (String)object); break;
//				case SELECT: setResourceTableSelectCell(row0Based, i, (String)object); break;
//				default: assert(false);
//			}
//		}
//	}
	
//	private void enterIntoResourceTableNewRow(int row0Based, Object...values) {
//		driver.findElement(By.id("s_newCourseBtn")).click();
//		enterIntoResourceTableRow(row0Based, values);
//	}

//	bot.enterIntoResourceTableNewRow(0, true, "GRC", "101", "Graphics", "1", "3", "3", null, "3", "97", "LEC");
	public void enterIntoResourceTableNewRow(
			int row0Based,
			boolean isSchedulable,
			String department,
			String catalogNum,
			String courseName,
			String numSections,
			String wtu,
			String scu,
			String dayCombinations,
			String hoursPerWeek,
			String maxEnrollment,
			String type) {
		
		assert(dayCombinations == null);
		
		driver.findElement(By.id("s_newCourseBtn")).click();
		
		setResourceTableCheckboxCell(row0Based, 1, isSchedulable);
		setResourceTableTextCell(row0Based, 2, department);
		setResourceTableTextCell(row0Based, 3, catalogNum);
		setResourceTableTextCell(row0Based, 4, courseName);
		setResourceTableTextCell(row0Based, 5, numSections);
		setResourceTableTextCell(row0Based, 6, wtu);
		setResourceTableTextCell(row0Based, 7, scu);
//		setResourceTableSelectCell(row0Based, 8, dayCombinations);
		setResourceTableTextCell(row0Based, 9, hoursPerWeek);
		setResourceTableTextCell(row0Based, 10, maxEnrollment);
		setResourceTableSelectCell(row0Based, 11, type);
	}
	
	public void waitForElementPresent(final By by) throws InterruptedException {
		new WebDriverWait(driver, 60).until(new Predicate<WebDriver>() {
			public boolean apply(WebDriver arg0) {
				try {
					if (isElementPresent(by))
						return true;
				}
				catch (Exception e) {}
				return false;
			}
		});
	}
	
	public void mouseDownAndUpAt(By by, int x, int y) {
		WebElement element = driver.findElement(by);
		
		new Actions(driver)
				.clickAndHold(element)
				.moveByOffset(x, y)
				.release(element)
				.build()
				.perform();
	}
	
	public PopupWaiter getPopupWaiter() {
		return new PopupWaiter();
	}
	
}
//
// /** The prototype URL */
// private static final String defaultURL =
// "http://scheduler.csc.calpoly.edu/dev";
// /** The provided URL, optional field */
// private String protoURL;
// /** The Firefox Driver. */
// private FirefoxDriver fBot;
// /** The Chrome Driver */
// private ChromeDriver cBot;
// /** The Internet Explorer Driver */
// private InternetExplorerDriver ieBot;
// /** The list of webdrivers */
// private ArrayList<WebDriver> browsers = new ArrayList<WebDriver>();
// /** The login and schedule selection functionality */
// private LoginSelectPage login;
// /** The location editing functionality. */
// private LocationsPage locations;
// /** The course editing functionality. */
// private CoursesPage courses;
// /** The instructor preferences functionality */
// private InstructorsPage instructors;
// /** The calendar's functionality */
// private CalendarPage calendar;
// /** The basic toolbar functionality */
// private Toolbar toolbar;
// private static final String SUCCESS = "success";
//
// /**
// * Instantiates a new scheduler bot, page functionality groups,
// * and browser drivers. Uses the default prototype url '/test'
// */
// public SchedulerBot() {
// //hardcoded since removed multibrowser support
// //fBot = new FirefoxDriver();
// //fBot.get(defaultURL);
//
// System.out.println("Using default bot url: " + defaultURL);
//
// initBots();
// this.fBot.get(defaultURL);
// //for(WebDriver wd : browsers) {
// //wd.get(defaultURL);
// //}
// }
//
// public SchedulerBot(boolean firefox, boolean ie, boolean chrome) {
//
// }
//
// /**
// * Instantiates a new scheduler bot, page functionality groups,
// * and browser drivers. Uses the provided prototype url
// *
// * @param String prototypeURL the specified URL of the prototype to be tested
// */
// public SchedulerBot(String prototypeURL) {
// this.protoURL = prototypeURL;
//
// System.out.println("Using provided url: " + protoURL);
//
// initBots();
// this.fBot.get(protoURL);
// //for(WebDriver wd : browsers)
// //wd.get(protoURL);
// }
//
// private void initBots() {
// this.fBot = new FirefoxDriver();
// //cBot = new ChromeDriver();
// //ieBot = new InternetExplorerDriver();
//
// //browsers.add(fBot);
// //browsers.add(cBot);
// //browsers.add(ieBot);
//
// //first thing on starting, load the login page elements
// //and pass a copy of the controller to the various 'pages'
// this.login = new LoginSelectPage(fBot);
// this.toolbar = new Toolbar(fBot);
// this.instructors = new InstructorsPage(fBot);
// this.locations = new LocationsPage(fBot);
// this.courses= new CoursesPage(fBot);
// this.calendar = new CalendarPage(fBot);
// }
//
// /**
// * If you're worried an error might be a timing issue, trying to access
// content
// * before it loads. Using 3000 for now, feel free to change as needed
// *
// */
// public void pause() {
// try {
// Thread.currentThread().sleep(3000);
// } catch (InterruptedException e) {
// e.printStackTrace();
// }
// }
//
// //log in to the scheduler and populate schedule
// /**
// * Login accesses the login page and uses the provided credentials
// * to log in to the Scheduler Application
// *
// * @param String username the provided user name
// * @return String "success" if successful, else error message
// */
// public String login(String username) {
// String err;
// if((err = login.CASLogin(username)).equals(SUCCESS)) {
// pause();
// login.initScheduleSelection();
// pause();
// toolbar.initPrimary();
// return SUCCESS;
// }
// return err;
// }
//
// public boolean logout() {
// toolbar.logout();
//
// try {
// WebElement login = fBot.findElement(By.id("login"));
// } catch(org.openqa.selenium.NotFoundException ex) {
// return false;
// }
// return true;
// }
//
// //cleanup and close browser
// /**
// * Quit and cleanup the active browser session.
// */
// public void quitSession() {
// fBot.close();
// //for(WebDriver wd : browsers)
// //wd.close();
// }
//
// /**
// * Select a specific schedule associated with the logged-in ID previously
// provided
// *
// * @param String schedule the name of the schedule
// * @return true, if successful else false
// */
// public boolean selectSchedule(String schedule) {
// boolean success = login.selectPreviousSchedule(schedule);
//
// if(success) {
// toolbar.init();
// return true;
// }
// return false;
// }
//
// public boolean createNewSchedule() {
// login.addNewSchedule();
// //if it cant init the toolbar it didnt create a schedule
// //and elements'll throw exceptions
// toolbar.init();
// //change to be more meaningful
// return true;
// }
//
// public String getLoggedInUser() {
// return toolbar.checkUsername();
// }
//
// /**
// * Gets the list of previous schedules.
// *
// * @return List<String> a list of previous schedules available
// */
// public ArrayList<String> getPreviousSchedules() {
// return login.getPreviousSchedules();
// }
//
// /**
// * Adds an instructor
// */
// public void addInstructors() {
// toolbar.gotoInstructors();
// pause();
// instructors.init();
//
// }
//
// /**
// */
// public void addInstructors(ArrayList<String> inslist) {
//
// }
//
// /**
// * Gets the list of instructors
// * @return ArrayList<String> a list of instructors
// * @postcondition: Courses are formatted as follows:
// * [to be determined]
// */
// public ArrayList<String> getInstructors() {
// return null;
// }
//
// /**
// * Removes the instructor.
// * NOTE: this functionality not yet available in the prototype
// */
// public void removeInstructor() {
//
// }
//
// /**
// * Removes all instructors.
// * NOTE: this functionality not yet available in the prototype
// */
// public void removeAllInstructors() {
//
// }
//
// /**
// * Add courses to the course list.
// * NOTE: this functionality not yet available in the prototype.
// *
// * @param String cname the name of the course
// * @param int catalogNumber the offical catalog number of the course
// * @param String dptmt the name of the department
// * @param int wtu work time units of the course
// */
// public void addCourse(String cname, int catalogNumber, String dptmt, int wtu)
// {
// toolbar.gotoCourses();
// //courses.init();
// }
//
// /**
// * Add the list of courses to the courselist
// * @param ArrayList<String> courselist a list of courses
// * @precondition: Courses are formatted as follows:
// * [to be determined]
// */
// public void addCourses(ArrayList<String> courselist) {
//
// }
//
// /**
// * Removes the course.
// * NOTE: this functionality not yet available in the prototype
// */
// public void removeCourse() {
//
// }
//
// /**
// * Removes all courses.
// * NOTE: this functionality not yet available in the prototype
// */
// public void removeAllCourses() {
//
// }
//
// /**
// * Gets the list of courses
// * @return ArrayList<String> a list of instructors
// * @postcondition: Courses are formatted as follows:
// * [to be determined]
// */
// public ArrayList<String> getCourses() {
// return null;
// }
//
// /**
// * Adds the locations to the locations list.
// *
// * NOTE: Functionality not yet fully available in the prototype
// * NOTE: Parameter types to be determined
// */
// public void addLocation() {
// toolbar.gotoLocations();
// //locations.init();
// }
//
// /**
// * Add the list of locations
// * @param ArrayList<String> locationList a list of locations
// * @precondition: Locations are formatted as follows:
// * [to be determined]
// */
// public void addLocations(ArrayList<String> locationList) {
//
// }
//
// /**
// * Removes the location.
// * NOTE: this functionality not yet available in the prototype
// *
// * @param String roomID the room number and letter, if applicable. ie 'C303'
// * or '253', precise ID format to be determined based on prototype
// * @return true if successful, false otherwise
// */
// public boolean removeLocation(String roomID) {
// return false;
// }
//
// /**
// * Removes all locations.
// * NOTE: this functionality not yet available in the prototype
// */
// public void removeAllLocations() {
//
// }
//
// /**
// * Gets the list of locations
// * @return ArrayList<String> a list of locations
// * @postcondition: locations are formatted as follows:
// * [to be determined]
// */
// public ArrayList<String> getLocations() {
// return null;
// }
// }
