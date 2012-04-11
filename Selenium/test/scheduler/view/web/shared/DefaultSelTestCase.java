package scheduler.view.web.shared;

import org.openqa.selenium.WebDriver;

import junit.framework.TestCase;
//import org.junit.*;
//import org.junit.runner.RunWith;
//import org.junit.runners.Suite;
import scheduler.view.web.shared.Selenium.*;

/**
 * Abstract class DefaultSelTestCase 
 * Provides a basic override-able test template for all Selenium/UI
 * based testcases
 */
//@RunWith(Suite.class)
public class DefaultSelTestCase extends TestCase {
	
	/** The bot in charge of navigating the Scheduler GUI. */
	//SchedulerBot bot;
	private WebDriver driver;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp(String url, WebDriver drv) {
		this.driver = drv;
		drv.get(url);
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	public void tearDown() {
		/** cleanup artifacts and quit the browser session for the current test*/
		driver.close();
		driver.quit();
	}
}
