package scheduler.view.web.shared;

import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	public void tearDown() {
		/** cleanup artifacts and quit the browser session for the current test*/
		driver.close();
		driver.quit();
	}

	protected WebElement findElementBySmartGWTID(String smartGWTID) {
		return driver.findElement(By.xpath("//div[@eventproxy='" + smartGWTID + "']"));
	}
}
