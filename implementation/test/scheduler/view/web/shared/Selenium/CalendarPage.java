package scheduler.view.web.shared.Selenium;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

public class CalendarPage {
	
	/** The firefox driver. */
	private FirefoxDriver fbot;
	
	/** The list of browsers */
	private ArrayList<WebDriver> browsers;
		
	private WebElement availableCoursesListBox;	
	private WebElement calendarTable;
	
	/** The options. */
	private List<WebElement> options;// = new ArrayList<WebElement>(); 
	
	/**
	 * Instantiates login and select schedule functionality.
	 *
	 * @param fbot the fbot
	 */
	protected CalendarPage(FirefoxDriver fbot) {
		this.fbot = fbot;
	}
	
	protected CalendarPage(ArrayList<WebDriver> browsers) {
		this.browsers = new ArrayList<WebDriver>(browsers);
	}
	
	protected String GenerateButtonPress() {
		WebElement generateScheduleBtn;
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
		WebElement filterBtn;
		String errorMsg = null;
		//had timing issues for page loading
		//pause();		
		
		try {
			filterBtn = fbot.findElement(By.id("filterButton")); 
			assertEquals("Filter", filterBtn.getText());			
			System.out.println("Pressing Filter Button");			
			filterBtn.click();
			
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
			errorMsg += "success";
		}

		return errorMsg;
	}
	
	protected String ListToCalendarDrag() {
		String errorMsg = null;
		
		try {
			WebElement startingSpot;
			WebElement endingSpot;
			
			startingSpot = fbot.findElement(By.id("generateButton")); 
			endingSpot = fbot.findElement(By.id("generateButton")); 
			
			System.out.println("Performing list to calendar drag and drop.");			
			Actions builder = new Actions(fbot);   
			Action dragAndDrop = builder.clickAndHold(startingSpot).moveToElement(endingSpot).release(startingSpot).build();   
			
			dragAndDrop.perform();								
			
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
			errorMsg += "success";
		}

		return errorMsg;
	}
	
	protected String ListToListDrag() {
		String errorMsg = null;
		
		try {
			WebElement startingSpot;
			WebElement endingSpot;
			
			startingSpot = fbot.findElement(By.id("generateButton")); 
			endingSpot = fbot.findElement(By.id("generateButton")); 
			
			System.out.println("Performing list to list drag and drop.");			
			Actions builder = new Actions(fbot);   
			Action dragAndDrop = builder.clickAndHold(startingSpot).moveToElement(endingSpot).release(startingSpot).build();   
			
			dragAndDrop.perform();								
			
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
			errorMsg += "success";
		}

		return errorMsg;
	}
	
	protected String CalendarToListDrag() {
		String errorMsg = null;
		
		try {
			WebElement startingSpot;
			WebElement endingSpot;
			
			startingSpot = fbot.findElement(By.id("generateButton")); 
			endingSpot = fbot.findElement(By.id("generateButton")); 
			
			System.out.println("Performing calendar to list drag and drop.");			
			Actions builder = new Actions(fbot);   
			Action dragAndDrop = builder.clickAndHold(startingSpot).moveToElement(endingSpot).release(startingSpot).build();   
			
			dragAndDrop.perform();								
			
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
			errorMsg += "success";
		}

		return errorMsg;
	}
	
	protected String CalendarToCalendarDrag() {
		String errorMsg = null;
		
		try {
			WebElement startingSpot;
			WebElement endingSpot;
			
			startingSpot = fbot.findElement(By.id("generateButton")); 
			endingSpot = fbot.findElement(By.id("generateButton")); 
			
			System.out.println("Performing calendar to calendar drag and drop.");			
			Actions builder = new Actions(fbot);   
			Action dragAndDrop = builder.clickAndHold(startingSpot).moveToElement(endingSpot).release(startingSpot).build();   
			
			dragAndDrop.perform();								
			
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
			errorMsg += "success";
		}

		return errorMsg;
	}
	
	protected String CalendarItemDoubleClick() {
		return null;
	}
	
	protected String ListItemDoubleClick() {
		return null;
	}
}
