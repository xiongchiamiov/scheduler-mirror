package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.views.InstructorPreferencesView;
import edu.calpoly.csc.scheduler.view.web.client.views.InstructorsView;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DayGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;
import edu.calpoly.csc.scheduler.view.web.shared.TimeGWT;
import edu.calpoly.csc.scheduler.view.web.shared.TimePreferenceGWT;

public class ITableBuilder implements TableBuilder<InstructorGWT>{

	private GreetingServiceAsync service;
	
	public ITableBuilder(GreetingServiceAsync service) {
		this.service = service;
	}
	
	@Override
	public ArrayList<ColumnObject<InstructorGWT>> getColumns(Widget hidden,
			ListDataProvider<InstructorGWT> dataProvider, ListHandler<InstructorGWT> sortHandler) {
		
		final ListDataProvider<InstructorGWT> fdataProvider = dataProvider;
		
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
		firstName.setCellStyleNames("tableColumnWidthString");
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
		lastName.setCellStyleNames("tableColumnWidthString");
		list.add(new ColumnObject<InstructorGWT>(lastName, TableConstants.INSTR_LASTNAME));
		
		// id
		Column<InstructorGWT, String> id = 
				new Column<InstructorGWT, String>(TableValidate.instrIDValidateCell(fdataProvider)) {
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
		id.setCellStyleNames("tableColumnWidthString");
		list.add(new ColumnObject<InstructorGWT>(id, TableConstants.INSTR_ID));
		
		// max wtu
		Column<InstructorGWT, String> maxwtu = 
				new Column<InstructorGWT, String>(TableValidate.intValidateCell(TableConstants.INSTR_MAX_WTU, true)) {
		      @Override
		      public String getValue(InstructorGWT instr) {
		        return "" + instr.getMaxWtu();
		      }
		};
		sortHandler.setComparator(maxwtu, new Comparator<InstructorGWT>() {
	        public int compare(InstructorGWT o1, InstructorGWT o2) {
	          return o1.getMaxWtu() - o2.getMaxWtu();
	        }
	    });
		maxwtu.setFieldUpdater(new FieldUpdater<InstructorGWT, String>() {
		      public void update(int index, InstructorGWT object, String value) {
		    	  object.setMaxWtu(TableValidate.positiveInt(value, true));
		      }
		});
		maxwtu.setCellStyleNames("tableColumnWidthInt");
		list.add(new ColumnObject<InstructorGWT>(maxwtu, TableConstants.INSTR_MAX_WTU));
		
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
		building.setCellStyleNames("tableColumnWidthInt");
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
		roomNum.setCellStyleNames("tableColumnWidthInt");
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
		disable.setCellStyleNames("tableColumnWidthInt");
		list.add(new ColumnObject<InstructorGWT>(disable, TableConstants.INSTR_DISABILITIES));
		
		// preferences
		Column<InstructorGWT, String> preferences = 
				new Column<InstructorGWT, String>(new ButtonCell()) {
		      @Override
		      public String getValue(InstructorGWT instr) {
		        return TableConstants.INSTR_PREFERENCES;
		      }
		};
		preferences.setFieldUpdater(new FieldUpdater<InstructorGWT, String>() {
		      public void update(int index, InstructorGWT object, String value) {
		    	InstructorsView.container.clear();
		    	InstructorsView.container.add(new InstructorPreferencesView(InstructorsView.container, InstructorsView.service, object));
		      }
		});
		preferences.setCellStyleNames("tableColumnWidthString");
		list.add(new ColumnObject<InstructorGWT>(preferences, TableConstants.INSTR_PREFERENCES));
		

		return list;
	}

	@Override
	public String getLabel(InstructorGWT object) {
		return object.getFirstName() + " " + object.getLastName();
	}

	@Override
	public InstructorGWT newObject() {
		InstructorGWT instructor = new InstructorGWT();
		instructor.setUserID("");
		instructor.setFirstName("");
		instructor.setLastName("");
		instructor.setRoomNumber("");
		instructor.setBuilding("");
		instructor.setCoursePreferences(new HashMap<CourseGWT, Integer>());
		instructor.settPrefs(new HashMap<DayGWT, Map<TimeGWT,TimePreferenceGWT>>());
		instructor.setItemsTaught(new Vector<ScheduleItemGWT>());
		instructor.verify();
		return instructor;
	}

	@Override
	public void save(ArrayList<InstructorGWT> list) {
		boolean valid = true;
		for (InstructorGWT instructor : list){
		
			instructor.verify();
			if(instructor.getUserID().trim().equals("")){
				valid = false;
			}
		}
		
		if(!valid){
			Window.alert("ID cannot be empty");
		}
		else{
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
	
	
	/**
	 * Panel for creating a new object
	 * @author David Seltzer
	 *
	 */
	public class NewIPanel implements NewObjPanel<InstructorGWT>{

		private Grid grid;
		private InstructorGWT instr;
		
		// error
		private String error;
		
		// input fields
		private TextBox firstName = new TextBox();
		private TextBox lastName = new TextBox();
		private TextBox id = new TextBox();
		private TextBox maxwtu = new TextBox();
		private TextBox building = new TextBox();
		private TextBox room = new TextBox();
		private CheckBox disable = new CheckBox();
		
		public NewIPanel(){
			instr = newObject();
			
			// build grid
			grid = new Grid(2, 7);
			
			// headers
			grid.setWidget(0, 0, new Label(TableConstants.INSTR_FIRSTNAME));
			grid.setWidget(0, 1, new Label(TableConstants.INSTR_LASTNAME));
			grid.setWidget(0, 2, new Label(TableConstants.INSTR_ID));
			grid.setWidget(0, 3, new Label(TableConstants.INSTR_MAX_WTU));
			grid.setWidget(0, 4, new Label(TableConstants.INSTR_BUILDING));
			grid.setWidget(0, 5, new Label(TableConstants.INSTR_ROOMNUMBER));
			grid.setWidget(0, 6, new Label(TableConstants.INSTR_DISABILITIES));
			
			// input fields
			grid.setWidget(1, 0, firstName);
			grid.setWidget(1, 1, lastName);
			grid.setWidget(1, 2, id);
			grid.setWidget(1, 3, maxwtu);
			grid.setWidget(1, 4, building);
			grid.setWidget(1, 5, room);
			grid.setWidget(1, 6, disable);
			
		}
		
		@Override
		public Grid getGrid() {
			return grid;
		}

		@Override
		public InstructorGWT getObject(ListDataProvider<InstructorGWT> dataProvider) {
			
			// update object with input values
			instr.setFirstName(firstName.getText().trim());
			instr.setLastName(lastName.getText().trim());
			instr.setUserID(id.getText().trim());
			Integer wtu = Table.parseInt(maxwtu.getText());
			if(wtu == null){ wtu = -1; }
			instr.setMaxWtu(wtu);
			instr.setBuilding(building.getText().trim());
			instr.setRoomNumber(room.getText().trim());
			instr.setDisabilities(disable.getValue());
			
			// validate
			error = validate(dataProvider);
			if(error != null){
				return null;
			}
			
			return instr;
		}
		
		@Override
		public void focus(){
			firstName.setFocus(true);
		}

		@Override
		public String getError() {
			return error;
		}
		
		// validation
		private String validate(ListDataProvider<InstructorGWT> dataProvider){
			
			// check for duplicate id
			String id = instr.getUserID().trim();
	    	List<InstructorGWT> list = dataProvider.getList();
	    	for(int i = 0; i < list.size(); i++){
	    		  
	    		if(list.get(i).getUserID().trim().equals(id)){
	    			return "There is already a user with the ID: \'" + id + "\'";
	    		}
	    	}
			
	    	// check max wtu
	    	int wtu = instr.getMaxWtu();
	    	if(wtu < 0){
	    		return TableConstants.INSTR_MAX_WTU + " must be a non-negative number";
	    	}
	    	
	    	// no errors
			return null;
		}
	}


	@Override
	public NewObjPanel<InstructorGWT> newObjPanel() {
		return new NewIPanel();
	}
}
