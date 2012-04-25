package scheduler.view.web.shared;

//import java.util.List;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import scheduler.view.web.shared.Selenium.SchedulerBot;
import scheduler.view.web.shared.Selenium.SchedulerBot.PopupWaiter;
import junit.framework.TestCase;

/**
 * This test class tests the admin view of the instructor settings
 * including adding and deleting istructors, as well as setting the
 * preferences for certain instructors
 * 
 * @author Carsten Pfeffer <pfeffer@tzi.de>
 */
public class InstructorsViewTest extends TestCase {
	private FirefoxDriver driver;
	private SchedulerBot bot;
	
	/**
	 * sets up the test class and initializes the Firefox driver
	 */
	public void setUp()
	{
		this.driver = new FirefoxDriver();
		driver.get("http://localhost:8080/dev/");
//		driver.get("http://scheduler.csc.calpoly.edu/dev");
		this.bot = new SchedulerBot(driver);
		
		try {
			this.bot.waitForElementPresent(By.id("s_loginBtn"));
		} catch (InterruptedException e) {
			// print the exception and wait 1 second instead and hope that it works
			e.printStackTrace();
			this.waitMillis(1000);
		}
		
		WebElement loginBtn = driver.findElement(By.id("s_loginBtn")); 
		WebElement unameField = driver.findElement(By.id("s_unameBox"));
		
		unameField.sendKeys("admin");
		loginBtn.click();
		
		
		try {
			this.waitForSmartGWTElement("s_createBtn");
		} catch (InterruptedException e1) {
			fail("Schedule document overview page was not loaded properly");
		}
		
		PopupWaiter popupWaiter = bot.getPopupWaiter();
		
		WebElement first_doc = this.getElementBySmartGWTID("sc_document_0");
		if(first_doc == null)
		{
			this.addNewDocument();
		}
		else
		{
			first_doc.click();
		}
		
		// change broswer tab here
		String newWindowHandle = popupWaiter.waitForPopup();
		driver.switchTo().window(newWindowHandle);
		
		// click on the instructors tab
		try {
			this.waitForSmartGWTElement("s_instructorsTab");
		} catch (InterruptedException e) {
			fail("instructors tab could not be found");
			return;
		}
		WebElement tab = this.getElementBySmartGWTID("s_instructorsTab");
		
		assertEquals("Instructors", tab.getText());
		tab.click();
		
		System.out.println("set up");
	}
	
	/**
	 * closes the Firefox driver
	 */
	public void tearDown() {
		driver.close();
		System.out.println("teared down\n");
	}
	
//	/**
//	 * tests if instructors can be added
//	 */
//	public void testAddInstructor()
//	{
//		this.addInstructor(true, "World", "Hello", "foo", "30");
//		this.saveData();
//	}
//	
//	/**
//	 * tests if instructors can be duplicated
//	 */
//	public void testDuplicateInstructor()
//	{
//		// select the first instructor
//		WebElement cell = bot.elementForResourceTableCell("s_instructorviewTab", 0, 1);
//		bot.mouseDownAndUpAt(cell, 1, 1);
//		this.waitMillis(500);
//		// click the button to duplicate an instructor
//		this.getElementBySmartGWTID("duplicateBtn").click();
//		
//		this.waitMillis(500);
//		
//		// change username of the duplicate, otherwise we won't be able to save
//		try {
//			bot.setResourceTableTextCell("s_instructorviewTab", 1, 4, "bar");
//		} catch (InterruptedException e) {
//			fail("failed to enter a new instructor user name");
//		}
//		
//		this.saveData();
//	}
//	
//	/**
//	 * tests if instructors can be removed
//	 */
//	public void testRemoveInstructor()
//	{		
//		// select the first instructor
//		WebElement cell = bot.elementForResourceTableCell("s_instructorviewTab", 0, 1);
//		bot.mouseDownAndUpAt(cell, 1, 1);
//		this.waitMillis(500);
//		
//		// click the button to remove an instructor
//		this.getElementBySmartGWTID("removeBtn").click();
//		
//		this.waitMillis(500);
//		
//		// select again the first instructor (which should be the former second one)
//		cell = bot.elementForResourceTableCell("s_instructorviewTab", 0, 1);
//		bot.mouseDownAndUpAt(cell, 1, 1);
//		this.waitMillis(500);
//		
//		// click the button to remove an instructor to remove the snd one as well
//		this.getElementBySmartGWTID("removeBtn").click();
//		
//		this.waitMillis(500);
//		
//		
//		this.saveData();
//	}
	
	/**
	 * tests if the time and course preferences
	 * can be set.
	 */
	public void testSetPreferences()
	{
//		// first add an instructor
//		this.addInstructor(true, "Jones", "Jupiter", "jujo", "10");
//		
//		this.waitMillis(500);
//		
//		// click on the preferences button of the first instructor
//		try {
//			this.bot.clickInstructorsResourceTablePreferencesButton(0);
//		} catch (InterruptedException e) {
//			fail("there is no button for setting preferences");
//			return;
//		}
//		
//		this.waitMillis(500);
//		
//		// since there is no course we will get a message which asks whether we
//		// want to proceed. We click "No"
//		try {
//			bot.waitForElementPresent(By.id("noButton"));
//		} catch (InterruptedException e) {
//			fail("a dialog should have popped up");
//			return;
//		}
//		driver.findElement(By.id("noButton")).click();
//		
//		// Then we add four courses:
//		WebElement tab = this.getElementBySmartGWTID("s_coursesTab");
//		tab.click();
//		
//		try {
//			this.waitForSmartGWTElement("s_newCourseBtn");
//		} catch (InterruptedException e1) {
//			fail("Button for adding courses could not be found");
//			return;
//		}
//		
//		// insert data
//		try {
//			bot.enterIntoCoursesResourceTableNewRow(0, true, "CPE", "406", "Software Deployment", "4", "8", "4", "TT", "8", "30", "LEC", "", "");
//			bot.enterIntoCoursesResourceTableNewRow(1, true, "CSC", "471", "Introduction to Computer Graphics", "4", "8", "4", "TT", "8", "30", "LEC", "", "");
//			bot.enterIntoCoursesResourceTableNewRow(2, true, "CSC", "484", "User Centered Interface Design", "4", "8", "4", "TT", "8", "30", "LEC", "", "");
//			bot.enterIntoCoursesResourceTableNewRow(3, true, "CSC", "530", "Languages and Translators", "4", "8", "4", "TT", "8", "30", "LEC", "", "");
//		} catch (InterruptedException e) {
//			fail("could not enter course data");
//			e.printStackTrace();
//		}
		WebElement tab; //!
		
		// then we switch back to the instructors tab
		tab = this.getElementBySmartGWTID("s_instructorsTab");
		tab.click();
		
		this.waitMillis(500);
		
		// click on the preferences button of the first instructor
		try {
			this.bot.clickInstructorsResourceTablePreferencesButton(0);
		} catch (InterruptedException e) {
			fail("there is no button for setting preferences");
			return;
		}
		
		
		WebElement timeTable = driver.findElement(By.id("timePrefsTable"));
		List<WebElement> rows = timeTable.findElements(By.tagName("tr"));;
		WebElement cell;
		WebElement select;
		
		// set Monday    7:00 am to "Not Preferred"
		cell = rows.get(1).findElements(By.tagName("td")).get(1);
		select = cell.findElement(By.tagName("select"));
		select.click();
		select.findElements(By.tagName("option")).get(1).click();
//		assertEquals("Not Preferred", );
		this.waitMillis(200);
		
		// set Tuesday   8:00 am to "Acceptable"
		cell = rows.get(2).findElements(By.tagName("td")).get(2);
		select = cell.findElement(By.tagName("select"));
		select.click();
		select.findElements(By.tagName("option")).get(2).click();
//		assertEquals("Acceptable", );
		this.waitMillis(200);
		
		// set Wednesday 9:00 am to "Preferred"
		cell = rows.get(3).findElements(By.tagName("td")).get(3);
		select = cell.findElement(By.tagName("select"));
		select.click();
		select.findElements(By.tagName("option")).get(3).click();
//		assertEquals("Preferred", );
		this.waitMillis(200);
		
		
		WebElement courseTable = driver.findElement(By.id("coursePrefsTable"));
		rows = courseTable.findElements(By.tagName("tr"));;
		
		// set the second course to "Not Preferred"
		cell = rows.get(2).findElements(By.tagName("td")).get(1);
		select = cell.findElement(By.tagName("select"));
		select.click();
		this.waitMillis(5000);
		select.findElements(By.tagName("option")).get(1).click();
//		assertEquals("Not Preferred", );
		this.waitMillis(5000);
		
		// set the third  course to "Acceptable"
		cell = rows.get(3).findElements(By.tagName("td")).get(1);
		select = cell.findElement(By.tagName("select"));
		select.click();
		this.waitMillis(200);
		select.findElements(By.tagName("option")).get(2).click();
//		assertEquals("Acceptable", );
		this.waitMillis(200);
		
		// set the fourth course to "Preferred"
		cell = rows.get(4).findElements(By.tagName("td")).get(1);
		select = cell.findElement(By.tagName("select"));
		select.click();
		this.waitMillis(200);
		select.findElements(By.tagName("option")).get(3).click();
//		assertEquals("Preferred", );
		this.waitMillis(200);
		
		this.waitMillis(2000);
		// TODO: check if all settings are correct
	}
	
	/**
	 * Waits the given amount, it is used when elements
	 * need more time to be loaded
	 * @param millis
	 */
	public void waitMillis(long millis)
	{
		long t0, t1;
		t0 = System.currentTimeMillis();
		do{
			t1 = System.currentTimeMillis();
		}while(t1-t0 < millis);
	}
	
	/**
	 * finds a smartgwt element by its id
	 * @param smartGWTID
	 * @return the found element
	 */
	private WebElement getElementBySmartGWTID(String smartGWTID) {
		try
		{
			return this.driver.findElement(By.xpath("//div[@eventproxy='" + smartGWTID + "']"));
		}
		catch(NoSuchElementException e)
		{
			return null;
		}
	}
	
	/**
	 * adds a new document if ther is no document for testing
	 */
	private void addNewDocument()
	{
		System.out.println("new document");
		WebElement createBtn = this.getElementBySmartGWTID("s_createBtn");
		createBtn.click();
		
		this.waitMillis(1000);
		
		WebElement docName = driver.findElement(By.id("s_createBox"));
		docName.sendKeys("first document");

		driver.findElement(By.id("s_createNamedDocBtn")).click();
	}
	
	/**
	 * waits for the given smartGWT element
	 * @param id
	 * @throws InterruptedException 
	 */
	private void waitForSmartGWTElement(String id) throws InterruptedException
	{
		this.bot.waitForElementPresent(By.xpath("//div[@eventproxy='" + id + "']"));
	}
	
	private void saveData()
	{
		// Test saving
		driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
		driver.findElement(By.xpath("//td[@class='menuTitleField']/nobr[text()='Save']")).click();
		driver.switchTo().alert().accept();
	}
	
	private void addInstructor(boolean schedulable, String lastName, String firstName,
			String userName, String wtu)
	{
		try {
			this.waitForSmartGWTElement("addInstructorBtn");
		} catch (InterruptedException e1) {
			fail("Button for adding instructors could not be found");
			return;
		}
		
		// insert data
		try {
			bot.enterIntoInstructorsResourceTableNewRow(0, schedulable, lastName, firstName, userName, wtu);
		} catch (InterruptedException e) {
			fail("could not enter instructor data");
			e.printStackTrace();
		}
	}
}
