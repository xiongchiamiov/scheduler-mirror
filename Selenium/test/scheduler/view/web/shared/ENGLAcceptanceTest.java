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
import scheduler.view.web.shared.Selenium.WebUtility.PopupWaiter;

public abstract class ENGLAcceptanceTest extends DefaultSelTestCase {	
	private WebDriver driver;
	private StringBuffer verificationErrors = new StringBuffer();
	//private static final String protoURL = "http://localhost:8080/ENGL";
	private static final String protoURL = "http://scheduler.csc.calpoly.edu/ENGLx/";
	
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

		PopupWaiter popupWaiter = new WebUtility.PopupWaiter(driver);
		
		driver.findElement(By.id("s_createNamedDocBtn")).click();
		
		String newWindowHandle = popupWaiter.waitForPopup();
		
		driver.switchTo().window(newWindowHandle);
	}
	
	private void openDocumentFromHomeTab(String documentName) throws InterruptedException {
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
	
	private void login(final String username) throws InterruptedException {
		driver.findElement(By.id("s_unameBox")).clear();
		driver.findElement(By.id("s_unameBox")).sendKeys(username);
		driver.findElement(By.id("s_loginBtn")).click();
		WebUtility.waitForElementPresent(driver, By.xpath("//div[@eventproxy='s_createBtn']"));
		Thread.sleep(4000); // To wait for it to retrieve documents
	}
	
	public void testAcceptanceForENGL() throws InterruptedException {
		login("tyero");
		
		final String documentName = "ENGL Acceptance Test Document";
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
	
	private void enterAllLocations() throws InterruptedException {
		// Click on the locations tab
	   driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Locations']")).click();

	   WebUtility.enterIntoLocationsResourceTableNewRow(driver, 0, false, "038-0219", "LEC", "35", null);
	   WebUtility.enterIntoLocationsResourceTableNewRow(driver, 1, false, "038-0219", "LEC", "35", null);
	   WebUtility.enterIntoLocationsResourceTableNewRow(driver, 2, false, "038-0219", "LEC", "35", null);
	   WebUtility.enterIntoLocationsResourceTableNewRow(driver, 3, false, "038-0219", "LEC", "35", null);
	   WebUtility.enterIntoLocationsResourceTableNewRow(driver, 4, false, "038-0219", "LEC", "35", null);
	   WebUtility.enterIntoLocationsResourceTableNewRow(driver, 5, false, "038-0219", "LEC", "35", null);
	   WebUtility.enterIntoLocationsResourceTableNewRow(driver, 6, false, "038-0219", "LEC", "35", null);
	   WebUtility.enterIntoLocationsResourceTableNewRow(driver, 7, false, "038-0219", "LEC", "35", null);	   
	   WebUtility.enterIntoLocationsResourceTableNewRow(driver, 8, false, "038-0219", "LEC", "35", null);
	   WebUtility.enterIntoLocationsResourceTableNewRow(driver, 9, false, "038-0219", "LEC", "35", null);
	   WebUtility.enterIntoLocationsResourceTableNewRow(driver, 10, false, "038-0219", "LEC", "35", null);	   
	}

	private void enterAllInstructors() throws InterruptedException {
		// Click on the locations tab
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Instructors']")).click();
		
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Ovadia", "Bob", "bovadia", "40");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 1, true, "Janke", "Dawn", "djanke", "40");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 2, true, "Wilhelm", "Deborah", "dwilhelm", "40");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 3, true, "Martin-Elston", "Erin", "ejmartin", "40");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 4, true, "Johann", "Sadie", "samart00", "40");
		
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 5, true, "Bartel", "Johnathan", "jbartel", "40");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 4, true, "Johann", "Sadie", "samart00", "40");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 5, true, "Maples", "Rebekah", "rmaples", "40");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 6, true, "Slocum", "Megan", "mslocum", "40");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 7, true, "Preston", "Alison", "apreston", "40");
		
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 8, true, "Woods", "Casey", "cwoods02", "40");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 9, true, "Sinclair", "Carli", "cmsincla", "40");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 10, true, "Ericson", "Leticia", "lericson", "40");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 11, true, "Brogno", "Courtney", "cbrogno", "40");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 12, true, "St. John", "Leslie", "lstjohn", "40");
		
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 13, true, "Wiens", "Jennie", "jpotteng", "40");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 14, true, "Belknap", "Jacquelyn", "jbelknap", "40");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 15, true, "Hendrix", "Ginger", "ghendrix", "40");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 16, true, "Senn", "Melanie", "msenn", "40");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 17, true, "Garner", "Annie", "agarner", "40");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 18, true, "Ashley", "Jennifer", "jashley", "40");
	}

	private void enterAllCourses() throws InterruptedException {

		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 0, true, "ENGL", "103", "Writing Labratory", "1", "3", "3", "MW,TuTh", "3", "50", "LAB", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 1, true, "ENGL", "115", "GWR Preparation", "1", "1", "1", "Tr", "1", "35", "LEC", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 2, true, "ENGL", "134", "Writing and Rhetoric", "27", "3", "3", "MWF", "3", "30", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 3, true, "ENGL", "145", "Reasoning, Argumentation, and Writing", "39", "3", "3", "MW,TuTh", "3", "30", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 4, true, "ENGL", "148", "Reasoning, Arugmentation and Professional Writing", "15", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 5, true, "ENGL", "149", "Technical Writing for Engineers", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 6, true, "ENGL", "202", "Introduction to Literary Studies", "3", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 7, true, "ENGL", "204", "Core II: 1485-1660", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 8, true, "ENGL", "205", "Core III: 1660-1789", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 9, true, "ENGL", "230", "Masterworks of British Literature through the Eighteenth Century", "3", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 10, true, "ENGL", "231", "Masterworks of British Literature from the Late 18th Century to the Present", "2", "3", "3", "TWTrF", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 11, true, "ENGL", "240", "The American Tradition in Literature", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 12, true, "ENGL", "251", "Great Books I: Introduction to Classical Literature", "12", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 13, true, "ENGL", "253", "Great Books III: Romanticism to Modernism Literature", "4", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 14, true, "ENGL", "290", "Introduction to Linguistics", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 15, true, "ENGL", "302", "Writing: Advanced Composition", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 16, true, "ENGL", "305", "Core VI: 1914 - Present", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 17, true, "ENGL", "310", "Corporate Communicati	on", "5", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 18, true, "ENGL", "333", "British Literature in the Age of Romanticism", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 19, true, "ENGL", "339", "Introduction to Shakespeare", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 20, true, "ENGL", "342", "The Literary Sources of the American Character: 1914-1956", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 21, true, "ENGL", "347", "African American Literature", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 22, true, "ENGL", "350", "The Modern Novel", "3", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 23, true, "ENGL", "351", "Modern Poetry", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 24, true, "ENGL", "360", "Literature for Adolescents", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 25, true, "ENGL", "368", "Theory and Practice of Peer-to-peer Writing Instruction", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 26, true, "ENGL", "371", "Film Styles and Genres", "6", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 27, true, "ENGL", "372", "Film Directors", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 28, true, "ENGL", "380", "Literary Themes", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 29, true, "ENGL", "382", "LGBT Literature and Media", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 30, true, "ENGL", "387", "Fiction Writing", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 31, true, "ENGL", "388", "Poetry Writing", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 32, true, "ENGL", "390", "Linguistic Structr: Modern Engl", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 33, true, "ENGL", "391", "Topics in Applied Linguistics", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 34, true, "ENGL", "400", "Special Problems for Advanced Undergraduates", "6", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 35, true, "ENGL", "408", "Internship", "1", "3", "3", "MW,TuTh", "3", "5", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 36, true, "ENGL", "430", "Chaucer", "1", "3", "3", "MW,TuTh", "3", "40", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 37, true, "ENGL", "439", "Significant British Writers", "1", "3", "3", "MW,TuTh", "3", "30", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 38, true, "ENGL", "449", "Significant American Writers", "1", "3", "3", "MW,TuTh", "3", "35", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 39, true, "ENGL", "459", "Significant World Writers", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 40, true, "ENGL", "461", "Senior Project", "8", "3", "3", "MW,TuTh", "3", "5", "LEC", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 41, true, "ENGL", "487", "Advanced Creative Writing: Fiction", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 42, true, "ENGL", "488", "Advanced Creative Writing: Poetry", "1", "3", "3", "MW,TuTh", "3", "24", "LEC", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 43, true, "ENGL", "499", "Practicum in Teaching English as a Second Language/Dialect", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 44, true, "ENGL", "505", "Writing Labratory", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 45, true, "ENGL", "511", "Writing Labratory", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 46, true, "ENGL", "512", "Writing Labratory", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 47, true, "ENGL", "513", "Writing Labratory", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 48, true, "ENGL", "515", "Writing Labratory", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);					
	}
	
	protected String performListToCalendarDrag() throws InterruptedException {
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Schedule']")).click();
		
		String errorMsg = null;
		
		try {
			WebElement startingSpot;
			WebElement endingSpot;
			
			startingSpot = driver.findElement(By.id("list0")); 
			endingSpot = driver.findElement(By.id("x0y0")); 
			
			System.out.println("Performing list to calendar drag and drop.");			
			Actions builder = new Actions(driver);   
			Action dragAndDrop = builder.clickAndHold(startingSpot).moveToElement(endingSpot).release(endingSpot).build();   
			
			dragAndDrop.perform();		
			
			Thread.sleep(5000);
			
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
			System.out.println("Performed list to calendar drag and drop.");
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
