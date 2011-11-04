package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Vector;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.ListDataProvider;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DayGWT;
import edu.calpoly.csc.scheduler.view.web.shared.WeekGWT;

public class CTableBuilder implements TableBuilder<CourseGWT>{

	private GreetingServiceAsync service;
	
	public CTableBuilder(GreetingServiceAsync service) {
		this.service = service;
	}
	
	@Override
	public ArrayList<ColumnObject<CourseGWT>> getColumns(
			ListDataProvider<CourseGWT> dataProvider, ListHandler<CourseGWT> sortHandler) {
		
		final ListDataProvider<CourseGWT> fdataProvider = dataProvider;
		
		ArrayList<ColumnObject<CourseGWT>> list = 
				new ArrayList<ColumnObject<CourseGWT>>();
		
		
		// course name		    
		Column<CourseGWT, String> courseName = 
				new Column<CourseGWT, String>(new EditTextCell()) {
		      @Override
		      public String getValue(CourseGWT course) {
		        return course.getCourseName();
		      }
		};
		sortHandler.setComparator(courseName, new Comparator<CourseGWT>() {
	        public int compare(CourseGWT o1, CourseGWT o2) {
	          return o1.getCourseName().compareTo(o2.getCourseName());
	        }
	    });
		courseName.setFieldUpdater(new FieldUpdater<CourseGWT, String>() {
		      public void update(int index, CourseGWT object, String value) {
		        object.setCourseName(value);
		      }
		});
		courseName.setCellStyleNames("tableColumnWidthString");
		list.add(new ColumnObject<CourseGWT>(courseName, TableConstants.COURSE_NAME));
		
		
		// catalog number		    
		Column<CourseGWT, String> catalogNum = 
				new Column<CourseGWT, String>(TableValidate.intValidateCell(TableConstants.COURSE_CATALOG_NUM, true)) {
		      @Override
		      public String getValue(CourseGWT course) {
		        return "" + course.getCatalogNum();
		      }
		};
		sortHandler.setComparator(catalogNum, new Comparator<CourseGWT>() {
	        public int compare(CourseGWT o1, CourseGWT o2) {
	          return o1.getCatalogNum() - o2.getCatalogNum();
	        }
	    });
		catalogNum.setFieldUpdater(new FieldUpdater<CourseGWT, String>() {
		      public void update(int index, CourseGWT object, String value) {
		    	  object.setCatalogNum(TableValidate.positiveInt(value, true));
		      }
		});
		catalogNum.setCellStyleNames("tableColumnWidthInt");
		list.add(new ColumnObject<CourseGWT>(catalogNum, TableConstants.COURSE_CATALOG_NUM));
		
		
		// dept		    
		Column<CourseGWT, String> dept = 
				new Column<CourseGWT, String>(new EditTextCell()) {
		      @Override
		      public String getValue(CourseGWT course) {
		        return course.getDept();
		      }
		};
		sortHandler.setComparator(dept, new Comparator<CourseGWT>() {
	        public int compare(CourseGWT o1, CourseGWT o2) {
	          return o1.getDept().compareTo(o2.getDept());
	        }
	    });
		dept.setFieldUpdater(new FieldUpdater<CourseGWT, String>() {
		      public void update(int index, CourseGWT object, String value) {
		        object.setDept(value);
		      }
		});
		dept.setCellStyleNames("tableColumnWidthInt");
		list.add(new ColumnObject<CourseGWT>(dept, TableConstants.COURSE_DEPARTMENT));
		
		
		// wtu		    
		Column<CourseGWT, String> wtu = 
				new Column<CourseGWT, String>(TableValidate.intValidateCell(TableConstants.COURSE_WTU, false)) {
		      @Override
		      public String getValue(CourseGWT course) {
		        return "" + course.getWtu();
		      }
		};
		sortHandler.setComparator(wtu, new Comparator<CourseGWT>() {
	        public int compare(CourseGWT o1, CourseGWT o2) {
	          return o1.getWtu() - o2.getWtu();
	        }
	    });
		wtu.setFieldUpdater(new FieldUpdater<CourseGWT, String>() {
		      public void update(int index, CourseGWT object, String value) {
		    	  object.setWtu(TableValidate.positiveInt(value, false));
		      }
		});
		wtu.setCellStyleNames("tableColumnWidthInt");
		list.add(new ColumnObject<CourseGWT>(wtu, TableConstants.COURSE_WTU));
		
		
		// stu		    
		Column<CourseGWT, String> stu = 
				new Column<CourseGWT, String>(TableValidate.intValidateCell(TableConstants.COURSE_SCU, false)) {
		      @Override
		      public String getValue(CourseGWT course) {
		        return "" + course.getScu();
		      }
		};
		sortHandler.setComparator(stu, new Comparator<CourseGWT>() {
	        public int compare(CourseGWT o1, CourseGWT o2) {
	          return o1.getScu() - o2.getScu();
	        }
	    });
		stu.setFieldUpdater(new FieldUpdater<CourseGWT, String>() {
		      public void update(int index, CourseGWT object, String value) {
		    	  object.setScu(TableValidate.positiveInt(value, false));
		      }
		});
		stu.setCellStyleNames("tableColumnWidthInt");
		list.add(new ColumnObject<CourseGWT>(stu, TableConstants.COURSE_SCU));
		
		
		// # sections		    
		Column<CourseGWT, String> numSections = 
				new Column<CourseGWT, String>(TableValidate.intValidateCell(TableConstants.COURSE_NUM_SECTIONS, false)) {
		      @Override
		      public String getValue(CourseGWT course) {
		        return "" + course.getNumSections();
		      }
		};
		sortHandler.setComparator(numSections, new Comparator<CourseGWT>() {
	        public int compare(CourseGWT o1, CourseGWT o2) {
	          return o1.getNumSections() - o2.getNumSections();
	        }
	    });
		numSections.setFieldUpdater(new FieldUpdater<CourseGWT, String>() {
		      public void update(int index, CourseGWT object, String value) {
		    	  object.setNumSections(TableValidate.positiveInt(value, false));
		      }
		});
		numSections.setCellStyleNames("tableColumnWidthInt");
		list.add(new ColumnObject<CourseGWT>(numSections, TableConstants.COURSE_NUM_SECTIONS));
		
		
		// course type
		ArrayList<String> typeOptions = new ArrayList<String>();
		typeOptions.add(TableConstants.LEC);
		typeOptions.add(TableConstants.LAB);
		SelectionCell selectionCellType = new SelectionCell(typeOptions);
		Column<CourseGWT, String> type = 
				new Column<CourseGWT, String>(selectionCellType) {
		      @Override
		      public String getValue(CourseGWT course) {
		        return course.getType();
		      }
		};
		sortHandler.setComparator(type, new Comparator<CourseGWT>() {
	        public int compare(CourseGWT o1, CourseGWT o2) {
	          return o1.getType().compareTo(o2.getType());
	        }
	    });
		type.setFieldUpdater(new FieldUpdater<CourseGWT, String>() {
		      public void update(int index, CourseGWT object, String value) {
		        object.setType(value);
		      }
		});
		type.setCellStyleNames("tableColumnWidthString");
		list.add(new ColumnObject<CourseGWT>(type, TableConstants.COURSE_TYPE));
		
		
		// max enroll		    
		Column<CourseGWT, String> maxEnroll = 
				new Column<CourseGWT, String>(TableValidate.intValidateCell(TableConstants.COURSE_MAX_ENROLLMENT, false)) {
		      @Override
		      public String getValue(CourseGWT course) {
		        return "" + course.getMaxEnroll();
		      }
		};
		sortHandler.setComparator(maxEnroll, new Comparator<CourseGWT>() {
	        public int compare(CourseGWT o1, CourseGWT o2) {
	          return o1.getMaxEnroll() - o2.getMaxEnroll();
	        }
	    });
		maxEnroll.setFieldUpdater(new FieldUpdater<CourseGWT, String>() {
		      public void update(int index, CourseGWT object, String value) {
		    	  object.setMaxEnroll(TableValidate.positiveInt(value, false));
		      }
		});
		maxEnroll.setCellStyleNames("tableColumnWidthInt");
		list.add(new ColumnObject<CourseGWT>(maxEnroll, TableConstants.COURSE_MAX_ENROLLMENT));
		
		
		// lab
		ArrayList<String> labOptions = new ArrayList<String>();
		for(CourseGWT c : dataProvider.getList()){
			if(c.getType().equals(TableConstants.LAB) && 
					!c.getDept().trim().equals("")){
				labOptions.add(c.getDept().trim() + c.getCatalogNum());
			}
		}
		SelectionCell selectionCellLab = new SelectionCell(labOptions);
		Column<CourseGWT, String> lab = 
				new Column<CourseGWT, String>(selectionCellLab) {
		      @Override
		      public String getValue(CourseGWT course) {
		        return "";//course.getLab();
		      }
		};
		sortHandler.setComparator(lab, new Comparator<CourseGWT>() {
	        public int compare(CourseGWT o1, CourseGWT o2) {
	          return 0;//o1.getLab().compareTo(o2.getLab());
	        }
	    });
		lab.setFieldUpdater(new FieldUpdater<CourseGWT, String>() {
		      public void update(int index, CourseGWT object, String value) {
		        //object.setLab(value);
		      }
		});
		lab.setCellStyleNames("tableColumnWidthString");
		list.add(new ColumnObject<CourseGWT>(lab, TableConstants.COURSE_LAB));
		
		
		return list;
	}

	@Override
	public String getLabel(CourseGWT object) {
		return object.getDept() + " " + object.getCatalogNum() + ": " + object.getCourseName();
	}

	@Override
	public CourseGWT newObject() {
		CourseGWT course = new CourseGWT();
		course.setCatalogNum(0);
		course.setLabId(0);
		course.setLabPad(0);
		course.setLength(0);
		course.setDept("");
		
		WeekGWT week = new WeekGWT();
		week.setDays(new Vector<DayGWT>());
		course.setDays(week);
		
		course.setQuarterID("");
		course.setScheduleID(0);
		course.setMaxEnroll(1);
		course.setCourseName("");
		course.setNumSections(1);
		course.setScu(1);
		course.setWtu(1);
		course.setType("");
		return course;
	}

	@Override
	public void save(ArrayList<CourseGWT> list) {
		for (CourseGWT course : list)
			course.verify();
		
		service.saveCourses(list, new AsyncCallback<Void>(){
			public void onFailure(Throwable caught){ 
				
				Window.alert("Error saving:\n" + 
						caught.getLocalizedMessage());
			}
			public void onSuccess(Void result){
				Window.alert("Successfully saved");
			}
		});
	}
}
