package scheduler.view.web.shared;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import scheduler.view.web.shared.Selenium.SchedulerBot;
import scheduler.view.web.shared.Selenium.SchedulerBot.PopupWaiter;

public abstract class EEAcceptanceTest extends DefaultSelTestCase {	
	private WebDriver driver;
	private StringBuffer verificationErrors = new StringBuffer();
	private static final String protoURL = "http://localhost:8080/EE";
	private SchedulerBot bot;	
	
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
			bot.mouseDownAndUpAt(existingDocumentClickableCell, 5, 5);
			
			assert("true".equals(driver.findElement(rowXPath).getAttribute("aria-selected"))); // Sanity check, sometimes it was selecting the wrong one, given my xpath...
			
			bot.mouseDownAndUpAt(By.xpath("//div[@eventproxy='s_deleteBtn']"), 5, 5);
			
			Thread.sleep(5000);
			
			assert(driver.findElements(By.xpath("//div[@class='gridBody']//td[contains(@class, 'homeDocumentLink')]")).size() == existingDocumentsNames.size() - 1);
		}
	}
	
	private void createDocumentFromHomeTabAndSwitchToItsWindow(final String documentName) throws InterruptedException {
		bot.mouseDownAndUpAt(By.xpath("//div[@eventproxy='s_createBtn']"), 5, 5);

		bot.waitForElementPresent(By.id("s_createBox"));
		
		driver.findElement(By.id("s_createBox")).clear();
		driver.findElement(By.id("s_createBox")).sendKeys(documentName);

		PopupWaiter popupWaiter = bot.getPopupWaiter();
		
		driver.findElement(By.id("s_createNamedDocBtn")).click();
		
		String newWindowHandle = popupWaiter.waitForPopup();
		
		driver.switchTo().window(newWindowHandle);
	}
	
	private void login(final String username) throws InterruptedException {
		driver.findElement(By.id("s_unameBox")).clear();
		driver.findElement(By.id("s_unameBox")).sendKeys("eovadia");
		driver.findElement(By.id("s_loginBtn")).click();
		bot.waitForElementPresent(By.xpath("//div[@eventproxy='s_createBtn']"));
		Thread.sleep(2000); // To wait for it to retrieve documents
	}
	
	public void testAcceptanceForEE() throws InterruptedException {
		login("eovadia");
		
		final String documentName = "EE Acceptance Test Document";
		deleteDocumentFromHomeTab(documentName);
		createDocumentFromHomeTabAndSwitchToItsWindow(documentName);

		// By default we're looking at the courses view, so start filling out courses
		bot.enterIntoCoursesResourceTableNewRow(0, true, "EE", "112", "Electric Circuit Analysis I", "5", "4", "2", "MW", "2", "48", "LEC", "Smart Room", null);
		bot.enterIntoCoursesResourceTableNewRow(1, true, "EE", "200", "Special Problems", "1", "3", "3", null, null, "10", "IND", null, null);
		bot.enterIntoCoursesResourceTableNewRow(2, true, "EE", "200", "Special Problems", "1", "3", "3", null, null, "10", "IND", null, null);
		bot.enterIntoCoursesResourceTableNewRow(3, true, "EE", "201", "Electric Circuit Theory", "2", "4", "3", "MWF", "3", "230", "LEC", null, null);
		bot.enterIntoCoursesResourceTableNewRow(4, true, "EE", "211", "Electric Circuit Analysis II", "1", "4", "3", "MWF", "3", "36", "LEC", null, null);
		bot.enterIntoCoursesResourceTableNewRow(5, true, "EE", "212", "Electric Circuit Analysis III", "2", "4", "3", "MWF", "3", "44", "LEC", null, null);
		bot.enterIntoCoursesResourceTableNewRow(6, true, "EE", "228", "Continuous-Time Signals and Systems", "3", "4", "4", "TuTh", "4", "40", "LEC", null, null);
		bot.enterIntoCoursesResourceTableNewRow(7, true, "EE", "241", "Electric Circuit Analysis Lab I", "2", "1", "1", "Tu", "3", "24", "LAB", null, "EE 112");
		bot.enterIntoCoursesResourceTableNewRow(8, true, "EE", "242", "Electric Circuit Analysis Lab II", "4", "1", "1", "Tu,W,Th", "3", "22", "LAB", null, "EE 211");
		bot.enterIntoCoursesResourceTableNewRow(9, true, "EE", "251", "Electric Circuits Lab", "6", "1", "1", "Tu,Th", "3", "24", "LAB", null, null);
		bot.enterIntoCoursesResourceTableNewRow(10, true, "EE", "255", "Energy Conversion Electromagnetics", "4", "4", "3", "MWF", "3", "40", "LEC", null, null);
		bot.enterIntoCoursesResourceTableNewRow(11, true, "EE", "295", "Energy Conversion Electromagnetics Lab", "8", "1", "1", "M,Tu,W,Th,F", "3", "18", "LAB", null, null);
		bot.enterIntoCoursesResourceTableNewRow(12, true, "EE", "302", "Classical Control Systems", "1", "4", "3", "MWF", "3", "36", "LEC", null, null);
		bot.enterIntoCoursesResourceTableNewRow(13, true, "EE", "307", "Digital Electronics and Integrated Circuits", "1", "4", "3", "MWF", "3", "60", "LEC", null, null);
		bot.enterIntoCoursesResourceTableNewRow(14, true, "EE", "308", "Analog Electronics and Integrated Circuits", "3", "4", "3", "MWF", "3", "36", "LEC", null, null);
		bot.enterIntoCoursesResourceTableNewRow(15, true, "EE", "314", "Introduction to Communication Systems", "1", "4", "3", "MWF", "3", "35", "LEC", null, null);
		bot.enterIntoCoursesResourceTableNewRow(16, true, "EE", "321", "Electronics", "3", "4", "3", "TuTh,MWF", "3", "55", "LEC", null, null);
		bot.enterIntoCoursesResourceTableNewRow(17, true, "EE", "335", "Electromagnetic Fields and Transmission", "3", "4", "4", "TuTh, MTuWTh", "4", "36", "LEC", null, null);
		bot.enterIntoCoursesResourceTableNewRow(18, true, "EE", "342", "Classical Control Systems Lab", "2", "1", "1", "Tu", "3", "18", "LAB", null, null);
		bot.enterIntoCoursesResourceTableNewRow(19, true, "EE", "347", "Digital Electronics and Integrated Circuits Lab", "4", "1", "1", "Tu,Th", "3", "24", "LAB", null, null);
		bot.enterIntoCoursesResourceTableNewRow(20, true, "EE", "348", "Analog Electronics and Integrated Circuits Lab", "6", "1", "1", "Tu,Th", "3", "24", "LAB", null, null);
		bot.enterIntoCoursesResourceTableNewRow(21, true, "EE", "361", "Electronics Lab", "4", "1", "1", "Tu,Th", "3", "24", "LAB", null, null);
		bot.enterIntoCoursesResourceTableNewRow(22, true, "EE", "375", "Electromagnetic Fields and Transmission Lab", "8", "1", "1", "Tu,W,Th", "3", "18", "LAB", null, null);
		bot.enterIntoCoursesResourceTableNewRow(23, true, "EE", "400", "Special Problems", "10", "3", "3", null, null, "10", "IND", null, null);
		bot.enterIntoCoursesResourceTableNewRow(24, true, "EE", "418", "Photonic Engineering", "1", "4", "3", "MWF", "3", "18", "LEC", null, null);
		bot.enterIntoCoursesResourceTableNewRow(25, true, "EE", "422", "Polymer Electronics Lab", "1", "1", "1", "Th", "3", "5", "LAB", null, null);
		bot.enterIntoCoursesResourceTableNewRow(26, true, "EE", "424", "Introduction to Remote Sensing", "1", "4", "3", "MW", "3", "40", "LEC", null, null);
		bot.enterIntoCoursesResourceTableNewRow(27, true, "EE", "424", "Introduction to Remote Sensing Lab", "2", "1", "1", "Th,F", "3", "32", "LAB", null, null);
		bot.enterIntoCoursesResourceTableNewRow(28, true, "EE", "425", "Analog Filter Design", "1", "4", "3", "MWF", "3", "42", "LEC", null, null);
		bot.enterIntoCoursesResourceTableNewRow(29, true, "EE", "444", "Power Systems Lab", "2", "1", "1", "Tu,F", "3", "18", "LAB", null, null);
		bot.enterIntoCoursesResourceTableNewRow(30, true, "EE", "455", "Analog Filter Design Lab", "1", "1", "1", "M", "3", "42", "LAB", null, null);
		bot.enterIntoCoursesResourceTableNewRow(31, true, "EE", "458", "Photonic Engineering Lab", "2", "1", "1", "Tu", "3", "12", "LAB", null, null);
		bot.enterIntoCoursesResourceTableNewRow(32, false, "EE", "463", "Senior Project Design Lab", "3", "3", "3", null, "3", "18", "LAB", null, null);
		bot.enterIntoCoursesResourceTableNewRow(33, false, "EE", "464", "Senior Project Design Lab II", "10", "2", "2", null, "3", "12", "LAB", null, null);
		bot.enterIntoCoursesResourceTableNewRow(34, true, "EE", "514", "Advanced Topics in Automatic Control", "1", "4", "4", "MF", "4", "36", "SEM", null, null);
		bot.enterIntoCoursesResourceTableNewRow(35, true, "EE", "518", "Power System Protection", "1", "4", "4", "MW", "4", "36", "SEM", null, null);
		bot.enterIntoCoursesResourceTableNewRow(36, true, "EE", "520", "Solar-Photovoltaic Systems Design", "1", "4", "4", "MW", "4", "36", "SEM", null, null);
		bot.enterIntoCoursesResourceTableNewRow(37, true, "EE", "521", "Computer Systems", "1", "3", "3", "MWF", "3", "32", "SEM", null, null);
		bot.enterIntoCoursesResourceTableNewRow(38, true, "EE", "521", "Computer Systems Lab", "1", "1", "1", "Th", "3", "32", "LAB", null, null);
		bot.enterIntoCoursesResourceTableNewRow(39, true, "EE", "527", "Advanced Topics in Power Electronics", "1", "4", "4", "MW", "4", "36", "SEM", null, null);
		bot.enterIntoCoursesResourceTableNewRow(40, true, "EE", "528", "Digital Image Processing", "1", "4", "4", "MTuWTh", "4", "36", "SEM", null, null);
		bot.enterIntoCoursesResourceTableNewRow(41, true, "EE", "533", "Antennas", "1", "4", "4", "TuTh", "4", "36", "SEM", null, null);
		bot.enterIntoCoursesResourceTableNewRow(42, true, "EE", "563", "Graduate Seminar", "1", "1", "1", "F", "1", "48", "SEM", null, null);

		// Test saving
		driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
		driver.findElement(By.xpath("//td[@class='menuTitleField']//nobr[text()='Save']")).click();
		Thread.sleep(500);
		driver.switchTo().alert().accept();
		Thread.sleep(500);

		// Click on the instructors tab
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Instructors']")).click();

		// Start filling out instructors
		bot.enterIntoInstructorsResourceTableNewRow(0, true, "Juszak", "Jake", "jjuszak", "12");
		
		// Test saving
		driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
		driver.findElement(By.xpath("//td[@class='menuTitleField']//nobr[text()='Save']")).click();
		Thread.sleep(500);
		driver.switchTo().alert().accept();
		Thread.sleep(500);

		// Click on the locations tab
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Locations']")).click();

		// Start filling out locations
		bot.enterIntoLocationsResourceTableNewRow(0, true, "14-255", "Smart Room", "9001", null);
		
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
