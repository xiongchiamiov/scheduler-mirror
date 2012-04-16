package scheduler.view.web.shared;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import scheduler.view.web.shared.Selenium.SchedulerBot;
import scheduler.view.web.shared.Selenium.SchedulerBot.PopupWaiter;

import com.google.common.base.Predicate;

public abstract class GRCAcceptanceTest extends DefaultSelTestCase {	
	private WebDriver driver;
	private StringBuffer verificationErrors = new StringBuffer();
	private static final String protoURL = "http://localhost:8080/GRC";
	private SchedulerBot bot;	
	
	/* (non-Javadoc)
	 * @see GWTTests.DefaultSelTestCase#setUp()
	 */
	public void setUp(WebDriver drv) {
		this.driver = drv;
		super.setUp(protoURL, drv);
		bot = new SchedulerBot(driver);
	}
	
	public void tearDown() {
		driver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}
	
	/**
	 * Test initializing page elements
	 * @throws InterruptedException 
	 */
	public void testAcceptanceForGRC() throws InterruptedException {
		final String documentName = "Winter 2012x";
		
		driver.get(protoURL);
		assert(driver.findElement(By.id("s_unameBox")) != null);
		driver.findElement(By.id("s_unameBox")).clear();
		driver.findElement(By.id("s_unameBox")).sendKeys("eovadia");
		driver.findElement(By.id("s_loginBtn")).click();
		bot.waitForElementPresent(By.xpath("//div[@eventproxy='s_createBtn']"));
		
		bot.mouseDownAndUpAt(By.xpath("//div[@eventproxy='s_createBtn']"), 5, 5);

		bot.waitForElementPresent(By.id("s_createBox"));
		
		driver.findElement(By.id("s_createBox")).clear();
		driver.findElement(By.id("s_createBox")).sendKeys(documentName);

		PopupWaiter popupWaiter = bot.getPopupWaiter();
		
		driver.findElement(By.id("s_createNamedDocBtn")).click();
		
		String newWindowHandle = popupWaiter.waitForPopup();
		
		driver.switchTo().window(newWindowHandle);

		// TODO: see if documentName appears anywhere on screen?

		bot.enterIntoResourceTableNewRow(0, null, null, "GRC", "201", "Graphics", "4", "3", "3", null, "4", "30");
		bot.enterIntoResourceTableNewRow(1, null, null, "GRC", "202", "GraphicsB", "4", "3", "3", null, "4", "30");

		// TODO: save
	}
}
