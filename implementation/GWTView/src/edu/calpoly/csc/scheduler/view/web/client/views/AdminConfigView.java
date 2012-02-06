package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.Comparator;
import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;
import edu.calpoly.csc.scheduler.view.web.client.table.IFactory;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticSetter;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingSelectColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingStringColumn;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.UserDataGWT;

public class AdminConfigView extends VerticalPanel implements IViewContents {	/** Images */
	/** configuration table */
	public static final String CONFIG_USERNAME = "Username";
	
	public static final String CONFIG_LEVEL = "Permission Level";
	
	private GreetingServiceAsync service;
	private OsmTable<UserDataGWT> table;
	int nextLocationID = 1;
	private String scheduleName;

	public AdminConfigView(GreetingServiceAsync greetingService,
			String scheduleName) {
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

		table = new OsmTable<UserDataGWT>(new IFactory<UserDataGWT>() {
			public UserDataGWT create() {
				return new UserDataGWT();
			}
		});

		table.addColumn(
				CONFIG_USERNAME,
				"6em",
				true,
				new Comparator<UserDataGWT>() {
					@Override
					public int compare(UserDataGWT o1, UserDataGWT o2) {
						return o1.getUserName().compareToIgnoreCase(o2.getUserName());
					}
				},
				new EditingStringColumn<UserDataGWT>(
						new IStaticGetter<UserDataGWT, String>() {
							public String getValueForObject(UserDataGWT object) {
								return object.getUserName();
							}
						},
						new IStaticSetter<UserDataGWT, String>() {
							public void setValueInObject(UserDataGWT object,
									String newValue) {
								object.setUserName(newValue);
							}
						},
						null));

		table.addColumn(
				CONFIG_LEVEL,
				"6em",
				true,
				new Comparator<UserDataGWT>() {
					@Override
					public int compare(UserDataGWT o1, UserDataGWT o2) {
						return o1.getPermissionLevel().compareTo(o2.getPermissionLevel());
					}
				},
				new EditingSelectColumn<UserDataGWT>(
						new String[] { "0", "1", "2" },
						new IStaticGetter<UserDataGWT, String>() {
							public String getValueForObject(UserDataGWT object) {
								return object.getPermissionLevel().toString();
							}
						},
						new IStaticSetter<UserDataGWT, String>() {
							public void setValueInObject(UserDataGWT object,
									String newValue) {
								object.setPermissionLevel(Integer.parseInt(newValue));
							}
						}));

		this.add(table);

		service.getCourses(new AsyncCallback<List<CourseGWT>>() {
			public void onFailure(Throwable caught) {
				popup.hide();
				Window.alert("Failed to get courses: " + caught.toString());
			}

			public void onSuccess(List<CourseGWT> result) {
				assert (result != null);
				popup.hide();
				for (CourseGWT crs : result)
					nextLocationID = Math.max(nextLocationID, crs.getID() + 1);
				// table.addRows(result);
			}
		});
	}

	@Override
	public boolean canPop() {
		assert (table != null);
		if (table.isSaved())
			return true;
		return Window
				.confirm("You have unsaved data which will be lost. Are you sure you want to navigate away?");
	}

	@Override
	public void beforePop() {
	}

	@Override
	public void beforeViewPushedAboveMe() {
	}

	@Override
	public void afterViewPoppedFromAboveMe() {
	}

	@Override
	public Widget getContents() {
		return this;
	}
}
