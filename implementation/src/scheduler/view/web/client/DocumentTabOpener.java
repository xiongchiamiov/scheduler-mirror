package scheduler.view.web.client;

import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.user.client.Window;

public class DocumentTabOpener {
	public static void openDocInNewTab(String username, DocumentGWT document) {
		String baseHref = Window.Location.getHref();
		
		if (Window.Location.getHref().contains("?userid="))
			baseHref = Window.Location.getHref().substring(0, Window.Location.getHref().lastIndexOf('?'));
		
		Window.open(baseHref + "?originaldocumentid=" + document.getID() + "&userid=" + username, "_new", null);
	}
}
