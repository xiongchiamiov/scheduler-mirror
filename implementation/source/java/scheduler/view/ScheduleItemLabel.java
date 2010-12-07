package scheduler.view;


import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.awt.*;


import scheduler.generate.ScheduleItem;
import scheduler.view.view_ui.ScheduleItemUI;
import scheduler.Scheduler;

import javax.swing.*;

public class ScheduleItemLabel {
	
	/* the scheduleItem for this label */
	protected ScheduleItem si;
	
	/** string label representing the info of the ScheduleItem */
	protected JLabel fullL;
	
	/** string label representing the first info of the ScheduleItem */	
	protected JLabel firstL;
	
	/** string label representing the info of the ScheduleItem for a popup window */	
	protected JLabel popL;
	
	public ScheduleItemLabel(ScheduleItem si){
		this.si = si;
		fullL = new JLabel();
		firstL = new JLabel();
		popL = new JLabel();
		updateLabels();
		fullL.addMouseListener(new MouseAdapter(){
			// adding moustListener to the Cal View
            public void mouseClicked(MouseEvent e) {
            	jLableMouseClicked(e);
            }
        } );
		firstL.addMouseListener(new MouseAdapter(){
			// adding moustListener to the Single Item Label in Cal View
            public void mouseClicked(MouseEvent e) {
            	jLableMouseClicked(e);
            }
        } );
		popL.addMouseListener(new MouseAdapter(){
			// adding moustListener to the POPUP window
            public void mouseClicked(MouseEvent e) {
            	jLableMouseClicked(e);
            }
        } );
	}
	
	/**
	 * Return the ScheduleItem object 
	 * 
	 * @return ScheduleItem
	 */
	public ScheduleItem getSI(){
		return this.si;
	}
	
	/**
	 * Update labels to display in the calendar view
	 */
	public void updateLabels(){
		getFirstString();
		getFullString();
		getPopupString();
		setLabelColor();
	}
	
	/**
	 * Set the text color indicating the course type
	 * Blue - Lecture Type
	 * Green - Lab Type
	 */
	protected void setLabelColor(){
		fullL.setForeground(Color.BLUE);
		firstL.setForeground(Color.BLUE);
		popL.setForeground(Color.BLUE);
		if (si.c.getCourseType().equals("lab")){
			fullL.setForeground(Color.GREEN);
			firstL.setForeground(Color.GREEN);
			popL.setForeground(Color.GREEN);
		}
		
	}
	
	/**
	 * Return JLabel with full information about the scheduleItem
	 *
	 * @return JLabel with full information about the scheduleItem
	 */
	public JLabel getFullLabel(){
		return this.fullL;
	}
	
	/**
	 * Return JLabel with the information for the first
	 * selected filter options 
	 *  
	 * @return first information selected in filter options
	 */
	public JLabel getFirstLabel(){
		return this.firstL;
	}
	
	/** 
	 * Return part of the information of the scheduleItem
	 * depending on the ViewMode, and ViewType
	 * 
	 * @return
	 */
	public JLabel getPopLabel(){
		return this.popL;
	}
	
	/**
	 * Compute the string to represent full information of the scheduleItem
	 */
	protected void getFullString() {
		String str = "";
		FilterOptions fo = Scheduler.schedView.getViewSettings().getFilterOptions();;
		boolean[] foArray = fo.toArray();
		if ( foArray[0])
			str = str + si.c.getCourseName() + " ";
		if ( foArray[1])
			str = str + "CPE"+si.c.getId() + " " ;
		if ( foArray[2])
			str = str + "Section:"+ si.section + " ";
		if ( foArray[4])
			str = str + si.c.getCourseType() + " ";
		if ( foArray[3])
			str = str + "WTU:" + si.c.getWTU() + " ";
		if ( foArray[5])	
			str = str + "Max Enrollment:" + si.c.getMaxEnrollment() + " ";
		if ( foArray[6]){
			if ( si.c.hasLab() )
				str = str + "Pairing:" + si.c.getLabPairing().getId() + " ";
			else
				str = str + "Pairing: None ";
		}
		if ( foArray[7]){
			str = str + "Equipment:";
			if ( si.c.getRequiredEquipment().hasOverhead() )
				str += "Overhead, ";
			if ( si.c.getRequiredEquipment().hasLaptopConnectivity() )
				str += "Laptop Connectivity, ";
			if ( si.c.getRequiredEquipment().isSmartroom() )
				str += "Smart Room";
		}
		if (foArray[8])
			str += "("+ si.i.getName() + ")";
		if ( foArray[9] )
			str += "Instructor ID:" + si.i.getId() + " ";
		if ( foArray[10] ){
			if ((si.i.getOffice()==null || si.i.getOffice().getBuilding()==null || 
				si.i.getOffice().getRoom()==null)){
				str += "Instrcutor Office: Bldg#TBA Room#TBA ";
			}
			else if ((Integer.parseInt((si.i.getOffice().getBuilding()).trim()) < 0 ||
				Integer.parseInt((si.i.getOffice().getRoom()).trim()) < 0 )) {
				str += "Instrcutor Office: Bldg#TBA Room#TBA ";
			}
			else {
				str += "Instrcutor Office: Bldg#" + si.i.getOffice().getBuilding()
				+ " Room#" + si.i.getOffice().getRoom() + " ";
			}
		}
		if ( foArray[11])
			str += "Instructor Max WTU:" + si.i.getMaxWTU() + " ";
		if ( foArray[12])
			str += "Instructor Disability:" + si.i.getDisability() + " ";
		if ( foArray[13]){
			if (Integer.parseInt((si.l.getBuilding()).trim()) < 0)
				str += "Buliding#TBA ";
			else
				str += "Buliding#" + si.l.getBuilding() + " ";
		}
		if ( foArray[14]){
			if (Integer.parseInt((si.l.getRoom()).trim()) < 0)
				str += "Room#TBA ";
			else
				str += "Room#" + si.l.getRoom() + " ";
		}
		if ( foArray[15])
			str +=  "Location Max Capacity:" + si.l.getMaxOccupancy()+ " ";
		if ( foArray[16])
			str +=  "Room Type:" + si.l.getType() + " ";
		if ( foArray[17]) //LocationDisabilitiesComplianceFilter
			str +=  " ";
		if ( foArray[18])
			str +=  "Start Time:" + si.start.toString() + " ";
		if ( foArray[19])
			str +=  "End Time:" + si.end.toString() + " ";
		if ( foArray[20])
			str +=  si.days.toString();
		
		this.fullL.setText(str);
		//fullL.setAlignmentX(Component.LEFT_ALIGNMENT);
		
	}
	
	/**
	 * Compute a string wtih first selected filter options information
	 */
	protected void getFirstString() {
		String str = "";
		FilterOptions fo = Scheduler.schedView.getViewSettings().getFilterOptions();;
		boolean[] foArray = fo.toArray();
		if ( foArray[0])
			str =  si.c.getCourseName() + " ";
		else if ( foArray[1])
			str =  "CPE"+si.c.getId() + " " ;
		else if ( foArray[2])
			str =  "Section:"+ si.section + " ";
		else if ( foArray[4])
			str =  si.c.getCourseType() + " ";
		else if ( foArray[3])
			str =  "WTU:" + si.c.getWTU() + " ";
		else if ( foArray[5])	
			str = "Max Enrollment:" + si.c.getMaxEnrollment() + " ";
		else if ( foArray[6]) {
			if ( si.c.hasLab() )
				str = str + "Pairing:" + si.c.getLabPairing().getId() + " ";
			else
				str = str + "Pairing: None ";
		}
		else if ( foArray[7]){
			str =  "Equipment:";
			if ( si.c.getRequiredEquipment().hasOverhead() )
				str += "Overhead, ";
			if ( si.c.getRequiredEquipment().hasLaptopConnectivity() )
				str += "Laptop Connectivity, ";
			if ( si.c.getRequiredEquipment().isSmartroom() )
				str += "Smart Room";
		}
		else if (foArray[8])
			str = "("+ si.i.getName() + ")";
		else if ( foArray[9] )
			str = "Instructor ID:" + si.i.getId() + " ";
		else if ( foArray[10] ){
			if ((si.i.getOffice()==null || si.i.getOffice().getBuilding()==null || 
					si.i.getOffice().getRoom()==null) && 
					(Integer.parseInt((si.i.getOffice().getBuilding()).trim()) < 0 ||
					Integer.parseInt((si.i.getOffice().getRoom()).trim()) < 0 )) {
					str += "Instrcutor Office: Bldg#TBA Room#TBA ";
				}
				else {
					str += "Instrcutor Office: Bldg#" + si.i.getOffice().getBuilding()
					+ " Room#" + si.i.getOffice().getRoom() + " ";
				}
			}
		else if ( foArray[11])
			str = "Instructor Max WTU:" + si.i.getMaxWTU() + " ";
		else if ( foArray[12])
			str = "Instructor Disability:" + si.i.getDisability() + " ";
		else if ( foArray[13]){
			if (Integer.parseInt((si.l.getBuilding()).trim()) < 0)
				str += "Buliding#TBA ";
			else
				str += "Buliding#" + si.l.getBuilding() + " ";
		}
		else if ( foArray[14]){
			if (Integer.parseInt((si.l.getRoom()).trim()) < 0)
				str += "Room#TBA ";
			else
				str += "Room#" + si.l.getRoom() + " ";
		}
		else if ( foArray[15])
			str =  "Location Max Capacity:" + si.l.getMaxOccupancy()+ " ";
		else if ( foArray[16])
			str =  "Room Type:" + si.l.getType() + " ";
		else if ( foArray[17]) //LocationDisabilitiesComplianceFilter
			str =  " ";
		else if ( foArray[18])
			str =  "Start Time:" + si.start.toString() + " ";
		else if ( foArray[19])
			str =  "End Time:" + si.end.toString() + " ";
		else if ( foArray[20])
			str =  si.days.toString();
		
		this.firstL.setText(str);
		//firstL.setHorizontalTextPosition(JLabel.LEFT);
		
		
	}
	
	/**
	 * Compute a string to display certain information 
	 * in the calendar view
	 */
	protected void getPopupString(){

		String str = si.c.toString() + " - " + si.section;
		
		if ( Scheduler.schedView.getViewSettings().getViewType() == ViewType.LOCATION ) { 
			str = str + " Bldg#" + si.l.getBuilding() 
					+ " Room#" + si.l.getRoom();
		}
		else if (Scheduler.schedView.getViewSettings().getViewType() == ViewType.INSTRUCTOR ) {
			str = str + " (" + si.i.getLastName() + ", " 
					+ si.i.getFirstName() + ")";
		}
		
		if ( Scheduler.schedView.getViewSettings().getViewLevel().getLevel() == ViewLevel.Level.WEEKLY)
			str = str + " " + si.days.toString();
		
		this.popL.setText(str);
		//popL.setHorizontalTextPosition(JLabel.LEFT);
	}
	
	/**
	 * Find the scheduleItem that matches the given JLabel
	 * 
	 * @param l - JLabel to search for
	 * @return scheduleItem object found, the result after the search
	 */
	protected ScheduleItemLabel findScheduleItemLabel(JLabel l){
		ArrayList<ScheduleItemLabel> labelList = Scheduler.schedView.getCalendarView().getCalViewUI().getLabelList();
		
		for (ScheduleItemLabel si: labelList){
			if ( l == (si.getFullLabel()) )
				return si;
			if ( l == (si.getPopLabel()) )
				return si;
			if ( l == (si.getFirstLabel()) )
				return si;
		}
		return null;
	}
	
	public void jLableMouseClicked(MouseEvent e) {
    	// adding moustListener to the FIRST 3 items in the Cal View
		JLabel l = (JLabel) (e.getSource());
		ScheduleItemLabel siLabel = findScheduleItemLabel(l);
		ScheduleItemUI scheduleItemUI;
        if (e.getClickCount() == 2){
        	try {
        		scheduleItemUI = new ScheduleItemUI(siLabel.getSI() ,
        				Scheduler.schedView.getViewSettings().getFilterOptions());
        		scheduleItemUI.setLocation(e.getLocationOnScreen());
        	}
        	catch (Exception e1) {
        		System.out.println("ScheduleItemLabel:Can't match ScheduleItem");
        		e1.printStackTrace();
        	}
        }
    }
}
