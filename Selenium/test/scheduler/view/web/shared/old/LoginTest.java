package scheduler.view.web.shared.old;

import scheduler.view.web.shared.DefaultSelTestCase;
import scheduler.view.web.shared.Selenium.*;


public class LoginTest extends DefaultSelTestCase {	
	
	/** The sbot. */
	SchedulerBot sbot;
	/** url to use, if not default */
	private static final String protoURL = "http://scheduler.csc.calpoly.edu/dev";
	
	/* (non-Javadoc)
	 * @see GWTTests.DefaultSelTestCase#setUp()
	 */
	//@Before
	public void setUp() {
		//log in to scheduler
		//super.setUp(protoURL);
		//sbot = super.bot;
	}
	
//	/**
//	 * Test fail login.
//	 */
//	//@Test 
//	public void testEmptyLogin() {
//		System.out.println();
//		System.out.println("--------Login Testcase 1: Empty username-------------------------");
//
//		assertEquals("Please enter a username.", sbot.login(""));
//	}
//	
//	//need a real username eventually
//	/**
//	 * Test real login.
//	 */
//	//@Test
//	public void testRealLogin() {
//		System.out.println();
//		System.out.println("--------Login Testcase 2: Login user 'SelTCLogin'------------------");
//		
//		assertEquals("success", sbot.login("SelTCLogin"));
//	}
//	
	/* (non-Javadoc)
	 * @see GWTTests.DefaultSelTestCase#tearDown()
	 */
	//@After
	public void tearDown() {
		//close browser session
		super.tearDown();
	}
}
