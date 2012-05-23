package scheduler.view.web.shared;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import scheduler.view.web.shared.Selenium.WebUtility;

public abstract class FSNAcceptanceTest extends DefaultSelTestCase {	
	private WebDriver driver;
	private StringBuffer verificationErrors = new StringBuffer();
	private static String protoURL;
	
	public void setUp(WebDriver drv) throws java.io.IOException {
		Properties properties = new Properties();
		properties.load(this.getClass().getResourceAsStream("selenium.properties"));
		this.protoURL = properties.getProperty("domain") + "/FSN";
		
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
	
	private void deleteDocumentFromHomeTab(final String documentName) throws InterruptedException {
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
		List<WebElement> existingDocumentsNames = driver.findElements(By.xpath("//div[@class='gridBody']//td[contains(@class, 'homeDocumentLink')]"));
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		
		Integer existingDocumentIndex = null;
		for (int i = 0; existingDocumentIndex == null && i < existingDocumentsNames.size(); i++) {
			System.out.println("Existing document index " + i + " has name " + existingDocumentsNames.get(i).getText().trim() + " comparing to " + documentName);
			if (existingDocumentsNames.get(i).getText().trim().equalsIgnoreCase(documentName))
				existingDocumentIndex = i;
		}
		System.out.println("Found document " + documentName + " at: " + existingDocumentIndex);
		
		if (existingDocumentIndex != null) {
			By rowXPath = By.xpath("//table[@class='listTable']/tbody/tr[@role='listitem'][" + (existingDocumentIndex + 1) + "]");
			
			WebElement existingDocumentClickableCell = driver.findElement(rowXPath).findElement(By.xpath("td[2]/div"));
			WebUtility.mouseDownAndUpAt(driver, existingDocumentClickableCell, 5, 5);
			
			assert("true".equals(driver.findElement(rowXPath).getAttribute("aria-selected"))); // Sanity check, sometimes it was selecting the wrong one, given my xpath...
			
			WebUtility.mouseDownAndUpAt(driver, By.xpath("//div/table/tbody/tr/td[text()='Delete Selected Documents']"), 5, 5);
			
			Thread.sleep(5000);
			
			assert(driver.findElements(By.xpath("//div[@class='gridBody']//td[contains(@class, 'homeDocumentLink')]")).size() == existingDocumentsNames.size() - 1);
		}
	}
	
	private void createDocumentFromHomeTabAndSwitchToItsWindow(final String documentName) throws InterruptedException {
		WebUtility.mouseDownAndUpAt(driver, By.xpath("//div/table/tbody/tr/td[text()='Create New Document']"), 5, 5);

		WebUtility.waitForElementPresent(driver, By.id("s_createBox"));
		
		driver.findElement(By.id("s_createBox")).clear();
		driver.findElement(By.id("s_createBox")).sendKeys(documentName);
		
		driver.findElement(By.id("s_createNamedDocBtn")).click();
	}
	
	private void login(final String username) throws InterruptedException {
		driver.findElement(By.id("s_unameBox")).clear();
		driver.findElement(By.id("s_unameBox")).sendKeys("pearson");
		driver.findElement(By.id("s_loginBtn")).click();
		WebUtility.waitForElementPresent(driver, By.xpath("//div/table/tbody/tr/td[text()='Create New Document']"));
		Thread.sleep(2000); // To wait for it to retrieve documents
	}
	
	public void testAcceptanceForGRC() throws InterruptedException {
		login("pearson");
		
		final String documentName = "FSN Acceptance Test Document";
		deleteDocumentFromHomeTab(documentName);
		createDocumentFromHomeTabAndSwitchToItsWindow(documentName);

		// By default we're looking at the courses view, so start filling out courses
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 0, true, "FSN", "121", "Fundamentals of Food", "1", "3", "3", "MW,TR", "3", "97", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 1, true, "FSN", "200", "Special Problems", "1", "3", "3", null, null, "10", "IND", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 2, true, "FSN", "200", "Special Problems", "1", "3", "3", null, null, "10", "IND", null, null);
		
		// Test saving
		driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
		driver.findElement(By.xpath("//td[@class='menuTitleField']//nobr[text()='Save']")).click();
		Thread.sleep(500);
		driver.switchTo().alert().accept();
		Thread.sleep(500);

		// Click on the instructors tab
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Instructors']")).click();
		Thread.sleep(500);
		
		// Start filling out instructors
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Pearson", "James", "pearson", "20");
		
		// Test saving
		driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
		driver.findElement(By.xpath("//td[@class='menuTitleField']//nobr[text()='Save']")).click();
		Thread.sleep(500);
		driver.switchTo().alert().accept();
		Thread.sleep(500);

		// Click on the locations tab
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Locations']")).click();

		// Start filling out locations
		WebUtility.enterIntoLocationsResourceTableNewRow(driver, 0, true, "14-255", "Smart Room", "9001", null);
		
		// Test saving
		driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
		driver.findElement(By.xpath("//td[@class='menuTitleField']//nobr[text()='Save']")).click();
		Thread.sleep(500);
		driver.switchTo().alert().accept();
		Thread.sleep(500);

	}
}

// TODO: see if documentName appears anywhere on screen?

// saving:
