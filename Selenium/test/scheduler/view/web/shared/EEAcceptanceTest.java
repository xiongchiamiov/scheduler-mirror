package scheduler.view.web.shared;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import scheduler.view.web.shared.Selenium.WebUtility;

public abstract class EEAcceptanceTest extends DefaultSelTestCase {	
	private WebDriver driver;
	private StringBuffer verificationErrors = new StringBuffer();
	private static String protoURL;// = "http://localhost:8080/EE";
	
	public void setUp(WebDriver drv) throws java.io.IOException{
		Properties properties = new Properties();
		properties.load(this.getClass().getResourceAsStream("selenium.properties"));
		this.protoURL = properties.getProperty("domain") + "/EEx";
		
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
			if (existingDocumentsNames.get(i).getText().trim().equalsIgnoreCase(documentName))
				existingDocumentIndex = i;
		}
		
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

		driver.findElement(By.id("s_createNamedDocBtn")).click();
	}
	
	private void login(final String username) throws InterruptedException {
		driver.findElement(By.id("s_unameBox")).clear();
		driver.findElement(By.id("s_unameBox")).sendKeys("Mofo");
		driver.findElement(By.id("s_loginBtn")).click();
		WebUtility.waitForElementPresent(driver, By.xpath("//div[@eventproxy='s_createBtn']"));
		Thread.sleep(2000); // To wait for it to retrieve documents
	}
	
	public void testAcceptanceForEE() throws InterruptedException {
		login("pooper");
		
		final String documentName = "EE Acceptance Test Document";
		deleteDocumentFromHomeTab(documentName);
		createDocumentFromHomeTabAndSwitchToItsWindow(documentName);

		// By default we're looking at the courses view, so start filling out courses
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 0, true, "EE", "112", "Electric Circuit Analysis I", "5", "4", "2", "MW", "2", "48", "LEC", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 1, true, "EE", "200", "Special Problems", "1", "3", "3", null, null, "10", "IND", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 2, true, "EE", "200", "Special Problems", "1", "3", "3", null, null, "10", "IND", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 3, true, "EE", "201", "Electric Circuit Theory", "2", "4", "3", "MWF", "3", "230", "LEC", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 4, true, "EE", "211", "Electric Circuit Analysis II", "1", "4", "3", "MWF", "3", "36", "LEC", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 5, true, "EE", "212", "Electric Circuit Analysis III", "2", "4", "3", "MWF", "3", "44", "LEC", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 6, true, "EE", "228", "Continuous-Time Signals and Systems", "3", "4", "4", "TuTh", "4", "40", "LEC", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 7, true, "EE", "241", "Electric Circuit Analysis Lab I", "2", "1", "1", "T", "3", "24", "LAB", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 8, true, "EE", "242", "Electric Circuit Analysis Lab II", "4", "1", "1", "Tu,W,Th", "3", "22", "LAB", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 9, true, "EE", "251", "Electric Circuits Lab", "6", "1", "1", "Tu,Th", "3", "24", "LAB", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 10, true, "EE", "255", "Energy Conversion Electromagnetics", "4", "4", "3", "MWF", "3", "40", "LEC", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 11, true, "EE", "295", "Energy Conversion Electromagnetics Lab", "8", "1", "1", "M,Tu,W,Th,F", "3", "18", "LAB", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 12, true, "EE", "302", "Classical Control Systems", "1", "4", "3", "MWF", "3", "36", "LEC", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 13, true, "EE", "307", "Digital Electronics and Integrated Circuits", "1", "4", "3", "MWF", "3", "60", "LEC", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 14, true, "EE", "308", "Analog Electronics and Integrated Circuits", "3", "4", "3", "MWF", "3", "36", "LEC", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 15, true, "EE", "314", "Introduction to Communication Systems", "1", "4", "3", "MWF", "3", "35", "LEC", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 16, true, "EE", "321", "Electronics", "3", "4", "3", "TuTh,MWF", "3", "55", "LEC", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 17, true, "EE", "335", "Electromagnetic Fields and Transmission", "3", "4", "4", "TuTh, MTuWTh", "4", "36", "LEC", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 18, true, "EE", "342", "Classical Control Systems Lab", "2", "1", "1", "T", "3", "18", "LAB", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 19, true, "EE", "347", "Digital Electronics and Integrated Circuits Lab", "4", "1", "1", "Tu,Th", "3", "24", "LAB", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 20, true, "EE", "348", "Analog Electronics and Integrated Circuits Lab", "6", "1", "1", "Tu,Th", "3", "24", "LAB", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 21, true, "EE", "361", "Electronics Lab", "4", "1", "1", "Tu,Th", "3", "24", "LAB", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 22, true, "EE", "375", "Electromagnetic Fields and Transmission Lab", "8", "1", "1", "Tu,W,Th", "3", "18", "LAB", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 23, true, "EE", "400", "Special Problems", "10", "3", "3", null, null, "10", "IND", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 24, true, "EE", "418", "Photonic Engineering", "1", "4", "3", "MWF", "3", "18", "LEC", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 25, true, "EE", "422", "Polymer Electronics Lab", "1", "1", "1", "R", "3", "5", "LAB", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 26, true, "EE", "424", "Introduction to Remote Sensing", "1", "4", "3", "MW", "3", "40", "LEC", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 27, true, "EE", "424", "Introduction to Remote Sensing Lab", "2", "1", "1", "Th,F", "3", "32", "LAB", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 28, true, "EE", "425", "Analog Filter Design", "1", "4", "3", "MWF", "3", "42", "LEC", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 29, true, "EE", "444", "Power Systems Lab", "2", "1", "1", "Tu,F", "3", "18", "LAB", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 30, true, "EE", "455", "Analog Filter Design Lab", "1", "1", "1", "M", "3", "42", "LAB", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 31, true, "EE", "458", "Photonic Engineering Lab", "2", "1", "1", "T", "3", "12", "LAB", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 32, false, "EE", "463", "Senior Project Design Lab", "3", "3", "3", null, "3", "18", "LAB", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 33, false, "EE", "464", "Senior Project Design Lab II", "10", "2", "2", null, "3", "12", "LAB", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 34, true, "EE", "514", "Advanced Topics in Automatic Control", "1", "4", "4", "MF", "4", "36", "SEM", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 35, true, "EE", "518", "Power System Protection", "1", "4", "4", "MW", "4", "36", "SEM", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 36, true, "EE", "520", "Solar-Photovoltaic Systems Design", "1", "4", "4", "MW", "4", "36", "SEM", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 37, true, "EE", "521", "Computer Systems", "1", "3", "3", "MWF", "3", "32", "SEM", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 38, true, "EE", "521", "Computer Systems Lab", "1", "1", "1", "R", "3", "32", "LAB", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 39, true, "EE", "527", "Advanced Topics in Power Electronics", "1", "4", "4", "MW", "4", "36", "SEM", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 40, true, "EE", "528", "Digital Image Processing", "1", "4", "4", "MTuWTh", "4", "36", "SEM", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 41, true, "EE", "533", "Antennas", "1", "4", "4", "TuTh", "4", "36", "SEM", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 42, true, "EE", "563", "Graduate Seminar", "1", "1", "1", "F", "1", "48", "SEM", null, null);

		// Test saving
		driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
		driver.findElement(By.xpath("//td[@class='menuTitleField']//nobr[text()='Save']")).click();
		Thread.sleep(500);
		driver.switchTo().alert().accept();
		Thread.sleep(500);

		// Click on the instructors tab
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Instructors']")).click();

		// Start filling out instructors
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Juszak", "Jake", "jjuszak", "12");
		
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
