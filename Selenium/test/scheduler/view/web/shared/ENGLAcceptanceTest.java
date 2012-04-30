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

import scheduler.view.web.shared.Selenium.SchedulerBot;
import scheduler.view.web.shared.Selenium.SchedulerBot.PopupWaiter;

public abstract class ENGLAcceptanceTest extends DefaultSelTestCase {	
	private WebDriver driver;
	private StringBuffer verificationErrors = new StringBuffer();
	//private static final String protoURL = "http://localhost:8080/ENGL";
	private static final String protoURL = "http://scheduler.csc.calpoly.edu/dev/";
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
		driver.findElement(By.id("s_unameBox")).sendKeys(username);
		driver.findElement(By.id("s_loginBtn")).click();
		bot.waitForElementPresent(By.xpath("//div[@eventproxy='s_createBtn']"));
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

	   bot.enterIntoLocationsResourceTableNewRow(0, false, "038-0219", "LEC", "35", null);
//	   bot.enterIntoLocationsResourceTableNewRow(1, false, "038-0219", "LEC", "35", null);
//	   bot.enterIntoLocationsResourceTableNewRow(2, false, "038-0219", "LEC", "35", null);
//	   bot.enterIntoLocationsResourceTableNewRow(3, false, "038-0219", "LEC", "35", null);
//	   bot.enterIntoLocationsResourceTableNewRow(4, false, "038-0219", "LEC", "35", null);
//	   bot.enterIntoLocationsResourceTableNewRow(5, false, "038-0219", "LEC", "35", null);
//	   bot.enterIntoLocationsResourceTableNewRow(6, false, "038-0219", "LEC", "35", null);
//	   bot.enterIntoLocationsResourceTableNewRow(7, false, "038-0219", "LEC", "35", null);	   
//	   bot.enterIntoLocationsResourceTableNewRow(8, false, "038-0219", "LEC", "35", null);
//	   bot.enterIntoLocationsResourceTableNewRow(9, false, "038-0219", "LEC", "35", null);
//	   bot.enterIntoLocationsResourceTableNewRow(10, false, "038-0219", "LEC", "35", null);	   
	}

	private void enterAllInstructors() throws InterruptedException {
		// Click on the locations tab
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Instructors']")).click();
		
		bot.enterIntoInstructorsResourceTableNewRow(0, true, "Ovadia", "Bob", "bovadia", "40");
//		bot.enterIntoInstructorsResourceTableNewRow(1, true, "Janke", "Dawn", "djanke", "40");
//		bot.enterIntoInstructorsResourceTableNewRow(2, true, "Wilhelm", "Deborah", "dwilhelm", "40");
//		bot.enterIntoInstructorsResourceTableNewRow(3, true, "Martin-Elston", "Erin", "ejmartin", "40");
//		bot.enterIntoInstructorsResourceTableNewRow(4, true, "Johann", "Sadie", "samart00", "40");
//		
//		bot.enterIntoInstructorsResourceTableNewRow(5, true, "Bartel", "Johnathan", "jbartel", "40");
//		bot.enterIntoInstructorsResourceTableNewRow(4, true, "Johann", "Sadie", "samart00", "40");
//		bot.enterIntoInstructorsResourceTableNewRow(5, true, "Maples", "Rebekah", "rmaples", "40");
//		bot.enterIntoInstructorsResourceTableNewRow(6, true, "Slocum", "Megan", "mslocum", "40");
//		bot.enterIntoInstructorsResourceTableNewRow(7, true, "Preston", "Alison", "apreston", "40");
//		
//		bot.enterIntoInstructorsResourceTableNewRow(8, true, "Woods", "Casey", "cwoods02", "40");
//		bot.enterIntoInstructorsResourceTableNewRow(9, true, "Sinclair", "Carli", "cmsincla", "40");
//		bot.enterIntoInstructorsResourceTableNewRow(10, true, "Ericson", "Leticia", "lericson", "40");
//		bot.enterIntoInstructorsResourceTableNewRow(11, true, "Brogno", "Courtney", "cbrogno", "40");
//		bot.enterIntoInstructorsResourceTableNewRow(12, true, "St. John", "Leslie", "lstjohn", "40");
//		
//		bot.enterIntoInstructorsResourceTableNewRow(13, true, "Wiens", "Jennie", "jpotteng", "40");
//		bot.enterIntoInstructorsResourceTableNewRow(14, true, "Belknap", "Jacquelyn", "jbelknap", "40");
//		bot.enterIntoInstructorsResourceTableNewRow(15, true, "Hendrix", "Ginger", "ghendrix", "40");
//		bot.enterIntoInstructorsResourceTableNewRow(16, true, "Senn", "Melanie", "msenn", "40");
//		bot.enterIntoInstructorsResourceTableNewRow(17, true, "Garner", "Annie", "agarner", "40");
//		bot.enterIntoInstructorsResourceTableNewRow(18, true, "Ashley", "Jennifer", "jashley", "40");
	}

	private void enterAllCourses() throws InterruptedException {

		bot.enterIntoCoursesResourceTableNewRow(0, true, "ENGL", "103", "Writing Labratory", "1", "3", "3", "MW,TuTh", "3", "50", "LAB", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(1, true, "ENGL", "115", "GWR Preparation", "1", "1", "1", "Tr", "1", "35", "LEC", null, null);
//		bot.enterIntoCoursesResourceTableNewRow(2, true, "ENGL", "134", "Writing and Rhetoric", "27", "3", "3", "MWF", "3", "30", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(3, true, "ENGL", "145", "Reasoning, Argumentation, and Writing", "39", "3", "3", "MW,TuTh", "3", "30", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(4, true, "ENGL", "148", "Reasoning, Arugmentation and Professional Writing", "15", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(5, true, "ENGL", "149", "Technical Writing for Engineers", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(6, true, "ENGL", "202", "Introduction to Literary Studies", "3", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(7, true, "ENGL", "204", "Core II: 1485-1660", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(8, true, "ENGL", "205", "Core III: 1660-1789", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(9, true, "ENGL", "230", "Masterworks of British Literature through the Eighteenth Century", "3", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(10, true, "ENGL", "231", "Masterworks of British Literature from the Late 18th Century to the Present", "2", "3", "3", "TWTrF", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(11, true, "ENGL", "240", "The American Tradition in Literature", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(12, true, "ENGL", "251", "Great Books I: Introduction to Classical Literature", "12", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(13, true, "ENGL", "253", "Great Books III: Romanticism to Modernism Literature", "4", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(14, true, "ENGL", "290", "Introduction to Linguistics", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		
//		bot.enterIntoCoursesResourceTableNewRow(15, true, "ENGL", "302", "Writing: Advanced Composition", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(16, true, "ENGL", "305", "Core VI: 1914 - Present", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(17, true, "ENGL", "310", "Corporate Communication", "5", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(18, true, "ENGL", "333", "British Literature in the Age of Romanticism", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(19, true, "ENGL", "339", "Introduction to Shakespeare", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(20, true, "ENGL", "342", "The Literary Sources of the American Character: 1914-1956", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(21, true, "ENGL", "347", "African American Literature", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(22, true, "ENGL", "350", "The Modern Novel", "3", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);		
//		bot.enterIntoCoursesResourceTableNewRow(23, true, "ENGL", "351", "Modern Poetry", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(24, true, "ENGL", "360", "Literature for Adolescents", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(25, true, "ENGL", "368", "Theory and Practice of Peer-to-peer Writing Instruction", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(26, true, "ENGL", "371", "Film Styles and Genres", "6", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(27, true, "ENGL", "372", "Film Directors", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(28, true, "ENGL", "380", "Literary Themes", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(29, true, "ENGL", "382", "LGBT Literature and Media", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(30, true, "ENGL", "387", "Fiction Writing", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(31, true, "ENGL", "388", "Poetry Writing", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(32, true, "ENGL", "390", "Linguistic Structr: Modern Engl", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(33, true, "ENGL", "391", "Topics in Applied Linguistics", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		
//		bot.enterIntoCoursesResourceTableNewRow(34, true, "ENGL", "400", "Special Problems for Advanced Undergraduates", "6", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(35, true, "ENGL", "408", "Internship", "1", "3", "3", "MW,TuTh", "3", "5", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(36, true, "ENGL", "430", "Chaucer", "1", "3", "3", "MW,TuTh", "3", "40", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(37, true, "ENGL", "439", "Significant British Writers", "1", "3", "3", "MW,TuTh", "3", "30", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(38, true, "ENGL", "449", "Significant American Writers", "1", "3", "3", "MW,TuTh", "3", "35", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(39, true, "ENGL", "459", "Significant World Writers", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(40, true, "ENGL", "461", "Senior Project", "8", "3", "3", "MW,TuTh", "3", "5", "LEC", null, null);
//		bot.enterIntoCoursesResourceTableNewRow(41, true, "ENGL", "487", "Advanced Creative Writing: Fiction", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(42, true, "ENGL", "488", "Advanced Creative Writing: Poetry", "1", "3", "3", "MW,TuTh", "3", "24", "LEC", null, null);
//		bot.enterIntoCoursesResourceTableNewRow(43, true, "ENGL", "499", "Practicum in Teaching English as a Second Language/Dialect", "2", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		
//		bot.enterIntoCoursesResourceTableNewRow(44, true, "ENGL", "505", "Writing Labratory", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(45, true, "ENGL", "511", "Writing Labratory", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(46, true, "ENGL", "512", "Writing Labratory", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(47, true, "ENGL", "513", "Writing Labratory", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);
//		bot.enterIntoCoursesResourceTableNewRow(48, true, "ENGL", "515", "Writing Labratory", "1", "3", "3", "MW,TuTh", "3", "50", "LEC", "Smart Room", null);					
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
