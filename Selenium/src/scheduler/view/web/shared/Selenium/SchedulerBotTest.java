package scheduler.view.web.shared.Selenium;

import static org.junit.Assert.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import junit.framework.*;

/*
 * Useful for determining if there's something wrong with the basic driver abilities.
 * If this fails then there's definitely a selenium problem.
 * 
 */
public class SchedulerBotTest extends TestCase {
	private SchedulerBot bot;
	private FirefoxDriver fbot;
	private ChromeDriver cbot;
	private InternetExplorerDriver iebot;
	
	public void testSchedbotFF() {
		//this.bot = new SchedulerBot("http://scheduler.csc.calpoly.edu/dev");
	}
	
	public void testFirefoxDriver() {
		this.fbot = new FirefoxDriver();
		
		//this.iebot = new InternetExplorerDriver();
		//driver.get("http://www.google.com");
		//driver.close();
		
		WebElement loginBtn, unameField, appTitle, logTitle;
		//some url not localhost
		fbot.get("http://scheduler.csc.calpoly.edu/dev");

			loginBtn = fbot.findElement(By.id("s_loginBtn")); 
			appTitle = fbot.findElement(By.id("appNameTtl"));
			assertEquals("Schedulizerifier", appTitle.getText());
			assertEquals("Login", loginBtn.getText());
			unameField = fbot.findElement(By.id("s_unameBox"));
		    //logTitle = fbot.findElement(By.id("s_LoginTag"));
		    //assertEquals("Login", logTitle.getText());
		    //System.out.println("logTitle text: " + logTitle.getText());
			
			unameField.sendKeys("snavarre");
			loginBtn.click();

		    
			
			//WebElement title = fbot.findElement(By.id("s_loginTag"));
			//assertEquals("Login", title.getText());

			WebElement user = fbot.findElement(By.id("s_unameLbl"));
			assertEquals("snavarre", user.getText());
			WebElement logout = fbot.findElement(By.id("s_logoutLnk"));
			WebElement hometab = fbot.findElement(By.id("s_HomeTab"));
			assertEquals("Home", hometab.getText());
			
			logout.click();

		fbot.close();
	}
	
	public void testChromeDriver() {
		String path = this.getClass().getClassLoader().getResource("chromedriver").getPath(); 
//		System.setProperty("webdriver.chrome.driver", path); 
//		System.out.println(path);
//		this.cbot = new ChromeDriver();
//		cbot.get("http://www.google.com");
//		cbot.close();
	}
}
