package scheduler.view.web.shared;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.pagefactory.ByChained; 

public class GRCAcceptanceTestFirefox extends GRCAcceptanceTest {	
	public void setUp() {
		super.setUp(new FirefoxDriver());
	}
}
