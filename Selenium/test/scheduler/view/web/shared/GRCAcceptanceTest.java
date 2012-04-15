package scheduler.view.web.shared;

import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public abstract class GRCAcceptanceTest extends DefaultSelTestCase {	
	private WebDriver driver;
	private StringBuffer verificationErrors = new StringBuffer();
	private static final String protoURL = "http://localhost:8080/GRC";
	
	
	
	/* (non-Javadoc)
	 * @see GWTTests.DefaultSelTestCase#setUp()
	 */
	public void setUp(WebDriver drv) {
		this.driver = drv;
		super.setUp(protoURL, drv);
	}
	
	public void tearDown() {
		driver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}

	private boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}
	
	/**
	 * Test initializing page elements
	 * @throws InterruptedException 
	 */
	public void testAcceptanceForGRC() throws InterruptedException {
		driver.get(protoURL);
		assert(driver.findElement(By.id("s_unameBox")) != null);
		driver.findElement(By.id("s_unameBox")).clear();
		driver.findElement(By.id("s_unameBox")).sendKeys("eovadia");
		driver.findElement(By.id("s_loginBtn")).click();
		// ERROR: Caught exception [ERROR: Unsupported command [isTextPresent]]
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (isElementPresent(By.xpath("//div[@eventproxy='s_createBtn']"))) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

//		// ERROR: Caught exception [ERROR: Unsupported command [mouseDownAt]]
//		// ERROR: Caught exception [ERROR: Unsupported command [mouseUpAt]]
//		for (int second = 0;; second++) {
//			if (second >= 60) fail("timeout");
//			try { if (isElementPresent(By.id("s_createBox"))) break; } catch (Exception e) {}
//			Thread.sleep(1000);
//		}
//
//		driver.findElement(By.id("s_createBox")).clear();
//		driver.findElement(By.id("s_createBox")).sendKeys("Winter 2012c");
//		driver.findElement(By.id("s_createNamedDocBtn")).click();
//		// ERROR: Caught exception [ERROR: Unsupported command [waitForPopUp]]
//		// ERROR: Caught exception [ERROR: Unsupported command [selectWindow]]
//		driver.findElement(By.id("s_newCourseBtn")).click();
//		driver.findElement(By.id("isc_2M")).clear();
//		driver.findElement(By.id("isc_2M")).sendKeys("GRC");
//		driver.findElement(By.id("isc_2P")).clear();
//		driver.findElement(By.id("isc_2P")).sendKeys("201");
//		driver.findElement(By.id("isc_2S")).clear();
//		driver.findElement(By.id("isc_2S")).sendKeys("Graphics");
//		driver.findElement(By.id("isc_2V")).clear();
//		driver.findElement(By.id("isc_2V")).sendKeys("3");
//		driver.findElement(By.id("isc_2Y")).clear();
//		driver.findElement(By.id("isc_2Y")).sendKeys("4");
//		driver.findElement(By.id("isc_31")).clear();
//		driver.findElement(By.id("isc_31")).sendKeys("4");
//		driver.findElement(By.id("isc_38")).click();
//		driver.findElement(By.id("isc_3H")).clear();
//		driver.findElement(By.id("isc_3H")).sendKeys("4");
//		driver.findElement(By.id("isc_3K")).clear();
//		driver.findElement(By.id("isc_3K")).sendKeys("20");
//		driver.findElement(By.xpath("//table[@id='isc_3Otable']/tbody[2]/tr[4]/td[2]")).click();
//		// ERROR: Caught exception [ERROR: Unsupported command [getAlert]]
	}
}
