package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;

public class Table<T> {
	
	private VerticalPanel mainPanel;
	private VerticalPanel showColPanel;
	private CellTable<T> table;
	private ListDataProvider<T> dataProvider;
	private ListHandler<T> sortHandler;
	private Button saveButton, addButton, filterButton;
	private TableBuilder<T> builder;
	
	public Table(TableBuilder<T> builder){
		this.builder = builder;
		
		// Create table objects
		dataProvider = new ListDataProvider<T>();
		table = new CellTable<T>();
	    table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

	    sortHandler =
	            new ListHandler<T>(dataProvider.getList());
	    table.addColumnSortHandler(sortHandler);
	    
	    // hide/show columns
	    showColPanel = new VerticalPanel(); 
	    
	    // create columns
	    removeColumn();
	    createColumns();
	    
	    // create save button
	    saveButton();
	    
	    // add button
	    addButton();

	    // filter button
	    filterButton();
	    
	    // add everything to panel
	    dataProvider.addDataDisplay(table);
	    
	    HorizontalPanel buttonPanel = new HorizontalPanel();
	    buttonPanel.add(saveButton);
	    buttonPanel.add(addButton);
	    buttonPanel.add(filterButton);
	    buttonPanel.setSpacing(14);
	    
	    mainPanel = new VerticalPanel();
	    mainPanel.add(buttonPanel);
	    mainPanel.add(showColPanel);
	    mainPanel.add(table);
	    mainPanel.setSpacing(14);
	}
	
	
	/**
	 * Get the widget representing this table
	 * @return
	 */
	public VerticalPanel getWidget(){
		return mainPanel;
	}
	
	
	/**
	 * Clear the table
	 */
	public void clear(){
		dataProvider.getList().clear();
		table.setRowCount(0, true);
	}
	
	
	/**
	 * Add an object to the table
	 */
	public void add(T object){

		dataProvider.getList().add(0, object);
		table.redraw();
	}
	
	
	/**
	 * Set the list of objects to the table (clears current entries)
	 */
	public void set(ArrayList<T> objects){
		
		List<T> list = dataProvider.getList();
		list.clear();
		list.addAll(objects);
		table.setRowCount(list.size(), true);
		table.setRowData(0, list);
	}
	
	
	/**
	 * Save button for the table
	 * @return
	 */
	private void saveButton(){
		
		saveButton = new Button("Save");
		saveButton.addStyleName("tableButton");
		
		saveButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				
				ArrayList<T> list = new ArrayList<T>();
				for(T i : dataProvider.getList()){
					list.add(i);
				}
				
				builder.save(list);
			}
		});
	}
	
	
	/**
	 * Add new row button
	 */
	private void addButton(){
		
		addButton = new Button("+ New");
		addButton.addStyleName("tableButton");
		
		addButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				
				add(builder.newObject());
			}
		});
	}
	
	
	/**
	 * Column for removing items
	 */
	private void removeColumn(){
		
		// remove
		Column<T, Boolean> remove = 
				new Column<T, Boolean>(new CheckboxCell(true, false)) {
		      @Override
		      public Boolean getValue(T value) {
		        return false;
		      }
		};
		
		remove.setFieldUpdater(new FieldUpdater<T, Boolean>() {
		      public void update(int index, T object, Boolean value) {
		        
		    	  if(value){
		    		  
		    		  boolean confirm = Window.confirm("Remove " + builder.getLabel(object) + "?");
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
	 * Create columns for the table
	 */
	private void createColumns(){
		
		ArrayList<ColumnObject<T>> columnObjs = builder.getColumns(dataProvider, sortHandler);
		
		for(ColumnObject<T> c : columnObjs){
			
			Column<T, ?> column = c.getColumn();
			String label = c.getLabel();
			
			column.setSortable(true);
			
			showColPanel.add(showHideBox(column, label));
			table.addColumn(column, label);
		}		
	}
	
	
	/**
	 * Filter button
	 */
	private void filterButton(){
		
		filterButton = new Button("Filter");
		filterButton.addStyleName("tableButton");
		
		filterButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				
				/** TODO */
			}
		});
	}
	
	
	/**
	 * Create check box for showing, hiding column
	 * @param col
	 * @param name
	 * @return
	 */
	private CheckBox showHideBox(Column<T, ?> col, String name){
		
		final Column<T, ?> fcol = col;
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
