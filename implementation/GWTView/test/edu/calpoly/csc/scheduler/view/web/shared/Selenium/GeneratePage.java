package edu.calpoly.csc.scheduler.view.web.shared.Selenium;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class GeneratePage {
	
	/** The firefox driver. */
	private FirefoxDriver fbot;
	
	/** The list of browsers */
	private ArrayList<WebDriver> browsers;
	
	private WebElement generateScheduleBtn;
	private WebElement filterBtn;
	
	private WebElement availableCoursesListBox;
	
	private WebElement calendarTable;
		
	/** The options. */
	private List<WebElement> options;// = new ArrayList<WebElement>(); 
	
	/**
	 * Instantiates login and select schedule functionality.
	 *
	 * @param fbot the fbot
	 */
	protected GeneratePage(FirefoxDriver fbot) {
		this.fbot = fbot;
	}
	
	protected GeneratePage(ArrayList<WebDriver> browsers) {
		this.browsers = new ArrayList<WebDriver>(browsers);
	}
	
	protected String GenerateButtonPress() {
		String errorMsg = null;
		//had timing issues for page loading
		//pause();		
		
		try {
			generateScheduleBtn = fbot.findElement(By.id("generateButton")); 
			assertEquals("Generate Schedule", generateScheduleBtn.getText());			
			System.out.println("Pressing Generate Schedule Button");			
			generateScheduleBtn.click();
			
		} catch (org.openqa.selenium.NotFoundException ex){
			System.out.println("");
			errorMsg += "Selenium Page Elements [intial login] not located, check ID's";
		}		
		
		try {
			Alert popup = fbot.switchTo().alert();
			errorMsg += popup.getText();
			popup.accept();		
			
		} catch (NoAlertPresentException ex) {
			System.out.println("Valid credentials");
			//initScheduleSelection();
			 errorMsg += "success";
		}

		return errorMsg;
	}

	protected String FilterButtonPress() {
		return null;
	}
	
	protected String ListToCalendarDrag() {
		return null;
	}
	
	protected String ListToListDrag() {
		return null;
	}
	
	protected String CalendarToListDrag() {
		return null;
	}
	
	protected String CalendarToCalendarDrag() {
		return null;
	}
	
	protected String CalendarItemDoubleClick() {
		return null;
	}
	
	protected String ListItemDoubleClick() {
		return null;
	}
}
