package scheduler.view.web.shared;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import scheduler.view.web.shared.Selenium.WebUtility;

public abstract class AEROAcceptanceTest extends DefaultSelTestCase {	
	private WebDriver driver;
	private StringBuffer verificationErrors = new StringBuffer();
	private static String protoURL;
	
	public void setUp(WebDriver drv) throws java.io.IOException {
		Properties properties = new Properties();
		properties.load(this.getClass().getResourceAsStream("selenium.properties"));
		this.protoURL = properties.getProperty("domain") + "/AERO";
		
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
		driver.findElement(By.id("s_unameBox")).sendKeys("admin"); //because admin is the new cool thing
		driver.findElement(By.id("s_loginBtn")).click();
		WebUtility.waitForElementPresent(driver, By.xpath("//div/table/tbody/tr/td[text()='Create New Document']"));
		Thread.sleep(2000); // To wait for it to retrieve documents
	}
	
	public void testAcceptanceForAERO() throws InterruptedException {
		login("admin");
		
		final String documentName = "AERO Acceptance Test Document Spring 2012";
		deleteDocumentFromHomeTab(documentName);
		createDocumentFromHomeTabAndSwitchToItsWindow(documentName);

		// By default we're looking at the courses view, so start filling out courses
		//driver, row, schedulable, dptmt, catnum, name, numsect, wtu, scu, daycombos, hpw, maxenrl, type, usedquip,istethered
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 0, true, "AERO", "215L", "Introduction to Aerospace Design", "2", "4", "4", "MW, TR", "6", "23", "LAB", null, null);
		//not sure on convention for lab/acts with same name type
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 1, true, "AERO", "301", "Aerothermodynamics", "2", "4", "4", "MTWR", "4", "50", "LEC", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 2, true, "AERO", "304", "Experimental Aerothermodynamics", "2", "4", "4", "MTWR", "4", "50", "LEC", null, null);
		//not sure on some o f these, should be 0 for enroll but dont remember if thats valid
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 3, true, "AERO", "200", "Independent Study", "14", "4", "4", "MW, TR", "4", "24", "LEC", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 4, true, "AERO", "304L", "Experimental Aerothermodynamics", "6", "1", "1", "M, T, W, R", "3", "20", "LAB", null, "AERO 304");
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 5, true, "AERO", "203", "Air and Space", "1", "2", "3", "TR", "1", "45", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 6, true, "AERO", "203", "Digital File Preparation and Workflow", "3", "3", "3", null, "3", "15", "LAB", null, "AERO 203 (tethered)");
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 7, true, "AERO", "211", "Substrates, Inks and Toners", "1", "3", "3", "TR", "3", "43", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 8, true, "AERO", "211", "Substrates, Inks and Toners", "3", "3", "3", null, "3", "14", "LAB", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 9, true, "AERO", "212", "Substrates, Inks and Toners: Reory", "1", "3", "3", "TR", "3", "6", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 10, true, "AERO", "320", "Managing Quality in Graphic Communication", "1", "3", "3", "TR", "3", "46", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 11, true, "AERO", "320", "Managing Quality in Graphic Communication", "3", "3", "3", null, "3", "16", "LAB", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 12, true, "AERO", "324", "Binding, Finishing and Distribution Process", "1", "3", "3", "WF", "3", "44", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 13, true, "AERO", "324", "Binding, Finishing and Distribution Process", "3", "3", "3", null, "3", "14", "LAB", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 14, true, "AERO", "325", "Binding, Finishing and Distribution Process: Theory", "1", "3", "3", "WF", "3", "66", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 15, true, "AERO", "328", "Sheetfed Printing Technology", "1", "3", "3", "TR", "3", "84", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 16, true, "AERO", "328", "Sheetfed Printing Technology", "4", "3", "3", null, "3", "12", "LAB", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 17, true, "AERO", "331", "Color Management and Quality Analysis", "1", "3", "3", "TR", "3", "36", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 18, true, "AERO", "331", "Color Management and Quality Analysis", "2", "3", "3", null, "2", "12", "ACT", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 19, false, "AERO", "337", "Consumer Packaging", "1", "3", "3", "MW", "2", "48", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 20, false, "AERO", "337", "Consumer Packaging", "2", "3", "3", null, "3", "20", "LAB", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 21, true, "AERO", "377", "Web and Print Publishing", "1", "3", "3", "MW", "3", "48", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 22, true, "AERO", "377", "Web and Print Publishing", "3", "3", "3", null, "3", "20", "LAB", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 23, true, "AERO", "400", "Special Problems", "8", "3", "3", null, null, "10", "IND", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 24, true, "AERO", "402", "Digital Printing and Emerging Technologies in Graphic Communication", "1", "3", "3", "MW", "2", "54", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 25, true, "AERO", "402", "Digital Printing and Emerging Technologies in Graphic Communication", "3", "3", "3", null, "2", "12", "ACT", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 26, true, "AERO", "403", "Estimating for Print and Digital Media", "1", "3", "3", "TR", "3", "56", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 27, true, "AERO", "403", "Estimating for Print and Digital Media", "3", "3", "3", null, "3", "20", "LAB", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 28, true, "AERO", "411", "Strategic Trends and Costing Issues in Print and Digital Media", "1", "3", "3", "TR", "3", "50", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 29, true, "AERO", "411", "Strategic Trends and Costing Issues in Print and Digital Media", "2", "3", "3", null, "2", "20", "ACT", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 30, true, "AERO", "421", "Production Management for Print and Digital Media", "1", "3", "3", "TR", "3", "56", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 31, true, "AERO", "421", "Production Management for Print and Digital Media", "2", "3", "3", null, "2", "20", "ACT", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 32, false, "AERO", "422", "Human Resource Management Issues for Print and Digital Media", "1", "3", "3", "TR", "3", "40", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 33, false, "AERO", "422", "Human Resource Management Issues for Print and Digital Media", "2", "3", "3", null, "3", "20", "LAB", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 34, true, "AERO", "429", "Digital Media", "1", "3", "3", "M", "2", "46", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 35, true, "AERO", "429", "Digital Media", "2", "3", "3", null, "3", "12", "LAB", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 36, true, "AERO", "440", "Magazine and Newspaper Design Technology", "1", "3", "3", "TR", "3", "48", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 37, true, "AERO", "440", "Magazine and Newspaper Design Technology", "2", "3", "3", null, "3", "12", "LAB", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 38, true, "AERO", "460", "Research Methods in Graphic Communication", "1", "3", "3", "M", "1", "24", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 39, true, "AERO", "460", "Research Methods in Graphic Communication", "1", "3", "3", null, "3", "20", "LAB", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 40, true, "AERO", "461", "Senior Project", "13", "3", "3", null, null, "10", "IND", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 41, true, "AERO", "461", "Senior Project", "1", "3", "3", null, null, "15", "IND", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 42, true, "AERO", "472", "Applied Graphic Communication Practices", "1", "3", "3", "W", "2", "75", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 43, false, "AERO", "473", "Applied Graphic Communication Practices", "1", "3", "3", "M", "2", "20", "LEC", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 44, true, "AERO", "485", "Cooperative Education Experience", "3", "3", "3", null, null, "10", "IND", null, null);
//		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 45, true, "AERO", "495", "Cooperative Education Experience", "2", "3", "3", null, null, "5", "IND", null, null);
		
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
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Jacobs", "J", "jjacobs", "20");
		
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

