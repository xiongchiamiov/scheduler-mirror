package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;
import edu.calpoly.csc.scheduler.view.web.client.table.Factory;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.SelectColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.StaticGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.StaticSetter;
import edu.calpoly.csc.scheduler.view.web.client.table.StringColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.TableConstants;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.UserDataGWT;

public class AdminConfigView extends VerticalPanel implements IViewContents {
	private GreetingServiceAsync service;
	private OsmTable<UserDataGWT> table;
	int nextLocationID = 1;
	private String scheduleName;

	public AdminConfigView(GreetingServiceAsync greetingService, String scheduleName) {
		this.service = greetingService;
		this.scheduleName = scheduleName;
	}
	
	@Override
	public void afterPush(ViewFrame frame) {
		this.setWidth("100%");
		this.setHeight("100%");
				
		this.add(new HTML("<h2>" + scheduleName + " - Configuration</h2>"));
		
		final LoadingPopup popup = new LoadingPopup();
		popup.show();
						
		table = new OsmTable<UserDataGWT>(
				new Factory<UserDataGWT>() {
					public UserDataGWT create() {
						return new UserDataGWT();
					}
					public UserDataGWT createHistoryFor(UserDataGWT course) {
						UserDataGWT i = course.clone();
//						i.setId(-course.getId());
						return i;
					}
				},
				new OsmTable.SaveHandler<UserDataGWT>() {
					public void saveButtonClicked() {
						save();
					}
				});
				
		table.addColumn(new StringColumn<UserDataGWT>(TableConstants.CONFIG_USERNAME, "6em",
				new StaticGetter<UserDataGWT, String>() {
					public String getValueForObject(UserDataGWT object) { return object.getCourseName(); }
				},
				new StaticSetter<UserDataGWT, String>() {
					public void setValueInObject(UserDataGWT object, String newValue) { object.setCourseName(newValue); }
				},
				String.CASE_INSENSITIVE_ORDER, null));
		
		table.addColumn(new SelectColumn<UserDataGWT>(TableConstants.CONFIG_LEVEL, "6em",
				
				new String[] { "0", "1", "2" },
				
				new StaticGetter<UserDataGWT, String>() {
					public String getValueForObject(UserDataGWT object) { return object.getCourseName(); }
				},
				
				new StaticSetter<UserDataGWT, String>() {
					public void setValueInObject(UserDataGWT object, String newValue) { object.setCourseName(newValue); }
				},
				
				String.CASE_INSENSITIVE_ORDER));		
						
		this.add(table);
		
		service.getCourses(new AsyncCallback<List<CourseGWT>>() {
			public void onFailure(Throwable caught) {
				popup.hide();
				Window.alert("Failed to get courses: " + caught.toString());
			}
			
			public void onSuccess(List<CourseGWT> result){
				assert(result != null);
				popup.hide();
				for (CourseGWT crs : result)
					nextLocationID = Math.max(nextLocationID, crs.getId() + 1);
				//table.addRows(result);
			}
		});
	}
		
	private void save() {
		/*service.saveCourses(table.getAddedUntouchedAndEditedObjects(), new AsyncCallback<Collection<CourseGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub				
			}
			@Override
			public void onSuccess(Collection<CourseGWT> result) {
				table.clear();
				//table.addRows(result);
			}
		});*/
	}

	@Override
	public boolean canPop() {
		assert(table != null);
		if (table.isSaved())
			return true;
		return Window.confirm("You have unsaved data which will be lost. Are you sure you want to navigate away?");
	}

	@Override
	public void beforePop() { }
	@Override
	public void beforeViewPushedAboveMe() { }
	@Override
	public void afterViewPoppedFromAboveMe() { }
	@Override
	public Widget getContents() { return this; }
}
