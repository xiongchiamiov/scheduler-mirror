package scheduler.view.web.shared;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import scheduler.view.web.shared.Selenium.WebUtility;

public abstract class MATHAcceptanceTest extends DefaultSelTestCase {	
	private WebDriver driver;
	private StringBuffer verificationErrors = new StringBuffer();
	private static String protoURL;
	
	public void setUp(WebDriver drv) throws java.io.IOException {
		Properties properties = new Properties();
		properties.load(this.getClass().getResourceAsStream("selenium.properties"));
		this.protoURL = properties.getProperty("domain") + "/MATH";
		
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
		driver.findElement(By.id("s_unameBox")).sendKeys(username); //because admin is the new cool thing
		driver.findElement(By.id("s_loginBtn")).click();
		WebUtility.waitForElementPresent(driver, By.xpath("//div/table/tbody/tr/td[text()='Create New Document']"));
		Thread.sleep(2000); // To wait for it to retrieve documents
	}
	
	public void testAcceptanceForMATH() throws InterruptedException {
		login("admin");
		
		final String documentName = "MATH Acceptance Test Document Spring 2012";
		deleteDocumentFromHomeTab(documentName);
		createDocumentFromHomeTabAndSwitchToItsWindow(documentName);

		// By default we're looking at the courses view, so start filling out courses
		//driver, row, schedulable, dptmt, catnum, name, numsect, wtu, scu, daycombos, hpw, maxenrl, type, usedquip,istethered
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 0, true, "MATH", "182", "Calculus for Arch and Construction Management", "2", "2", "2", "TR", "2", "35", "LEC", null, null);
		//not sure on convention for lab/acts with same name type
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 1, true, "MATH", "117", "Precalculus Algebra II", "1", "2", "2", "F", "2", "35", "ACT", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 2, true, "MATH", "142", "Calculus II", "1", "3", "3", "W", "3", "35", "LEC", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 3, true, "MATH", "143", "Calculus III", "1", "1", "1", "M, W, T, R, F", "2", "35", "ACT", null, "MATH 252");
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 4, true, "MATH", "192", "Calculus for Arch and Construction Management Workshop", "1", "3", "3", "TR", "3", "25", "LAB", null, "MATH 182");
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 5, true, "MATH", "126", "Precalculus Algebra Worshop I", "1", "1", "1", "W", "2", "25", "LAB", null, "MATH 260");
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 6, true, "MATH", "141", "Calculus I", "3", "3", "3", null, "3", "35", "LAB", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 7, true, "MATH", "116", "Precalculus Algebra I", "1", "3", "3", "TR", "3", "35", "LEC", null, null);

		
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
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Grishchenko", "Svetlana", "sgrishch", "8");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 1, true, "McCaughey", "Timothy", "tmccaugh", "4");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 2, true, "Martin", "Emily", "emarti46", "4");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 3, true, "Ponder", "Christian", "cponder", "4");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 4, true, "Pearson", "Staci", "sapearso", "12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 5, true, "Bates", "Garret", "gbates", "8");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 6, true, "Millan", "Jose", "jmillan", "12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 7, true, "Stankus", "Mark", "mstankus", "14");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 8, true, "Sze", "Lawrence", "lsze", "12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 9, true, "Kato", "Goro", "gkato", "12");
		
		// Test saving
		driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
		driver.findElement(By.xpath("//td[@class='menuTitleField']//nobr[text()='Save']")).click();
		Thread.sleep(500);
		driver.switchTo().alert().accept();
		Thread.sleep(500);

		// Click on the locations tab
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Locations']")).click();

		// Start filling out locations
		WebUtility.enterIntoLocationsResourceTableNewRow(driver, 0, true, "038-0226", "Smart Room", "9001", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver, 1, true, "038-0221", "Smart Room", "9001", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver, 2, true, "042-0205E", "Smart Room", "9001", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver, 3, true, "038-0219", "Smart Room", "9001", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver, 4, true, "038-0202", "Smart Room", "9001", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver, 5, true, "038-0225", "Smart Room", "9001", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver, 6, true, "038-0227", "Smart Room", "9001", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver, 7, true, "038-0220", "Smart Room", "9001", null);
		
		// Test saving
		driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
		driver.findElement(By.xpath("//td[@class='menuTitleField']//nobr[text()='Save']")).click();
		Thread.sleep(500);
		driver.switchTo().alert().accept();
		Thread.sleep(500);

	}
}

