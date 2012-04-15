package scheduler.view.web.shared;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;

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
	
	interface Waitable {
		public boolean stopWaiting();
	}
	
	private void waitForElementPresent(final By by) throws InterruptedException {
		new WebDriverWait(driver, 60).until(new Predicate<WebDriver>() {
			public boolean apply(WebDriver arg0) {
				try {
					if (isElementPresent(by))
						return true;
				} catch (Exception e) {}
				return false;
			}
		});
	}
	
	private void mouseDownAndUpAt(By by, int x, int y) {
		WebElement element = driver.findElement(by);

		new Actions(driver)
				.clickAndHold(element)
				.moveByOffset(x, y)
				.release(element)
				.build()
				.perform(); 
	}
	
	class PopupWaiter {
		final Set<String> initialWindows;
		String poppedUpWindow;
		public PopupWaiter() {
			initialWindows = driver.getWindowHandles();
		}
		String waitForPopup() {
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
	
	private void enterIntoResourceTableCell(int row0Based, int col0Based, String text) {
		WebElement newCourseDeptCell = elementForResourceTableCell(row0Based, col0Based);
		newCourseDeptCell.click();
		
		WebElement input = elementForResourceTableCell(0, 2).findElement(By.xpath("//input"));
		input.sendKeys(text);

		driver.findElement(By.tagName("body")).click();
	}
	
	private void enterIntoResourceTableRow(int row0Based, Object... values) {
		for (int i = 0; i < values.length; i++) {
			Object object = values[i];
			if (object == null)
				;
			else if (object instanceof String)
				enterIntoResourceTableCell(row0Based, i, (String)object);
			else
				assert(false);
		}
	}
	
	private void enterIntoResourceTableNewRow(int row0Based, Object...values) {
		driver.findElement(By.id("s_newCourseBtn")).click();
		enterIntoResourceTableRow(row0Based, values);
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
		waitForElementPresent(By.xpath("//div[@eventproxy='s_createBtn']"));
		
		mouseDownAndUpAt(By.xpath("//div[@eventproxy='s_createBtn']"), 5, 5);

		waitForElementPresent(By.id("s_createBox"));
		
		driver.findElement(By.id("s_createBox")).clear();
		driver.findElement(By.id("s_createBox")).sendKeys(documentName);

		PopupWaiter popupWaiter = new PopupWaiter();
		
		driver.findElement(By.id("s_createNamedDocBtn")).click();
		
		String newWindowHandle = popupWaiter.waitForPopup();
		
		driver.switchTo().window(newWindowHandle);

		// TODO: see if documentName appears anywhere on screen?

		enterIntoResourceTableNewRow(0, null, null, "GRC", "201", "Graphics", "4", "3", "3", null, "4", "30");
		enterIntoResourceTableNewRow(1, null, null, "GRC", "202", "GraphicsB", "4", "3", "3", null, "4", "30");

		// TODO: save
	}
}
