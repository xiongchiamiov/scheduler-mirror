package scheduler.view.web.shared;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import scheduler.view.web.shared.Selenium.SchedulerBot;
import scheduler.view.web.shared.Selenium.SchedulerBot.PopupWaiter;

public abstract class IMEAcceptanceTest extends DefaultSelTestCase {	
	private WebDriver driver;
	private StringBuffer verificationErrors = new StringBuffer();
	private static final String protoURL = "http://localhost:8080/IME";
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
		driver.findElement(By.id("s_unameBox")).sendKeys("krscanlo");
		driver.findElement(By.id("s_loginBtn")).click();
		bot.waitForElementPresent(By.xpath("//div[@eventproxy='s_createBtn']"));
		Thread.sleep(2000); // To wait for it to retrieve documents
	}
	
	public void testAcceptanceForIME() throws InterruptedException {
		login("krscanlo");
		
		final String documentName = "IME Acceptance Test Document";
		deleteDocumentFromHomeTab(documentName);
		createDocumentFromHomeTabAndSwitchToItsWindow(documentName);

		// By default we're looking at the courses view, so start filling out courses
//		bot.enterIntoCoursesResourceTableNewRow(0,TRUE,IME,"141","""Manufacturing Processes: Net Shape""","""1""","""4""","""4""","""MW,TuTh""","""4""","""22""","""LEC""",null,null);
//		bot.enterIntoCoursesResourceTableNewRow(1, true, "GRC", "200", "Special Problems", "1", "3", "3", null, null, "10", "IND", null, null);
//		bot.enterIntoCoursesResourceTableNewRow(2, true, "GRC", "200", "Special Problems", "1", "3", "3", null, null, "10", "IND", null, null);
//		bot.enterIntoCoursesResourceTableNewRow(3, true, "GRC", "202", "Digital Photography", "1", "3", "3", "MW", "2", "50", "LEC", null, null);
//		bot.enterIntoCoursesResourceTableNewRow(4, true, "GRC", "202", "Digital Photography", "3", "3", "3", null, "3", "20", "LAB", null, "GRC 202");
bot.enterIntoCoursesResourceTableNewRow(0,TRUE,IME,”141”,”Manufacturing Processes: Net Shape”,”1”,”4”,”4”,”MW,TuTh”,”4”,”22”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(1,TRUE,”IME”,”142”,”Manufacturing Processes: Materials Joining”,”1”,”4”,”4”,”MW,TuTh”,”4”,”115”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(2,TRUE,”IME”,”143”,”Manufacturing Processes: Material Removal”,”1”,”4”,”4”,”MW,TuTh”,”4”,”96”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(3,TRUE,”IME”,”144”,”Introduction to Design and Manufacturing”,”1”,”4”,”4”,”MW,TuTh”,”4”,”48”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(4,TRUE,”IME”,”156”,”Basic Electronics Manufacturing”,”1”,”4”,”4”,”MW,TuTh”,”4”,”144”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(5,TRUE,”IME”,”200”,”Special Problems for Undergraduates”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(6,TRUE,”IME”,”223”,”Process Improvement Fundamentals”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(7,TRUE,”IME”,”239”,”Industrial Costs and Controls”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(8,TRUE,”IME”,”270”,”Selected Topics”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(9,TRUE,”IME”,”301”,”Operations Research I”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(10,TRUE,”IME”,”303”,”Project Organization and Management",”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(11,TRUE,”IME”,”312”,”Data Management and System Design”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(12,TRUE,”IME”,”314”,”Engineering Economics”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(13,TRUE,”IME”,”319”,”Human Factors Engineering”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(14,TRUE,”IME”,”322”,”Leadership and Project Management”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(15,TRUE,”IME”,”326”,”Engineering Test Design and Analysis”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(16,TRUE,”IME”,”335”,”Computer-Aided Manufacturing”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(17,TRUE,”IME”,”352”,”Manufacturing Process Design II”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(18,TRUE,”IME”,”400”,”Special Problem for Advanced Undergraduates”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(19,TRUE,”IME”,”401”,”Sales Egineering”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(20,TRUE,”IME”,”405”,”Operations Research II”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(21,TRUE,”IME”,”409”,”Economic Decision Systems”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(22,TRUE,”IME”,”410”,”Production Planning and Control Systems”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(23,TRUE,”IME”,”418”,”Product-Process Design”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(24,TRUE,”IME”,”420”,”Simulation”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(25,TRUE,”IME”,”429”,”Ergonomics Laboratory”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(26,TRUE,”IME”,”430”,”Quality Engineering”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(27,TRUE,”IME”,”441”,”Engineering Supervision I”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(28,TRUE,”IME”,”442”,”Engineering Supervision II”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(29,TRUE,”IME”,”443”,”Facilities Planning and Design”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(30,TRUE,”IME”,”481”,”Senior Project Design Laboratory I”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(31,TRUE,”IME”,”482”,”Senior Project Design Laboratory II”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(32,TRUE,”IME”,”494”,”Cooperative Education Experience”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(33,TRUE,”IME”,”495”,”Cooperative Education Experience 12”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(34,TRUE,”IME”,”500”,”Individual Study”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(35,TRUE,”IME”,”510”,”Systems Engineering I”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(36,TRUE,”IME”,”577”,”Engineering Entrepreneurship”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(37,TRUE,”IME”,”580”,”Manufacturing Systems”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(38,TRUE,”IME”,”596”,”Team Project/Internship”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);bot.enterIntoCoursesResourceTableNewRow(39,TRUE,”IME”,”599”,”Design Project (Thesis)”,”1”,”4”,”4”,”MW,TuTh”,”4”,”24”,”LEC”,null,null);
		
		// Test saving
		driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
		driver.findElement(By.xpath("//td[@class='menuTitleField']//nobr[text()='Save']")).click();
		Thread.sleep(500);
		driver.switchTo().alert().accept();
		Thread.sleep(500);

		// Click on the instructors tab
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Instructors']")).click();

		// Start filling out instructors
		//bot.enterIntoInstructorsResourceTableNewRow(0, true, "Ovadia", "Evan", "eovadia", "20");
		bot.enterIntoInstructorsResourceTableNewRow(0,TRUE,”Alptekin”,”Sema”,”salpteki”,12);bot.enterIntoInstructorsResourceTableNewRow(1,TRUE,”Colvin”,”Kurt”,”kcolv”,12);bot.enterIntoInstructorsResourceTableNewRow(2,TRUE,”Freed”,”Tali”,”tfreed”,12);bot.enterIntoInstructorsResourceTableNewRow(3,TRUE,”Javadpour”,”Roya”,”rjava”,12);bot.enterIntoInstructorsResourceTableNewRow(4,TRUE,”Macedo”,”Jose”,”jmaced”,12);bot.enterIntoInstructorsResourceTableNewRow(5,TRUE,”Pan”,”Jianbao (John)”,”jpan”,12);bot.enterIntoInstructorsResourceTableNewRow(6,TRUE,”Pouraghabagher”,”Reza”,”rpourag”,12);bot.enterIntoInstructorsResourceTableNewRow(7,TRUE,”Schlemer”,”Lizabeth”,”lschlem”,12);bot.enterIntoInstructorsResourceTableNewRow(8,TRUE,”Waldorf”,”Daniel”,”dwald”,12);bot.enterIntoInstructorsResourceTableNewRow(9,TRUE,”Yang”,”Tao”,”ytao”,12);bot.enterIntoInstructorsResourceTableNewRow(10,TRUE,”Bangs”,”Karen”,”kbangs”,12);bot.enterIntoInstructorsResourceTableNewRow(11,TRUE,”Callow-Adams”,”Virginia”,”vcall”,12);bot.enterIntoInstructorsResourceTableNewRow(12,TRUE,”Carter”,”Rob”,”rcarter”,12);bot.enterIntoInstructorsResourceTableNewRow(13,TRUE,”Davis”,”Katherine”,”kdavis”,12);bot.enterIntoInstructorsResourceTableNewRow(14,TRUE,”Gibbs”,”David”,”dgibb”,12);bot.enterIntoInstructorsResourceTableNewRow(15,TRUE,”Hoadley”,”Rodney”,”rhoadl”,12);bot.enterIntoInstructorsResourceTableNewRow(16,TRUE,”Koch”,”Martain”,”mkoch”,12);bot.enterIntoInstructorsResourceTableNewRow(17,TRUE,”Larson”,”John”,”jlarson”,12);bot.enterIntoInstructorsResourceTableNewRow(18,TRUE,”Malone Sr.”,”Daniel J.”,”dmalon”,12);bot.enterIntoInstructorsResourceTableNewRow(19,TRUE,”McFarland”,”Marshall (Lee)”,”mmcfar”,12);bot.enterIntoInstructorsResourceTableNewRow(20,TRUE,”Perks”,”Gary”,”gperks”,12);bot.enterIntoInstructorsResourceTableNewRow(21,TRUE,”pulse”,”Eric”,”epulse”,12);bot.enterIntoInstructorsResourceTableNewRow(22,TRUE,”Vigent”,”Danielle”,”dvigen”,12);bot.enterIntoInstructorsResourceTableNewRow(23,TRUE,”Williams”,”Kevin”,”kwill”,12);bot.enterIntoInstructorsResourceTableNewRow(24,TRUE,”Wilson”,”Michael”,”mwils”,12);bot.enterIntoInstructorsResourceTableNewRow(25,TRUE,”Wolf”,”Mitch”,”mwolf”,12);bot.enterIntoInstructorsResourceTableNewRow(26,FALSE,”Rainey”,”Paul”,”prainey”,12);
		// Test saving
		driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
		driver.findElement(By.xpath("//td[@class='menuTitleField']//nobr[text()='Save']")).click();
		Thread.sleep(500);
		driver.switchTo().alert().accept();
		Thread.sleep(500);

		// Click on the locations tab
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Locations']")).click();

		// Start filling out locations
		//bot.enterIntoLocationsResourceTableNewRow(0, true, "14-255", "Smart Room", "9001", null);
bot.enterIntoLocationsResourceTableNewRow0,TRUE,”041-0103”,”LEC”,”24”,null);bot.enterIntoLocationsResourceTableNewRow1,TRUE,”041-0104”,”LEC”,”24”,null);bot.enterIntoLocationsResourceTableNewRow2,TRUE,”041-0105”,”LEC”,”24”,null);bot.enterIntoLocationsResourceTableNewRow3,TRUE,”041-0106”,”LEC”,”24”,null);bot.enterIntoLocationsResourceTableNewRow4,TRUE,”041-0107”,”LEC”,”24”,null);bot.enterIntoLocationsResourceTableNewRow4,TRUE,”041-0108”,”LEC”,”24”,null);bot.enterIntoLocationsResourceTableNewRow5,TRUE,”041-0109”,”LEC”,”24”,null);bot.enterIntoLocationsResourceTableNewRow6,TRUE,”041-0110”,”LEC”,”24”,null);bot.enterIntoLocationsResourceTableNewRow7,TRUE,”041-0111”,”LEC”,”24”,null);bot.enterIntoLocationsResourceTableNewRow8,TRUE,”041-0112”,”LEC”,”24”,null);bot.enterIntoLocationsResourceTableNewRow9,TRUE,”041-0113”,”LEC”,”24”,null);bot.enterIntoLocationsResourceTableNewRow10,TRUE,”041-0114”,”LEC”,”24”,null);
		
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
