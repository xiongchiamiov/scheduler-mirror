package edu.calpoly.csc.scheduler.view.web.shared;

import junit.framework.TestCase;
//import org.junit.*;
//import org.junit.runner.RunWith;
//import org.junit.runners.Suite;
import edu.calpoly.csc.scheduler.view.web.shared.Selenium.*;

/**
 * Abstract class DefaultSelTestCase 
 * Provides a basic override-able test template for all Selenium/UI
 * based testcases
 */
//@RunWith(Suite.class)
public class DefaultSelTestCase extends TestCase {
	
	/** The bot in charge of navigating the Scheduler GUI. */
	SchedulerBot bot;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	//@Before
	public void setUp(String url) {
		if(url.equals(""))
			bot = new SchedulerBot();
		else
			bot = new SchedulerBot(url);
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	//@After
	public void tearDown() {
		/** cleanup artifacts and quit the browser session for the current test*/
		bot.quitSession();
	}
}
