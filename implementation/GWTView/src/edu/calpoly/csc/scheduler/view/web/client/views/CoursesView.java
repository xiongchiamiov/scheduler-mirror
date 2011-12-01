package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;
import edu.calpoly.csc.scheduler.view.web.client.table.IFactory;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticSetter;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticValidator;
import edu.calpoly.csc.scheduler.view.web.client.table.MemberIntegerComparator;
import edu.calpoly.csc.scheduler.view.web.client.table.MemberStringComparator;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.ButtonColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.ButtonColumn.ClickCallback;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingStringColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.IntColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.SelectColumn;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.WeekGWT;

public class CoursesView extends VerticalPanel implements IViewContents {
	/** Course table */
	public static final String COURSE_NAME = "Course Name";

	public static final String COURSE_ID = "ID";

	public static final String COURSE_CATALOG_NUM = "Catalog Number";
	
	public static final String COURSE_DEPARTMENT = "Department";
	
	public static final String COURSE_WTU = "WTU";

	public static final String COURSE_LABID = "Lab ID";

	public static final String COURSE_SMARTROOM = "Smartroom";

	public static final String COURSE_LAPTOP = "Laptop";

	public static final String COURSE_OVERHEAD = "Overhead";

	public static final String COURSE_LENGTH = "Hours Per Week";
	
	public static final String COURSE_CTPREFIX = "ctPrefix";
	
	public static final String COURSE_PREFIX = "Prefix";

	public static final String COURSE_SCU = "SCU";
	
	public static final String COURSE_NUM_SECTIONS = "# of Sections";
	
	public static final String COURSE_TYPE = "Course Type";
	
	public static final String COURSE_MAX_ENROLLMENT = "Max Enrollment";
	
	public static final String COURSE_LAB = "Lab";
	
	
	private GreetingServiceAsync service;
	private OsmTable<CourseGWT> table;
	int nextLocationID = 1;
	private String scheduleName;

	public CoursesView(GreetingServiceAsync greetingService, String scheduleName) {
		this.service = greetingService;
		this.scheduleName = scheduleName;
		this.addStyleName("iViewPadding");
	}

	@Override
	public boolean canPop() {
		assert(table != null);
		if (table.isSaved())
			return true;
		return Window.confirm("You have unsaved data which will be lost. Are you sure you want to navigate away?");
	}
	
	@Override
	public void afterPush(ViewFrame frame) {		
		this.setWidth("100%");
		this.setHeight("100%");

		this.add(new HTML("<h2>" + scheduleName + " - Courses</h2>"));

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
				new IFactory<CourseGWT>() {
					public CourseGWT create() {
						return new CourseGWT("", 0, "", 0, 0, 0, "", 0, 0, "", "", 0, 6, new WeekGWT(), 0, 0);
					}
//					@Override
//					public CourseGWT createCopy(CourseGWT object) { return new CourseGWT(object); }
				},
				new OsmTable.ModifyHandler<CourseGWT>() {
					@Override
					public void objectsModified(List<CourseGWT> added,
							List<CourseGWT> edited, List<CourseGWT> removed,
							AsyncCallback<Void> callback) {
						service.saveCourses(added, edited, removed, callback);
					}
				});

		table.addColumn(
				COURSE_NAME,
				"6em",
				new MemberStringComparator<CourseGWT>(new IStaticGetter<CourseGWT, String>() {
					public String getValueForObject(CourseGWT object) { return object.getCourseName(); }
				}),
				new EditingStringColumn<CourseGWT>(
						new IStaticGetter<CourseGWT, String>() {
							public String getValueForObject(CourseGWT object) { return object.getCourseName(); }
						},
						new IStaticSetter<CourseGWT, String>() {
							public void setValueInObject(CourseGWT object, String newValue) { object.setCourseName(newValue); }
						},
						null));
		
		table.addColumn(
				COURSE_CATALOG_NUM,
				"4em",
				new MemberIntegerComparator<CourseGWT>(new IStaticGetter<CourseGWT, Integer>() {
					public Integer getValueForObject(CourseGWT object) { return object.getCatalogNum(); }
				}),
				new IntColumn<CourseGWT>(
						new IStaticGetter<CourseGWT, Integer>() {
							public Integer getValueForObject(CourseGWT object) { return object.getCatalogNum(); }
						},
						new IStaticSetter<CourseGWT, Integer>() {
							public void setValueInObject(CourseGWT object, Integer newValue) { object.setCatalogNum(newValue); }
						}, 
						new IStaticValidator<CourseGWT, Integer>() {
							public void validate(CourseGWT object, Integer newValue) throws InvalidValueException {
								if (newValue < 0)
									throw new InvalidValueException(COURSE_CATALOG_NUM + " must be positive: " + newValue + " is invalid.");
							}
						}));
		
		table.addColumn(
				COURSE_DEPARTMENT,
				"6em",
				new MemberStringComparator<CourseGWT>(new IStaticGetter<CourseGWT, String>() {
					public String getValueForObject(CourseGWT object) { return object.getDept(); }
				}),
				new EditingStringColumn<CourseGWT>(
						new IStaticGetter<CourseGWT, String>() {
							public String getValueForObject(CourseGWT object) { return object.getDept(); }
						},
						new IStaticSetter<CourseGWT, String>() {
							public void setValueInObject(CourseGWT object, String newValue) { object.setDept(newValue); }
						},
						null));
		
		table.addColumn(COURSE_WTU, "4em", null, new IntColumn<CourseGWT>(
				new IStaticGetter<CourseGWT, Integer>() {
					public Integer getValueForObject(CourseGWT object) { return object.getWtu(); }
				}, new IStaticSetter<CourseGWT, Integer>() {
					public void setValueInObject(CourseGWT object, Integer newValue) { object.setWtu(newValue); }
				}, 
				new IStaticValidator<CourseGWT, Integer>() {
					public void validate(CourseGWT object, Integer newValue) throws InvalidValueException {
						if (newValue < 1)
							throw new InvalidValueException(COURSE_WTU + " must be greater than 0: " + newValue + " is invalid.");
					}
				}));
		
		table.addColumn(COURSE_SCU, "4em", null, new IntColumn<CourseGWT>(
				new IStaticGetter<CourseGWT, Integer>() {
					public Integer getValueForObject(CourseGWT object) { return object.getScu(); }
				}, new IStaticSetter<CourseGWT, Integer>() {
					public void setValueInObject(CourseGWT object, Integer newValue) { object.setScu(newValue); }
				}, 
				new IStaticValidator<CourseGWT, Integer>() {
					public void validate(CourseGWT object, Integer newValue) throws InvalidValueException {
						if (newValue < 1)
							throw new InvalidValueException(COURSE_SCU + " must be greater than 0: " + newValue + " is invalid.");
					}
				}));
		
		table.addColumn(COURSE_NUM_SECTIONS, "4em", null, new IntColumn<CourseGWT>(
				new IStaticGetter<CourseGWT, Integer>() {
					public Integer getValueForObject(CourseGWT object) { return object.getNumSections(); }
				}, new IStaticSetter<CourseGWT, Integer>() {
					public void setValueInObject(CourseGWT object, Integer newValue) { object.setNumSections(newValue); }
				}, 
				new IStaticValidator<CourseGWT, Integer>() {
					public void validate(CourseGWT object, Integer newValue) throws InvalidValueException {
						if (newValue < 1)
							throw new InvalidValueException(COURSE_NUM_SECTIONS + " must be greater than 0: " + newValue + " is invalid.");
					}
				}));
		
		table.addColumn(
				COURSE_TYPE,
				"6em",
				new MemberStringComparator<CourseGWT>(new IStaticGetter<CourseGWT, String>() {
					public String getValueForObject(CourseGWT object) { return object.getType(); }
				}),
				new SelectColumn<CourseGWT>(
						new String[] { "LEC", "LAB" },
						new IStaticGetter<CourseGWT, String>() {
							public String getValueForObject(CourseGWT object) { return object.getType(); }
						},
						new IStaticSetter<CourseGWT, String>() {
							public void setValueInObject(CourseGWT object, String newValue) { object.setType(newValue); }
						}));
		
		table.addColumn(COURSE_MAX_ENROLLMENT, "4em", null, new IntColumn<CourseGWT>(
				new IStaticGetter<CourseGWT, Integer>() {
					public Integer getValueForObject(CourseGWT object) { return object.getMaxEnroll(); }
				}, new IStaticSetter<CourseGWT, Integer>() {
					public void setValueInObject(CourseGWT object, Integer newValue) { object.setMaxEnroll(newValue); }
				}, 
				new IStaticValidator<CourseGWT, Integer>() {
					public void validate(CourseGWT object, Integer newValue) throws InvalidValueException {
						if (newValue < 1)
							throw new InvalidValueException(COURSE_MAX_ENROLLMENT + " must be greater than 0: " + newValue + " is invalid.");
					}
				}));
		
		table.addColumn(COURSE_LENGTH, "4em", null, new IntColumn<CourseGWT>(
				new IStaticGetter<CourseGWT, Integer>() {
					public Integer getValueForObject(CourseGWT object) { return object.getLength() / 2; }
				}, new IStaticSetter<CourseGWT, Integer>() {
					public void setValueInObject(CourseGWT object, Integer newValue) { object.setLength(newValue * 2); }
				}, 
				new IStaticValidator<CourseGWT, Integer>() {
					public void validate(CourseGWT object, Integer newValue) throws InvalidValueException {
						if (newValue < 1)
							throw new InvalidValueException(COURSE_LENGTH + " must be greater than 0: " + newValue + " is invalid.");
					}
				}));
		
		table.addColumn(
				COURSE_LAB,
				"4em",
				null,
				new ButtonColumn<CourseGWT>(
						"Lab",
						new ClickCallback<CourseGWT>() {
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
						  		for(CourseGWT c : table.getObjects()){
						  			/*
						  			if(c.getType().equals(LAB) && 
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
		
								if(object.getLabDept().trim().equals(""))
									button.setHTML("(none)");
								else
									button.setHTML(object.getLabDept().trim() + object.getLabCatalogNum());
							}
							}));

		table.addDeleteColumn();
		
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
					nextLocationID = Math.max(nextLocationID, crs.getID() + 1);
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

	@Override
	public void beforePop() { }
	@Override
	public void beforeViewPushedAboveMe() { }
	@Override
	public void afterViewPoppedFromAboveMe() { }
	@Override
	public Widget getContents() { return this; }
}
