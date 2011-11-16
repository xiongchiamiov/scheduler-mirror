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

public class AdminConfigView extends ScrollPanel {
	private GreetingServiceAsync service;
	private Table<CourseGWT> cTable;
	private static OsmTable<CourseGWT> table;
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
						
		table = new OsmTable<CourseGWT>(
				new Factory<CourseGWT>() {
					public CourseGWT create() {
						return new CourseGWT(nextLocationID++);
					}
					public CourseGWT createHistoryFor(CourseGWT course) {
						CourseGWT i = course.clone();
						i.setId(-course.getId());
						return i;
					}
				},
				new OsmTable.SaveHandler<CourseGWT>() {
					public void saveButtonClicked() {
						save();
					}
				});
				
		table.addColumn(new IntColumn<CourseGWT>(TableConstants.COURSE_WTU, "4em",
				new StaticGetter<CourseGWT, Integer>() {
					public Integer getValueForObject(CourseGWT object) { return object.getWtu(); }
				}, new StaticSetter<CourseGWT, Integer>() {
					public void setValueInObject(CourseGWT object, Integer newValue) { object.setWtu(newValue); }
				}, 
				new StaticValidator<CourseGWT, Integer>() {
					public void validate(CourseGWT object, Integer newValue) throws InvalidValueException {
						if (newValue < 1)
							throw new InvalidValueException(TableConstants.COURSE_WTU + " must be greater than 0: " + newValue + " is invalid.");
					}
				}));
		
		table.addColumn(new ButtonColumn<CourseGWT>(TableConstants.COURSE_LAB, "4em",
				new ClickCallback<CourseGWT>(){
				
					public String initialLabel(CourseGWT object) {
						if(object.getLabDept().trim().equals("")) { 
							return ""; 
						}
						
						return object.getLabDept().trim() + object.getLabCatalogNum();
					}
			
					public void buttonClickedForObject(CourseGWT object, Button button) {
						
						final PopupPanel popup = new PopupPanel(true);
				    	final CourseGWT fobject = object;
				    	final Button fButton = button;
				    	final ListBox listbox = new ListBox();
				    	
				    	popup.setGlassEnabled(true);
				    	popup.addCloseHandler(new CloseHandler<PopupPanel>(){
							public void onClose(CloseEvent<PopupPanel> event) {
								labSelectionHandler(listbox, fobject, fButton);
							}
				    	});
				    	
				    	// get lab options
				    	ArrayList<String> labOptions = new ArrayList<String>();
				    	labOptions.add("");
				    	
				  		for(CourseGWT c : table.getAddedUntouchedAndEditedObjects()) {
				  			labOptions.add(c.getDept().trim() + c.getCatalogNum());
				  		}
				  		
				  		Collections.sort(labOptions);
				  		
				  		listbox.addChangeHandler(new ChangeHandler(){
				  			public void onChange(ChangeEvent event){
				  				labSelectionHandler(listbox, fobject, fButton);
				  				popup.hide();
				  			}
				  		});
				  		
				  		for(int i = 0; i < labOptions.size(); i++){
				  			String s = labOptions.get(i);
				  			listbox.addItem(s);
				  			if((object.getLabDept().trim() + object.getLabCatalogNum()).equals(s)){
				  				listbox.setSelectedIndex(i);
				  			}
				  		}
				    	
				  		popup.setWidget(listbox);
				  		popup.center();
					}
		}));
						
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
		service.saveCourses(table.getAddedUntouchedAndEditedObjects(), new AsyncCallback<Collection<CourseGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub				
			}
			@Override
			public void onSuccess(Collection<CourseGWT> result) {
				table.clear();
				table.addRows(result);
			}
		});
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
