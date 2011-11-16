package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.table.Factory;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.SelectColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.StaticGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.StaticSetter;
import edu.calpoly.csc.scheduler.view.web.client.table.StringColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.TableConstants;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.UserGWT;

public class AdminConfigView extends ScrollPanel implements IView<ScheduleNavView> {
	private GreetingServiceAsync service;
	private OsmTable<UserGWT> table;
	int nextLocationID = 1;
	private String scheduleName;

	public AdminConfigView(GreetingServiceAsync greetingService, String scheduleName) {
		this.service = greetingService;
		this.scheduleName = scheduleName;
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();

		setWidth("100%");
		setHeight("100%");
		
		VerticalPanel vp = new VerticalPanel();		
		vp.add(new HTML("<h2>" + scheduleName + " - Configuration</h2>"));
		this.add(vp);
		
		final LoadingPopup popup = new LoadingPopup();
		popup.show();
						
		table = new OsmTable<UserGWT>(
				new Factory<UserGWT>() {
					public UserGWT create() {
						return new UserGWT();
					}
					public UserGWT createHistoryFor(UserGWT course) {
						UserGWT i = course.clone();
//						i.setId(-course.getId());
						return i;
					}
				},
				new OsmTable.SaveHandler<UserGWT>() {
					public void saveButtonClicked() {
						save();
					}
				});
				
		table.addColumn(new StringColumn<UserGWT>(TableConstants.CONFIG_USERNAME, "6em",
				new StaticGetter<UserGWT, String>() {
					public String getValueForObject(UserGWT object) { return object.getCourseName(); }
				},
				new StaticSetter<UserGWT, String>() {
					public void setValueInObject(UserGWT object, String newValue) { object.setCourseName(newValue); }
				},
				String.CASE_INSENSITIVE_ORDER, null));
		
		table.addColumn(new SelectColumn<UserGWT>(TableConstants.CONFIG_LEVEL, "6em",
				
				new String[] { "0", "1", "2" },
				
				new StaticGetter<UserGWT, String>() {
					public String getValueForObject(UserGWT object) { return object.getCourseName(); }
				},
				
				new StaticSetter<UserGWT, String>() {
					public void setValueInObject(UserGWT object, String newValue) { object.setCourseName(newValue); }
				},
				
				String.CASE_INSENSITIVE_ORDER));		
						
		vp.add(table);
		
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
	public Widget getViewWidget() { return this; }

	@Override
	public void willOpenView(ScheduleNavView container) { }

	@Override
	public boolean canCloseView() {
		assert(table != null);
		if (table.isSaved())
			return true;
		return Window.confirm("You have unsaved data which will be lost. Are you sure you want to navigate away?");
	}
}
