package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.CourseCache;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;
import edu.calpoly.csc.scheduler.view.web.client.table.IFactory;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticSetter;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticValidator;
import edu.calpoly.csc.scheduler.view.web.client.table.MemberStringComparator;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingIntColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingSelectColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingStringColumn;
import edu.calpoly.csc.scheduler.view.web.client.views.AssociationsCell.GetCoursesCallback;
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
	
	
	private CourseCache courseCache;
	private OsmTable<CourseGWT> table;
	int nextCourseID = -2;
	private String scheduleName;

	private int generateTemporaryCourseID() {
		return nextCourseID--;
	}
	
	public CoursesView(CourseCache courseCache, String scheduleName) {
		this.courseCache = courseCache;
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
		
		table = new OsmTable<CourseGWT>(
				new IFactory<CourseGWT>() {
					public CourseGWT create() {
						return new CourseGWT("", "", "", 0, 0, 0, "LEC", 0, -1, 6, new WeekGWT(), 0, generateTemporaryCourseID(), false);
					}
				},
				new OsmTable.ModifyHandler<CourseGWT>() {
					public void add(CourseGWT toAdd, AsyncCallback<CourseGWT> callback) {
						courseCache.addCourse(toAdd, callback);
					}
					public void edit(CourseGWT toEdit, AsyncCallback<Void> callback) {
						courseCache.editCourse(toEdit, callback);
					}
					public void remove(CourseGWT toRemove, AsyncCallback<Void> callback) {
						courseCache.removeCourse(toRemove, callback);
					}
				});

		table.addEditSaveColumn();
		table.addDeleteColumn();

		table.addColumn(
				COURSE_DEPARTMENT,
				"6em",
				true,
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
		
		table.addColumn(
				COURSE_CATALOG_NUM,
				"4em",
				true,
				new MemberStringComparator<CourseGWT>(new IStaticGetter<CourseGWT, String>() {
					public String getValueForObject(CourseGWT object) { return object.getCatalogNum(); }
				}),
				new EditingStringColumn<CourseGWT>(
						new IStaticGetter<CourseGWT, String>() {
							public String getValueForObject(CourseGWT object) { return object.getCatalogNum(); }
						},
						new IStaticSetter<CourseGWT, String>() {
							public void setValueInObject(CourseGWT object, String newValue) { object.setCatalogNum(newValue); }
						}, 
						new IStaticValidator<CourseGWT, String>() {
							public void validate(CourseGWT object, String newValue) throws InvalidValueException {
								if (newValue.trim().equals(""))
									throw new InvalidValueException(COURSE_CATALOG_NUM + " must be present.");
							}
						}));

		table.addColumn(
				COURSE_NAME,
				"6em",
				true,
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
		
		table.addColumn(COURSE_WTU, "4em", true, null, new EditingIntColumn<CourseGWT>(
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
		
		table.addColumn(COURSE_SCU, "4em", true, null, new EditingIntColumn<CourseGWT>(
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
		
		table.addColumn(COURSE_NUM_SECTIONS, "4em", true, null, new EditingIntColumn<CourseGWT>(
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
				true,
				new MemberStringComparator<CourseGWT>(new IStaticGetter<CourseGWT, String>() {
					public String getValueForObject(CourseGWT object) { return object.getType(); }
				}),
				new EditingSelectColumn<CourseGWT>(
						new String[] { "LEC", "LAB" },
						new IStaticGetter<CourseGWT, String>() {
							public String getValueForObject(CourseGWT object) { return object.getType(); }
						},
						new IStaticSetter<CourseGWT, String>() {
							public void setValueInObject(CourseGWT object, String newValue) {
								object.setType(newValue);
							}
						}));
		
		table.addColumn(COURSE_MAX_ENROLLMENT, "4em", true, null, new EditingIntColumn<CourseGWT>(
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
		
		table.addColumn(COURSE_LENGTH, "4em", true, null, new EditingIntColumn<CourseGWT>(
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
				"Associations",
				"4em",
				true,
				null,
				new AssociationsColumn(new GetCoursesCallback() {
					public ArrayList<CourseGWT> getCourses() {
						return courseCache.getCourses();
					}
				}));

		this.add(table);
		
		courseCache.getCourses(new AsyncCallback<List<CourseGWT>>() {
			public void onFailure(Throwable caught) {
				popup.hide();
				Window.alert("Failed to get courses: " + caught.toString());
			}
			
			public void onSuccess(List<CourseGWT> result){
				assert(result != null);
				popup.hide();
				table.addRows(result);
			}
		});
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
