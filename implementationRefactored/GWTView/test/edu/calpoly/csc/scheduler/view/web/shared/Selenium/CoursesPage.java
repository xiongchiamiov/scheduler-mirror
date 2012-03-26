package edu.calpoly.csc.scheduler.view.web.shared.Selenium;

import java.util.ArrayList;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.*;

/**
 * The Class Courses.
 */
public class CoursesPage {
	
	/** The Firefox Driver. */
	private FirefoxDriver fbot;
	/** The list of browsers */
	private ArrayList<WebDriver> browsers; 
	
	private WebElement courseTab;
	private WebElement newCourseBtn;
	private WebElement delBtn;
	private WebElement editBtn;
	
	/**
	 * Instantiates a new courses.
	 *
	 * @param fbot the fbot
	 */
	protected CoursesPage(FirefoxDriver fbot) {
		this.fbot = fbot;
	}
	
	protected void selectCoursesTab() {
		//courseTab = fbot.findElement(By.id("coursetab"));	
		
	}
	protected CoursesPage(ArrayList<WebDriver> browsers) {
		this.browsers = new ArrayList<WebDriver>(browsers);		
	}
}
