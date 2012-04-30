package scheduler.view.web.shared;

import org.openqa.selenium.firefox.FirefoxDriver;

public class ENGLAcceptanceTestFirefox extends ENGLAcceptanceTest {	
	public void setUp() {
		super.setUp(new FirefoxDriver());
	}
}
