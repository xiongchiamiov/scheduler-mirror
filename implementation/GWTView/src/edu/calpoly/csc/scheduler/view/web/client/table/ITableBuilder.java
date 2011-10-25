package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;
import java.util.Comparator;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.calpoly.csc.scheduler.view.web.client.GreetingService;
import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;

public class ITableBuilder implements TableBuilder<InstructorGWT>{

	private static final GreetingServiceAsync service = GWT
			.create(GreetingService.class);
	
	@Override
	public ArrayList<ColumnObject<InstructorGWT>> getColumns(
			ListHandler<InstructorGWT> sortHandler) {
		
		ArrayList<ColumnObject<InstructorGWT>> list = 
				new ArrayList<ColumnObject<InstructorGWT>>();
		
		// first name		    
		Column<InstructorGWT, String> firstName = 
				new Column<InstructorGWT, String>(new EditTextCell()) {
		      @Override
		      public String getValue(InstructorGWT instr) {
		        return instr.getFirstName();
		      }
		};
		sortHandler.setComparator(firstName, new Comparator<InstructorGWT>() {
	        public int compare(InstructorGWT o1, InstructorGWT o2) {
	          return o1.getFirstName().compareTo(o2.getFirstName());
	        }
	    });
		firstName.setFieldUpdater(new FieldUpdater<InstructorGWT, String>() {
		      public void update(int index, InstructorGWT object, String value) {
		        object.setFirstName(value);
		      }
		});
		list.add(new ColumnObject<InstructorGWT>(firstName, TableConstants.INSTR_FIRSTNAME));
		
		// last name
		Column<InstructorGWT, String> lastName = 
				new Column<InstructorGWT, String>(new EditTextCell()) {
		      @Override
		      public String getValue(InstructorGWT instr) {
		        return instr.getLastName();
		      }
		};
		sortHandler.setComparator(lastName, new Comparator<InstructorGWT>() {
	        public int compare(InstructorGWT o1, InstructorGWT o2) {
	          return o1.getLastName().compareTo(o2.getLastName());
	        }
	    });
		lastName.setFieldUpdater(new FieldUpdater<InstructorGWT, String>() {
		      public void update(int index, InstructorGWT object, String value) {
		        object.setLastName(value);
		      }
		});
		list.add(new ColumnObject<InstructorGWT>(lastName, TableConstants.INSTR_LASTNAME));
		
		// id
		Column<InstructorGWT, String> id = 
				new Column<InstructorGWT, String>(new EditTextCell()) {
		      @Override
		      public String getValue(InstructorGWT instr) {
		        return instr.getUserID();
		      }
		};
		sortHandler.setComparator(id, new Comparator<InstructorGWT>() {
	        public int compare(InstructorGWT o1, InstructorGWT o2) {
	          return o1.getUserID().compareTo(o2.getUserID());
	        }
	    });
		id.setFieldUpdater(new FieldUpdater<InstructorGWT, String>() {
		      public void update(int index, InstructorGWT object, String value) {
		        object.setUserID(value);
		      }
		});
		list.add(new ColumnObject<InstructorGWT>(id, TableConstants.INSTR_ID));
		
		// wtu
		Column<InstructorGWT, String> wtu = 
				new Column<InstructorGWT, String>(new EditTextCell()) {
		      @Override
		      public String getValue(InstructorGWT instr) {
		        return "" + instr.getWtu();
		      }
		};
		sortHandler.setComparator(wtu, new Comparator<InstructorGWT>() {
	        public int compare(InstructorGWT o1, InstructorGWT o2) {
	          return o1.getWtu() - o2.getWtu();
	        }
	    });
		wtu.setFieldUpdater(new FieldUpdater<InstructorGWT, String>() {
		      public void update(int index, InstructorGWT object, String value) {
		        
		    	  value = value.trim();
		    	  Integer i = null;
		    	  try{
		    		  i = Integer.parseInt(value);
		    	  }catch(Exception e){}
		    	  
		    	  if(i == null){
		    		  Window.alert(TableConstants.INSTR_WTU + " must be a number. \'" + value + "\' is invalid.");
		    	  }
		    	  else{
		    		  object.setWtu(i);
		    	  }
		      }
		});
		list.add(new ColumnObject<InstructorGWT>(wtu, TableConstants.INSTR_WTU));
		
		// building
		Column<InstructorGWT, String> building = 
				new Column<InstructorGWT, String>(new EditTextCell()) {
		      @Override
		      public String getValue(InstructorGWT instr) {
		        return instr.getBuilding();
		      }
		};
		sortHandler.setComparator(building, new Comparator<InstructorGWT>() {
	        public int compare(InstructorGWT o1, InstructorGWT o2) {
	          return o1.getBuilding().compareTo(o2.getBuilding());
	        }
	    });
		building.setFieldUpdater(new FieldUpdater<InstructorGWT, String>() {
		      public void update(int index, InstructorGWT object, String value) {
		        object.setBuilding(value);
		      }
		});
		list.add(new ColumnObject<InstructorGWT>(building, TableConstants.INSTR_BUILDING));
		
		// room number
		Column<InstructorGWT, String> roomNum = 
				new Column<InstructorGWT, String>(new EditTextCell()) {
		      @Override
		      public String getValue(InstructorGWT instr) {
		        return instr.getRoomNumber();
		      }
		};
		sortHandler.setComparator(roomNum, new Comparator<InstructorGWT>() {
	        public int compare(InstructorGWT o1, InstructorGWT o2) {
	          return o1.getRoomNumber().compareTo(o2.getRoomNumber());
	        }
	    });
		roomNum.setFieldUpdater(new FieldUpdater<InstructorGWT, String>() {
		      public void update(int index, InstructorGWT object, String value) {
		        object.setRoomNumber(value);
		      }
		});
		list.add(new ColumnObject<InstructorGWT>(roomNum, TableConstants.INSTR_ROOMNUMBER));
		
		// disability
		Column<InstructorGWT, Boolean> disable = 
				new Column<InstructorGWT, Boolean>(new CheckboxCell(true, false)) {
		      @Override
		      public Boolean getValue(InstructorGWT instr) {
		        return instr.getDisabilities();
		      }
		};
		sortHandler.setComparator(disable, new Comparator<InstructorGWT>() {
	        public int compare(InstructorGWT o1, InstructorGWT o2) {
	          
	        	boolean b1 = o1.getDisabilities();
	        	boolean b2 = o2.getDisabilities();
	        	if(b1 == b2){ return 0;}
	        	if(b2){ return -1;}
	        	return 1;
	        }
	    });
		disable.setFieldUpdater(new FieldUpdater<InstructorGWT, Boolean>() {
		      public void update(int index, InstructorGWT object, Boolean value) {
		        object.setDisabilities(value);
		      }
		});
		list.add(new ColumnObject<InstructorGWT>(disable, TableConstants.INSTR_DISABILITIES));
		

		return list;
	}

	@Override
	public String getLabel(InstructorGWT object) {
		return object.getFirstName() + " " + object.getLastName();
	}

	@Override
	public InstructorGWT newObject() {
		return new InstructorGWT();
	}

	@Override
	public void save(ArrayList<InstructorGWT> list) {
	
		service.saveInstructors(list, new AsyncCallback<Void>(){
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
