package scheduler.view.web.shared;

import org.openqa.selenium.firefox.FirefoxDriver;

public class MATHAcceptanceTestFirefox extends MATHAcceptanceTest {	
	public void setUp() throws java.io.IOException {
		super.setUp(new FirefoxDriver());
	}
}
