package scheduler.view.web.shared;

import org.openqa.selenium.firefox.FirefoxDriver;

public class GRCAcceptanceTestFirefox extends GRCAcceptanceTest {	
	public void setUp() throws java.io.IOException {
		super.setUp(new FirefoxDriver());
	}
}
