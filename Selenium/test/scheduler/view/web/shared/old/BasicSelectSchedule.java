package scheduler.view.web.shared.old;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import scheduler.view.web.shared.DefaultSelTestCase;
import scheduler.view.web.shared.Selenium.*;



/**
 * The Class SelectSchedule. Just Basic test of selecting schedules
 */
public class BasicSelectSchedule extends DefaultSelTestCase {
	
	/** The sbot. */
	WebUtility sbot;
	/** prototype url */
	private static final String protoURL = "http://scheduler.csc.calpoly.edu/RPTA";
	
	/* (non-Javadoc)
	 * @see GWTTests.DefaultSelTestCase#setUp()
	 */
	@Before
	public void setUp() {
		//log in to scheduler
		//super.setUp(protoURL);
		//sbot = super.bot;
	}	
	
//	/**
//	 * Test add an empty untitled schedule.
//	 */
//	@Test
//	public void testAddSchedule() {	
//		System.out.println();
//		System.out.println("--------Testcase 1: Add an empty untitled Schedule-----------------------------");
//		
//		sbot.login("SelTCSelect");
//		assertTrue(sbot.createNewSchedule());
//		sbot.logout();
//		sbot.login("SelTCSelect");
//		ArrayList<String> list = sbot.getPreviousSchedules();
//		
//		for(String str : list)
//			System.out.println("Selected Schedule: " + str);	
//	}
//	
//	/**
//	 * Test find existing schedules. Assumes you log into an account that has schedule
//	 * in it already associated with the username.
//	 */
//	@Test
//	public void testExistingSchedules() {	
//		System.out.println();
//		System.out.println("--------Testcase 2: Get Previous Schedules-----------------------------");
//		
//		sbot.login("SelTCSelect");
//		ArrayList<String> list = sbot.getPreviousSchedules();
//	
//		if(list.isEmpty())
//			assertTrue(false);
//		else assertTrue(true);
//		
//		for(String str : list) {
//			System.out.println("Selected Schedule: " + str);
//			//searching for crossover data issues
//			if(str.equals("CHEM")) 
//				System.out.println("Possible data corruption");
//		}
//	} 
//	
//	/**
//	 * Test select a specific schedule.
//	 */
//	
////	@Test
//	//a specific schedule needs to be there
//	public void testSelectSpecificSchedule() {
//		System.out.println();
//		System.out.println("--------Testcase 3: Get Specific Schedule 'Untitled'--------------------------");
//		
//		sbot.login("SelTCSelect");
//		assertTrue(sbot.selectSchedule("Untitled"));
//	}  	
//		
//	/* (non-Javadoc)
//	 * @see GWTTests.DefaultSelTestCase#tearDown()
//	 */ 
//	@After
//	public void tearDown() {
//		//close browser session
//		super.tearDown();
//	}
}
