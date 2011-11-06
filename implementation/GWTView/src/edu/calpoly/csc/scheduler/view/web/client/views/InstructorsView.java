package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.table.Table;
import edu.calpoly.csc.scheduler.view.web.client.table.TableFactory;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;

public class InstructorsView extends ScrollPanel {
	// These static variables are a temporary hack to get around the table bug
	public static GreetingServiceAsync service;
	public static Panel container;
	
	private final String scheduleName;
	private Table<InstructorGWT> iTable;
	private VerticalPanel vp;

	public InstructorsView(Panel container, GreetingServiceAsync service, String scheduleName) {
		assert(service != null);
		this.container = container;
		
		InstructorsView.service = service;
		this.scheduleName = scheduleName;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();

		setWidth("100%");
		setHeight("100%");
		
		vp = new VerticalPanel();
		this.add(vp);

		vp.add(new HTML("<h2>" + scheduleName + " - Instructors</h2>"));
		
		iTable = TableFactory.instructor(service);
		vp.add(iTable.getWidget());
		
		iTable.clear();


		final LoadingPopup popup = new LoadingPopup();
		popup.show();
		
		
		service.getInstructors(new AsyncCallback<ArrayList<InstructorGWT>>() {
			public void onFailure(Throwable caught) {
				popup.hide();
				
				Window.alert("Failed to get professors: " + caught.toString());
			}
			
			public void onSuccess(ArrayList<InstructorGWT> result){
				popup.hide();
				
				if (result != null) {
					for (InstructorGWT ins : result)
						ins.verify();
					
					iTable.set(result);
				}
			}
		});
	}
}
