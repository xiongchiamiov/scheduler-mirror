package scheduler.view.web.shared;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

import scheduler.view.web.shared.Selenium.WebUtility;

public abstract class IMEAcceptanceTest extends DefaultSelTestCase {	
	private WebDriver driver;
	private StringBuffer verificationErrors = new StringBuffer();
	//private static final String protoURL = "http://localhost:8080/ENGL";
	private static final String protoURL = "http://scheduler.csc.calpoly.edu/BUSx/";
	
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
		driver.findElement(By.id("s_unameBox")).sendKeys(username);
		driver.findElement(By.id("s_loginBtn")).click();
		WebUtility.waitForElementPresent(driver, By.xpath("//div[@eventproxy='s_createBtn']"));
		Thread.sleep(4000); // To wait for it to retrieve documents
	}
	
	public void testAcceptanceForBUS() throws InterruptedException {
		login("weener");
		
		final String documentName = "BUS Acceptance Test Document";
		deleteDocumentFromHomeTab(documentName);
		createDocumentFromHomeTabAndSwitchToItsWindow(documentName);

		// By default we're looking at the courses view, so start filling out courses
		enterAllCourses();
		performSave();
        
		enterAllInstructors();
        performSave();
		
        enterAllLocations();
		performSave();
		
		performListToCalendarDrag();		
		performSave();
	}

	private void performSave() {
		// Test saving
		driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
		driver.findElement(By.xpath("//td[@class='menuTitleField']/nobr[text()='Save']")).click();
		
		// Get a handle to the open alert, prompt or confirmation
		Alert alert = driver.switchTo().alert();
		// Get the text of the alert or prompt
		alert.getText();  
		// And acknowledge the alert (equivalent to clicking "OK")
		alert.accept();
	}

	private void enterAllCourses() throws InterruptedException {
		//WebUtility.enterIntoCoursesResourceTableNewRow(, row, schedulable, dept, catalogNum, courseName, sections, wtu, scu, dayCombos, hoursPerWeek, maxEnrollment, type, usedEquipment, association)
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 0,true,"IME","141","Manufacturing Processes: Net Shape","1","4","4","MW,TuTh","4","22","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,1,true,"IME","142","Manufacturing Processes: Materials Joining","1","4","4","MW,TuTh","4","115","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,2,true,"IME","143","Manufacturing Processes: Material Removal","1","4","4","MW,TuTh","4","96","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,3,true,"IME","144","Introduction to Design and Manufacturing","1","4","4","MW,TuTh","4","48","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,4,true,"IME","156","Basic Electronics Manufacturing","1","4","4","MW,TuTh","4","144","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,5,true,"IME","200","Special Problems for Undergraduates","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,6,true,"IME","223","Process Improvement Fundamentals","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,7,true,"IME","239","Industrial Costs and Controls","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,8,true,"IME","270","Selected Topics","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,9,true,"IME","301","Operations Research I","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,10,true,"IME","303","Project Organization and Management","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,11,true,"IME","312","Data Management and System Design","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,12,true,"IME","314","Engineering Economics","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,13,true,"IME","319","Human Factors Engineering","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,14,true,"IME","322","Leadership and Project Management","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,15,true,"IME","326","Engineering Test Design and Analysis","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,16,true,"IME","335","Computer-Aided Manufacturing","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,17,true,"IME","352","Manufacturing Process Design II","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,18,true,"IME","400","Special Problem for Advanced Undergraduates","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,19,true,"IME","401","Sales Egineering","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,20,true,"IME","405","Operations Research II","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,21,true,"IME","409","Economic Decision Systems","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,22,true,"IME","410","Production Planning and Control Systems","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,23,true,"IME","418","Product-Process Design","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,24,true,"IME","420","Simulation","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,25,true,"IME","429","Ergonomics Laboratory","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,26,true,"IME","430","Quality Engineering","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,27,true,"IME","441","Engineering Supervision I","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,28,true,"IME","442","Engineering Supervision II","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,29,true,"IME","443","Facilities Planning and Design","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,30,true,"IME","481","Senior Project Design Laboratory I","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,31,true,"IME","482","Senior Project Design Laboratory II","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,32,true,"IME","494","Cooperative Education Experience","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,33,true,"IME","495","Cooperative Education Experience 12","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,34,true,"IME","500","Individual Study","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,35,true,"IME","510","Systems Engineering I","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,36,true,"IME","577","Engineering Entrepreneurship","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,37,true,"IME","580","Manufacturing Systems","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,38,true,"IME","596","Team Project/Internship","1","4","4","MW,TuTh","4","24","LEC",null,null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,39,true,"IME","599","Design Project (Thesis)","1","4","4","MW,TuTh","4","24","LEC",null,null);
		

	}

	private void enterAllInstructors() throws InterruptedException {
		// Click on the locations tab
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Instructors']")).click();
		
		//WebUtility.enterIntoInstructorsResourceTableNewRow(, row, schedulable, lastName, firstName, username, maxWTU)
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,0,true,"Alptekin","Sema","salpteki","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,1,true,"Colvin","Kurt","kcolv","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,2,true,"Freed","Tali","tfreed","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,3,true,"Javadpour","Roya","rjava","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,4,true,"Macedo","Jose","jmaced","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,5,true,"Pan","Jianbao (John)","jpan","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,6,true,"Pouraghabagher","Reza","rpourag","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,7,true,"Schlemer","Lizabeth","lschlem","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,8,true,"Waldorf","Daniel","dwald","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,9,true,"Yang","Tao","ytao","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,10,true,"Bangs","Karen","kbangs","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,11,true,"Callow-Adams","Virginia","vcall","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,12,true,"Carter","Rob","rcarter","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,13,true,"Davis","Katherine","kdavis","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,14,true,"Gibbs","David","dgibb","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,15,true,"Hoadley","Rodney","rhoadl","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,16,true,"Koch","Martain","mkoch","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,17,true,"Larson","John","jlarson","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,18,true,"Malone Sr.","Daniel J.","dmalon","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,19,true,"McFarland","Marshall (Lee)","mmcfar","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,20,true,"Perks","Gary","gperks","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,21,true,"pulse","Eric","epulse","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,22,true,"Vigent","Danielle","dvigen","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,23,true,"Williams","Kevin","kwill","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,24,true,"Wilson","Michael","mwils","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,25,true,"Wolf","Mitch","mwolf","12");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,26,false,"Rainey","Paul","prainey","12");

	}

	private void enterAllLocations() throws InterruptedException {
		// Click on the locations tab
	   driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Locations']")).click();

	   //WebUtility.enterIntoLocationsResourceTableNewRow(, row, schedulable, room, type, maxOccupancy, equipment)
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,0,true,"041-0103","LEC","24",null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,0,true,"041-0104","LEC","24",null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,0,true,"041-0105","LEC","24",null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,0,true,"041-0106","LEC","24",null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,0,true,"041-0107","LEC","24",null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,0,true,"041-0108","LEC","24",null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,0,true,"041-0109","LEC","24",null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,0,true,"041-0110","LEC","24",null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,0,true,"041-0111","LEC","24",null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,0,true,"041-0112","LEC","24",null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,0,true,"041-0113","LEC","24",null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,0,true,"041-0114","LEC","24",null);
	}

	protected String performListToCalendarDrag() {
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Schedule']")).click();
		
		String errorMsg = null;
		
		try {
			WebElement startingSpot;
			WebElement endingSpot;
			
			startingSpot = driver.findElement(By.id("list0")); 
			endingSpot = driver.findElement(By.id("x0y0")); 
			
			System.out.println("Performing list to calendar drag and drop.");			
			Actions builder = new Actions(driver);   
			Action dragAndDrop = builder.clickAndHold(startingSpot).moveToElement(endingSpot).release(startingSpot).build();   
			
			dragAndDrop.perform();								
			
		} catch (org.openqa.selenium.NotFoundException ex){
			System.out.println("");
			errorMsg += "Selenium Page Elements [intial login] not located, check ID's";
		}		
		
		try {
			Alert popup = driver.switchTo().alert();
			errorMsg += popup.getText();
			popup.accept();		
			
		} catch (NoAlertPresentException ex) {
			System.out.println("Valid credentials");
			errorMsg += "success";
		}

		return errorMsg;
	}
	
	protected String TestGenerateButton() {
		String errorMsg = null;
		
		try {
			WebElement startingSpot;
			WebElement endingSpot;
			
			startingSpot = driver.findElement(By.id("generateButton")); 
			endingSpot = driver.findElement(By.id("generateButton")); 
			
			System.out.println("Performing list to calendar drag and drop.");			
			Actions builder = new Actions(driver);   
			Action dragAndDrop = builder.clickAndHold(startingSpot).moveToElement(endingSpot).release(startingSpot).build();   
			
			dragAndDrop.perform();								
			
		} catch (org.openqa.selenium.NotFoundException ex){
			System.out.println("");
			errorMsg += "Selenium Page Elements [intial login] not located, check ID's";
		}		
		
		try {
			Alert popup = driver.switchTo().alert();
			errorMsg += popup.getText();
			popup.accept();		
			
		} catch (NoAlertPresentException ex) {
			System.out.println("Valid credentials");
			errorMsg += "success";
		}

		return errorMsg;
	}
}

// TODO: see if documentName appears anywhere on screen?

// saving:
