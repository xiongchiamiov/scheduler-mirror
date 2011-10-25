package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EditableTable{

	private FlexTable table;
	private VerticalPanel grid;
	private int cols;
	private FlexCellFormatter cellFormatter;
	private ArrayList<EditableTableEntry> entries;
	private Button editButton, saveButton, cancelButton, addRowButton;
	private HorizontalPanel headerPanel;
	private ArrayList<Short> columnTypes;
	
	/**
	 * Creates an editable table with a column for each passed in attribute
	 * @param attributes column headings for the table
	 */
	public EditableTable(ArrayList<AttributeInfo> attributes){
		try{
		// initialize variables
		cols = 0;
		entries = new ArrayList<EditableTableEntry>();
		
		// tables
		table = new FlexTable();
		FocusPanel focusPanel = new FocusPanel();
		headerPanel = 
				new HorizontalPanel();
		focusPanel.add(headerPanel);
		
		focusPanel.addMouseOverHandler(new MouseOverHandler(){
			public void onMouseOver(MouseOverEvent event){
				resizeColumns();
			}
		});
		

		columnTypes = new ArrayList<Short>();
		cellFormatter = table.getFlexCellFormatter();
		
		// null check the parameter
		if(attributes != null){
		
			cols = attributes.size();
			int i;
			
			// make each attribute name a column heading
			for(i = 0; i < cols; i++){
				
				AttributeInfo attr = attributes.get(i);
				String str = attr.getAttr();
				short type = attr.getType();
				columnTypes.add(type);
				headerPanel.add(createColumn(i, str));
				
				
				// null check attribute name
				if(str != null){
					
					Label label = new Label(str);
					label.addStyleName("editTableHeading");
					cellFormatter.setHorizontalAlignment(
					        0, i, HasHorizontalAlignment.ALIGN_CENTER);
					table.setWidget(0, i, label);
					
					String style = "";
					if(type == AttributeInfo.STR){
						style = "editTableCellStr";
					}
					else if(type == AttributeInfo.INT){
						style = "editTableCellInt";
					}
					else if(type == AttributeInfo.BOOL){
						style = "editTableCellBool";
					}
					
					cellFormatter.addStyleName(0, i, style);
				}
			}	
			
			table.setWidget(0, i, new Label(""));
		}
		
		
		// edit, save, cancel panel
		Grid editGrid = new Grid(1, 3);
		
		editButton = editButton();
		saveButton = saveButton();
		cancelButton = cancelButton();
		addRowButton = addButton();
		
		editGrid.setWidget(0,  0, editButton);
		editGrid.setWidget(0,  1, saveButton);
		editGrid.setWidget(0,  2, cancelButton);
		
		headerPanel.addStyleName("editTableHeaderPanel");
		
	    // add elements to the grid
	    grid = new VerticalPanel();
	    
	    grid.add(editGrid);
	    
	    //grid.add(focusPanel);
	    
	    grid.add(table);
	    
	    
	    grid.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
	    grid.add(addRowButton);
	    

	    // disable buttons while not editing
	    saveButton.setVisible(false);
		cancelButton.setVisible(false);
		addRowButton.setVisible(false);
		}catch(Exception e){
			Window.alert(e.getLocalizedMessage());
		}
	}
	
	
	private HorizontalSplitPanel createColumn(int index, String name){
		
		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
		splitPanel.setLeftWidget(new Label(name));
		splitPanel.setSplitPosition("100%");
		
	    // Return the content
	    return splitPanel;
	}
	
	
	private void resizeColumns(){
		
		int totalWidth = 0;
		
		for(int i = 0; i < headerPanel.getWidgetCount(); i++){
			
			HorizontalSplitPanel split = 
					(HorizontalSplitPanel)headerPanel.getWidget(i);
			
			int width = split.getLeftWidget().getOffsetWidth() + 5;
			
			totalWidth += width;
			
			//table.getColumnFormatter().setWidth(i, width);
		}
		
		headerPanel.setWidth("" + totalWidth + "px");
	}
	
	
	/**
	 * Add table entry to the table
	 * @param entry
	 */
	public void add(EditableTableEntry entry){
		
		add(entry.getValues(), false);
		entries.add(entry);
	}
	
	
	/**
	 * Add a row of values to the table object
	 * @param values
	 * @param editMode true to set the new row to be in edit mode, false if not edit mode
	 */
	private void add(ArrayList<String> values, boolean editMode){
		
		int row = table.getRowCount();
		
		table.insertRow(row);
		
		int i;
		
		for(i = 0; i < cols && i < values.size(); i++){
			
			Widget w;
			if(editMode){
				TextBox tbox = new TextBox();
				tbox.setText(values.get(i));
				w = tbox;
			}
			else{
				w = new Label(values.get(i));
			}
			
			table.setWidget(row, i, w);
			
			// set style
			short type = columnTypes.get(i);
			String style = "";
			if(type == AttributeInfo.STR){
				style = "editTableCellStr";
			}
			else if(type == AttributeInfo.INT){
				style = "editTableCellInt";
			}
			else if(type == AttributeInfo.BOOL){
				style = "editTableCellBool";
			}
			
			cellFormatter.addStyleName(row, i, style);
		}
		
		
		cellFormatter.setHorizontalAlignment(
		        row, i, HasHorizontalAlignment.ALIGN_CENTER);
		Button removeButton = removeButton();
		
		removeButton.setVisible(editMode);
		table.setWidget(row, i, removeButton);
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
	 * Clear the table
	 */
	public void clear(){
		
		while(table.getRowCount() > 1){
			table.removeRow(1);
		}
		
		// disable buttons while not editing
	    saveButton.setVisible(false);
		cancelButton.setVisible(false);
		addRowButton.setVisible(false);
		
		// restore edit button
		editButton.setVisible(true);
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
		
		Button button = new Button(EditableTableConstants.ADD,
				new ClickHandler(){
			public void onClick(ClickEvent event){
				
				ArrayList<String> list = new ArrayList<String>();
				
				for(int i = 0; i < cols; i++){
					list.add("");
				}
				
				add(list, true);
				entries.add(new EditableTableEntry(cols));
		   }
		});
		
		button.addStyleName("editTableButton");
		
		return button;
	}
	
	
	/**
	 * Button to turn on edit mode
	 * @return the edit mode button
	 */
	private Button editButton(){

		final Button button = new Button(EditableTableConstants.EDIT);
		button.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){

				// toggle which buttons are visible
				saveButton.setVisible(true);
				cancelButton.setVisible(true);
				addRowButton.setVisible(true);
				button.setVisible(false);
				
				// convert all labels to text boxes
				for(int r = 1; r < table.getRowCount(); r++){
					ArrayList<String> vals = entries.get(r-1).getValues();
					for(int c = 0; c < cols; c++){
						String str = vals.get(c);
							
						TextBox tbox = new TextBox();
						tbox.setText(str);
						
						table.setWidget(r, c, tbox);
					}
					
					// show remove button
					Button rButton = (Button)table.getWidget(r, cols);
					rButton.setVisible(true);
				}
		   }
		});
		
		button.addStyleName("editTableButton");
		
		return button;
	}
	
	
	/**
	 * Button to save changes
	 * @return the button to save changes
	 */
	private Button saveButton(){
		
		Button button = new Button(
				EditableTableConstants.SAVE);
		
		button.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
			
				// toggle which buttons are visible
				saveButton.setVisible(false);
				cancelButton.setVisible(false);
				addRowButton.setVisible(false);
				editButton.setVisible(true);
				
				entries.clear();
				
				// convert all labels to text boxes
				for(int r = 1; r < table.getRowCount(); r++){
					EditableTableEntry entry;
					ArrayList<String> vals = new ArrayList<String>();
					for(int c = 0; c < cols; c++){
						
						TextBox tBox = (TextBox)table.getWidget(r, c);
						String str = tBox.getText();
						vals.add(str);
						table.setWidget(r, c, new Label(str));
					}
					
					entry = new EditableTableEntry(vals);
					entries.add(entry);
					
					// show remove button
					Button rButton = (Button)table.getWidget(r, cols);
					rButton.setVisible(false);
				}
			}
		});
		
		button.addStyleName("editTableButton");
		
		return button;
	}
	
	
	/**
	 * Defines the click handler for the table's save button
	 * @param clickHandler
	 */
	public void addSaveHandler(ClickHandler clickHandler){
		saveButton.addClickHandler(clickHandler);
	}
	
	
	/**
	 * Button to cancel changes
	 * @return the button to cancel changes
	 */
	private Button cancelButton(){
		
		Button button = new Button(EditableTableConstants.CANCEL, 
				new ClickHandler(){
			public void onClick(ClickEvent event){
				// clear table
				clear();
				
				// add back the previous values
				for(EditableTableEntry e : entries){
					add(e.getValues(), false);
				}
		   }
		});
		
		button.addStyleName("editTableButton");
		
		return button;
	}
	
	
	/**
	 * Get the current table entries
	 * @return
	 */
	public ArrayList<EditableTableEntry> getEntries(){
		return entries;
	}
	
	
	public void saveEntries(){
		
		// save all values to entries
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
}
