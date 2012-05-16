package scheduler.view.web.shared;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import scheduler.view.web.shared.Selenium.WebUtility;


public class CHEMAcceptanceTest extends DefaultSelTestCase{
	private WebDriver driver;
	private StringBuffer verificationErrors = new StringBuffer();
	private static String protoURL;
	
	public void setUp(WebDriver drv) throws java.io.IOException {
		Properties properties = new Properties();
		properties.load(this.getClass().getResourceAsStream("selenium.properties"));
		this.protoURL = properties.getProperty("domain") + "/CHEM";
		
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
			WebUtility.mouseDownAndUpAt(driver,existingDocumentClickableCell, 5, 5);
			
			assert("true".equals(driver.findElement(rowXPath).getAttribute("aria-selected"))); // Sanity check, sometimes it was selecting the wrong one, given my xpath...
			
			WebUtility.mouseDownAndUpAt(driver,By.xpath("//div[@eventproxy='s_deleteBtn']"), 5, 5);
			
			Thread.sleep(5000);
			
			assert(driver.findElements(By.xpath("//div[@class='gridBody']//td[contains(@class, 'homeDocumentLink')]")).size() == existingDocumentsNames.size() - 1);
		}
	}
	
	private void createDocumentFromHomeTabAndSwitchToItsWindow(final String documentName) throws InterruptedException {
		WebUtility.mouseDownAndUpAt(driver, By.xpath("//div[@eventproxy='s_createBtn']"), 5, 5);

		WebUtility.waitForElementPresent(driver,By.id("s_createBox"));
	
		driver.findElement(By.id("s_createBox")).clear();
		driver.findElement(By.id("s_createBox")).sendKeys(documentName);
		
		driver.findElement(By.id("s_createNamedDocBtn")).click();

	}
	
	private void login(final String username) throws InterruptedException {
		driver.findElement(By.id("s_unameBox")).clear();
		driver.findElement(By.id("s_unameBox")).sendKeys("CHEM");
		driver.findElement(By.id("s_loginBtn")).click();
		WebUtility.waitForElementPresent(driver,By.xpath("//div[@eventproxy='s_createBtn']"));
		Thread.sleep(2000); // To wait for it to retrieve documents
	}
	
	public void testAcceptanceForCHEM() throws InterruptedException {
		login("chem");
		
		final String documentName = "CHEM Acceptance Test Document";
		deleteDocumentFromHomeTab(documentName);
		createDocumentFromHomeTabAndSwitchToItsWindow(documentName);

		// By default we're looking at the courses view, so start filling out courses
	//	bot.enterIntoCoursesResourceTableNewRow(row0Based, isSchedulable, department, catalogNum, courseName, 
			// numSections, wtu, scu, dayCombinations, hoursPerWeek, maxEnrollment, type, usedEquipment, association)

		//TODO fix WTU and SCU amounts
		//TODO fix Tu / Thurs vals
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,0, true, "CHEM", "101", "World Of Chemistry-Essentials", 
				"2", "5", "4", "MW,MWF", "3", "72", "LEC", "", null);
/*		WebUtility.enterIntoCoursesResourceTableNewRow(driver,1, true, "CHEM", "101Lab", "World Of Chemistry-Essentials", 
				"6", "3", "2", "W,Th,F", "3", "24", "LAB", "", "0" );
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,2, true, "CHEM", "111", "Survey Of Chemistry", 
				"1", "5", "4", "MW", "4", "102", "LEC", "", null );
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,3, true, "CHEM", "111L", "Survey Of Chemistry Lab", 
				"4", "3", "2", "M,Tu", "3", "24", "LAB", "", "2" );
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,4, true, "CHEM", "124", "Genl Chem : Engr Disciplines I", 
				"4", "5", "4", "F,TuTh,TuThF", "3", "64", "LEC", "", null );
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,5, true, "CHEM", "124L", "Genl Chem : Engr Disciplines I Lab", 
				"3", "3", "2", "TuTh", "3", "64", "LAB", "", "4" );
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,5, true, "CHEM", "125", "Genl Chem: Engr Disciplines II", 
				"3", "5", "4", "MWF", "3", "64", "LEC", "", null );
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,6, true, "CHEM", "125L", "Genl Chem: Engr Disciplines II Lab", 
				"3", "3", "2", "MW", "3", "64", "LAB", "", "5" );
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,7, true, "CHEM", "127", "General Chemistry I", 
				"1", "5", "4", "MWF", "3", "102", "LEC", "", null );
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,8, true, "CHEM", "127L", "General Chemistry I Lab", 
				"4", "3", "2", "M", "3", "24", "LAB", "", "7" );
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,9, true, "CHEM", "128", "General Chemistry II", 
				"2", "4", "4", "MWF", "3", "102", "LEC", "", null );
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,10, true, "CHEM", "128L", "General Chemistry II Lab", 
				"7", "3", "2", "MWF", "3", "24", "LAB", "", "9" );
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,11, true, "CHEM", "129", "General Chemistry III", 
				"6", "4", "3", "TuTh,MWF", "3", "102", "LEC", "", null );
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,12, true, "CHEM", "129L", "General Chemistry III Lab", 
				"21", "2", "1", "Tu,Th,F,W", "3", "24", "LAB", "", "11" );

		//TODO Double Check on 201
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,13, true, "CHEM", "305", "Physical Chemistry For Engineers", 
				"1", "4", "3", "MW", "4", "66", "LEC", "", null );
	
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,14, true, "CHEM", "312", "Survey Of Organic Chemistry", 
				"3", "4", "3", "MTWR,TuTh, MTuWF", "4", "56", "LEC", "", null );
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,15, true, "CHEM", "312L", "Survey Of Organic Chemistry Lab", 
				"9", "2", "1", "M,Tu,F,W,Th", "4", "18", "LAB", "", "14" );
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,16, true, "CHEM", "313", "Survey Of Biochem/biotechnlgy", 
				"1", "4", "3", "MTuWTh", "4", "72", "LEC", "", null );
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,17, true, "CHEM", "313L", "Survey Of Biochem/biotechnlgy Lab", 
				"4", "2", "1", "M,Tu", "3", "20", "LAB", "", "16" );
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,18, true, "CHEM", "317", "Organic Chemistry II", 
				"2", "4", "3", "MWF,TuTh", "3", "43", "LEC", "", null );
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,19, true, "CHEM", "317L", "Organic Chemistry II Lab", 
				"6", "2", "1", "MW,TuTh", "6", "24", "LAB", "", "18" );
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,20, true, "CHEM", "349", "Chemical & Biological Warfare", 
				"1", "4", "4", "TuTh", "2", "35", "LEC", "", "" );
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,21, true, "CHEM", "349S", "Chemical & Biological Warfare Seminar", 
				"1", "4", "4", "TuTh", "2", "35", "LEC", "", "" );
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,22, true, "CHEM", "351", "Physical Chemistry I", 
				"1", "4", "4", "TuTh", "3", "40", "LEC", "", "" );
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,23, true, "CHEM", "352", "Physical Chemistry II", 
				"1", "4", "4", "MWF", "3", "56", "LEC", "", "" );
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,24, true, "CHEM", "354", "Physical Chemistry Laboratory", 
				"2", "4", "4", "MW", "6", "24", "LAB", "", "" );
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,25, true, "CHEM", "371", "Biochemical Principles", 
				"1", "4", "3", "MTuWTh", "4", "66", "LEC", "", "" );
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,26, true, "CHEM", "371L", "Biochemical Principles Lab", 
				"4", "3", "2", "M,Tu", "3", "20", "LAB", "", "25" );
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,27, true, "CHEM", "373", "Molecular Biology", 
				"1", "4", "4", "TuTh", "3", "45", "LEC", "", "" );
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,28, true, "CHEM", "375", "Molecular Biology Lab", 
				"1", "4", "3", "F", "1", "48", "LEC", "", "" );
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,29, true, "CHEM", "375L", "Molecular Biology Lab Lab", 
				"3", "2", "1", "TuTh,WF", "6", "24", "LAB", "", "28" );
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,30, true, "CHEM", "377", "Chemistry of Drugs and Poisons", 
				"1", "4", "4", "TuTh", "3", "40", "LEC", "", "" );
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,31, true, "CHEM", "439", "Instrumental Analysis", 
				"1", "4", "3", "MWF", "3", "36", "LEC", "", "" );
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,32, true, "CHEM", "439L", "Instrumental Analysis Lab", 
				"2", "2", "1", "TuTh", "6", "24", "LAB", "", "32" );
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,33, true, "CHEM", "445", "Polymers & Coatings II", 
				"1", "4", "4", "TuTh", "3", "25", "LEC", "", "" );
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,34, true, "CHEM", "448", "Polymers/coatings Lab II", 
				"1", "3", "3", "MW", "6", "24", "LAB", "", "" );
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,35, true, "CHEM", "459", "Undergraduate Seminar", 
				"1", "4", "4", "W", "2", "35", "SEM", "", "" );
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,36, true, "CHEM", "474", "Protein Techniques Lab", 
				"1", "4", "4", "MW", "6", "24", "LAB", "", "" );
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,37, true, "CHEM", "528", "Nutritional Biochemistry", 
				"1", "4", "4", "MW", "4", "36", "SEM", "", "" );
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,38, true, "CHEM", "545", "Polymer Synthesis & Mechanisms", 
				"1", "4", "4", "TuTh", "3", "25", "LEC", "", "" );
		
		
		WebUtility.enterIntoCoursesResourceTableNewRow(driver,39, true, "CHEM", "590", "Graduate Seminar in Polymers & Coating", 
				"1", "4", "4", "F", "1", "36", "SEM", "", "" );
		
	*/	
		
		// Test saving
		driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
		driver.findElement(By.xpath("//td[@class='menuTitleField']//nobr[text()='Save']")).click();
		Thread.sleep(500);
		driver.switchTo().alert().accept();
		Thread.sleep(500);

		// Click on the instructors tab
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Instructors']")).click();

		// Start filling out instructors
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,0, true, "Fantin", "Dennis", "dfantin", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,1, true, "Lowell", "Carol", "clowell", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,2, true, "Baxley", "Lara", "lbaxley", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,3, true, "McDonald", "Ashley Ringer ", "armcdona", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,4, true, "Friend", "Kerry", "kmfriend", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,5, true, "Heying", "Michael David", "mdheying", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,6, true, "Retsek", "Jennifer", "jretsek", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,7, true, "Neff", "Grace", "gneff", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,8, true, "Cichowski", "Robert", "rcichows", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,9, true, "Couch", "Vernon", "vcouch", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,10, true, "Drew", "Michael", "drew", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,11, true, "Martinez", "Andres", "awmartin", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,12, true, "Goers", "John", "jgoers", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,13, true, "Zhang", "Shanju", "szhang05", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,14, true, "Fogle", "Emily Joyce", "efogle", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,15, true, "Bush", "Seth", "sbush", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,16, true, "Gragson", "Derek", "dgragson", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,17, true, "Keeling", "David", "dkeeling", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,18, true, "Lehr", "Corinne", "clehr", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,19, true, "Scott", "Gregory", "gscott02", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,20, true, "Kiste", "Alan", "akiste", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,21, true, "Lindert", "Lisa", "llindert", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,22, true, "Kirkhart", "David Byron", "dkirkhar", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,23, true, "Tice", "Russell", "rtice", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,24, true, "Kolkailah", "Noha", "nkolkail", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,25, true, "Bailey", "Christina", "cbailey", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,26, true, "Frey", "Thomas", "tfrey", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,27, true, "Carroll", "Jennifer", "jacarrol", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,28, true, "Kingsbury", "Kevin", "kkingsbu", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,29, true, "Turner", "Barbara", "beturner", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,30, true, "Robins", "Lori Ilene", "lrobins", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,31, true, "Goodman", "Anya", "agoodman", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,32, true, "Meisenheimer", "Kristen", "kmeisenh", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,33, true, "Silvestri", "Michael", "msilvest", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,34, true, "Bailey", "Philip", "pbailey", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,35, true, "Van Draanen", "Nanine", "nvandraa", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,36, true, "Endres", "Leland", "lendres", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,37, true, "Palandoken", "Hasan", "hpalando", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,38, true, "Wills", "Max", "mwills", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,39, true, "Marlier", "John", "jmarlier", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,40, true, "Costanzo", "Philip", "pcostanz", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,41, true, "Kantorowski", "Eric", "ekantoro", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,42, true, "Jones", "Dane", "djones", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,43, true, "Hagen", "John", "jhagen", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,44, true, "Rice", "Margaret", "msrice", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,45, true, "Hillers", "Kenneth", "khillers", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,46, true, "Clement", "Sandra", "slclemen", "20");
		WebUtility.enterIntoInstructorsResourceTableNewRow(driver,47, true, "Fernando", "Raymond", "rhfernan", "20");
		
		// Test saving
		driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
		driver.findElement(By.xpath("//td[@class='menuTitleField']//nobr[text()='Save']")).click();
		Thread.sleep(500);
		driver.switchTo().alert().accept();
		Thread.sleep(500);

		// Click on the locations tab
		driver.findElement(By.xpath("//td[@class='tabTitle'][text()='Locations']")).click();

		// Start filling out locations
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,0, true, "026-0104", "", "72", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,1, true, "033-0351", "", "24", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,2, true, "052-0E27", "", "102", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,3, true, "038-0121", "", "64", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,4, true, "033-0286", "", "146", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,5, true, "026-0103", "", "93", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,6, true, "052-0D25", "", "24", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,7, true, "052-0D13", "", "24", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,8, true, "052-0D15", "", "24", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,9, true, "052-0D18", "", "24", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,10, true, "052-0D11", "", "16", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,11, true, "010-0220", "", "58", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,12, true, "052-0E26", "", "56", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,13, true, "003-0112", "", "65", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,14, true, "033-0356", "", "20", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,15, true, "053-0215", "", "70", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,16, true, "026-0106", "", "66", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,17, true, "186-C303", "", "48", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,18, true, "033-0287", "", "48", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,19, true, "053-0213", "", "48", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,20, true, "038-0219", "", "35", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,21, true, "186-C101", "", "40", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,22, true, "052-0D20", "", "24", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,23, true, "034-0227", "", "66", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,24, true, "010-0221", "", "45", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,25, true, "033-0390", "", "24", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,26, true, "052-0D27", "", "24", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,27, true, "020-0139", "", "36", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,28, true, "020-0143", "", "25", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,29, true, "038-0227", "", "35", null);
		WebUtility.enterIntoLocationsResourceTableNewRow(driver,30, true, "033-0457", "", "36", null);
		

		// Test saving
		driver.findElement(By.xpath("//div[@class='toolStrip']//td[@class='buttonTitle'][text()='File']")).click();
		driver.findElement(By.xpath("//td[@class='menuTitleField']//nobr[text()='Save']")).click();
		Thread.sleep(500);
		driver.switchTo().alert().accept();
		Thread.sleep(500);

	}
}
