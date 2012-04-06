package scheduler.view.web.client;

import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.user.client.Window;

public class TabOpener {
	public static void openDocInNewTab(String username, DocumentGWT document) {
		String url = URLUtilities.getBaseURL();
		System.out.println("url: " + url);
		url = URLUtilities.appendArgumentToURL(url, "userid", username);
		url = URLUtilities.appendArgumentToURL(url, "originaldocumentid", Integer.toString(document.getID()));
		Window.open(url, "_blank", null);
	}
	
	public static void openHomeInNewTab(String username) {
		String baseHref = URLUtilities.getBaseURL();
		String url = URLUtilities.appendArgumentToURL(baseHref, "userid", username);
		Window.open(url, "_blank", null);
	}

	public static void openHomeInThisTab(String username) {
		String baseHref = URLUtilities.getBaseURL();
		String url = URLUtilities.appendArgumentToURL(baseHref, "userid", username);
		Window.Location.replace(url);
	}

	public static void openLoginInThisTab() {
		String url = URLUtilities.getBaseURL();
		Window.Location.replace(url);
	}
}
