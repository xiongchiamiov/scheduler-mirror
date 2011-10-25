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

	private static final GreetingServiceAsync service = GWT
			.create(GreetingService.class);
	
	@Override
	public ArrayList<ColumnObject<CourseGWT>> getColumns(
			ListHandler<CourseGWT> sortHandler) {
		
		ArrayList<ColumnObject<CourseGWT>> list = 
				new ArrayList<ColumnObject<CourseGWT>>();
		
		// id		    
		Column<CourseGWT, String> id = 
				new Column<CourseGWT, String>(new EditTextCell()) {
		      @Override
		      public String getValue(CourseGWT course) {
		        return "" + course.getID();
		      }
		};
		sortHandler.setComparator(id, new Comparator<CourseGWT>() {
	        public int compare(CourseGWT o1, CourseGWT o2) {
	          return o1.getID() - o2.getID();
	        }
	    });
		id.setFieldUpdater(new FieldUpdater<CourseGWT, String>() {
		      public void update(int index, CourseGWT object, String value) {
		    	  value = value.trim();
		    	  Integer i = null;
		    	  try{
		    		  i = Integer.parseInt(value);
		    	  }catch(Exception e){}
		    	  
		    	  if(i == null){
		    		  Window.alert(TableConstants.COURSE_ID + " must be a number. \'" + value + "\' is invalid.");
		    	  }
		    	  else{
		    		  object.setId(i);
		    	  }
		      }
		});
		list.add(new ColumnObject<CourseGWT>(id, TableConstants.COURSE_ID));
		
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
