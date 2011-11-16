package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.table.ButtonColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.ButtonColumn.ClickCallback;
import edu.calpoly.csc.scheduler.view.web.client.table.Factory;
import edu.calpoly.csc.scheduler.view.web.client.table.IntColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.SelectColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.StaticGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.StaticSetter;
import edu.calpoly.csc.scheduler.view.web.client.table.StaticValidator;
import edu.calpoly.csc.scheduler.view.web.client.table.StringColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.Table;
import edu.calpoly.csc.scheduler.view.web.client.table.TableConstants;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.UserGWT;

public class AdminConfigView extends ScrollPanel {
	private GreetingServiceAsync service;
	private Table<UserGWT> cTable;
	private static OsmTable<UserGWT> table;
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
		
		service.getCourses2(new AsyncCallback<Collection<CourseGWT>>() {
			public void onFailure(Throwable caught) {
				popup.hide();
				Window.alert("Failed to get courses: " + caught.toString());
			}
			
			public void onSuccess(Collection<CourseGWT> result){
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
	
	private void labSelectionHandler(ListBox listbox, CourseGWT object, Button button){
		
    	final CourseGWT fobject = object;
    	final Button fButton = button;
		
		String value = listbox.getValue(listbox.getSelectedIndex());
		if(value.equals("")){
			fobject.setLabDept("");
			fobject.setLabName("");
			fobject.setLabCatalogNum(0);
			fButton.setText("");
		}
		else{			
			
			// get first integer
			int i;
			for(i = 0; !Character.isDigit(value.charAt(i)) && i < value.length(); i++){}
			
			try{
				int cnum = Integer.parseInt(value.substring(i));
				
				fobject.setLabDept(value.substring(0, i));
				fobject.setLabName("");
				fobject.setLabCatalogNum(cnum);
				
				fButton.setText(value);				
			} catch(Exception e){
				fobject.setLabDept("");
				fobject.setLabName("");
				fobject.setLabCatalogNum(0);
				
				fButton.setText("");
			}
		}
		
	}
		
	public static boolean isSaved(){
		if(table == null) { 
			return true; 
		}
		
		return table.isSaved();
	}
	
	public static void clearChanges(){
		if(table != null) {
			table.clearChanges();
		}
	}
}
