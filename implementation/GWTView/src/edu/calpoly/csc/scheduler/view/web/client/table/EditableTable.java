package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EditableTable{

	private FlexTable table;
	private Grid grid;
	private int cols;
	private FlexCellFormatter cellFormatter;
	private boolean editMode;
	private ArrayList<EditableTableEntry> entries, deleted;
	
	/**
	 * Creates an editable table with a column for each passed in attribute
	 * @param attributes column headings for the table
	 */
	public EditableTable(ArrayList<String> attributes){
		
		// initialize variables
		cols = 0;
		editMode = false;
		entries = new ArrayList<EditableTableEntry>();
		deleted = new ArrayList<EditableTableEntry>();
		
		/* table */
		table = new FlexTable();
		
		cellFormatter = table.getFlexCellFormatter();
		
		// null check the parameter
		if(attributes != null){
		
			cols = attributes.size();
			
			int i;
			
			// make each attribute name a column heading
			for(i = 0; i < cols; i++){
				
				String str = attributes.get(i);
				
				// null check attribute name
				if(str != null){
					
					Label label = new Label(str);
					label.addStyleName("editTableHeading");
					cellFormatter.setHorizontalAlignment(
					        0, i, HasHorizontalAlignment.ALIGN_CENTER);
					table.setWidget(0, i, label);
					cellFormatter.addStyleName(0, i, "editableTableCell");
				}
			}	
			
			table.setWidget(0, i, new Label(""));
		}
		
		
	    // table, add button to panel
	    grid = new Grid(2, 2);
	    grid.setWidget(0, 0, table);
	    grid.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
	    grid.setWidget(0, 1, editButton());
	    grid.setWidget(1, 1, addButton());
	}
	
	
	public void add(EditableTableEntry entry){
		
		add(entry.getValues());
		entries.add(entry);
	}
	
	
	private void add(ArrayList<String> values){
		
		int row = table.getRowCount();
		
		table.insertRow(row);
		
		int i;
		
		for(i = 0; i < cols && i < values.size(); i++){
			if(editMode){
				TextBox tbox = new TextBox();
				tbox.setText(values.get(i));
				table.setWidget(row, i, tbox);
			}
			else{
				table.setWidget(row, i, new Label(values.get(i)));
			}
			cellFormatter.addStyleName(row, i, "editableTableCell");
		}
		
		
		cellFormatter.setHorizontalAlignment(
		        row, i, HasHorizontalAlignment.ALIGN_CENTER);
		table.setWidget(row, i, removeButton());
		cellFormatter.addStyleName(row, i, "editableTableCellRemove");
	}
	
	
	/**
	 * Returns this object as a widget.
	 * @return the table associtated with this object
	 */
	public Widget getWidget(){
		return grid;
	}
	
	
	/**
	 * Button to remove a row from the table
	 * @return the button to remove the row
	 */
	private Button removeButton(){
		
		final Button button = new Button(EditableTableConstants.REMOVE);
		
		button.addClickHandler(
				new ClickHandler(){
			public void onClick(ClickEvent event){
				
				boolean flag = true;
				
				for(int r = 1; flag && r < table.getRowCount(); r++){
				
					// remove from table and delete corresponding entry
					if(table.getWidget(r, cols) == button){
						table.removeRow(r);
						EditableTableEntry entry = entries.remove(r-1);
						entry.delete();
						deleted.add(entry);
					}
				}
			}
		 });
		
		return button;
	}
	
	
	/**
	 * Button to add a new row to the table
	 * @return the button to add a new row
	 */
	private Button addButton(){
		
		return new Button(EditableTableConstants.ADD,
				new ClickHandler(){
			public void onClick(ClickEvent event){
				
				ArrayList<String> list = new ArrayList<String>();
				
				for(int i = 0; i < cols; i++){
					list.add("");
				}
				
				add(list);
				entries.add(new EditableTableEntry(cols));
		   }
		});
	}
	
	
	/**
	 * Button to toggle edit mode
	 * @return the edit mode button
	 */
	private Button editButton(){

		final Button button = new Button(EditableTableConstants.EDIT);
		button.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				
				// change mode
				editMode = !editMode;
				
				// edit
				if(editMode){
					button.setText(EditableTableConstants.EDIT_DONE);
					
					for(int r = 1; r < table.getRowCount(); r++){
						ArrayList<String> vals = entries.get(r-1).getValues();
						for(int c = 0; c < cols; c++){
							String str = vals.get(c);
								
							TextBox tbox = new TextBox();
							tbox.setText(str);
							
							table.setWidget(r, c, tbox);
						}
					}
				}
				
				// done
				else{
					button.setText(EditableTableConstants.EDIT);
					
					for(int r = 1; r < table.getRowCount(); r++){
						EditableTableEntry entry = entries.get(r-1);
						for(int c = 0; c < cols; c++){
							
							String str = "";
							
							try{
								TextBox tbox = (TextBox)table.getWidget(r, c);
								str = tbox.getText();
							}catch(Exception e){}
							
							entry.setValue(c, str);
							
							Label lbl = new Label(str);
							
							table.setWidget(r, c, lbl);
							
							// set cell color
							if(entry.isChanged(c)){
								cellFormatter.addStyleName(r, c, "editTableModified");
							}
							else{
								cellFormatter.removeStyleName(r, c, "editTableModified");
							}
						}
					}
				}
		   }
		});
		
		return button;
	}
}
