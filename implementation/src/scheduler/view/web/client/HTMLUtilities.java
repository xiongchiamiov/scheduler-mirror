package scheduler.view.web.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;

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

	public static boolean elementIsAncestorOfElement(Element current, Element ancestor) {
		while (current != null) {
			if (current.equals(ancestor))
				return true;
			current = current.getParentElement();
		}
		return false;
	}

	public static void addSpace(HLayout canvas, int width) {
		LayoutSpacer spacer = new LayoutSpacer();
		spacer.setWidth(width);
		spacer.setHeight100();
		canvas.addMember(spacer);
	}

	public static void addSpace(VLayout canvas, int height) {
		LayoutSpacer spacer = new LayoutSpacer();
		spacer.setHeight(height);
		spacer.setWidth100();
		canvas.addMember(spacer);
	}
}
