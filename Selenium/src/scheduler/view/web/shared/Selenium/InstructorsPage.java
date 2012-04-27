package scheduler.view.web.shared.Selenium;

import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * The Class Instructors.
 */
public class InstructorsPage {

	/** The Firefox Driver. */
	private FirefoxDriver fbot;
	/** The list of browser drivers */
	private ArrayList<WebDriver> browsers;
	
	/** newbutton */
	private WebElement newButton;
	
	// A web browser driver, firefox support only for now
	/**
	 * Instantiates a new instructors.
	 *
	 * @param fbot the fbot
	 */
	protected InstructorsPage(FirefoxDriver fbot) {
		this.fbot = fbot;
	}
	
	protected InstructorsPage(ArrayList<WebDriver> browsers) {
		this.browsers = new ArrayList<WebDriver>(browsers);		
	}
	
	protected void init() {
		try {
			newButton = fbot.findElement(By.id("s_newInstructorButton"));
		} catch(org.openqa.selenium.NoSuchElementException ex) {
			System.out.println("Selenium Page Elements [instructors] not located, check ID's");
		}
	}
	
	protected boolean save() {
		
		return true;
	}
	
	protected void addNew() {
		
	}
	
	protected ArrayList<String> getInstructors() {
		
		ArrayList<String> instructors = new ArrayList<String>();
		
		return instructors;		
	}

	protected void modifyInstructor(int index) {
		
	}
	
	/**
	 * Removes the instructor.
	 *
	 * @param index the index
	 */
	protected void removeInstructor(int index) {
		
	}
	
	/**
	 * Removes the instructor cancel.
	 *
	 * @param index the index
	 */
	protected void removeInstructorCancel(int index) {
		
	}
}
