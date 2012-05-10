package scheduler.view.web.client;

import com.google.gwt.user.client.Window;

public class TabOpener {
	public static void openDocInNewTab(String username, int originalDocumentID, boolean openExistingWorkingDocument) {
		String url = URLUtilities.getBaseURL();
		System.out.println("url: " + url);
		url = URLUtilities.appendArgumentToURL(url, "userid", username);
		url = URLUtilities.appendArgumentToURL(url, "originaldocumentid", Integer.toString(originalDocumentID));
		url = URLUtilities.appendArgumentToURL(url, "openExistingWorkingDocument", openExistingWorkingDocument ? "true" : "false");
		Window.open(url, "scheduler_documentID_" + originalDocumentID, null);
	}

	public static void openHomeInNewTab(String username) {
		String baseHref = URLUtilities.getBaseURL();
		String url = URLUtilities.appendArgumentToURL(baseHref, "userid", username);
		Window.open(url, "schedulerhome", null);
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
