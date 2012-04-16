package scheduler.view.web.shared;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import scheduler.view.web.shared.Selenium.SchedulerBot;
import scheduler.view.web.shared.Selenium.SchedulerBot.PopupWaiter;

public abstract class GRCAcceptanceTest extends DefaultSelTestCase {	
	private WebDriver driver;
	private StringBuffer verificationErrors = new StringBuffer();
	private static final String protoURL = "http://localhost:8080/GRC";
	private SchedulerBot bot;	
	
	/* (non-Javadoc)
	 * @see GWTTests.DefaultSelTestCase#setUp()
	 */
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
	
	/**
	 * Test initializing page elements
	 * @throws InterruptedException 
	 */
	public void testAcceptanceForGRC() throws InterruptedException {
		final String documentName = "Winter 2012x";
		
		driver.get(protoURL);
		assert(driver.findElement(By.id("s_unameBox")) != null);
		driver.findElement(By.id("s_unameBox")).clear();
		driver.findElement(By.id("s_unameBox")).sendKeys("eovadia");
		driver.findElement(By.id("s_loginBtn")).click();
		bot.waitForElementPresent(By.xpath("//div[@eventproxy='s_createBtn']"));
		
		bot.mouseDownAndUpAt(By.xpath("//div[@eventproxy='s_createBtn']"), 5, 5);

		bot.waitForElementPresent(By.id("s_createBox"));
		
		driver.findElement(By.id("s_createBox")).clear();
		driver.findElement(By.id("s_createBox")).sendKeys(documentName);

		PopupWaiter popupWaiter = bot.getPopupWaiter();
		
		driver.findElement(By.id("s_createNamedDocBtn")).click();
		
		String newWindowHandle = popupWaiter.waitForPopup();
		
		driver.switchTo().window(newWindowHandle);

		// TODO: see if documentName appears anywhere on screen?

		bot.enterIntoResourceTableNewRow(0, true, "GRC", "101", "Graphics", "1", "3", "3", null, "3", "97", "LEC");
		bot.enterIntoResourceTableNewRow(1, true, "GRC", "200", "Special Problems", "1", "3", "3", null, null, "10", "IND");
		bot.enterIntoResourceTableNewRow(2, true, "GRC", "200", "Special Problems", "1", "3", "3", null, null, "10", "IND");
		bot.enterIntoResourceTableNewRow(3, true, "GRC", "202", "Digital Photography", "1", "3", "3", null, "2", "50", "LEC");
		bot.enterIntoResourceTableNewRow(4, true, "GRC", "202", "Digital Photography", "3", "3", "3", null, "3", "20", "LAB");
		bot.enterIntoResourceTableNewRow(5, true, "GRC", "203", "Digital File Preparation and Workflow", "1", "2", "3", null, "1", "45", "LEC");
		bot.enterIntoResourceTableNewRow(6, true, "GRC", "203", "Digital File Preparation and Workflow", "3", "3", "3", null, "3", "15", "LAB");
		bot.enterIntoResourceTableNewRow(7, true, "GRC", "211", "Substrates, Inks and Toners", "1", "3", "3", null, "3", "43", "LEC");
		bot.enterIntoResourceTableNewRow(8, true, "GRC", "211", "Substrates, Inks and Toners", "3", "3", "3", null, "3", "14", "LAB");
		bot.enterIntoResourceTableNewRow(9, true, "GRC", "212", "Substrates, Inks and Toners: Theory", "1", "3", "3", null, "3", "6", "LEC");
		bot.enterIntoResourceTableNewRow(10, true, "GRC", "320", "Managing Quality in Graphic Communication", "1", "3", "3", null, "3", "46", "LEC");
		bot.enterIntoResourceTableNewRow(11, true, "GRC", "320", "Managing Quality in Graphic Communication", "3", "3", "3", null, "3", "16", "LAB");
		bot.enterIntoResourceTableNewRow(12, true, "GRC", "324", "Binding, Finishing and Distribution Process", "1", "3", "3", null, "3", "44", "LEC");
		bot.enterIntoResourceTableNewRow(13, true, "GRC", "324", "Binding, Finishing and Distribution Process", "3", "3", "3", null, "3", "14", "LAB");
		bot.enterIntoResourceTableNewRow(14, true, "GRC", "325", "Binding, Finishing and Distribution Process: Theory", "1", "3", "3", null, "3", "66", "LEC");
		bot.enterIntoResourceTableNewRow(15, true, "GRC", "328", "Sheetfed Printing Technology", "1", "3", "3", null, "3", "84", "LEC");
		bot.enterIntoResourceTableNewRow(16, true, "GRC", "328", "Sheetfed Printing Technology", "4", "3", "3", null, "3", "12", "LAB");
		bot.enterIntoResourceTableNewRow(17, true, "GRC", "331", "Color Management and Quality Analysis", "1", "3", "3", null, "3", "36", "LEC");
		bot.enterIntoResourceTableNewRow(18, true, "GRC", "331", "Color Management and Quality Analysis", "2", "3", "3", null, "2", "12", "ACT");
		bot.enterIntoResourceTableNewRow(19, false, "GRC", "337", "Consumer Packaging", "1", "3", "3", null, "2", "48", "LEC");
		bot.enterIntoResourceTableNewRow(20, false, "GRC", "337", "Consumer Packaging", "2", "3", "3", null, "3", "20", "LAB");
		bot.enterIntoResourceTableNewRow(21, true, "GRC", "377", "Web and Print Publishing", "1", "3", "3", null, "3", "48", "LEC");
		bot.enterIntoResourceTableNewRow(22, true, "GRC", "377", "Web and Print Publishing", "3", "3", "3", null, "3", "20", "LAB");
		bot.enterIntoResourceTableNewRow(23, true, "GRC", "400", "Special Problems", "8", "3", "3", null, null, "10", "IND");
		bot.enterIntoResourceTableNewRow(24, true, "GRC", "402", "Digital Printing and Emerging Technologies in Graphic Communication", "1", "3", "3", null, "2", "54", "LEC");
		bot.enterIntoResourceTableNewRow(25, true, "GRC", "402", "Digital Printing and Emerging Technologies in Graphic Communication", "3", "3", "3", null, "2", "12", "ACT");
		bot.enterIntoResourceTableNewRow(26, true, "GRC", "403", "Estimating for Print and Digital Media", "1", "3", "3", null, "3", "56", "LEC");
		bot.enterIntoResourceTableNewRow(27, true, "GRC", "403", "Estimating for Print and Digital Media", "3", "3", "3", null, "3", "20", "LAB");
		bot.enterIntoResourceTableNewRow(28, true, "GRC", "411", "Strategic Trends and Costing Issues in Print and Digital Media", "1", "3", "3", null, "3", "50", "LEC");
		bot.enterIntoResourceTableNewRow(29, true, "GRC", "411", "Strategic Trends and Costing Issues in Print and Digital Media", "2", "3", "3", null, "2", "20", "ACT");
		bot.enterIntoResourceTableNewRow(30, true, "GRC", "421", "Production Management for Print and Digital Media", "1", "3", "3", null, "3", "56", "LEC");
		bot.enterIntoResourceTableNewRow(31, true, "GRC", "421", "Production Management for Print and Digital Media", "2", "3", "3", null, "2", "20", "ACT");
		bot.enterIntoResourceTableNewRow(32, false, "GRC", "422", "Human Resource Management Issues for Print and Digital Media", "1", "3", "3", null, "3", "40", "LEC");
		bot.enterIntoResourceTableNewRow(33, false, "GRC", "422", "Human Resource Management Issues for Print and Digital Media", "2", "3", "3", null, "3", "20", "LAB");
		bot.enterIntoResourceTableNewRow(34, true, "GRC", "429", "Digital Media", "1", "3", "3", null, "2", "46", "LEC");
		bot.enterIntoResourceTableNewRow(35, true, "GRC", "429", "Digital Media", "2", "3", "3", null, "3", "12", "LAB");
		bot.enterIntoResourceTableNewRow(36, true, "GRC", "440", "Magazine and Newspaper Design Technology", "1", "3", "3", null, "3", "48", "LEC");
		bot.enterIntoResourceTableNewRow(37, true, "GRC", "440", "Magazine and Newspaper Design Technology", "2", "3", "3", null, "3", "12", "LAB");
		bot.enterIntoResourceTableNewRow(38, true, "GRC", "460", "Research Methods in Graphic Communication", "1", "3", "3", null, "1", "24", "LEC");
		bot.enterIntoResourceTableNewRow(39, true, "GRC", "460", "Research Methods in Graphic Communication", "1", "3", "3", null, "3", "20", "LAB");
		bot.enterIntoResourceTableNewRow(40, true, "GRC", "461", "Senior Project", "13", "3", "3", null, null, "10", "IND");
		bot.enterIntoResourceTableNewRow(41, true, "GRC", "461", "Senior Project", "1", "3", "3", null, null, "15", "IND");
		bot.enterIntoResourceTableNewRow(42, true, "GRC", "472", "Applied Graphic Communication Practices", "1", "3", "3", null, "2", "75", "LEC");
		bot.enterIntoResourceTableNewRow(43, false, "GRC", "473", "Applied Graphic Communication Practices", "1", "3", "3", null, "2", "20", "LEC");
		bot.enterIntoResourceTableNewRow(44, true, "GRC", "485", "Cooperative Education Experience", "3", "3", "3", null, null, "10", "IND");
		bot.enterIntoResourceTableNewRow(45, true, "GRC", "495", "Cooperative Education Experience", "2", "3", "3", null, null, "5", "IND");

		// TODO: save
	}
}
