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
import com.google.gwt.user.client.ui.Widget;

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
import edu.calpoly.csc.scheduler.view.web.client.table.TableConstants;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;

public class CoursesView extends ScrollPanel implements IView<ScheduleNavView> {
	private GreetingServiceAsync service;
	private OsmTable<CourseGWT> table;
	int nextLocationID = 1;
	private String scheduleName;

	public CoursesView(GreetingServiceAsync greetingService, String scheduleName) {
		this.service = greetingService;
		this.scheduleName = scheduleName;
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
	
	@Override
	protected void onLoad() {
		super.onLoad();

		setWidth("100%");
		setHeight("100%");
		
		VerticalPanel vp = new VerticalPanel();
		this.add(vp);

		vp.add(new HTML("<h2>" + scheduleName + " - Courses</h2>"));

		final LoadingPopup popup = new LoadingPopup();
		popup.show();
		
		/*
		final LoadingPopup popup = new LoadingPopup();
		popup.show();

		
		cTable.clear();
		
		service.getCourses(new AsyncCallback<ArrayList<CourseGWT>>() {
			public void onFailure(Throwable caught) {
				popup.hide();
				
				Window.alert("Failed to get courses: " + caught.toString());
			}
			
			public void onSuccess(ArrayList<CourseGWT> result){
				popup.hide();
				
				if (result != null) {
					cTable.set(result);
				}
			}
		});
		*/
		
		
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

		table.addColumn(new StringColumn<CourseGWT>(TableConstants.COURSE_NAME, "6em",
				new StaticGetter<CourseGWT, String>() {
					public String getValueForObject(CourseGWT object) { return object.getCourseName(); }
				},
				new StaticSetter<CourseGWT, String>() {
					public void setValueInObject(CourseGWT object, String newValue) { object.setCourseName(newValue); }
				},
				String.CASE_INSENSITIVE_ORDER, null));
		
		table.addColumn(new IntColumn<CourseGWT>(TableConstants.COURSE_CATALOG_NUM, "4em",
				new StaticGetter<CourseGWT, Integer>() {
					public Integer getValueForObject(CourseGWT object) { return object.getCatalogNum(); }
				}, new StaticSetter<CourseGWT, Integer>() {
					public void setValueInObject(CourseGWT object, Integer newValue) { object.setCatalogNum(newValue); }
				}, 
				new StaticValidator<CourseGWT, Integer>() {
					public void validate(CourseGWT object, Integer newValue) throws InvalidValueException {
						if (newValue < 0)
							throw new InvalidValueException(TableConstants.COURSE_CATALOG_NUM + " must be positive: " + newValue + " is invalid.");
					}
				}));
		
		table.addColumn(new StringColumn<CourseGWT>(TableConstants.COURSE_DEPARTMENT, "6em",
				new StaticGetter<CourseGWT, String>() {
					public String getValueForObject(CourseGWT object) { return object.getDept(); }
				},
				new StaticSetter<CourseGWT, String>() {
					public void setValueInObject(CourseGWT object, String newValue) { object.setDept(newValue); }
				},
				String.CASE_INSENSITIVE_ORDER, null));
		
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
		
		table.addColumn(new IntColumn<CourseGWT>(TableConstants.COURSE_SCU, "4em",
				new StaticGetter<CourseGWT, Integer>() {
					public Integer getValueForObject(CourseGWT object) { return object.getScu(); }
				}, new StaticSetter<CourseGWT, Integer>() {
					public void setValueInObject(CourseGWT object, Integer newValue) { object.setScu(newValue); }
				}, 
				new StaticValidator<CourseGWT, Integer>() {
					public void validate(CourseGWT object, Integer newValue) throws InvalidValueException {
						if (newValue < 1)
							throw new InvalidValueException(TableConstants.COURSE_SCU + " must be greater than 0: " + newValue + " is invalid.");
					}
				}));
		
		table.addColumn(new IntColumn<CourseGWT>(TableConstants.COURSE_NUM_SECTIONS, "4em",
				new StaticGetter<CourseGWT, Integer>() {
					public Integer getValueForObject(CourseGWT object) { return object.getNumSections(); }
				}, new StaticSetter<CourseGWT, Integer>() {
					public void setValueInObject(CourseGWT object, Integer newValue) { object.setNumSections(newValue); }
				}, 
				new StaticValidator<CourseGWT, Integer>() {
					public void validate(CourseGWT object, Integer newValue) throws InvalidValueException {
						if (newValue < 1)
							throw new InvalidValueException(TableConstants.COURSE_NUM_SECTIONS + " must be greater than 0: " + newValue + " is invalid.");
					}
				}));
		
		table.addColumn(new SelectColumn<CourseGWT>(TableConstants.COURSE_TYPE, "6em",
				new String[] { "LEC", "LAB" },
				new StaticGetter<CourseGWT, String>() {
					public String getValueForObject(CourseGWT object) { return object.getType(); }
				},
				new StaticSetter<CourseGWT, String>() {
					public void setValueInObject(CourseGWT object, String newValue) { object.setType(newValue); }
				},
				String.CASE_INSENSITIVE_ORDER));
		
		table.addColumn(new IntColumn<CourseGWT>(TableConstants.COURSE_MAX_ENROLLMENT, "4em",
				new StaticGetter<CourseGWT, Integer>() {
					public Integer getValueForObject(CourseGWT object) { return object.getMaxEnroll(); }
				}, new StaticSetter<CourseGWT, Integer>() {
					public void setValueInObject(CourseGWT object, Integer newValue) { object.setMaxEnroll(newValue); }
				}, 
				new StaticValidator<CourseGWT, Integer>() {
					public void validate(CourseGWT object, Integer newValue) throws InvalidValueException {
						if (newValue < 1)
							throw new InvalidValueException(TableConstants.COURSE_MAX_ENROLLMENT + " must be greater than 0: " + newValue + " is invalid.");
					}
				}));
		
		table.addColumn(new IntColumn<CourseGWT>(TableConstants.COURSE_LENGTH, "4em",
				new StaticGetter<CourseGWT, Integer>() {
					public Integer getValueForObject(CourseGWT object) { return object.getLength() / 2; }
				}, new StaticSetter<CourseGWT, Integer>() {
					public void setValueInObject(CourseGWT object, Integer newValue) { object.setLength(newValue * 2); }
				}, 
				new StaticValidator<CourseGWT, Integer>() {
					public void validate(CourseGWT object, Integer newValue) throws InvalidValueException {
						if (newValue < 1)
							throw new InvalidValueException(TableConstants.COURSE_LENGTH + " must be greater than 0: " + newValue + " is invalid.");
					}
				}));
		
		table.addColumn(new ButtonColumn<CourseGWT>(TableConstants.COURSE_LAB, "4em",
				new ClickCallback<CourseGWT>(){
				
					public String initialLabel(CourseGWT object) {
						if(object.getLabDept().trim().equals("")){ return ""; }
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
				  		for(CourseGWT c : table.getAddedUntouchedAndEditedObjects()){
				  			/*
				  			if(c.getType().equals(TableConstants.LAB) && 
				  					!c.getDept().trim().equals("")){
				  				labOptions.add(c.getDept().trim() + c.getCatalogNum());
				  			}
				  			*/
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
				table.addRows(result);
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
				
			}catch(Exception e){
				fobject.setLabDept("");
				fobject.setLabName("");
				fobject.setLabCatalogNum(0);
				fButton.setText("");
			}
		}
		
	}
}
