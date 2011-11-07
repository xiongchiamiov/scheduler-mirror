package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;

public class TableValidate {

	/**********************
	 * Integer validation *
	 **********************/
	
	/**
	 * Create an edit textcell that validates for integers
	 * @param type the row type that this cell is being created for
	 * @param acceptZero true if 0 is valid, false if 0 is invalid
	 * @return
	 */
	public static EditTextCell intValidateCell(String type, boolean acceptZero){
		
		final String ftype = type;
		final int min = acceptZero ? 0 : 1;
		String error = " must be a non-negative number";
		if(!acceptZero){
			error += ", greater than 0";
		}
		final String ferror = error;
		
		return new EditTextCell(){
			@Override
			public void onBrowserEvent(Cell.Context context, Element parent, String value,
					NativeEvent event, ValueUpdater<String> valueUpdater){
				// call super
				super.onBrowserEvent(context, parent, value,
						event, valueUpdater);
				
				
				// Ignore events that don't target the input
				Element target = event.getEventTarget().cast();
				if(!parent.getFirstChildElement().isOrHasChild(target)){
					return;
				}
				
				// Ignore events that don't match desired type
				if(Event.getTypeInt(event.getType()) != Event.ONKEYUP){
					return;
				}
				
				
				// validate input, and change if necessary
				InputElement input = parent.getFirstChild().cast();
				
				String newValue = input.getValue().trim();
				if(newValue.equals("")){
					return;
				}
				
				Integer i = null;
				try{
					i = Integer.parseInt(newValue);
				}catch(Exception e){}
				
				if(i == null || i <= min){
					input.setValue("" + min);
					Window.alert(ftype + ferror + ".\n\n\'" + newValue + "\' is invalid.");
				}
			}
		};
	}
	
	
	/**
	 * Parse a String for its integer value, and return it if valid
	 * @param value string value to parse
	 * @param acceptZero true if 0 is valid, false if 0 is invalid
	 * @return the parsed integer, or 0 if the value is invalid (1 if 0 is considered invalid)
	 */
	public static int positiveInt(String value, boolean acceptZero){
		value = value.trim();
  	  	Integer i = null;
  	  	try{
  	  		i = Integer.parseInt(value);
  	  	}catch(Exception e){}
  	  
  	  	int min = acceptZero ? 0 : 1;
  	  	
  	  	if(i == null || i < min){
  	  		return min;
  	  	}
  	  	return i;
	}
	
	
	/***************************
	 * Duplicate Instructor ID *
	 ***************************/
	/**
	 * Create an edit textcell that checks instructor ID for duplicates
	 * @param dataProvider data provider to use for checking duplicates
	 * @return
	 */
	public static EditTextCell instrIDValidateCell(ListDataProvider<InstructorGWT> dataProvider){
		
		final ListDataProvider<InstructorGWT> fdataProvider = dataProvider;
		
		return new EditTextCell(){
			@Override
			public void onBrowserEvent(Cell.Context context, Element parent, String value,
					NativeEvent event, ValueUpdater<String> valueUpdater){
				// call super
				super.onBrowserEvent(context, parent, value,
						event, valueUpdater);
				
				
				// Ignore events that don't target the input
				Element target = event.getEventTarget().cast();
				if(!parent.getFirstChildElement().isOrHasChild(target)){
					return;
				}
				
				// Ignore events that don't match desired type
				if(Event.getTypeInt(event.getType()) != Event.ONKEYUP){
					return;
				}
				
				
				// validate input, and change if necessary
				InputElement input = parent.getFirstChild().cast();
				
				String newValue = input.getValue().trim();
				if(newValue.equals("") || newValue.equals(value.trim())){
					return;
				}
				
				// check for duplicate
		    	List<InstructorGWT> list = fdataProvider.getList();
		    	for(int i = 0; i < list.size(); i++){
		    		  
		    		if(list.get(i).getUserID().trim().equals(newValue)){
		    			Window.alert("There is already a user with the ID: \'" + newValue + "\'");
		    			input.setValue("");
		    			return;
		    		}
		    	}
			}
		};
	}
	
	
	/**********************
	 * Duplicate Location *
	 **********************/
	/**
	 * Create an edit textcell that checks building and room numbers for duplicates
	 * @param isBuilding true if this cell contains building data, false if it contains room number data
	 * @param dataProvider data provider to use for checking duplicates
	 * @return
	 */
	public static void validateLoc(boolean isBuilding, ListDataProvider<LocationGWT> dataProvider, Cell.Context context, Element elem, LocationGWT object, NativeEvent event){
		
		// Ignore events that don't target the input
		Element target = event.getEventTarget().cast();
		if(!elem.getFirstChildElement().isOrHasChild(target)){
			return;
		}
		
		// Ignore events that don't match desired type
		if(Event.getTypeInt(event.getType()) != Event.ONKEYUP){
			return;
		}
		
		// validate input, and change if necessary
		InputElement input = elem.getFirstChild().cast();
		String newValue = input.getValue().trim();

		String building = object.getBuilding().trim();
		String room = object.getRoom().trim();
		
		// check for case that no changes were made
		if(isBuilding){
			if(building.equals(newValue)){ return; }
			building = newValue;
		}
		else{
			if(room.equals(newValue)){ return; }
			room = newValue;
		}

		// check for empty
		if(building.equals("") || room.equals("")){
			if(isBuilding){
	    		object.setBuilding(newValue);
	    	}
	    	else{
	    		object.setRoom(newValue);
	    	}
			return;
		}
		
		// check for duplicate
		final ListDataProvider<LocationGWT> fdataProvider = dataProvider;
    	List<LocationGWT> list = fdataProvider.getList();
    	for(int i = 0; i < list.size(); i++){
  
    		// check
    		if(list.get(i).getBuilding().trim().equals(building) &&
    				list.get(i).getRoom().trim().equals(room)){
    			
    			Window.alert("There is already a location with building: \'" + building + "\' room: \'" + room + "\'");
    			input.setValue("");
    			if(isBuilding){
    	    		object.setBuilding("");
    	    	}
    	    	else{
    	    		object.setRoom("");
    	    	}
    			return;
    		}
    	}
    	
    	if(isBuilding){
    		object.setBuilding(newValue);
    	}
    	else{
    		object.setRoom(newValue);
    	}
	}
	
	
	/***************
	 * Course Labs *
	 ***************/
	public static ButtonCell labButtonCell(Widget hidden, ListDataProvider<CourseGWT> dataProvider){
		
		final Label fhidden = (Label)hidden;
		final ListDataProvider<CourseGWT> fdataProvider = dataProvider;
		
		return new ButtonCell(){
			@Override
			public void onBrowserEvent(Cell.Context context, Element parent, String value,
					NativeEvent event, ValueUpdater<String> valueUpdater){
				// call super
				super.onBrowserEvent(context, parent, value,
						event, valueUpdater);
				
				
				// Ignore events that don't target the input
				Element target = event.getEventTarget().cast();
				if(!parent.getFirstChildElement().isOrHasChild(target)){
					return;
				}
				
				final PopupPanel popup = new PopupPanel(true);
		    	
				// get lab options
		    	ArrayList<String> labOptions = new ArrayList<String>();
		    	labOptions.add("");
		  		for(CourseGWT c : fdataProvider.getList()){
		  			
		  			if(c.getType().equals(TableConstants.LAB) && 
		  					!c.getDept().trim().equals("")){
		  				labOptions.add(c.getDept().trim() + c.getCatalogNum());
		  			}
		  			
		  		}
		  		final InputElement input = parent.getFirstChild().cast();
		  		Collections.sort(labOptions);
		  		
		  		final ListBox listbox = new ListBox();
		  		listbox.addChangeHandler(new ChangeHandler(){
		  			public void onChange(ChangeEvent event){
		  				String newValue = listbox.getValue(listbox.getSelectedIndex());
		  				input.setValue(newValue);
		  				popup.hide();
		  			}
		  		});
		  		
		  		for(int i = 0; i < labOptions.size(); i++){
		  			String s = labOptions.get(i);
		  			listbox.addItem(s);
		  			if(value.equals(s)){
		  				listbox.setSelectedIndex(i);
		  			}
		  		}
		    	
		  		popup.setWidget(listbox);
		  		popup.showRelativeTo(fhidden);
			}
		};
	}


	public static double positiveMultipleOfHalf(String valueStr, double defaultValue) {
		try {
			double value = Double.parseDouble(valueStr);
			double nearestHalf = Math.round(defaultValue * 2) / 2.0;
			if (Math.abs(value - nearestHalf) >= .05)
				throw new NumberFormatException();
			return nearestHalf;
		}
		catch (NumberFormatException e) {
			return defaultValue;
		}
	}
}
