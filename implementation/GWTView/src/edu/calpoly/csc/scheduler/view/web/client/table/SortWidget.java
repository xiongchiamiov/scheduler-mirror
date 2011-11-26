package edu.calpoly.csc.scheduler.view.web.client.table;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

class SortWidget extends VerticalPanel {
	public interface SortCallback {
		void sort(boolean ascending);
	}
	
	enum Status { NOT_SORTING, ASCENDING, DESCENDING }
	
	SortCallback callback;
	
	public SortWidget() {
		addSortButton(true);
		addSortButton(false);
	}
	
	public void setSortCallback(SortCallback callback) {
		this.callback = callback;
	}
	
	private void addSortButton(final boolean ascending) {
		FocusPanel panel = new FocusPanel();
		panel.addStyleName("sortButton");
		panel.addStyleName(ascending ? "ascending" : "descending");
		add(panel);
		
		panel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Window.alert("Click! " + ascending);
			}
		});
	}
}
