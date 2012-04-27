package scheduler.view.web.shared.Selenium;

import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.support.ui.Select;

/**
 * The Class Locations.
 */
public class LocationsPage {
	
	/** The Firefox driver. */
	private FirefoxDriver fbot;
	/** The list of browser drivers */
	private ArrayList<WebDriver> browsers;
	/** Create a new location */
	private WebElement newBtn;
	/** Save a location */
	private WebElement saveBtn;
	/** Delete a location */
	private WebElement deleteBtn;
	/** Edit a location */
	private WebElement bldField;
	/** Edit a location */
	private WebElement rmField;
	/** Edit a location */
	private WebElement occField;
	/** Edit a location */
	private Select typeField;
	/** Edit a location */
	private WebElement adaField;
	/** Edit a location */
	private WebElement laptopField;
	/** Edit a location */
	private WebElement overheadField;
	/** Edit a location */
	private WebElement smartRmField;
	
	/**
	 * Instantiates a new locations.
	 *
	 * @param fbot the fbot
	 */
	protected LocationsPage(FirefoxDriver fbot) {
		this.fbot = fbot;
	}
	
	protected LocationsPage(ArrayList<WebDriver> browsers) {
		this.browsers = new ArrayList<WebDriver>(browsers);
	}
	
	protected void createNewLocation(String bld, String roomNum, boolean type, String occu, boolean ada,
			boolean laptopConn, boolean overhead, boolean smartRoom) {
		newBtn.click();
		
		bldField = fbot.findElement(By.id("building"));
		bldField.sendKeys(bld);
		
		rmField = fbot.findElement(By.id("room"));
		rmField.sendKeys(roomNum);
		
		occField = fbot.findElement(By.id("occupancy"));
		occField.sendKeys(occu);
		
		typeField = new Select(fbot.findElement(By.id("type")));
		overheadField = fbot.findElement(By.id("overhead"));
		laptopField = fbot.findElement(By.id("laptop"));
		adaField = fbot.findElement(By.id("ada"));
		smartRmField = fbot.findElement(By.id("smartroom"));
		
		if(ada)
			adaField.click();
		if(laptopConn)
			laptopField.click();
		if(overhead)
			overheadField.click();
		if(smartRoom)
			smartRmField.click();
		//lecture
		if(type) {
			typeField.selectByVisibleText("LEC");
		}
		//lab
		else {
			typeField.selectByVisibleText("LAB");
		}
		
		saveBtn = fbot.findElement(By.id("saveButton"));
		saveBtn.click();
	}	
	
	protected ArrayList<WebElement> getLocations() {
		ArrayList<WebElement> items = new ArrayList<WebElement>();
		items.addAll(fbot.findElements(By.className("")));
		
		return items;
	}
	
	protected void init() {
		newBtn = fbot.findElement(By.id("s_newLocationBtn"));
	}
	
	private void pause() {
		try {
			Thread.currentThread().sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
