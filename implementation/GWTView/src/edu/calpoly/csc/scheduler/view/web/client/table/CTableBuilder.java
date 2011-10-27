package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;
import java.util.Comparator;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.calpoly.csc.scheduler.view.web.client.GreetingService;
import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;

public class CTableBuilder implements TableBuilder<CourseGWT>{

	private String quarterID;
	private GreetingServiceAsync service;
	
	public CTableBuilder(String quarterID, GreetingServiceAsync service) {
		this.quarterID = quarterID;
		this.service = service;
	}
	
	@Override
	public ArrayList<ColumnObject<CourseGWT>> getColumns(
			ListHandler<CourseGWT> sortHandler) {
		
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
		list.add(new ColumnObject<CourseGWT>(courseName, TableConstants.COURSE_NAME));
		
		
		// catalog number		    
		Column<CourseGWT, String> catalogNum = 
				new Column<CourseGWT, String>(new EditTextCell()) {
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
		    	  value = value.trim();
		    	  Integer i = null;
		    	  try{
		    		  i = Integer.parseInt(value);
		    	  }catch(Exception e){}
		    	  
		    	  if(i == null){
		    		  Window.alert(TableConstants.COURSE_CATALOG_NUM + " must be a number. \'" + value + "\' is invalid.");
		    	  }
		    	  else{
		    		  object.setCatalogNum(i);
		    	  }
		      }
		});
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
		list.add(new ColumnObject<CourseGWT>(dept, TableConstants.COURSE_DEPARTMENT));
		
		
		// wtu		    
		Column<CourseGWT, String> wtu = 
				new Column<CourseGWT, String>(new EditTextCell()) {
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
		    	  value = value.trim();
		    	  Integer i = null;
		    	  try{
		    		  i = Integer.parseInt(value);
		    	  }catch(Exception e){}
		    	  
		    	  if(i == null){
		    		  Window.alert(TableConstants.COURSE_WTU + " must be a number. \'" + value + "\' is invalid.");
		    	  }
		    	  else{
		    		  object.setWtu(i);
		    	  }
		      }
		});
		list.add(new ColumnObject<CourseGWT>(wtu, TableConstants.COURSE_WTU));
		
		
		// stu		    
		Column<CourseGWT, String> stu = 
				new Column<CourseGWT, String>(new EditTextCell()) {
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
		    	  value = value.trim();
		    	  Integer i = null;
		    	  try{
		    		  i = Integer.parseInt(value);
		    	  }catch(Exception e){}
		    	  
		    	  if(i == null){
		    		  Window.alert(TableConstants.COURSE_STU + " must be a number. \'" + value + "\' is invalid.");
		    	  }
		    	  else{
		    		  object.setScu(i);
		    	  }
		      }
		});
		list.add(new ColumnObject<CourseGWT>(stu, TableConstants.COURSE_STU));
		
		
		// # sections		    
		Column<CourseGWT, String> numSections = 
				new Column<CourseGWT, String>(new EditTextCell()) {
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
		    	  value = value.trim();
		    	  Integer i = null;
		    	  try{
		    		  i = Integer.parseInt(value);
		    	  }catch(Exception e){}
		    	  
		    	  if(i == null){
		    		  Window.alert(TableConstants.COURSE_NUM_SECTIONS + " must be a number. \'" + value + "\' is invalid.");
		    	  }
		    	  else{
		    		  object.setNumSections(i);
		    	  }
		      }
		});
		list.add(new ColumnObject<CourseGWT>(numSections, TableConstants.COURSE_NUM_SECTIONS));
		
		
		// course type		    
		Column<CourseGWT, String> type = 
				new Column<CourseGWT, String>(new EditTextCell()) {
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
		list.add(new ColumnObject<CourseGWT>(type, TableConstants.COURSE_TYPE));
		
		
		// max enroll		    
		Column<CourseGWT, String> maxEnroll = 
				new Column<CourseGWT, String>(new EditTextCell()) {
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
		    	  value = value.trim();
		    	  Integer i = null;
		    	  try{
		    		  i = Integer.parseInt(value);
		    	  }catch(Exception e){}
		    	  
		    	  if(i == null){
		    		  Window.alert(TableConstants.COURSE_MAX_ENROLLMENT + " must be a number. \'" + value + "\' is invalid.");
		    	  }
		    	  else{
		    		  object.setMaxEnroll(i);
		    	  }
		      }
		});
		list.add(new ColumnObject<CourseGWT>(maxEnroll, TableConstants.COURSE_MAX_ENROLLMENT));
		
		
		// lab		    
		Column<CourseGWT, String> lab = 
				new Column<CourseGWT, String>(new EditTextCell()) {
		      @Override
		      public String getValue(CourseGWT course) {
		        return course.getLab();
		      }
		};
		sortHandler.setComparator(lab, new Comparator<CourseGWT>() {
	        public int compare(CourseGWT o1, CourseGWT o2) {
	          return o1.getLab().compareTo(o2.getLab());
	        }
	    });
		lab.setFieldUpdater(new FieldUpdater<CourseGWT, String>() {
		      public void update(int index, CourseGWT object, String value) {
		        object.setLab(value);
		      }
		});
		list.add(new ColumnObject<CourseGWT>(lab, TableConstants.COURSE_LAB));
		
		
		return list;
	}

	@Override
	public String getLabel(CourseGWT object) {
		return object.getDept() + " " + object.getCatalogNum() + ": " + object.getCourseName();
	}

	@Override
	public CourseGWT newObject() {
		return new CourseGWT();
	}

	@Override
	public void save(ArrayList<CourseGWT> list) {
		
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
