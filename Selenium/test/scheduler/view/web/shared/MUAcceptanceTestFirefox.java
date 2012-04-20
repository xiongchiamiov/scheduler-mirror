package scheduler.view.web.shared;

import org.openqa.selenium.firefox.FirefoxDriver;

public class MUAcceptanceTestFirefox extends MUAcceptanceTest {	
	public void setUp() {
		super.setUp(new FirefoxDriver());
	}
}
