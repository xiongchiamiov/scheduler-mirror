package scheduler.view.web.shared;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.pagefactory.ByChained; 

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
		
		
		
		// Unfortunately, my CSS idea didnt work, it's retardedly hard to set CSS styles (you can do it,
		// but it requires calling getElement(), which lazily generates an element, which has side effects
		// regarding layout). So we need to use XPath.

		// The secret ingredient for finding an element we set the ID on is the eventproxy attribute.
		// When we call setID on something in smartgwt, it sets the eventproxy attribute to that string.
		// (in case you're curious, eventproxy refers to a javascript global. you can see this by going
		// into the browser and typing "javascript:void(window.alert(s_HomeTab))" without the quotes.
		// I think we can rely on eventproxy always being there, because setID is how user *javascript*
		// is supposed to access the object.)
		
		WebElement hometab = driver.findElement(By.xpath("//div[@eventproxy='s_HomeTab']"));
		// Home tab is a gigantic element containing another div, which contains a table, which eventually contains "Home".
		// Calling getText() on such a complex element just returns all the getText()'s of the children appended together, that's why this works.
		assertEquals("Home", hometab.getText());

		// If we want to be super specific, we can do this.
		WebElement labelInsideHomeTab = driver.findElement(By.xpath("//div[@eventproxy='s_HomeTab']/div/table/tbody/tr/td"));
		assertEquals("Home", labelInsideHomeTab.getText());
		
		
		// I just made a helper function called getElementBySmartGWTID below, you can use it like this
		// to achieve the same effect:
		WebElement homeTabGottenFromMyFunc = getElementBySmartGWTID("s_HomeTab");
		assertEquals("Home", homeTabGottenFromMyFunc.getText());
		
		// Again, using my function, but being more specific about which element's contents we want to check:
		WebElement labelInsideHomeTabGottenFromMyFunc = getElementBySmartGWTID("s_HomeTab").findElement(By.xpath("div/table/tbody/tr/td"));
		assertEquals("Home", labelInsideHomeTabGottenFromMyFunc.getText());
		
		
		
		logout.click();

		
	}
	
	private WebElement getElementBySmartGWTID(String smartGWTID) {
		return driver.findElement(By.xpath("//div[@eventproxy='" + smartGWTID + "']"));
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
