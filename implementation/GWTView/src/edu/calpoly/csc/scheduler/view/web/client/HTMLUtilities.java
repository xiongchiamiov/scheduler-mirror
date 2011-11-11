package edu.calpoly.csc.scheduler.view.web.client;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTML;

public class HTMLUtilities {
	public static HTML createLink(String label, String styleName, ClickHandler handler) {
		HTML result = createLabel(label, styleName);
		result.addClickHandler(handler);
		return result;
	}
	
	public static HTML createLabel(String label, String styleName) {
		HTML result = new HTML(label);
		result.addStyleName(styleName);
		return result;
	}
	
	public static Element getClosestContainingElementOfType(Element element, String type) {
		assert(element != null);
		while (!element.getNodeName().equalsIgnoreCase(type)) {
			element = element.getParentElement();
			assert(element != null);
		}
		return element;
	}
}
