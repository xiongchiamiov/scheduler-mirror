package scheduler.view.web.client.views;

import com.google.gwt.user.client.ui.Widget;

public interface View {
	boolean canClose();
	void close();
	Widget viewAsWidget();
}
