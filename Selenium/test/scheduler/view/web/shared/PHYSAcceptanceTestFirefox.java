package scheduler.view.web.shared;

import org.openqa.selenium.firefox.FirefoxDriver;

public class PHYSAcceptanceTestFirefox extends PHYSAcceptanceTest {	
	public void setUp() throws java.io.IOException {
		super.setUp(new FirefoxDriver());
	}
}