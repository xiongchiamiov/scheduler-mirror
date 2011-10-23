package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.table.InstructorTable;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;

public class InstructorsView extends ScrollPanel {
	private Panel container;
	private GreetingServiceAsync service;
	private String quarterID;
	private InstructorTable iTable;

	public InstructorsView(Panel container, GreetingServiceAsync service, String quarterID) {
		assert(service != null);
		
		this.container = container;
		this.service = service;
		this.quarterID = quarterID;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();

		setWidth("100%");
		setHeight("100%");
		
		VerticalPanel vp = new VerticalPanel();
		this.add(vp);
		
		iTable = new InstructorTable();
		vp.add(iTable.getWidget());
		populateInstructors();
		
		vp.add(createInstructorViewLinkList());
	}
	
	public Widget createInstructorViewLinkList() {
		final VerticalPanel vp = new VerticalPanel();
		
		service.getInstructorNames(new AsyncCallback<ArrayList<InstructorGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
			
			@Override
			public void onSuccess(ArrayList<InstructorGWT> result) {
				// TODO Auto-generated method stub
				for (InstructorGWT instructorBlerk : result) {
					final InstructorGWT instructor = instructorBlerk;
					HTML link = new HTML(instructor.getLastName() + ", " + instructor.getFirstName());
					link.addStyleName("inAppLink");
					link.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							container.clear();
							container.add(new InstructorPreferencesView(container, service, quarterID, instructor.getUserID()));
						}
					});
					vp.add(link);
				}
			}
		});
		
		return vp;
	}

	public void populateInstructors() {
		iTable.clear();
		
		service.getInstructorNames(new AsyncCallback<ArrayList<InstructorGWT>>() {
			public void onFailure(Throwable caught) {
				Window.alert("Failed to get professors: " + caught.toString());
			}
			
			public void onSuccess(ArrayList<InstructorGWT> result){
				if (result != null) {
					iTable.set(result);
				}
			}
		});
	}
}
