package scheduler.view.web.shared;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import junit.framework.TestCase;

/**
 * This test class tests the admin view of the instructor settings
 * including adding and deleting istructors, as well as setting the
 * preferences for certain instructors
 * 
 * @author Carsten Pfeffer <pfeffer@tzi.de>
 */
public class InstructorsViewTest extends TestCase {
	private FirefoxDriver fbot;
	
	/**
	 * sets up the test class and initializes the Firefox driver
	 */
	public void setUp()
	{
		this.fbot = new FirefoxDriver();
		fbot.get("http://scheduler.csc.calpoly.edu/dev");
		
		WebElement unameBox, loginButton;
		
		// type in a username
		unameBox = fbot.findElement(By.id("s_unameBox"));
		unameBox.sendKeys("username");
		
		// log in
		loginButton = fbot.findElement(By.id("s_loginBtn"));
		loginButton.click();
		
		this.waitMillis(1000);
		
		// klick the first item in the schedule document list
		WebElement firstRow = fbot.findElement(By.id("isc_1G"));
		firstRow.click();
		
		// change tab here
		// ... [ToDo]
		
		// click on the instructors tab
		fbot.findElement(By.id("isc_1V")).click();
		
		
		System.out.println("set up");
	}
	
	/**
	 * closes the Firefox driver
	 */
	public void tearDown() {
		fbot.close();
		System.out.println("teared down\n");
	}
	
	/**
	 * tests if instructors can be added
	 */
	public void testAddInstructor()
	{
		// click the Button to add an instructor
		fbot.findElement(By.id("addInstructorBtn")).click();
		
		// insert a last name
		
		// insert a first name
		
		// insert a username
		
		// set focus somewhere else
		
		// check if there is a new instructor with this data
	}
	
	/**
	 * tests if instructors can be duplicated
	 */
	public void testDuplicateInstructor()
	{
		// select the first instructor
		
		// click the button to duplicate an instructor
		fbot.findElement(By.id("duplicateInstructorBtn")).click();
		
		// check if there are two of them
	}
	
	/**
	 * tests if instructors can be removed
	 */
	public void testRemoveInstructor()
	{
		// select the first instructor
		
		// click the button to remove an instructor
		fbot.findElement(By.id("removeInstructorBtn")).click();
		
		// check if the instructor is still in the system
	}
	
	/**
	 * tests if the time and course preferences
	 * can be set.
	 */
	public void testSetPreferences()
	{
		// click on the preferences button of the first instructor
		fbot.findElement(By.id("isc_32")).click();
		
		// set Monday    7:00 am to "Not Preferred"
		
		// set Tuesday   8:00 am to "Acceptable"
		
		// set Wednesday 9:00 am to "Preferred"
		
		// set the secont course to "Not Preferred"
		
		// set the third course to "Acceptable"
		
		// set the fourth course to "Preferred"
		
		// check if all settings are correct
	}
	
	
	public void waitMillis(long millis)
	{
		long t0, t1;
		t0 = System.currentTimeMillis();
		do{
			t1 = System.currentTimeMillis();
		}while(t1-t0 < millis);
	}
}
