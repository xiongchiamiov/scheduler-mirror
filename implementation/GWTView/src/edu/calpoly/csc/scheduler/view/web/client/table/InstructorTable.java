package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;

import edu.calpoly.csc.scheduler.view.web.client.GreetingService;
import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;

public class InstructorTable {

	private static final GreetingServiceAsync service = GWT
			.create(GreetingService.class);
	
	private VerticalPanel mainPanel;
	private HorizontalPanel showColPanel;
	private CellTable<InstructorGWT> table;
	private ListDataProvider<InstructorGWT> dataProvider;
	private ListHandler<InstructorGWT> sortHandler;
	private Button saveButton, addButton;
	
	/**
	 * Create an instructor table
	 */
	public InstructorTable(){
		
		// Create table objects
		dataProvider = new ListDataProvider<InstructorGWT>();
		table = new CellTable<InstructorGWT>();
	    table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

	    sortHandler =
	            new ListHandler<InstructorGWT>(dataProvider.getList());
	    table.addColumnSortHandler(sortHandler);
	    
	    // hide/show columns
	    showColPanel = new HorizontalPanel(); 
	    
	    // create columns
	    removeColumn();
	    createColumns();
	    
	    // create save button
	    saveButton();
	    
	    // add button
	    addButton();

	    // add everything to panel
	    dataProvider.addDataDisplay(table);
	    
	    HorizontalPanel buttonPanel = new HorizontalPanel();
	    buttonPanel.add(saveButton);
	    buttonPanel.add(addButton);
	    buttonPanel.setSpacing(14);
	    
	    mainPanel = new VerticalPanel();
	    mainPanel.add(buttonPanel);
	    mainPanel.add(showColPanel);
	    mainPanel.add(table);
	    mainPanel.setSpacing(14);
	}
	
	
	/**
	 * Add new row button
	 */
	private void addButton(){
		
		addButton = new Button("+ New");
		addButton.addStyleName("tableButton");
		
		addButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				
				add(new InstructorGWT());
			}
		});
	}
	
	
	/**
	 * Save button for the table
	 * @return
	 */
	private void saveButton(){
		
		saveButton = new Button("Save");
		saveButton.setEnabled(false);
		saveButton.addStyleName("tableButton");
		
		saveButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				
				saveButton.setEnabled(false);
				
				ArrayList<InstructorGWT> list = new ArrayList<InstructorGWT>();
				for(InstructorGWT i : dataProvider.getList()){
					list.add(i);
				}
				
				service.saveInstructors(list, new AsyncCallback<Void>(){
					public void onFailure(Throwable caught){ 
						
						saveButton.setEnabled(true);
						
						Window.alert("Error saving instructors:\n" + 
								caught.getLocalizedMessage());
					}
					public void onSuccess(Void result){
						Window.alert("Successfully saved");
					}
				});
			}
		});
	}
	
	
	/**
	 * Add an object to the table
	 * @param instructor
	 */
	public void add(InstructorGWT instructor){
		
		ArrayList<InstructorGWT> newList = new ArrayList<InstructorGWT>();
		newList.add(instructor);
		
		List<InstructorGWT> list = dataProvider.getList();
		int oldSize = list.size();
		list.add(instructor);
		
		table.setRowCount(list.size(), true);
		table.setRowData(oldSize, newList);
	}
	
	
	/**
	 * Set the list of objects to the table (clears current entries)
	 * @param instructors
	 */
	public void set(ArrayList<InstructorGWT> instructors){
		
		List<InstructorGWT> list = dataProvider.getList();
		list.clear();
		list.addAll(instructors);
		table.setRowCount(list.size(), true);
		table.setRowData(0, list);
	}
	
	
	/**
	 * Clear the table
	 */
	public void clear(){
		dataProvider.getList().clear();
		table.setRowCount(0, true);
	}
	
	
	/**
	 * Get the widget representing this table
	 * @return
	 */
	public VerticalPanel getWidget(){
		return mainPanel;
	}
	
	
	/**
	 * Create columns for the table
	 */
	private void createColumns(){
		
		// first name		    
		Column<InstructorGWT, String> firstName = 
				new Column<InstructorGWT, String>(new EditTextCell()) {
		      @Override
		      public String getValue(InstructorGWT instr) {
		        return instr.getFirstName();
		      }
		};
		firstName.setSortable(true);
		sortHandler.setComparator(firstName, new Comparator<InstructorGWT>() {
	        public int compare(InstructorGWT o1, InstructorGWT o2) {
	          return o1.getFirstName().compareTo(o2.getFirstName());
	        }
	    });
		firstName.setFieldUpdater(new FieldUpdater<InstructorGWT, String>() {
		      public void update(int index, InstructorGWT object, String value) {
		        object.setFirstName(value);
		        saveButton.setEnabled(true);
		      }
		    });
		table.addColumn(firstName, TableConstants.INSTR_FIRSTNAME);
		showColPanel.add(showHideBox(firstName, TableConstants.INSTR_FIRSTNAME));
		
		// last name
		TextColumn<InstructorGWT> lastName = 
				new TextColumn<InstructorGWT>() {
		      @Override
		      public String getValue(InstructorGWT instr) {
		        return instr.getLastName();
		      }
		};
		lastName.setSortable(true);
		sortHandler.setComparator(lastName, new Comparator<InstructorGWT>() {
	        public int compare(InstructorGWT o1, InstructorGWT o2) {
	          return o1.getLastName().compareTo(o2.getLastName());
	        }
	    });
		table.addColumn(lastName, TableConstants.INSTR_LASTNAME);
		showColPanel.add(showHideBox(lastName, TableConstants.INSTR_LASTNAME));
		
		// id
		TextColumn<InstructorGWT> id = 
				new TextColumn<InstructorGWT>() {
		      @Override
		      public String getValue(InstructorGWT instr) {
		        return instr.getUserID();
		      }
		};
		id.setSortable(true);
		sortHandler.setComparator(id, new Comparator<InstructorGWT>() {
	        public int compare(InstructorGWT o1, InstructorGWT o2) {
	          return o1.getUserID().compareTo(o2.getUserID());
	        }
	    });
		table.addColumn(id, TableConstants.INSTR_ID);
		showColPanel.add(showHideBox(id, TableConstants.INSTR_ID));
		
		// wtu
		TextColumn<InstructorGWT> wtu = 
				new TextColumn<InstructorGWT>() {
		      @Override
		      public String getValue(InstructorGWT instr) {
		        return "" + instr.getMaxWtu();
		      }
		};
		wtu.setSortable(true);
		sortHandler.setComparator(wtu, new Comparator<InstructorGWT>() {
	        public int compare(InstructorGWT o1, InstructorGWT o2) {
	          return o1.getMaxWtu() - o2.getMaxWtu();
	        }
	    });
		table.addColumn(wtu, TableConstants.INSTR_MAX_WTU);
		showColPanel.add(showHideBox(wtu, TableConstants.INSTR_MAX_WTU));
		
		// building
		TextColumn<InstructorGWT> building = 
				new TextColumn<InstructorGWT>() {
		      @Override
		      public String getValue(InstructorGWT instr) {
		        return instr.getBuilding();
		      }
		};
		building.setSortable(true);
		sortHandler.setComparator(building, new Comparator<InstructorGWT>() {
	        public int compare(InstructorGWT o1, InstructorGWT o2) {
	          return o1.getBuilding().compareTo(o2.getBuilding());
	        }
	    });
		table.addColumn(building, TableConstants.INSTR_BUILDING);
		showColPanel.add(showHideBox(building, TableConstants.INSTR_BUILDING));
		
		// room number
		TextColumn<InstructorGWT> roomNum = 
				new TextColumn<InstructorGWT>() {
		      @Override
		      public String getValue(InstructorGWT instr) {
		        return instr.getRoomNumber();
		      }
		};
		roomNum.setSortable(true);
		sortHandler.setComparator(roomNum, new Comparator<InstructorGWT>() {
	        public int compare(InstructorGWT o1, InstructorGWT o2) {
	          return o1.getRoomNumber().compareTo(o2.getRoomNumber());
	        }
	    });
		table.addColumn(roomNum, TableConstants.INSTR_ROOMNUMBER);
		showColPanel.add(showHideBox(roomNum, TableConstants.INSTR_ROOMNUMBER));
		
		// disability
		Column<InstructorGWT, Boolean> disable = 
				new Column<InstructorGWT, Boolean>(new CheckboxCell(true, false)) {
		      @Override
		      public Boolean getValue(InstructorGWT instr) {
		        return instr.getDisabilities();
		      }
		};
		disable.setSortable(true);
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
		        saveButton.setEnabled(true);
		      }
		});
		table.addColumn(disable, TableConstants.INSTR_DISABILITIES);
		showColPanel.add(showHideBox(disable, TableConstants.INSTR_DISABILITIES));
	}
	
	
	/**
	 * Column for removing items
	 */
	private void removeColumn(){
		
		// remove
		Column<InstructorGWT, Boolean> remove = 
				new Column<InstructorGWT, Boolean>(new CheckboxCell(true, false)) {
		      @Override
		      public Boolean getValue(InstructorGWT instr) {
		        return false;
		      }
		};
		
		remove.setFieldUpdater(new FieldUpdater<InstructorGWT, Boolean>() {
		      public void update(int index, InstructorGWT object, Boolean value) {
		        
		    	  if(value){
		    		  
		    		  boolean confirm = Window.confirm("Remove " + object.getFirstName() + " " + object.getLastName() + "?");
		    		  if(confirm){
		    			  dataProvider.getList().remove(object);
		    		  }
		    		  
		    		  table.redraw();
		    		  
		    		  saveButton.setEnabled(true);
		    	  }
		      }
		});
		table.addColumn(remove, "REMOVE");
	}
	
	
	/**
	 * Create check box for showing, hiding column
	 * @param col
	 * @param name
	 * @return
	 */
	private CheckBox showHideBox(Column<InstructorGWT, ?> col, String name){
		
		final Column<InstructorGWT, ?> fcol = col;
		final String fname = name;
		CheckBox cb = new CheckBox(name);
		cb.addValueChangeHandler(new ValueChangeHandler<Boolean>(){
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				
				if(event.getValue()){
					table.addColumn(fcol, fname);
				}
				else{
					table.removeColumn(fcol);
				}
				table.redraw();
			}
		});
		cb.setValue(true);
		cb.addStyleName("tableColumnCheckBox");
		
		return cb;
	}
}
