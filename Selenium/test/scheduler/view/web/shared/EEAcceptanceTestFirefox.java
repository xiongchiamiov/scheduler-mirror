package scheduler.view.web.shared;

import java.util.Arrays;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class EEAcceptanceTestFirefox extends EEAcceptanceTest {
	public void setUp() throws java.io.IOException {
		//DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		//capabilities.setCapability("chrome.switches", Arrays.asList("--disable-popup-blocking"));
		super.setUp(new FirefoxDriver());
	}
}
