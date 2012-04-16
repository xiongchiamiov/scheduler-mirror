package scheduler.view.web.shared.old;

//import static org.junit.Assert.*;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import scheduler.view.web.shared.DefaultSelTestCase;
import scheduler.view.web.shared.Selenium.*;



// TODO: Auto-generated Javadoc
/**
 * The Class CreateSchedule
 */
public class CreateScheduleTest extends DefaultSelTestCase {
	
	/** The bot. */
	SchedulerBot sbot;
	/** url to use, if not default */
	String protoURL = "http://scheduler.csc.calpoly.edu/RPTA";
	
	/* (non-Javadoc)
	 * @see GWTTests.DefaultSelTestCase#setUp()
	 */
	//@Before
	public void setUp() {
		//log in to scheduler
		//super.setUp(protoURL);
		//sbot = super.bot;
		//sbot.login("SelTCLogin");
	}
	
	
	
//	/**
//	 * create a new schedule
//	 */
//	//@Test
//	public void testCreateSchedule() {	
//		System.out.println();
//		System.out.println("--------Testcase 1: Create Schedule-----------------------------");
//		
//		sbot.createNewSchedule();
//	}
//	
//	
//		
//	/* (non-Javadoc)
//	 * @see GWTTests.DefaultSelTestCase#tearDown()
//	 */
//	//@After
//	public void tearDown() {
//		//close browser session
//		super.tearDown();
//	}
}
