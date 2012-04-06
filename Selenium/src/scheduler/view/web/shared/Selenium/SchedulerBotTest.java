package scheduler.view.web.shared.Selenium;

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
		//this.fbot = new FirefoxDriver();
		
		//this.iebot = new InternetExplorerDriver();
		//driver.get("http://www.google.com");
		//driver.close();
		
		//fbot.get("http://127.0.0.1:8888/Scheduler.html?gwt.codesvr=127.0.0.1:9997");
		//fbot.close();
	}
	
	public void testChromeDriver() {
		String path = this.getClass().getClassLoader().getResource("chromedriver").getPath(); 
		System.setProperty("webdriver.chrome.driver", path); 
		System.out.println(path);
		this.cbot = new ChromeDriver();
		cbot.get("http://127.0.0.1:8888/Scheduler.html?gwt.codesvr=127.0.0.1:9997");
		cbot.close();
	}
}
