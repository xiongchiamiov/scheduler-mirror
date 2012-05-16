package scheduler.view.web.shared;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import scheduler.view.web.shared.Selenium.WebUtility;
import scheduler.view.web.shared.Selenium.WebUtility.PopupWaiter;

public class PHYSAcceptanceTest extends DefaultSelTestCase {
	private WebDriver driver;
	private StringBuffer verificationErrors = new StringBuffer();
	private static String protoURL;
	
	public void setUp(WebDriver drv) throws java.io.IOException {
		Properties properties = new Properties();
		properties.load(this.getClass().getResourceAsStream("selenium.properties"));
		this.protoURL = properties.getProperty("domain") + "/PHYSx";
		
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
			
			WebUtility.mouseDownAndUpAt(driver, By.xpath("//div[@eventproxy='s_deleteBtn']"), 5, 5);
			
			Thread.sleep(5000);
			
			assert(driver.findElements(By.xpath("//div[@class='gridBody']//td[contains(@class, 'homeDocumentLink')]")).size() == existingDocumentsNames.size() - 1);
		}
	}
	
	private void createDocumentFromHomeTabAndSwitchToItsWindow(final String documentName) throws InterruptedException {
		WebUtility.mouseDownAndUpAt(driver, By.xpath("//div[@eventproxy='s_createBtn']"), 5, 5);

		WebUtility.waitForElementPresent(driver, By.id("s_createBox"));
		
		driver.findElement(By.id("s_createBox")).clear();
		driver.findElement(By.id("s_createBox")).sendKeys(documentName);

		PopupWaiter popupWaiter = new WebUtility.PopupWaiter(driver);
		
		driver.findElement(By.id("s_createNamedDocBtn")).click();
		
		String newWindowHandle = popupWaiter.waitForPopup();
		
		driver.switchTo().window(newWindowHandle);
	}
	
	private void login(final String username) throws InterruptedException {
		driver.findElement(By.id("s_unameBox")).clear();
		driver.findElement(By.id("s_unameBox")).sendKeys(username);
		driver.findElement(By.id("s_loginBtn")).click();
		WebUtility.waitForElementPresent(driver, By.xpath("//div[@eventproxy='s_createBtn']"));
		Thread.sleep(2000); // To wait for it to retrieve documents
	}
	
	public void testAcceptanceForPHYS() throws InterruptedException {
		login("cpfeffer");
		
		final String documentName = "PHYS Acceptance Test Document";
		deleteDocumentFromHomeTab(documentName);
		createDocumentFromHomeTabAndSwitchToItsWindow(documentName);

		// By default we're looking at the courses view, so start filling out courses
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,  0, true, "PHYS", "104", "Introductory Physics",                          "1", "4", "4", "",             "3", "30", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,  2, true, "PHYS", "107", "Introduction to Meteorology",                   "1", "4", "4", "MTuThF",       "3", "30", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,  3, true, "PHYS", "111", "Contemporary Physics for Nonscientists",        "1", "4", "3", "MWThF",        "3", "30", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,  4, true, "PHYS", "121", "College Physics 1",                             "6", "4", "4", "MWF",          "3", "30", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,  5, true, "PHYS", "121", "College Physics 1",                            "10", "1", "1", "T",            "3", "30", "LAB", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,  6, true, "PHYS", "122", "College Physics 2",                             "5", "4", "4", "TuTh",         "3", "30", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,  7, true, "PHYS", "122", "College Physics 2",                            "10", "1", "1", "TuTh",         "3", "30", "LAB", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,  8, true, "PHYS", "123", "College Physics 3",                             "2", "4", "4", "MWF",          "3", "30", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,  9, true, "PHYS", "123", "College Physics 3",                             "4", "1", "1", "M",            "3", "30", "LAB", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 10, true, "PHYS", "131", "General Physics 1",                             "5", "4", "4", "MWF",          "3", "30", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 11, true, "PHYS", "131", "General Physics 1",                             "9", "1", "1", "Th,M,F",       "3", "30", "LAB", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 12, true, "PHYS", "132", "General Physics 2",                            "10", "4", "4", "MWF",          "3", "30", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 13, true, "PHYS", "132", "General Physics 2",                            "19", "1", "1", "W",            "3", "30", "LAB", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 14, true, "PHYS", "133", "General Physics 3",                             "7", "4", "4", "MWF,TuTh",     "3", "30", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 15, true, "PHYS", "133", "General Physics 3",                            "14", "1", "1", "T,W,F",        "3", "30", "LAB", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 16, true, "PHYS", "141", "General Physics IA",                           "17", "4", "4", "MTuWTh",       "3", "30", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 17, true, "PHYS", "200", "Special Problems for Undergraduates",           "8", "0", "0", "W",            "3", "30", "IND", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 18, true, "PHYS", "201", "Learning Center Tutor",                         "1", "0", "0", "",             "3", "30", "IND", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 19, true, "PHYS", "206", "Instrumentation in Experimental Physics",       "1", "4", "4", "MWF",          "3", "30", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 20, true, "PHYS", "211", "Modern Physics 1",                              "2", "4", "4", "MTuWF,MTuWTh", "3", "30", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 21, true, "PHYS", "256", "Electrical Measurements Laboratory",            "5", "1", "1", "T",            "3", "30", "LAB", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 22, true, "PHYS", "301", "Thermal Physics1",                              "1", "4", "4", "MWThF",        "3", "30", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 23, true, "PHYS", "317", "Special Theory of Relativity",                  "1", "4", "4", "MWF",          "3", "30", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 24, true, "PHYS", "323", "Optics",                                        "1", "4", "4", "MWF",          "3", "30", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 25, true, "PHYS", "323", "Optics",                                        "4", "1", "1", "Tu,Th",        "3", "30", "LAB", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 26, true, "PHYS", "341", "Quantum Physics Laboratory 2",                  "5", "1", "1", "TuTh",         "3", "30", "LAB", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 27, true, "PHYS", "400", "Special Problems for Advanced Undergraduates", "10", "0", "0", "",             "3", "30", "IND", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 28, true, "PHYS", "409", "Electromagnetic Fields and Waves 2",            "1", "4", "4", "MWF",          "3", "30", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 29, true, "PHYS", "461", "Senior Project 1",                              "5", "0", "0", "",             "3", "30", "IND", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 30, true, "PHYS", "462", "Senior Project 2",                              "5", "0", "0", "",             "3", "30", "IND", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 31, true, "PHYS", "463", "Senior Project - Laboratory Research 1",        "1", "1", "1", "S",            "3", "30", "LAB", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 32, true, "PHYS", "464", "Senior Project - Laboratory Research 2",        "1", "1", "1", "S",            "3", "30", "LAB", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 33, true, "PHYS", "485", "Cooperative Education Experience",              "1", "0", "0", "",             "3", "30", "IND", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 34, true, "PHYS", "495", "Cooperative Education Experience",              "1", "0", "0", "",             "3", "30", "IND", "", null); 

		// Test saving
		driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
		driver.findElement(By.xpath("//td[@class='menuTitleField']//nobr[text()='Save']")).click();
		Thread.sleep(500);
		driver.switchTo().alert().accept();
		Thread.sleep(500);

		// Click on the instructors tab
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Instructors']")).click();

		// Start filling out instructors
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Pfeffer", "Carsten", "cpfeffer", "20");
		
		// Test saving
		driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
		driver.findElement(By.xpath("//td[@class='menuTitleField']//nobr[text()='Save']")).click();
		Thread.sleep(500);
		driver.switchTo().alert().accept();
		Thread.sleep(500);

		// Click on the locations tab
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Locations']")).click();

		// Start filling out locations
		WebUtility.enterIntoLocationsResourceTableNewRow(driver, 0, true, "14-255", "", "9001", null);
		
		// Test saving
		driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
		driver.findElement(By.xpath("//td[@class='menuTitleField']//nobr[text()='Save']")).click();
		Thread.sleep(500);
		driver.switchTo().alert().accept();
		Thread.sleep(500);

	}
}
