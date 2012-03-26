package scheduler.view.web.client.calendar;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public class DOMUtility {

	private DOMUtility() {
		assert false;
	}
	
	/**
	 * Helper method to guard against a bug that may occur when setting a style attribute.
	 * If them element you pass to DOM.setStyleAttribute is null, the application won't
	 * complain when you run as web application in Eclipse, but it will fail without
	 * printing any error message when you deploy the application and run it as
	 * javascript.
	 * 
	 * @param elemID The ID of the element
	 * @param attribute The name of the attribute that will be set
	 * @param value The new value of the attribute
	 * @return true on success, false otherwise.
	 */
	public static boolean setStyleAttribute(String elemID, String attribute, String value) {
		final Element elem = DOM.getElementById(elemID);
		if (elem == null) 
			return false;
		
		DOM.setStyleAttribute(elem, attribute, value);
		return true;
	}
}
