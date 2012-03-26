package scheduler.view.web.client.views;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;

public class WindowHandler {

	public static void setExitWarning(boolean isOn){
		
		final String val = isOn ? "You are about to navigate away. All unsaved data will be lost." : null;
		
		// set exit handler
		Window.addWindowClosingHandler(new Window.ClosingHandler() {
			public void onWindowClosing(ClosingEvent event) {
				
				// turn on warning for navigating away
				event.setMessage(val);
		}});
	}
}
