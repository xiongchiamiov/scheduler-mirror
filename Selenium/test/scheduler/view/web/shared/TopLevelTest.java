package scheduler.view.web.shared;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;

import scheduler.view.web.shared.Selenium.*;
import junit.framework.TestCase;

/**
 * This tests the top level UI. Primarily this will cover the File and Settings
 * menu.
 * 
 * @author Jordan Hand
 * 
 */
public class TopLevelTest extends DefaultSelTestCase {
	private WebDriver driver;
	private static final String URL = "http://scheduler.csc.calpoly.edu/dev";
	private static final String schedName = "FileMenuTest";

	public void setUp() {
		driver = new FirefoxDriver();
		super.setUp(URL, driver);
	}

	/**
	 * Testing the file menu
	 */
	public void testFileMenu() {

		login();

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO Decide how to handle if schedule of the same name already exists
		createSchedule(schedName);

		// TODO Check New
		// TODO Check Open
		// TODO Check Save
		// TODO Check Save As
		// TODO Check close
		// TODO check merge
		// TODO check import
		// TODO check export

		// TODO CHeck settings

		this.driver.close();
	}

	private void login() {

		WebElement unameBox = this.driver.findElement(By.id("s_unameBox"));
		unameBox.sendKeys("FileMenuTest");

		WebElement loginButton = this.driver.findElement(By.id("s_loginBtn"));
		loginButton.click();
	}

	private void createSchedule(String testName) {
		this.findElementBySmartGWTID("s_createBtn").click();
		
		WebElement createScheduleName = this.driver.findElement(By
				.id("s_createBox"));
		createScheduleName.sendKeys(testName);
		
		WebElement createSchedule = this.driver.findElement(By
				.id("s_createNamedDocBtn"));

		createSchedule.click();

	}

	public void tearDown() {
		super.tearDown();
	}

}
