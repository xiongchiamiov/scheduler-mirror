package edu.calpoly.csc.scheduler.view.web.client.schedule;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

/**
 * A dialog that allows the user to edit the instructor, location, and time of a schedule item
 */
public class EditScheduleItemWidget extends DialogBox {

	private final VerticalPanel mMainPanel = new VerticalPanel();
	private final ListBox mInstructorsLB = new ListBox(false);
	private final ListBox mLocationsLB = new ListBox(false);
	
	private final ScheduleItemGWT mItem;
	
	public EditScheduleItemWidget(ScheduleItemGWT item) {
		super(false);
		
		mItem = item;
		
		draw();
	}
	
	private void draw() {
		mMainPanel.setWidth("300px");
		mMainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		mMainPanel.add(createTitlePanel());
		mMainPanel.add(new HTML("<p />"));
		mMainPanel.add(createInstructorsPanel());
		mMainPanel.add(createLocationsPanel());
		mMainPanel.add(new HTML("<p />"));
		mMainPanel.add(createButtonPanel());
		
		setWidget(mMainPanel);
	}
	
	private void cancel() {
		hide();
	}
	
	private void ok() {
		hide();
	}
	
	private Widget createTitlePanel() {
		final HTML titlePanel = new HTML("<center><b>Edit "+mItem.getCourseString()+"</b></center><p>");
		titlePanel.setHeight("30px");
		return titlePanel;
	}
	
	private Widget createInstructorsPanel() {
		final HorizontalPanel instructorsPanel = new HorizontalPanel();
		instructorsPanel.setWidth("100%");
		
		instructorsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		instructorsPanel.add(new HTML("Instructor:&nbsp;&nbsp;"));
		
		instructorsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		mInstructorsLB.setVisibleItemCount(1);
		mInstructorsLB.setWidth("200px");
		instructorsPanel.add(mInstructorsLB);
		
		return instructorsPanel;
	}
	
	private Widget createLocationsPanel() {
		final HorizontalPanel locationsPanel = new HorizontalPanel();
		locationsPanel.setWidth("100%;");

		locationsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		locationsPanel.add(new HTML("Location:&nbsp;&nbsp;"));

		locationsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		mLocationsLB.setVisibleItemCount(1);
		mLocationsLB.setWidth("200px");
		locationsPanel.add(mLocationsLB);
		
		return locationsPanel;
	}
	
	private Widget createButtonPanel() {
		final HorizontalPanel buttonPanel = new HorizontalPanel();
		
		buttonPanel.add(new Button("OK", new ClickHandler() {
			public void onClick(ClickEvent event) {
				ok();
			}
		}));
		
		buttonPanel.add(new HTML("&nbsp;&nbsp;"));
		
		buttonPanel.add(new Button("Cancel", new ClickHandler() {
			public void onClick(ClickEvent event) {
				cancel();
			}
		}));
		
		return buttonPanel;
	}
}
