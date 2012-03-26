package scheduler.view.web.client.views;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LoadingPopup extends PopupPanel {
	public LoadingPopup() {
		center();
		
		addStyleName("loadingPopup");
		
		VerticalPanel popupVP = new VerticalPanel();
		add(popupVP);
		
		popupVP.add(new Image("imgs/loading.gif"));
		HTML loadingLabel = new HTML("Loading...");
		loadingLabel.addStyleName("loadingLabel");
		popupVP.add(loadingLabel);
		
		center();
	}
}
