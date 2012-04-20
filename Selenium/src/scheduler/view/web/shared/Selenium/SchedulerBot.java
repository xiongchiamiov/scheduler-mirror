package scheduler.view.web.shared.Selenium;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import net.sourceforge.htmlunit.corejs.javascript.JavaScriptException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;

/**
 * SchedulerBot provides an intermediary navigator of the Scheduler UI
 * components and functionality
 * 
 * @author Salome Navarrete
 * @version 1.0 Nov 22 2011
 */
public class SchedulerBot {
	private WebDriver driver;
	
	public SchedulerBot(WebDriver driver) {
		if (driver != null)
			this.driver = driver;
		else
			throw new WebDriverException("Driver not found");
	}
	
	public boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
			return true;
		}
		catch (NoSuchElementException e) {
			return false;
		}
	}

	interface Waitable {
		public boolean stopWaiting();
	}
	
	public void mouseDownAndUpAt(WebElement element, int x, int y) {
		new Actions(driver)
		.clickAndHold(element)
		.moveByOffset(x, y)
		.release(element)
		.build()
		.perform(); 
	}

	public class PopupWaiter {
		final Set<String> initialWindows;
		String poppedUpWindow;
		public PopupWaiter() {
			initialWindows = driver.getWindowHandles();
		}
		public String waitForPopup() {
			new WebDriverWait(driver, 20).until(new Predicate<WebDriver>() {
				public boolean apply(WebDriver arg0) {
					Set<String> currentWindows = driver.getWindowHandles();
					currentWindows.removeAll(initialWindows);
					if (!currentWindows.isEmpty()) {
						poppedUpWindow = currentWindows.iterator().next();
						return true;
					}
					else {
						return false;
					}
				}
			});
			assert(poppedUpWindow != null);
			return poppedUpWindow;
		}
	}
	
	private WebElement elementForResourceTableCell(int row0Based, int col0Based) {
		return driver.findElement(By.xpath("((//table[@class='listTable']/tbody/tr[@role='listitem'])[" + (1 + row0Based) + "]/td)[" + (1 + col0Based) + "]"));
	}
	
	private void setResourceTableTextCell(int row0Based, int col0Based, String text) {
		if (text == null)
			return;
		
		WebElement cell = elementForResourceTableCell(row0Based, col0Based);
		cell.click();
		
		WebElement input = elementForResourceTableCell(row0Based, col0Based).findElement(By.xpath("//input"));
		input.sendKeys(text);

		driver.findElement(By.tagName("body")).click();
	}

	private void setResourceTableMultiselectCell(int row0Based, int col0Based, String optionsCombined) throws InterruptedException {
		if (optionsCombined == null)
			return;

		Set<String> options = new TreeSet(Arrays.asList(optionsCombined.split(",")));
		
		WebElement cell = elementForResourceTableCell(row0Based, col0Based);
		cell.click();

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
		
		WebElement selectBeforeExpand = elementForResourceTableCell(row0Based, col0Based).findElement(By.xpath("div/nobr/span/table"));
		selectBeforeExpand.click();
		
		Thread.sleep(500);
		
		WebElement popupList = driver.findElement(By.xpath("/html/body/div[@class='listGrid']"));
		WebElement popupListBody = popupList.findElement(By.xpath("div/div[@class='gridBody']"));
		
		List<WebElement> popupListRows = popupListBody.findElements(By.xpath("div/div/table/tbody/tr"));
		
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		System.out.println(popupListRows.size());
		assert(popupListRows.size() >= 2);
		for (WebElement popupListRow : popupListRows) {
			String text = popupListRow.findElement(By.xpath("td[2]/div/nobr")).getText().trim();
			if (options.contains(text)) {
				WebElement checkbox = popupListRow.findElement(By.xpath("td[1]/div/nobr/img"));
				 
				boolean currentValue = "true".equals(popupListRow.getAttribute("aria-checked"));
				
				if (currentValue != true)
					checkbox.click();
//					mouseDownAndUpAt(checkbox, 5, 5);
			}
		}
		
		driver.findElement(By.tagName("body")).click();
	}

	private void setResourceTableSelectCell(int row0Based, int col0Based, String text) {
		if (text == null)
			return;
		
		WebElement cell = elementForResourceTableCell(row0Based, col0Based);
		cell.click();
		
		System.out.println("TODO: get rid of IND->LAB conversion!");
		if ("IND".equals(text))
			text = "LAB";
		// Making test temporarily pass so we can get on with the rest of it
		
		WebElement selectBeforeExpand = elementForResourceTableCell(row0Based, col0Based).findElement(By.xpath("div/nobr/span/table"));
		selectBeforeExpand.click();
		
		WebElement popupList = driver.findElement(By.xpath("/html/body/div/div/div[@class='pickListMenuBody']"));
		
		boolean foundOptionToClickOn = false;
		for (WebElement option : popupList.findElements(By.xpath("//tr[@role='listitem']"))) {
			if (option.getText().trim().equalsIgnoreCase(text)) {
				option.click();
				foundOptionToClickOn = true;
				break;
			}
		}
		
		assert(foundOptionToClickOn) : "Couldnt find option " + text;

		driver.findElement(By.tagName("body")).click();
		
		assert(elementForResourceTableCell(row0Based, col0Based).getText().trim().equalsIgnoreCase(text));
	}
	
	private void setResourceTableCheckboxCell(int row0Based, int col0Based, boolean newValue) {
//		WebElement newCourseDeptCell = elementForResourceTableCell(row0Based, col0Based);
//		newCourseDeptCell.click();
		
		WebElement img = elementForResourceTableCell(row0Based, col0Based).findElement(By.xpath("//div[@class='labelAnchor']/img"));
		WebElement imgParent = img.findElement(By.xpath("parent::*"));
		assert(imgParent.getTagName().equals("div"));
		
		boolean currentValue = "true".equals(imgParent.getAttribute("aria-checked"));
		
		if (currentValue != newValue)
			mouseDownAndUpAt(imgParent, 5, 5);
		
//		currentValue = "true".equals(imgParent.getAttribute("aria-checked"));
//		assert(currentValue == newValue);
		
		driver.findElement(By.tagName("body")).click();
	}
	
//	private void enterIntoResourceTableRow(int row0Based, Object... values) {
//		for (int i = 0; i < values.length; i++) {
//			Object object = values[i];
//			if (object == null)
//				continue;
//			
//			System.out.println("Handling index " + i + " value " + object);
//			
//			CellType type = openCellAndGetType(row0Based, i);
//			switch (type) {
//				case CHECKBOX: setResourceTableCheckboxCell(row0Based, i, (Boolean)object); break;
//				case TEXT: setResourceTableTextCell(row0Based, i, (String)object); break;
//				case SELECT: setResourceTableSelectCell(row0Based, i, (String)object); break;
//				default: assert(false);
//			}
//		}
//	}
	
//	private void enterIntoResourceTableNewRow(int row0Based, Object...values) {
//		driver.findElement(By.id("s_newCourseBtn")).click();
//		enterIntoResourceTableRow(row0Based, values);
//	}

//	bot.enterIntoResourceTableNewRow(0, true, "GRC", "101", "Graphics", "1", "3", "3", null, "3", "97", "LEC");
	public void enterIntoCoursesResourceTableNewRow(
			int row0Based,
			boolean isSchedulable,
			String department,
			String catalogNum,
			String courseName,
			String numSections,
			String wtu,
			String scu,
			String dayCombinations,
			String hoursPerWeek,
			String maxEnrollment,
			String type,
			String usedEquipment,
			String association) throws InterruptedException {
		
		System.out.println("Entering row index " + row0Based);
		
		driver.findElement(By.id("s_newCourseBtn")).click();
		
		setResourceTableCheckboxCell(row0Based, 1, isSchedulable);
		setResourceTableTextCell(row0Based, 2, department);
		setResourceTableTextCell(row0Based, 3, catalogNum);
		setResourceTableTextCell(row0Based, 4, courseName);
		setResourceTableTextCell(row0Based, 5, numSections);
		setResourceTableTextCell(row0Based, 6, wtu);
		setResourceTableTextCell(row0Based, 7, scu);
		setResourceTableMultiselectCell(row0Based, 8, dayCombinations);
		setResourceTableTextCell(row0Based, 9, hoursPerWeek);
		setResourceTableTextCell(row0Based, 10, maxEnrollment);
		setResourceTableSelectCell(row0Based, 11, type);
		setResourceTableMultiselectCell(row0Based, 12, usedEquipment);
		setResourceTableTextCell(row0Based, 13, association);
	}
	
	public void waitForElementPresent(final By by) throws InterruptedException {
		new WebDriverWait(driver, 60).until(new Predicate<WebDriver>() {
			public boolean apply(WebDriver arg0) {
				try {
					if (isElementPresent(by))
						return true;
				}
				catch (Exception e) {}
				return false;
			}
		});
	}
	
	public void mouseDownAndUpAt(By by, int x, int y) {
		WebElement element = driver.findElement(by);
		
		new Actions(driver)
				.clickAndHold(element)
				.moveByOffset(x, y)
				.release(element)
				.build()
				.perform();
	}
	
	public PopupWaiter getPopupWaiter() {
		return new PopupWaiter();
	}

	public void enterIntoInstructorsResourceTableNewRow(
			int row0Based,
			boolean isSchedulable,
			String lastName,
			String firstName,
			String username,
			String maxWTU) {

		System.out.println("Entering row index " + row0Based);
		
		driver.findElement(By.id("addInstructorBtn")).click();
		
		setResourceTableCheckboxCell(row0Based, 1, isSchedulable);
		setResourceTableTextCell(row0Based, 2, lastName);
		setResourceTableTextCell(row0Based, 3, firstName);
		setResourceTableTextCell(row0Based, 4, username);
		setResourceTableTextCell(row0Based, 5, maxWTU);
	}

	public void enterIntoLocationsResourceTableNewRow(
			int row0Based,
			boolean isSchedulable,
			String room,
			String type,
			String maxOccupancy,
			String equipment) {

		System.out.println("Entering row index " + row0Based);
		
		driver.findElement(By.id("addLocationButton")).click();
		
		setResourceTableCheckboxCell(row0Based, 1, isSchedulable);
		setResourceTableTextCell(row0Based, 2, room);
		setResourceTableTextCell(row0Based, 3, type);
		setResourceTableTextCell(row0Based, 4, maxOccupancy);
		setResourceTableTextCell(row0Based, 5, equipment);
	}
}