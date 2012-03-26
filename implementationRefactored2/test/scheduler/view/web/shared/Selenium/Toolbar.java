package edu.calpoly.csc.scheduler.view.web.shared.Selenium;

import java.util.ArrayList;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * The Class Toolbar.
 */
public class Toolbar {	
	/** The firefox webdriver. */
	private FirefoxDriver fbot;	
	/** The list of browsers */
	private ArrayList<WebDriver> browsers;
	/** The instructors btn. */
	private WebElement instructorsBtn;	
	/** The back select btn. */
//	private WebElement backSelectBtn;	deprecated from view
	/** The build view btn. */
//	private WebElement buildViewBtn; deprecated from view
	/** The locations btn. */
	private WebElement locationsBtn;	
	/** The courses btn. */
	private WebElement coursesBtn;
	/** The logout btn. */
	private WebElement logoutBtn;
	/** The Schedule btn*/
	private WebElement scheduleBtn;
	
	/** The Filemenu and assorted elements*/
	private WebElement fileMenu;
	private WebElement newMenuItem;
	private WebElement openMenuItem;
	private WebElement importMenuItem;
	private WebElement saveMenuItem;
	private WebElement saveAsMenuItem;
	private WebElement downloadAsMenuItem;
	private WebElement mergeMenuItem;
	
	/**
	 * Instantiates a new toolbar.
	 *
	 * @param fbot the fbot
	 */
	protected Toolbar(FirefoxDriver fbot) {
		this.fbot = fbot;
	}
	
	protected Toolbar(ArrayList<WebDriver> browsers) {
		this.browsers = new ArrayList<WebDriver>(browsers);
	}
	
	protected String checkUsername() {
		return logoutBtn.getText();
	}
	
	//deprecated from view
	/**
	 * Go back select quarter.
	 */
	protected void goBackSelectQuarter() {
		//backSelectBtn.click();
	}
	
	/**
	 * Goto instructors.
	 */
	protected void gotoInstructors() {
		instructorsBtn.click();
		pause();
	}
	
	/**
	 * Goto courses.
	 */
	protected void gotoCourses() {
		coursesBtn.click();
		pause();
	}
	
	/**
	 * Goto locations.
	 */
	protected void gotoLocations() {
		locationsBtn.click();
		pause();
	}
	
	protected void logout() {
		logoutBtn.click();
		pause();
	}
	
	//deprecated
	/**
	 * Goto build view.
	 */
	protected void gotoBuildView() {
		//buildViewBtn.click();
	}
	
	protected boolean init() {
		try {
			//instructorsBtn = fbot.findElement(By.id("instructors"));
			//locationsBtn = fbot.findElement(By.id("locations"));
			//coursesBtn = fbot.findElement(By.id("courses"));
			//scheduleBtn = fbot.findElement(By.id("generate"));
			
		} catch (org.openqa.selenium.NotFoundException ex) {
			System.out.println("Selenium page element [toolbar] not found, check ID's");
			return false;
		}
		return true;
	}
	
	protected boolean initPrimary() {
		try {
			logoutBtn = fbot.findElement(By.id("logout"));
			
			//a bunch of these id's got replaced over in the view code...need to readd them there so these dont all fail
			//file menu--nothing right now except making sure elements exist
			//fileMenu = fbot.findElement(By.id("FileVIitem"));
			//newMenuItem = fbot.findElement(By.id("newScheduleBtn"));
			//openMenuItem = fbot.findElement(By.id("openItem"));
			//importMenuItem = fbot.findElement(By.id("importItem"));
			//saveMenuItem = fbot.findElement(By.id("saveItem"));
			//saveAsMenuItem = fbot.findElement(By.id("saveAsItem"));
			//downloadAsMenuItem = fbot.findElement(By.id("exportItem"));
			//mergeMenuItem = fbot.findElement(By.id("mergeItem"));
		
		} catch (org.openqa.selenium.NotFoundException ex) {
			System.out.println("Selenium page element [logout/file menu--toolbar] not found, check ID's");
			return false;
		}
		return true;
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
