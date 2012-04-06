package scheduler.view.web.shared.Selenium;

import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.junit.Assert.*;

/**
 * The Class LoginSelectSched.
 */
public class LoginSelectPage {
	
	/** The firefox driver. */
	private FirefoxDriver fbot;
	/** The list of browsers */
	private ArrayList<WebDriver> browsers;
	/** The login btn. */
	private WebElement loginBtn;
	/** The uname field. */
	private WebElement unameField;	
	/** The sched list box. */
	private WebElement schedListBox;
	/** The options. */
	private List<WebElement> options;// = new ArrayList<WebElement>(); 
	/** The open btn. */
	private WebElement openBtn;	
	/** The new sched btn. */
	private WebElement newSchedBtn;
	
	/**
	 * Instantiates login and select schedule functionality.
	 *
	 * @param fbot the fbot
	 */
	protected LoginSelectPage(FirefoxDriver fbot) {
		this.fbot = fbot;
	}
	
	protected LoginSelectPage(ArrayList<WebDriver> browsers) {
		this.browsers = new ArrayList<WebDriver>(browsers);
	}
	
	/**
	 * CAS Login finds the components of the login page and uses the provided credentials
	 * to log in to the Scheduler Application
	 *
	 * @param String loginID the provided user name
	 * @return true, if logged in successfully else false 
	 */
	protected String CASLogin(String loginID) {
		String errorMsg = "";
		//had timing issues for page loading
		//pause();		
		
		try {
			loginBtn = fbot.findElement(By.id("login")); 
			assertEquals("Login", loginBtn.getText());
			unameField = fbot.findElement(By.id("uname"));
			
			System.out.println("Logging in with ID: " + loginID);
			
			unameField.sendKeys(loginID);
			loginBtn.click();
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
	
	/*
	 * Selects a schedule from the list of previous schedules to load if the 
	 * schedule is found. User can provide a schedule from the list.
	 * 
	 * @param String schedule an optional schedule in the list of 
	 * previous schedules
	 * @return boolean found true if provided schedule is found 
	 * 
	 * Must be logged in already and on the 'Select a Schedule' page
	 */
	/**
	 * Select previous schedule.
	 *
	 * @param schedule the schedule
	 * @return true, if successful
	 */
	protected boolean selectPreviousSchedule(String schedule) {	
		assert(openBtn!=null);
		assert(schedListBox!=null);
		//options = schedListBox.findElements(By.tagName("option"));
		
		for(WebElement e : schedListBox.findElements(By.tagName("option"))) {			
			if(e.getText().equals(schedule)) {
				e.click();
				openBtn.click();
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Gets the list of names of previous schedules that can be chosen
	 * @return ArrayList<String> list of the names of accessible previous schedules
	 */
	/**
	 * Gets the previous schedules.
	 *
	 * @return the previous schedules
	 */
	protected ArrayList<String> getPreviousSchedules() {		
		ArrayList<String> items = new ArrayList<String>();

		assert(schedListBox!=null);
		options = schedListBox.findElements(By.tagName("option"));
		for(WebElement e : options)
			items.add(e.getText());

		return items;
	}
	
	/**
	 * Adds a new schedule [untitled] right now.
	 */
	protected void addNewSchedule() {
		assert(newSchedBtn!=null);
		newSchedBtn.click();
	}
	
	// Once logged in, initialize all of the schedule selection elements
	/**
	 * Inits the schedule selection.
	 */
	protected void initScheduleSelection()
	{			
		pause();
		//get the list of schedules
		try {
			schedListBox = fbot.findElement(By.id("listBox"));
			
			//options = schedListBox.findElements(By.tagName("option"));
			
			//for(WebElement e: options)
				//System.out.println(e.getText());	
			
			newSchedBtn = fbot.findElement(By.id("newScheduleButton"));
			openBtn = fbot.findElement(By.id("openButton"));
			
		} catch (org.openqa.selenium.NotFoundException ex) {
			System.out.println("Selenium Page Elements [login] not located, check ID's");
		}
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
