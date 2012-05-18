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

public abstract class BUSAcceptanceTest extends DefaultSelTestCase {	
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
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 0, true, "BUS", "207", "", "4", "0", "0", "MW,TuTh", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 1, true, "BUS", "214", "", "6", "0", "0", "MW,TuTh", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 2, true, "BUS", "215", "", "5", "0", "0", "MW,TuTh,Th", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 3, true, "BUS", "270", "", "1", "0", "0", "Th", "2", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 4, true, "BUS", "302", "", "2", "0", "0", "MW", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 5, true, "BUS", "303", "", "1", "0", "0", "MW,TuTh", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 6, true, "BUS", "310", "", "1", "0", "0", "MW", "2", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 7, true, "BUS", "319", "", "1", "0", "0", "TuTh", "3", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 8, true, "BUS", "319", "", "1", "0", "0", "TuTh", "1", "0", "ACT", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 9, true, "BUS", "320", "", "1", "0", "0", "TuTh", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 11, true, "BUS", "321", "", "2", "0", "0", "MW", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 12, true, "BUS", "342", "", "5", "0", "0", "MW,TuTh,WF", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 13, true, "BUS", "384", "", "2", "0", "0", "MW", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 14, true, "BUS", "387", "", "4", "0", "0", "MW,TuTh", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 15, true, "BUS", "391", "", "4", "0", "0", "MW,TuTh", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 16, true, "BUS", "394", "", "2", "0", "0", "MW", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 17, true, "BUS", "400", "", "11", "0", "0", "", "0", "0", "IND", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 18, true, "BUS", "401", "", "5", "0", "0", "MW,TuTh", "4", "0", "SEM", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 19, true, "BUS", "402", "", "1", "0", "0", "MW", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 20, true, "BUS", "404", "", "3", "0", "0", "MW", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 21, true, "BUS", "407", "", "2", "0", "0", "TuTh", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 22, true, "BUS", "416", "", "1", "0", "0", "TuTh", "2", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 23, true, "BUS", "416", "", "1", "0", "0", "TuTh", "2", "0", "ACT", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 24, true, "BUS", "417", "", "1", "0", "0", "TuTh", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 25, true, "BUS", "418", "", "2", "0", "0", "TuTh", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 26, true, "BUS", "419", "", "1", "0", "0", "TuTh", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 27, true, "BUS", "422", "", "2", "0", "0", "MW,TuTh", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 28, true, "BUS", "424", "", "1", "0", "0", "TuTh", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 29, true, "BUS", "430", "", "1", "0", "0", "", "0", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 30, true, "BUS", "431", "", "2", "0", "0", "MW", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 31, true, "BUS", "434", "", "1", "0", "0", "MW", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 32, true, "BUS", "438", "", "2", "0", "0", "TuTh", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 33, true, "BUS", "439", "", "1", "0", "0", "TuTh", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 34, true, "BUS", "442", "", "1", "0", "0", "MW", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 35, true, "BUS", "446", "", "1", "0", "0", "TuTh", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 36, true, "BUS", "451", "", "1", "0", "0", "MW", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 37, true, "BUS", "452", "", "1", "0", "0", "MW", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 38, true, "BUS", "454", "", "1", "0", "0", "MW", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 39, true, "BUS", "461", "", "13", "0", "0", "", "0", "0", "IND", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 40, true, "BUS", "462", "", "13", "0", "0", "", "0", "0", "IND", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 41, true, "BUS", "464", "", "7", "0", "0", "MW,TuTh,WF,F", "4", "0", "SEM", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 42, true, "BUS", "488", "", "1", "0", "0", "TuTh", "4", "0", "SEM", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 43, true, "BUS", "489", "", "1", "0", "0", "TuTh", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 44, true, "BUS", "498", "", "1", "0", "0", "MW", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 45, true, "BUS", "499", "", "1", "0", "0", "TuTh", "4", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 46, true, "BUS", "P416", "", "1", "0", "0", "TuTh", "2", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 47, true, "BUS", "P416", "", "1", "0", "0", "TuTh", "2", "0", "ACT", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 48, true, "BUS", "P462", "", "1", "0", "0", "Th", "2", "0", "IND", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 49, true, "BUS", "P464", "", "1", "0", "0", "WF", "4", "0", "SEM", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 50, true, "BUS", "S304", "", "1", "0", "0", "F", "3", "0", "LEC", "", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 51, true, "BUS", "S304", "", "1", "0", "0", "", "0", "0", "ACT", "", null);
	}

	private void enterAllInstructors() throws InterruptedException {
		// Click on the locations tab
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Instructors']")).click();
		
		//WebUtility.enterIntoInstructorsResourceTableNewRow(, row, schedulable, lastName, firstName, username, maxWTU)
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Bing", "Anderson", "bianders", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Richard A.", "Asplund", "rasplund", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Ronda A.", "Beaman", "rbeaman", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Roger H.", "Bishop", "rbishop", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Michelle G.", "Bissonnette", "mbissonn", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Norm A.", "Borin", "nborin", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Lee B.", "Burgunder", "lburgund", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Chris A.", "Carr", "ccarr", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Janice L.", "Carr", "jcarr", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Doug C.", "Cerf", "dcerf", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Dawn Elizabeth", "Chanland", "dachandl", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Jean-Francois Axel Hugues", "Coget", "jcoget", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Jeffrey E.", "Danes", "jdanes", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Li", "Dang", "ldang", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "John", "Dobson", "jdobson", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Sharon L.", "Dobson", "sdobson", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Samir K.", "Dutt", "sdutt", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Barry D.", "Floyd", "bfloyd", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Colette A.", "Frayne", "cfrayne", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "John Michael", "Geringer", "mgeringe", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Larry R.", "Gorman", "lgorman", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Kenneth A.", "Griggs", "kgriggs", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Jeffrey S.", "Hess", "jhess", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Sanjiv", "Jaggia", "sjaggia", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Earl C.", "Keller", "eckeller", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Kathryn", "Lancaster", "klancast", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Kaveepan", "Lertwachara", "klertwac", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Joan", "Lindsey-Mullikin", "jlindsey", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Alison", "Mackey", "mackey", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Tyson B.", "Mackey", "tbmackey", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Kristina I.", "McKinlay", "kmckinla", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Patricia A.", "McQuaid", "pmcquaid", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Lynn E.", "Metcalf", "lmetcalf", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Charles R.", "Miller II", "cmiller", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Steven M.", "Mintz", "smintz", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Stern P.", "Neill", "sneill", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "William R.", "Pendergast", "wpenderg", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Cyrus A.", "Ramezani", "cramezan", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Herve J.", "Roche", "hroche", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "James Russell", "Roy", "rroy", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Arline A.", "Savage", "savage", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "James A.", "Sena", "jsena", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Abraham B.", "Shani", "ashani", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Andreas", "Simon", "ansimon", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Lisa R.", "Simon", "lsimon", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Elisabeth", "Sperow", "esperow", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Alan M.", "Weatherford", "aweather", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Rosemary H.", "Wild", "rwild", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Mitchell J.", "Wolf", "mjwolf", "0");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Jonathan Lind", "York", "jlyork", "0");
	}

	private void enterAllLocations() throws InterruptedException {
		// Click on the locations tab
	   driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Locations']")).click();

	   //WebUtility.enterIntoLocationsResourceTableNewRow(, row, schedulable, room, type, maxOccupancy, equipment)
	   WebUtility.enterIntoLocationsResourceTableNewRow(driver, 0, false, "003-0101", "LEC", "35", null);
	   WebUtility.enterIntoLocationsResourceTableNewRow(driver, 0, false, "003-0102", "LEC", "35", null);
	   WebUtility.enterIntoLocationsResourceTableNewRow(driver, 0, false, "003-0103", "LEC", "35", null);
	   WebUtility.enterIntoLocationsResourceTableNewRow(driver, 0, false, "003-0104", "LEC", "35", null);
	   WebUtility.enterIntoLocationsResourceTableNewRow(driver, 0, false, "003-0105", "LEC", "35", null);
	   WebUtility.enterIntoLocationsResourceTableNewRow(driver, 0, false, "003-0201", "LEC", "35", null);
	   WebUtility.enterIntoLocationsResourceTableNewRow(driver, 0, false, "003-0202", "LEC", "35", null);
	   WebUtility.enterIntoLocationsResourceTableNewRow(driver, 0, false, "003-0203", "LEC", "35", null);
	   WebUtility.enterIntoLocationsResourceTableNewRow(driver, 0, false, "003-0204", "LEC", "35", null);
	   WebUtility.enterIntoLocationsResourceTableNewRow(driver, 0, false, "003-0205", "LEC", "35", null);
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
