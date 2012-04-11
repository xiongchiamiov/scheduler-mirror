package scheduler.view.web.shared;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class FirefoxLoginTest extends DefaultSelTestCase {	
	
	/** The driver to use. */
	WebDriver driver;
	/** test url */
	private static final String protoURL = "http://scheduler.csc.calpoly.edu/dev";
	/** the webelements to test*/
	WebElement loginBtn, unameField, appTitle, logTitle;
	
	
	/* (non-Javadoc)
	 * @see GWTTests.DefaultSelTestCase#setUp()
	 */
	public void setUp() {
		//log in to scheduler
		driver = new FirefoxDriver();
		super.setUp(protoURL, driver);
	}
	
	/**
	 * Test initializing page elements
	 */
	public void testPageElements() {
		System.out.println();
		System.out.println("--------Login Testcase 1: Intialize page elements-------------------------");

		loginBtn = driver.findElement(By.id("s_loginBtn")); 
		appTitle = driver.findElement(By.id("appNameTtl"));
		assertEquals("Schedulizerifier", appTitle.getText());
		assertEquals("Login", loginBtn.getText());
		unameField = driver.findElement(By.id("s_unameBox"));
	    //logTitle = fbot.findElement(By.id("s_LoginTag"));
	    //assertEquals("Login", logTitle.getText());
	    //System.out.println("logTitle text: " + logTitle.getText());
		
		unameField.sendKeys("snavarre");
		loginBtn.click();
		
		//WebElement title = fbot.findElement(By.id("s_loginTag"));
		//assertEquals("Login", title.getText());

		WebElement user = driver.findElement(By.id("s_unameLbl"));
		assertEquals("snavarre", user.getText());
		WebElement logout = driver.findElement(By.id("s_logoutLnk"));
		WebElement hometab = driver.findElement(By.id("s_HomeTab"));
		assertEquals("Home", hometab.getText());
		
		logout.click();

		
	}
	
	/**
	 * Test fail login.
	 */
	//@Test 
	public void testEmptyLogin() {
//		System.out.println();
//		System.out.println("--------Login Testcase 2: Empty username-------------------------");		
	}
	
	/**
	 * Test real login.
	 */
	public void testRealLogin() {
//		System.out.println();
//		System.out.println("--------Login Testcase 3: Login user 'SelTCLogin'------------------");
//		
//		
	}
	
	/* (non-Javadoc)
	 * @see GWTTests.DefaultSelTestCase#tearDown()
	 */
	public void tearDown() {
		//close browser session
		super.tearDown();
	}
}
