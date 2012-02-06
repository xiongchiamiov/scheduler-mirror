package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticValidator;
import edu.calpoly.csc.scheduler.view.web.client.table.MemberStringComparator;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.ObjectChangedObserver;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.DeleteColumn.DeleteObserver;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingIntColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingMultiselectColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingSelectColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingStringColumn;
import edu.calpoly.csc.scheduler.view.web.client.views.AssociationsCell.GetCoursesCallback;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DayCombinationGWT;

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
	
	
//	private CourseCache courseCache;
	private GreetingServiceAsync service;
	private String scheduleName;
	private OsmTable<CourseGWT> table;
	final ArrayList<CourseGWT> tableCourses = new ArrayList<CourseGWT>();
	int nextTableCourseID = -2;
	int transactionsPending = 0;
	Map<Integer, Integer> realIDsByTableID = new HashMap<Integer, Integer>();
	
	final ArrayList<Integer> deletedTableCourseIDs = new ArrayList<Integer>();
	final ArrayList<CourseGWT> editedTableCourses = new ArrayList<CourseGWT>();
	final ArrayList<CourseGWT> addedTableCourses = new ArrayList<CourseGWT>();

	private void onTableCourseAdded(CourseGWT course) {
		tableCourses.add(course);
		addedTableCourses.add(course);
		
		assert(!editedTableCourses.contains(course));
		
		assert(!deletedTableCourseIDs.contains(course));

		sendUpdates();
	}
	
	private void onTableCourseEdited(CourseGWT course) {
		assert(!deletedTableCourseIDs.contains(course.getID()));
		
		if (realIDsByTableID.containsKey(course.getID())) {
			// exists on remote side
			if (!editedTableCourses.contains(course))
				editedTableCourses.add(course);
		}
		else {
			// doesnt exist on remote side
			// do nothing, its already on the add list.
			assert(addedTableCourses.contains(course));
		}
		
		sendUpdates();
	}
	
	private void onTableCourseDeleted(CourseGWT course) {
		tableCourses.remove(course);
		editedTableCourses.remove(course);
		
		if (addedTableCourses.contains(course)) {
			addedTableCourses.remove(course);
			return;
		}
		
		assert(!deletedTableCourseIDs.contains(course.getID()));
		deletedTableCourseIDs.add(course.getID());
		
		sendUpdates();
	}
	
	private void sendUpdates() {
		assert(transactionsPending == 0);
		transactionsPending = deletedTableCourseIDs.size() + editedTableCourses.size() + addedTableCourses.size();
		if (transactionsPending == 0)
			return;
		
		for (Integer deletedTableCourseID : deletedTableCourseIDs) {
			Integer realCourseID = realIDsByTableID.get(deletedTableCourseID);
			service.removeCourse(realCourseID, new AsyncCallback<Void>() {
				public void onSuccess(Void result) { updateFinished(); }
				public void onFailure(Throwable caught) { Window.alert("Update failed: " + caught.getMessage()); }
			});
		}
		
		for (CourseGWT editedTableCourse : editedTableCourses) {
			Integer realCourseID = realIDsByTableID.get(editedTableCourse.getID());
			CourseGWT realCourse = new CourseGWT(editedTableCourse);
			realCourse.setID(realCourseID);
			service.editCourse(realCourse, new AsyncCallback<Void>() {
				public void onSuccess(Void result) { updateFinished(); }
				public void onFailure(Throwable caught) { Window.alert("Update failed: " + caught.getMessage()); }
			});
		}
		
		for (CourseGWT addedTableCourse : addedTableCourses) {
			final int tableCourseID = addedTableCourse.getID();
			CourseGWT realCourse = new CourseGWT(addedTableCourse);
			realCourse.setID(-1);
			service.addCourse(realCourse, new AsyncCallback<CourseGWT>() {
				public void onSuccess(CourseGWT result) {
					realIDsByTableID.put(tableCourseID, result.getID());
					updateFinished();
				}
				public void onFailure(Throwable caught) { Window.alert("Update failed: " + caught.getMessage()); }
			});
		}
		
		deletedTableCourseIDs.clear();
		editedTableCourses.clear();
		addedTableCourses.clear();
	}
	
	private void updateFinished() {
		assert(transactionsPending > 0);
		transactionsPending--;
		if (transactionsPending == 0)
			sendUpdates();
	}
	
	private int generateTableCourseID() {
		return nextTableCourseID--;
	}
	
	public CoursesView(GreetingServiceAsync service, String scheduleName) {
		this.service = service;
		this.scheduleName = scheduleName;
		this.addStyleName("iViewPadding");
	}

	@Override
	public boolean canPop() {
		return true;
//		assert(table != null);
//		if (table.isSaved())
//			return true;
//		return Window.confirm("You have unsaved data which will be lost. Are you sure you want to navigate away?");
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
						CourseGWT newCourse = new CourseGWT("", "", "", 0, 0, 0, "LEC", 0, -1, 6, new HashSet<DayCombinationGWT>(), 0, generateTableCourseID(), false);
						onTableCourseAdded(newCourse);
						return newCourse;
					}
				});
		
		table.setObjectChangedObserver(new ObjectChangedObserver<CourseGWT>() {
			public void objectChanged(final CourseGWT object) {
				onTableCourseEdited(object);
			}
		});

		table.addDeleteColumn(new DeleteObserver<CourseGWT>() {
			@Override
			public void afterDelete(CourseGWT object) {
				onTableCourseDeleted(object);
			}
		});

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
		
		table.addColumn("Day Combinations", null, true, null, new EditingMultiselectColumn<CourseGWT>(
				new String[] {
						"M",
						"Tu",
						"W",
						"Th",
						"F",
						"MW",
						"TuTh",
						"MWF",
						"TuThF",
						"MTuThF"
				},
				new IStaticGetter<CourseGWT, Set<String>>() {
					public Set<String> getValueForObject(CourseGWT object) {
						Set<String> result = new HashSet<String>();
						for (DayCombinationGWT combo : object.getDays())
							result.add(combo.toString());
						return result;
					}
				},
				new IStaticSetter<CourseGWT, Set<String>>() {
					public void setValueInObject(CourseGWT object, Set<String> newCombos) {
						Set<DayCombinationGWT> set = new HashSet<DayCombinationGWT>();
						for (String newCombo : newCombos)
							set.add(DayCombinationGWT.fromString(newCombo));
						object.setDays(set);
					}
				}));
		
		table.addColumn(
				"Associations",
				"4em",
				true,
				null,
				new AssociationsColumn(new GetCoursesCallback() {
					public ArrayList<CourseGWT> getCourses() {
						return tableCourses;
					}
				}));

		this.add(table);
		
		service.getCourses(new AsyncCallback<List<CourseGWT>>() {
			public void onFailure(Throwable caught) {
				popup.hide();
				Window.alert("Failed to get courses: " + caught.toString());
			}
			
			public void onSuccess(List<CourseGWT> coursesInCache){
				assert(coursesInCache != null);
				popup.hide();
				
				assert(tableCourses.isEmpty());
				for (CourseGWT courseInCache : coursesInCache)
					tableCourses.add(new CourseGWT(courseInCache));
				
				table.addRows(tableCourses);
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
