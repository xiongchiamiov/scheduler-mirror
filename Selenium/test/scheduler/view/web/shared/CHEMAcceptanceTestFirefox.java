package scheduler.view.web.shared;

import org.openqa.selenium.firefox.FirefoxDriver;

public class CHEMAcceptanceTestFirefox extends CHEMAcceptanceTest{
	public void setUp() throws java.io.IOException {
		super.setUp(new FirefoxDriver());
	}
}
