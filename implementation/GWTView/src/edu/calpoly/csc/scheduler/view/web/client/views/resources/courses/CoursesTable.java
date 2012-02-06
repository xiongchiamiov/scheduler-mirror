package edu.calpoly.csc.scheduler.view.web.client.views.resources.courses;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;

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
import edu.calpoly.csc.scheduler.view.web.client.views.resources.courses.AssociationsCell.GetCoursesCallback;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DayCombinationGWT;

public class CoursesTable extends SimplePanel {
	private static final String COURSE_NAME = "Course Name";
	private static final String COURSE_CATALOG_NUM = "Catalog Number";
	private static final String COURSE_DEPARTMENT = "Department";
	private static final String COURSE_WTU = "WTU";
	private static final String COURSE_LENGTH = "Hours Per Week";
	private static final String COURSE_SCU = "SCU";
	private static final String COURSE_NUM_SECTIONS = "# of Sections";
	private static final String COURSE_TYPE = "Course Type";
	private static final String COURSE_MAX_ENROLLMENT = "Max Enrollment";
	
	public interface Strategy {
		void getAllCourses(AsyncCallback<List<CourseGWT>> callback);
		CourseGWT createCourse();
		void onCourseEdited(CourseGWT course);
		void onCourseDeleted(CourseGWT course);
	}
	
	final OsmTable<CourseGWT> table;
	final Strategy strategy;
	final ArrayList<CourseGWT> tableCourses = new ArrayList<CourseGWT>();
	
	public CoursesTable(Strategy strategy_) {
		this.strategy = strategy_;
		
		table = new OsmTable<CourseGWT>(
				new IFactory<CourseGWT>() {
					public CourseGWT create() {
						CourseGWT newCourse = strategy.createCourse();
						tableCourses.add(newCourse);
						return newCourse;
					}
				});
		
		table.setObjectChangedObserver(new ObjectChangedObserver<CourseGWT>() {
			public void objectChanged(final CourseGWT object) {
				strategy.onCourseEdited(object);
			}
		});

		table.addDeleteColumn(new DeleteObserver<CourseGWT>() {
			@Override
			public void afterDelete(CourseGWT object) {
				tableCourses.remove(object);
				strategy.onCourseDeleted(object);
			}
		});
		
		addFieldColumns();

		this.add(table);
	}

	@Override
	public void onLoad() {
		strategy.getAllCourses(new AsyncCallback<List<CourseGWT>>() {
			public void onFailure(Throwable caught) {
				Window.alert("Failed to get courses: " + caught.toString());
			}
			
			public void onSuccess(List<CourseGWT> courses){
				assert(tableCourses.isEmpty());
				for (CourseGWT course : courses)
					tableCourses.add(new CourseGWT(course));
				
				table.addRows(tableCourses);
			}
		});
	}
	
	void addFieldColumns() {
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
						"M", "Tu", "W", "Th", "F",
						"MW", "MF", "WF", "TuTh",
						"MWF",
						"TuWThF", "MWThF", "MTuThF", "MTuWTh"
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
	}
}
