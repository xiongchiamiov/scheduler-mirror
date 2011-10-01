package edu.calpoly.csc.scheduler.view.desktop.old_view;

import javax.swing.*;

import edu.calpoly.csc.scheduler.Scheduler;
import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.schedule.ScheduleItem;
import edu.calpoly.csc.scheduler.view.desktop.old_view.DaysInWeek.Day;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import edu.calpoly.csc.scheduler.model.schedule.*;
import scheduler.view.*;
/**
 * Displaying a schedule in a calendar mode
 * 
 * @author Sasiluk Ruangrongsorakai
 * 
 */

public class CalendarViewUI extends JScrollPane {
	
	/** The Schedule to be drawn. 
    protected ArrayList<ScheduleItem> schedule;*/
	
	/** ArrayList of ScheduleItemLabel for each label in Calendar View */
	protected ArrayList<ScheduleItemLabel> labelList;
	
	/** Default constant for the height of calendar cell. */
	protected final int minCellHeight = 40;
	/** Default constant for the width of calendar cell. */
	protected final int minCellWidth = 200;
	
	/** Default constant for the width of time cell. */
	protected final int timeWidth = 65;
   
	/** Default constant for the height of calendar panel. */
	protected final int minWindowHeight = 700;
	
	/** Default constant for the width of calendar panel. */
	protected final int minWindowWidth = 1000;
	
	protected int cellHeight;
	protected int cellWidth;
	protected int windowHeight;
	protected int windowWidth;
	
	/** number of days in a weekly view or 1 for a daily view, indicate number of cols */
	protected int numDaysInWeek;
	
	/** Time obj representing the time range in calendar view */
	protected int startTime;
	/** Time obj representing the time range in calendar view */
	protected int endTime;
	
	/** number of hours in a day, indicate number of rows */
	protected int numHour;
	
	/**  Box to layout the Calendar View*/
	protected Box titleBox, headingBox,timeBox, scheduleBox, 
		scheduleScrollBox,innerBox, outerScrollBox,outerBox;
	
   /** panels inside each box */
   protected JPanel headingGrid, emptyheadingGrid;
   
   /** panels inside each box */
   protected JPanel timeGrid, scheduleGrid,outerPanel ;

   /** dimensions for each panel */
   protected Dimension emptyheadingGridDim, timeGridDim, 
   		outerGridDim, timeGridDimMax, scheduleGridDim;
   
   /** gridLayout for each panel */
   protected GridLayout timeLayout,  scheduleLayout;
   
	/** list of box for each column in the schedule. 
	 **/
	protected ArrayList<Box> colBox;
	
	/** list of box each day column in the schedule */
	protected ArrayList<Box> dayBox;
	
	/** list of grid each day column in the schedule */
	protected ArrayList<JPanel> dayGrid;
	
	/** The companion model object */
	protected CalendarView calView;
	
	/** List of CalColumn for selected days in AF */
	protected ArrayList<CalColumn> calColumnList;
	
	/** List of JPanel for Popup dialog in calendar view*/
	protected ArrayList<JPanel> popup;
	
	/** List of "More..." button in the view */
	protected ArrayList<JButton> moreButton;
	
	protected int popupInd;
	
	/** the number indicate number of col needed for that day 
	 *  more than 1 col means there is an overlapping calendar cell
	 *  for that day */
	protected int[] dayNumCol;
	
	/** Number of Column needed */
	protected int totalNumCol;
   
	protected int i;
	static final String daysList[] = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
	static final String timeList[] = {"12:00 AM", "12:30 AM", "1:00 AM", "1:30 AM", "2:00 AM","2:30 AM","3:00 AM", "3:30 AM",
		"4:00 AM", "4:30 AM", "5:00 AM", "5:30 AM", "6:00 AM", "6:30 AM", "7:00 AM", "7:30 AM", "8:00 AM", "8:30 AM",
		"9:00 AM","9:30 AM", "10:00 AM", "10:30 AM","11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM", "1:00 PM",
		"1:30 PM", "2:00 PM","2:30 PM","3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30 PM", "6:00 PM", 
		"6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM", "10:30 PM",
		"11:00 PM", "11:30 PM", "12:00 PM" };

	public CalendarViewUI(CalendarView calView) {
		this.calView = calView;
		
		setPreferredSize(new Dimension(minWindowWidth, minWindowHeight));
        
        outerPanel = new JPanel();
        labelList = new ArrayList<ScheduleItemLabel>(); 
		popup = new ArrayList<JPanel>();
		moreButton = new ArrayList<JButton>();
		popupInd = 0;
		updateData();
		calcDimension();
		createTitleBox();
		createTimeBox();
		createScheduleBox();
		createInnerOuterBox();
		outerPanel.add(outerBox);
		setViewportView(outerPanel);
		this.setBorder(BorderFactory.createTitledBorder("Calendar View"));
	}
	/**
	 * Print the calendar inforamtion
	 */
    public void printCalendarView(){
    	for ( CalColumn cc: calColumnList){
    		//Printing the day
			System.out.println("-----------------Day: " + cc.getColumnDay());
    		int j = 0;
			for ( CalCell cell: cc.getCalCellList()){
				for (ScheduleItem si: cell.getScheduleItemList()){
					System.out.println(si.start.toString()+ "-" + 
										si.end.toString()+" " +
										si.c.toString() + "-" + 
										si.section);
				}
    			j++;
    		}
    	}
    }
	
    /**
     * update the data and repaint the panel for calendar view 
     * when View receive notification from AF or checkboxes
     */
	public void update (){
	   	popup.clear();
	   	moreButton.clear();
	   	labelList.clear();
	   	popupInd = 0;
	    updateData();
	    calcDimension();
		updateTitleBox();
		updateTimeBox();
		updateScheduleBox();
		updateInnerOuterBox();
		setViewportView(outerPanel);
	}
	
	
	/**
     * Update the JLabel for each Calendar Cell
     */
	public void updateLabel(){
		for (ScheduleItemLabel siLabel: labelList){
    		siLabel.updateLabels();
    	}
	}
   
	/**
	 * Update calendar information for painting
	 */
	public void updateData() {
		//update the list of calColumnList
		calView.updateData();
		calView.updateCalColumn();
		calColumnList = calView.getCalColumnList();
		dayNumCol = calView.getDayNumCol();
		totalNumCol = calView.getTotalNumCol();
		numDaysInWeek = calView.getNumDaysInWeek(); 
		startTime = calView.getStartTimeInt();
		endTime = calView.getEndTimeInt();
		numHour = calView.getNumHourTotal();
	}
	
	/**
	 * Calculate the dimension of the Calendar view and Calendar Cell
	 */
	public void calcDimension(){
		windowHeight = ( ((numHour*minCellHeight) > minWindowHeight) ? 
				  		(numHour*minCellHeight) : minWindowHeight ) ;
		if ( numHour <= 0 )
			cellHeight = minCellHeight;
		else
			cellHeight = windowHeight/numHour ;
		
		windowWidth = ( ((totalNumCol*minCellWidth) > minWindowWidth) ?
						(totalNumCol*minCellWidth): minWindowWidth) ;
		
		if ( totalNumCol <= 0 )
			cellWidth = minCellWidth;
		else
			cellWidth = windowWidth/totalNumCol ;
	}
	
	/**
	 * Create the heading/Title row
	 */
	private void createTitleBox(){
		titleBox = Box.createHorizontalBox();
		
		emptyheadingGrid = setTimeCol("");
		emptyheadingGridDim = new Dimension(timeWidth,minCellHeight);
		emptyheadingGrid.setPreferredSize(emptyheadingGridDim);
		emptyheadingGrid.setMaximumSize(emptyheadingGridDim);
		emptyheadingGrid.setMinimumSize(emptyheadingGridDim);

		headingBox = Box.createHorizontalBox();
		updateHeadingGrid();
        
		titleBox.add(emptyheadingGrid);
		titleBox.add(headingBox);
	}

	/**
	 * Update the Title row when the setting changes
	 */
	private void updateTitleBox(){
	  titleBox.removeAll();
      headingBox.removeAll();

      updateHeadingGrid();

      /* add the panel to the box again ? */
      titleBox.add(emptyheadingGrid);
	  titleBox.add(headingBox);
   }
	/**
	 * Add Day's name or "Daily" to the heading box
	 */
	private void updateHeadingGrid(){
		if ( Scheduler.schedView.getViewSettings().getViewLevel().getLevel() == ViewLevel.Level.DAILY){
			headingBox.add(createHeadingCol(
				daysList[calView.getDayInt(Scheduler.schedView.getViewSettings().getViewLevel().getDay())],
				(cellWidth*dayNumCol[calView.getDayInt(Scheduler.schedView.getViewSettings().getViewLevel().getDay())]) ));
		}
		else
		{
			try {
				if (Scheduler.schedView.getAdvancedFilter().getDays().isDaySelected(DaysInWeek.Day.MON))
					headingBox.add(createHeadingCol(daysList[0],(cellWidth*dayNumCol[0])));
				if (Scheduler.schedView.getAdvancedFilter().getDays().isDaySelected(DaysInWeek.Day.TUE))
					headingBox.add(createHeadingCol(daysList[1],(cellWidth*dayNumCol[1])));
				if (Scheduler.schedView.getAdvancedFilter().getDays().isDaySelected(DaysInWeek.Day.WED))
					headingBox.add(createHeadingCol(daysList[2],(cellWidth*dayNumCol[2])));
				if (Scheduler.schedView.getAdvancedFilter().getDays().isDaySelected(DaysInWeek.Day.THU))
					headingBox.add(createHeadingCol(daysList[3],(cellWidth*dayNumCol[3])));
				if (Scheduler.schedView.getAdvancedFilter().getDays().isDaySelected(DaysInWeek.Day.FRI))
					headingBox.add(createHeadingCol(daysList[4],(cellWidth*dayNumCol[4])));
				if (Scheduler.schedView.getAdvancedFilter().getDays().isDaySelected(DaysInWeek.Day.SAT))
					headingBox.add(createHeadingCol(daysList[5],(cellWidth*dayNumCol[5])));
				if (Scheduler.schedView.getAdvancedFilter().getDays().isDaySelected(DaysInWeek.Day.SUN))
					headingBox.add(createHeadingCol(daysList[6],(cellWidth*dayNumCol[6])));
			}
			catch (NullDayException e) {
				System.out.println("Catching NullDayException in CalendarViewUI.java");
			}
		}
	}	
	
	/**
	 * Create Time Column
	 */
	private void createTimeBox(){
        timeLayout = new GridLayout(numHour,1);
		timeGrid = new JPanel(timeLayout);		
		timeBox = Box.createVerticalBox();
		
		for (i = startTime; i < (endTime); i++){
			timeGrid.add(setTimeCol(timeList[i]));
		}
      
		timeGridDim = new Dimension(timeWidth,windowHeight);
		timeGridDimMax = new Dimension(timeWidth,windowHeight*5);
      
		timeGrid.setPreferredSize(timeGridDim);
		//timeGrid.setMinimumSize(timeGridDim);
		timeGrid.setMaximumSize(timeGridDimMax);
      
		timeBox.add(timeGrid); //adding time grid to the timeBox
	}
	
	/**
	 * Update the Time column when the setting changes
	 */
	private void updateTimeBox() {
      timeBox.removeAll();
	  /* update grid size */
      timeLayout.setRows(numHour);
      /* apply new grid's size to the panel */
      timeGrid.setLayout(timeLayout);
      timeGrid.removeAll();
		for (i = startTime; i < (endTime); i++){
			timeGrid.add(setTimeCol(timeList[i]));
		}
      
      /* update panel's dimension */
	  timeGridDim.setSize(timeWidth,windowHeight);
      timeGridDimMax.setSize(timeWidth,windowHeight*5);
      /* apply new panel's dimension */
      timeGrid.setPreferredSize(timeGridDim);
		//timeGrid.setMinimumSize(timeGridDim);
      timeGrid.setMaximumSize(timeGridDimMax);
      
      /* add the panel to the box ? */
	  timeBox.add(timeGrid);
   }
   
	/**
	 * Create the main calendar view
	 * containing scheduleItems
	 */
	private void createScheduleBox(){
		scheduleBox = Box.createHorizontalBox();
		scheduleLayout = new GridLayout(1,totalNumCol);
		scheduleGrid = new JPanel(scheduleLayout);
		scheduleGrid.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		  
		scheduleGridDim = new Dimension(windowWidth, windowHeight);
		//scheduleGridDimMax = new Dimension(defaultCellWidth*numDaysInWeek,(numHour)*defaultCellHeight*2);
      
		//scheduleGrid.setMinimumSize(scheduleGridDim);
		scheduleGrid.setPreferredSize(scheduleGridDim);
		//scheduleGrid.setMaximumSize(scheduleGridDimMax);
      		      
		// add dayBox to each grid in scheduleBox's grid
		colBox = new ArrayList<Box>(totalNumCol);
		dayGrid = new ArrayList<JPanel>(numDaysInWeek);
		dayBox = new ArrayList<Box>(numDaysInWeek);
				
		createCalColumn();
		//scheduleBox.add(scheduleGrid);
	}	
	
	/**
	 * Update the main schedule view when the setting changes
	 */
	private void updateScheduleBox() {
      scheduleBox.removeAll();
      scheduleGrid.removeAll();
	  /* update grid size */
      scheduleLayout.setColumns(totalNumCol);
      /* apply new grid's size to the panel */
      scheduleGrid.setLayout(scheduleLayout);     
      /* update panel's dimension */
      scheduleGridDim.setSize(windowWidth, windowHeight);
      /* apply new panel's dimension */
      scheduleGrid.setPreferredSize(scheduleGridDim);
      //scheduleGrid.setMinimumSize(scheduleGridDim);
      //scheduleGrid.setMaximumSize(scheduleGridDimMax);
      
      // add dayBox to each grid in scheduleBox's grid
      colBox.clear();
      dayGrid.clear();
      dayBox.clear();
      //colGridLayout.setRows(numHour);
      //colGridDim.setSize(cellWidth,windowHeight);
      //colGridDimMax.setSize(defaultCellWidth,(numHour)*defaultCellHeight*2);
      
      createCalColumn();
      
      //scheduleBox.add(scheduleGrid);
   }
	
	/**
	 * Go through each calColumn in CalColumnList in order to 
	 * go through each CalCell in the current CalColumn in order to
	 * add ScheduleItem's label to the CalCell
	 */
	private void createCalColumn(){
		int dayInt, gap, i = 0;
		Day curDay;
		
		// adding empty tempBox inside each colBox before adding
		// CellBox to the tempBox
		for (i = 0; i < totalNumCol; i++ ) {
	    	  Box tempBox = Box.createVerticalBox();
	    	  colBox.add(tempBox);
	    	  colBox.get(i).setOpaque(true);
		}
		// setting up dayBox to go inside dayGrid
		for (i = 0; i < numDaysInWeek; i++ ) {
			Box tempBox = Box.createHorizontalBox();
	    	  dayBox.add(tempBox);
		}
		curDay = null;
		i = 0;
		// setting up dayGrid and the borders and the width for each day
		for (CalColumn calColumn: calColumnList) {
			JPanel p = new JPanel();
			if ( !(calColumn.getColumnDay().equals(curDay)) ){
				dayInt =  calView.getDayInt(calColumn.getColumnDay());
				p.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		    	p.setPreferredSize(new Dimension(
		    			cellWidth*(dayNumCol[calView.getDayInt(calColumn.getColumnDay())]),
		    			windowHeight));
		    	p.setMaximumSize(new Dimension(
		    			cellWidth*(dayNumCol[calView.getDayInt(calColumn.getColumnDay())]),
		    			windowHeight));
		    	p.setMinimumSize(new Dimension(
		    			cellWidth*(dayNumCol[calView.getDayInt(calColumn.getColumnDay())]),
		    			windowHeight));
		    	p.setLayout(new GridLayout(1,dayNumCol[dayInt]));
		    	curDay = calColumn.getColumnDay();
		    	dayGrid.add(p);
		    	scheduleBox.add(dayGrid.get(i));
		    	i++;
			}
		}
		i = 0;
		
		// Go through each calColumn in CalColumnList 
		// CalColumnList is created based on numDaysInWeek in AF
		curDay = (calColumnList.get(0)).getColumnDay();;
		int gridNdx = 0;
		for (CalColumn calColumn: calColumnList) {
			Time curTime = Scheduler.schedView.getAdvancedFilter().getStartTime();
			
			// Go through each CalCell in the current CalColumn
			// and ScheduleItems' labels to CalCell
			for ( CalCell cell: calColumn.getCalCellList()){
				
				gap = curTime.compareTo(cell.getStartTime());
				
				// there is a gap b/w each cell or before the first cell
				if ( gap < 0 ){
					(colBox.get(i)).add(createEmptyCellBox(
							calView.countHour(curTime, cell.getStartTime())));
					(colBox.get(i)).add(
							createCellBox(cell.getScheduleItemList(),
							calView.countHour(cell.getStartTime(),cell.getEndTime())));
					curTime = cell.getEndTime();
				}
				
				// no gap b/w each cell or before the first cell
				else if ( gap == 0 ){
					(colBox.get(i)).add(
							createCellBox(cell.getScheduleItemList(),
							calView.countHour(cell.getStartTime(),cell.getEndTime())));
					curTime = cell.getEndTime();
				}
				
				else if ( gap > 0 ) {
					System.out.println("Overlap Time Schedule");
				}
			}/* end of CalCell */
    	  
    	    // more gap after the last item, add 1 more empty cell box
			if ( curTime.compareTo(View.advancedFilter.getEndTime()) < 0 ){
				(colBox.get(i)).add(createEmptyCellBox(
						calView.countHour(curTime, View.advancedFilter.getEndTime())));
			}
			// adding the colBox to dayBox
			if ( calColumn.getColumnDay().equals(curDay)){
				dayBox.get(gridNdx).add(colBox.get(i));
			}
			else{
				dayGrid.get(gridNdx).add(dayBox.get(gridNdx));
				gridNdx++;
				dayBox.get(gridNdx).add(colBox.get(i));
				curDay = calColumn.getColumnDay();
			}
    	  	i++;
		}
		dayGrid.get(gridNdx).add(dayBox.get(gridNdx));
	}
	
    /**
     * Create a panel for each cell on the calendar view
     * Each panel contains a list of scheduleitem from the first param
     * If there are more than 3 scheduleItem in 1 panel,
     * this method will create "more" button to display the whole list of 
     * scheduleItem in a new dialog
     * @param siList - list of ScheduleItem in the panel
     * @param numHour - determine the height of the panel cell
     * @return a panel containing list of ScheduleItem (and "more" button)
     */
	private JPanel createCellBox(ArrayList<ScheduleItem> siList, int numH){
		int item = 0;
		Box box = Box.createVerticalBox();
		Box popupBox = Box.createVerticalBox();
		JPanel panel = new JPanel();
		panel.setPreferredSize(
				new Dimension(cellWidth, cellHeight*numH));
		panel.setMaximumSize(
				new Dimension(cellWidth, cellHeight*numH));
		panel.setMinimumSize(
				new Dimension(cellWidth, cellHeight*numH));
    	panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		for (item = 0 ; item < siList.size() ; item++){
			// create text to display on each cell box
			if ( item < 3 ){
				//print more info about the only 1 scheduleItem
				labelList.add(new ScheduleItemLabel(siList.get(item)));
				if ( siList.size() == 1){
					box.add( (labelList.get(labelList.size()-1)).getFullLabel() );	
				}
				else
					box.add( (labelList.get(labelList.size()-1)).getFirstLabel() ) ;
			}
			// create text to display on the popup view
			if ( siList.size() > 3){
				popup.add(new JPanel());
				labelList.add(new ScheduleItemLabel(siList.get(item)));
				popupBox.add( (labelList.get(labelList.size()-1)).getPopLabel() );
			}
		}
		
		//only add the button if there are more than 3 items
		if ( siList.size() > 3){
			popup.get(popupInd).add(popupBox);
			moreButton.add(new JButton());
			moreButton.set(popupInd,new JButton("More..."));
			moreButton.get(popupInd).addActionListener(new  ActionListener() {
	            public void actionPerformed( ActionEvent evt) {
	                //reset all the selections?
	                int ind = 0;
	                JFrame jframe = new JFrame();
	                jframe.setSize(new Dimension(400,150));
	                JButton b = (JButton)evt.getSource();
	                while ( b != moreButton.get(ind) )
	                	ind++;
	                
	                jframe.add(popup.get(ind));
	                jframe.setVisible(true);// a code to close the window
	            }
			});
			box.add(moreButton.get(popupInd));
			popupInd++;
		}

		panel.add(box);
		panel.setBackground(Color.white);	
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		return panel;		
	}
	
	/**
	 * Create empty JPanel when there are gaps b/w each cell
	 * 
	 * @param numH - the width of the empty cell
	 * @return an empty panel
	 */
	private JPanel createEmptyCellBox(int numH){
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setPreferredSize(
				new Dimension(cellWidth, cellHeight*numH));
		panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		return panel;
	}
	/**
	 * Combine each boxes together
	 */
	private void createInnerOuterBox(){
		//inner box
		innerBox = Box.createHorizontalBox();
		outerScrollBox = Box.createHorizontalBox();
		innerBox.add(timeBox);
		innerBox.add(scheduleBox);

		//outer box
		outerBox = Box.createVerticalBox();
		outerGridDim = new Dimension(minWindowWidth,minWindowHeight);
		outerBox.add(titleBox);
		outerBox.add(innerBox);
	}
	
	/** 
	 * Recombine the boxes when the setting changes
	 */
	private void updateInnerOuterBox() {
      //inner box
	  innerBox.removeAll();
	  innerBox.add(timeBox);
	  innerBox.add(scheduleBox);

	  //outer box
	  outerBox.removeAll();
	  outerBox.add(titleBox);
	  outerBox.add(innerBox);
   }

	/**
	 * Create JLabel for the given text with gray background
	 * 
	 * @param str - text to display in the JPanel
	 * @return the JPanel containing the given text
	 */
	protected JPanel setTimeCol(String str){
		JPanel panel = new JPanel();
		JLabel label1 = new JLabel(str);
		panel.setBackground(Color.lightGray);
		panel.setBorder(BorderFactory.createLineBorder(Color.black));
		panel.add(label1);
		return panel;
	}

	/**
	 * Create heading cell with the given text
	 * @param str - the text to be displayed
	 * @param w - the width unit of the cell
	 * @return JPanel containing the given text with the given width
	 */
	protected JPanel createHeadingCol(String str,int w){
		JPanel panel = new JPanel();
		JLabel label1 = new JLabel(str);
		panel.setBackground(Color.lightGray);
		panel.setBorder(BorderFactory.createLineBorder(Color.black));
		panel.setPreferredSize(
				new Dimension(w, minCellHeight));
		panel.setMaximumSize(
				new Dimension(w, minCellHeight));
		panel.setMinimumSize(
				new Dimension(w, minCellHeight));
		panel.add(label1);
		return panel;
	}

	/**
	 * return labelList of the calendar view
	 * @return labelList of the calendar view
	 */
	public ArrayList<ScheduleItemLabel> getLabelList(){
		return this.labelList;
	}
} // end class
