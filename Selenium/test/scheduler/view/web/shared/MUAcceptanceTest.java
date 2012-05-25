package scheduler.view.web.shared;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import scheduler.view.web.shared.Selenium.WebUtility;

public abstract class MUAcceptanceTest extends DefaultSelTestCase {	
	private WebDriver driver;
	private StringBuffer verificationErrors = new StringBuffer();
	private static String protoURL;
	
	public void setUp(WebDriver drv) throws java.io.IOException {
		Properties properties = new Properties();
		properties.load(this.getClass().getResourceAsStream("selenium.properties"));
		this.protoURL = properties.getProperty("domain") + "/MU";
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
			
			Thread.sleep(2000);
			
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
		Thread.sleep(2000); // To wait for it to retrieve documents
	}
	
	public void testAcceptanceForMU() throws InterruptedException {
		login("admin");
		
		final String documentName = "MU Test Document";
		deleteDocumentFromHomeTab(documentName);
		createDocumentFromHomeTabAndSwitchToItsWindow(documentName);

		// By default we're looking at the courses view, so start filling out courses
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 0, true, "MU", "101", "Introduction to Music Theory", "7", "4", "3", "MW,MWF,TuTh", "3", "27", "LEC", "Smart Room", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 1, true, "MU", "101", "Introduction to Music Theory Activity", "1", "4", "1", null, "0", "27", "ACT", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 2, true, "MU", "104", "Musicianship I", "1", "4", "2", "MWF", "3", "14", "ACT", "Laptop Connectivity, Overhead", null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 3, true, "MU", "105", "Music Theory II: Chromatic Materials", "1", "4", "4", "MWF", "4.5", "27", "LEC", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 4, true, "MU", "108", "Musicianship III", "1", "4", "2", "MWF", "3", "20", "ACT", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 5, true, "MU", "120", "Music Appreciation", "1", "4", "3", "TR", "3", "50", "LEC", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 6, true, "MU", "120", "Music Appreciation Activity", "1", "4", "1", null, "0", "50", "ACT", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 7, true, "MU", "121", "Introduction to Non-Western Musics", "1", "4", "3", "MW", "3", "27", "LEC", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 7, true, "MU", "121", "Introduction to Non-Western Musics Activity", "1", "4", "1", null, "0", "27", "ACT", null, null);
		WebUtility.enterIntoCoursesResourceTableNewRow(driver, 8, true, "MU", "150", "Applied Music", "26", "4", "1", null, "0", "27", "IND", null, null);
		
		// Click on the instructors tab
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Instructors']")).click();

		// Start filling out instructors
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 0, true, "Habib", "Kenneth", "khabib", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 1, true, "Barata", "Antonia", "abarata", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 2, true, "Arrivee", "David", "darrivee", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 3, true, "D'Avignon", "India", "idavigno", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 4, true, "McMahan", "Andrew", "amcmahan", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 5, true, "Brammeier", "Meredith", "mbrammei", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 6, true, "Woodruff", "Christopher", "cwoodruf", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 7, true, "Hustad", "Kenneth", "khustad", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 8, true, "Bachman", "James", "bachman", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 9, true, "Davies", "Thomas", "tdavies", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 10, true, "Galvan", "Santino", "segalvan", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 11, true, "Granger", "Shelly", "sgranger", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 12, true, "Spiller", "William", "wspiller", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 13, true, "Severtson", "Paul", "pseverts", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 14, true, "Waibel", "Keith", "kwaibel", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 15, true, "Astaire", "John", "jastaire", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 16, true, "Davies", "Susan", "sdavies", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 17, true, "Rinzler", "Paul", "prinzler", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 18, true, "McLamore", "Laura", "amclamor", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 19, true, "Kreitzer", "Jacalyn", "jkreitze", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 20, true, "Russell", "Craig", "crussell", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 21, true, "Castriotta", "Gabrielle", "gcastrio", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 22, true, "Albanese", "Brynn", "balbanes", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver, 23, true, "Galvan", "Jennifer", "jldodson", "20");

		// Click on the locations tab
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Locations']")).click();

		// Start filling out locations
		WebUtility.enterIntoLocationsResourceTableNewRow(driver, 0, true, "045-0126", "Smart Room", "200", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver, 1, true, "045-0130", "Smart Room", "200", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver, 2, true, "045-0101", "Lecture", "200", null);

	}
}

// TODO: see if documentName appears anywhere on screen?

// saving:
//driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
//driver.findElement(By.xpath("//td[@class='menuTitleField']/nobr[text()='Save']")).click();
