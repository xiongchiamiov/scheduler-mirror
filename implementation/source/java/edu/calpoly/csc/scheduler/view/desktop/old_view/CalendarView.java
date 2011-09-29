package edu.calpoly.csc.scheduler.view.desktop.old_view;

import scheduler.*;
import edu.calpoly.csc.scheduler.model.schedule.*;
import java.util.*;

import edu.calpoly.csc.scheduler.Scheduler;
import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.schedule.Schedule;
import edu.calpoly.csc.scheduler.model.schedule.ScheduleItem;
import edu.calpoly.csc.scheduler.model.schedule.Week;
import edu.calpoly.csc.scheduler.view.desktop.old_view.DaysInWeek.Day;


/****
 * The Calendar View model class. This class filters the data from
 * ViewSettings and passes it to its companion view class CalendarViewUI.
 *
 * @author Sasiluk Ruangrongsorakai
 */

class ScheduleItemComparator implements Comparator<ScheduleItem>{
	public int compare(ScheduleItem obj1, ScheduleItem obj2) {
		ScheduleItem si1 = (ScheduleItem) obj1;
		ScheduleItem si2 = (ScheduleItem) obj2;

        int ret = si1.start.compareTo(si2.start);

        return ((ret == 0) ? si1.end.compareTo(si2.end) : ret);
	}
}

public class CalendarView   {
    
	/** The Schedule to be drawn. */
    protected Schedule schedule;
    
    /** List of Selected ScheduleItem */
    protected ArrayList<ScheduleItem> siList;
    
    /** List of ScheduleItemLabel 
    protected ArrayList<ScheduleItemLabel> siLabelList;*/

    /** The parent view. */
    protected View view;
    
    /** The companion view object. */
    protected CalendarViewUI calViewUI;
   
    /** FilterOptions for the view. */
    protected FilterOptions filterOptions;    
    
	/** number of days in a weekly view or 1 for a daily view, indicate number of cols */
	protected int numDaysInWeek;
	
	/** Time obj representing the time range in calendar view */
	protected int startTime;
	/** Time obj representing the time range in calendar view */
	protected int endTime;
	
	/** number of hours in a day, indicate number of rows */
	protected int numHour;    
	
	/** List of CalColumn for selected days in AF */
	protected ArrayList<CalColumn> calColumnList;
	
	/** List of CalColumn for EVERDAY, Mon - Sun */
	protected ArrayList<CalColumn> fullColumnList;
	
	/** List contain all ScheduleItem for Monday*/
	protected ArrayList<ScheduleItem> monItem;
	/** List contain all ScheduleItem for Tuesday*/
	protected ArrayList<ScheduleItem> tueItem;
	/** List contain all ScheduleItem for Wednesday*/
	protected ArrayList<ScheduleItem> wedItem;
	/** List contain all ScheduleItem for Thursday*/
	protected ArrayList<ScheduleItem> thuItem;
	/** List contain all ScheduleItem for Friday*/
	protected ArrayList<ScheduleItem> friItem;
	/** List contain all ScheduleItem for Saturday*/
	protected ArrayList<ScheduleItem> satItem;
	/** List contain all ScheduleItem for Sunday*/
	protected ArrayList<ScheduleItem> sunItem;
	
	/** the number indicate number of col needed for that day 
	 *  more than 1 col means there is an overlapping calendar cell
	 *  for that day */
	protected int[] dayNumCol;

	/** Number of Column needed */
	protected int totalNumCol;

	static final String daysList[] = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
	static final String timeList[] = {"12:00 AM", "12:30 AM", "1:00 AM", "1:30 AM", "2:00 AM","2:30 AM","3:00 AM", "3:30 AM",
		"4:00 AM", "4:30 AM", "5:00 AM", "5:30 AM", "6:00 AM", "6:30 AM", "7:00 AM", "7:30 AM", "8:00 AM", "8:30 AM",
		"9:00 AM","9:30 AM", "10:00 AM", "10:30 AM","11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM", "1:00 PM",
		"1:30 PM", "2:00 PM","2:30 PM","3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30 PM", "6:00 PM", 
		"6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM", "9:00 PM", "9:30 PM", "10:00 PM", "10:30 PM",
		"11:00 PM", "11:30 PM", "12:00 PM" };

	/**
	 * Constructor for testing the getDayInt method
	 */
	public CalendarView(){
		
	}
	/**
     * Construct this class using its parent view object.
     */
    public CalendarView(Schedule schedule) {
        this.schedule = schedule;
        this.view = Scheduler.schedView;
        filterOptions = view.getViewSettings().getFilterOptions();
        calColumnList = new ArrayList<CalColumn>();
        fullColumnList = new ArrayList<CalColumn>();
        monItem =new ArrayList<ScheduleItem>();
    	tueItem =new ArrayList<ScheduleItem>();
    	wedItem =new ArrayList<ScheduleItem>();
    	thuItem =new ArrayList<ScheduleItem>();
    	friItem =new ArrayList<ScheduleItem>();
    	satItem =new ArrayList<ScheduleItem>();
    	sunItem =new ArrayList<ScheduleItem>();
    	siList  =new ArrayList<ScheduleItem>();
    	dayNumCol = new int[7];
    	createSchedule();
        updateData();

        sortScheduleItem();		// for all ScheduleItem within AF startTime and endTime
        callCreateCalCell();	// for 7 days, 7 columns but NOT ALL ScheduleItem will be created
        updateCalColumn();		// add only selected Days(Column) from fullColumnList to calColumnList
//        printCalendarView();
        calViewUI = new CalendarViewUI(this);

	}
    
    /**
     * create siList for the selected scheduleItems
     */
    
    public void createSchedule(){
    	siList.clear();
    	if (view.getViewSettings().getViewType() == ViewType.COURSE) 
    		createCourseSchedule();
    	else if (view.getViewSettings().getViewType() == ViewType.INSTRUCTOR)
    		createInstructorSchedule();
    	else if (view.getViewSettings().getViewType() == ViewType.LOCATION)
    		createLocationSchedule();
    	
    }
    
    /**
     * create siList for the selected scheduleItems for course view
     */
    protected void createCourseSchedule() {
        ArrayList<CourseFilterObj> filterObjList 
        	= view.getViewCourseFilter().getCourseFilterList();
        for ( ScheduleItem si: schedule.s){
        	brk:
        	for ( CourseFilterObj cfo : filterObjList ){
                if (cfo.isSelected() && si.c.toString().equals(cfo.getCourse().toString())) {
                	siList.add(si);
                	break brk;
                }
        	}
        }
    }
    
    /**
     * create siList for the selected scheduleItems for instructor view
     */
    protected void createInstructorSchedule() {
		ArrayList<InstructorFilterObj> filterObjList 
        	= view.getViewInstructorFilter().getInstructorFilterList();
        for ( ScheduleItem si: schedule.s){
        	brk:
        	for ( InstructorFilterObj ifo : filterObjList ){
                if (ifo.isSelected() && si.i.toString().equals(ifo.getInstructor().toString())) {
                	siList.add(si);
                	break brk;
                }
        	}
        }
	}
    /**
     * create siList for the selected scheduleItems for location view
     */
    protected void createLocationSchedule() {
	    ArrayList<LocationFilterObj> filterObjList 
        	= view.getViewLocationFilter().getLocationFilterList();
        for ( ScheduleItem si: schedule.s){
        	brk:
        	for ( LocationFilterObj lfo : filterObjList ){
                if (lfo.isSelected() && si.l.toString().equals(lfo.getLocation().toString())) {
                	siList.add(si);
                	break brk;
                }
        	}
        }
    }
    
    /**
     * Update Calendar View components when
     * View received observable notification
     */
    public void update(){
    	createSchedule();
    	//if the time changed, update FullColList
		updateData();
		sortScheduleItem();
		fullColumnList.clear();
		callCreateCalCell();
    		
    	calViewUI.update();
    }
    
    /**
     * Update the JLabel for each Calendar Cell
     */
    public void updateLabel(){
    	calViewUI.updateLabel();    		
    }
    
    /**
     * Adding CalColumn object to the calColumnList if that column(day) is selected in AF
     */
    public void updateCalColumn(){
    	int i = 0,ndx,start =0,end = 0;
    	calColumnList.clear();
    	totalNumCol = 0;
    	if (view.getViewSettings().getViewLevel().getLevel() == ViewLevel.Level.DAILY) {
    		ndx = getDayInt(view.getViewSettings().getViewLevel().getDay());
    		for ( i = 0; i <= ndx; i++){
    			end += dayNumCol[i];
    		}
    		start = end - dayNumCol[ndx];
    		for ( i = start; i < end; i++)
    			calColumnList.add(fullColumnList.get(i));
    		totalNumCol = dayNumCol[getDayInt(view.getViewSettings().getViewLevel().getDay())];
    	}
    	else 
    	{
	    	try {
				if (Scheduler.schedView.getAdvancedFilter().getDays().isDaySelected(Day.MON)){
					for ( i = 0 ; i < dayNumCol[0]; i++)
						calColumnList.add((fullColumnList.get(i)));
					totalNumCol += dayNumCol[0];
				}
				if (Scheduler.schedView.getAdvancedFilter().getDays().isDaySelected(Day.TUE)){
					for (	i = dayNumCol[0] ; 
							i < dayNumCol[0]+dayNumCol[1]; 
							i++)
						calColumnList.add((fullColumnList.get(i)));
					totalNumCol += dayNumCol[1];
				}
				if (Scheduler.schedView.getAdvancedFilter().getDays().isDaySelected(Day.WED)){
					for ( 	i = dayNumCol[0]+dayNumCol[1] ; 
							i < dayNumCol[0]+dayNumCol[1]+dayNumCol[2]; 
							i++)
						calColumnList.add((fullColumnList.get(i)));
					totalNumCol += dayNumCol[2];
				}
				if (Scheduler.schedView.getAdvancedFilter().getDays().isDaySelected(Day.THU)){
					for ( 	i = dayNumCol[0]+dayNumCol[2]+dayNumCol[1] ;
							i < dayNumCol[0]+dayNumCol[1]+dayNumCol[2]+dayNumCol[3];
							i++)
						calColumnList.add((fullColumnList.get(i)));
					totalNumCol += dayNumCol[3];
				}
				if (Scheduler.schedView.getAdvancedFilter().getDays().isDaySelected(Day.FRI)){
					for ( 	i = dayNumCol[3]+dayNumCol[2]+dayNumCol[1]+dayNumCol[0];
							i < dayNumCol[4]+dayNumCol[3]+dayNumCol[2]+dayNumCol[1]+dayNumCol[0]; 
							i++)
						calColumnList.add((fullColumnList.get(i)));
					totalNumCol += dayNumCol[4];
				}
				if (Scheduler.schedView.getAdvancedFilter().getDays().isDaySelected(Day.SAT)){
					for ( 	i = dayNumCol[4]+dayNumCol[3]+dayNumCol[2]+dayNumCol[1]+dayNumCol[0];
							i < dayNumCol[5]+dayNumCol[4]+dayNumCol[3]+dayNumCol[2]+dayNumCol[1]+dayNumCol[0];
							i++)
						calColumnList.add((fullColumnList.get(i)));
					totalNumCol += dayNumCol[5];
				}
				if (Scheduler.schedView.getAdvancedFilter().getDays().isDaySelected(Day.SUN)){
					for ( 	i = dayNumCol[5]+dayNumCol[4]+dayNumCol[3]+dayNumCol[2]+dayNumCol[1]+dayNumCol[0];
							i < dayNumCol[6]+dayNumCol[5]+dayNumCol[4]+dayNumCol[3]+dayNumCol[2]+dayNumCol[1]+dayNumCol[0];
							i++)
						calColumnList.add((fullColumnList.get(i)));
					totalNumCol += dayNumCol[6];
				}
			}
			catch (NullDayException e) {
				System.out.println("Catching NullDayException in CalendarView.java");
			}
    	}
    }
    /**
     * Calling createCalCell() for EVERY DAY.
     * NOT ALL ScheduleItem will be added to the CalCell
     */
    public void callCreateCalCell(){
    	dayNumCol[0] = 0;
    	dayNumCol[1] = 0;
    	dayNumCol[2] = 0;
    	dayNumCol[3] = 0;
    	dayNumCol[4] = 0;
    	dayNumCol[5] = 0;
    	dayNumCol[6] = 0;
    	createCalCell(Day.MON,monItem,0);
    	createCalCell(Day.TUE,tueItem, dayNumCol[0]);
    	createCalCell(Day.WED,wedItem, dayNumCol[0]+dayNumCol[1]);
    	createCalCell(Day.THU,thuItem, dayNumCol[0]+dayNumCol[1]+dayNumCol[2]);
    	createCalCell(Day.FRI,friItem, dayNumCol[0]+dayNumCol[1]+dayNumCol[2]+dayNumCol[3]);
    	createCalCell(Day.SAT,satItem, dayNumCol[0]+dayNumCol[1]+dayNumCol[2]
                                       +dayNumCol[3]+dayNumCol[4]);
    	createCalCell(Day.SUN,sunItem, dayNumCol[0]+dayNumCol[1]+dayNumCol[2]
                                       +dayNumCol[3]+dayNumCol[4]+dayNumCol[5]);
    }
    
    /**
     * create CalCell object containing scheduleItem(s) for the particular 
     * CalColumn in fullColumnList determined from ind parameter.
     * NOT ALL ScheduleItem will be added to the CalCell
     * @param list - list of scheduleItem for particular column/day
     * @param ind - the column number or index in fullColumnList
     */
    public void createCalCell(Day d,ArrayList<ScheduleItem> list, int ind){
    	int numCalCell = 0;
    	Time startPrev = new Time(0,0);
    	Time endPrev =  new Time(0,0);
    	Time siStart, siEnd;
    	ArrayList<CalCell> curCB = new ArrayList<CalCell>();
    	ArrayList<ScheduleItem> tempList = new ArrayList<ScheduleItem>();
    	fullColumnList.add(new CalColumn(d));
    	for (ScheduleItem si : list){
    		siStart = si.start;
    		siEnd = si.end;
    		if ( siStart.compareTo(View.advancedFilter.getStartTime()) < 0 )
    			siStart = View.advancedFilter.getStartTime();
    		if ( siEnd.compareTo(View.advancedFilter.getEndTime()) > 0 )
    			siEnd = View.advancedFilter.getEndTime();
    		
    		//adding ScheduleItem into the same CalCell
    		if ( siStart.equals(startPrev) && siEnd.equals(endPrev)){
    			(curCB.get(numCalCell-1)).addScheduleItem(si);
    		}
    		//adding ScheduleItem into new CalCell
    		else {
    			//the new start time is not after the current end time
    			// => add it to the temp ScheduleItemLabe list 
    			// This list will be put into FullColumnList
    			if (siStart.compareTo(endPrev) < 0){
    				tempList.add(si);
    			}
    			// make a new CalCell for the current list
    			else {
        			curCB.add(new CalCell(siStart,siEnd));
    				(fullColumnList.get(ind)).addCalCell(curCB.get(numCalCell));
    				(curCB.get(numCalCell)).addScheduleItem(si);
    				startPrev = siStart;
	    			endPrev = siEnd;
	    			numCalCell++;
    			}
    		}
    	}
    	dayNumCol[getDayInt(d)] += 1;
		// call to create cell box for the new list and
		// add the new list to FullColumnList
		if ( tempList.size() > 0 ){
			createCalCell(d,tempList,(ind + 1));
		}
    }
    /**
     * Print the Calendar View
     */
    public void printCalendarView(){
    	for ( CalColumn cc: calColumnList){
    		//Printing the day
			System.out.println("-----------------Day: " + cc.getColumnDay());
    		int j = 0;
			for ( CalCell cell: cc.getCalCellList()){
				System.out.println("CalCell#"+j);
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
     * categorize EVERY scheduleItem in Schedule by days then
     * in each day category, group each item by
     * its start time and end time.
     * Store the sorted ScheduleItem in "'day'Item" ArrayList
     * 
     */
    public void sortScheduleItem(){
    	//sort all schedule item by day category 
		monItem.clear();
		tueItem.clear();
		wedItem.clear();
		thuItem.clear();
		friItem.clear();
		satItem.clear();
		sunItem.clear();
        for (ScheduleItem si : siList) {
        	if ((calcTime(si.start) >= startTime && calcTime(si.start) <= endTime)||//start time is b/w the hours
    			(calcTime(si.end) >= startTime && calcTime(si.end) <= endTime) || // end time is b/w the hours
    			(calcTime(si.start) <= startTime && calcTime(si.end) >= endTime)){// start time is before the hour and end time is after the hour
	            try {
	            	if ( si.days.contains(Week.MON))
	            		monItem.add(si);
	            	if ( si.days.contains(Week.TUE))
	            		tueItem.add(si);
	            	if ( si.days.contains(Week.WED))
	            		wedItem.add(si);
	            	if ( si.days.contains(Week.THU))
	            		thuItem.add(si);
	            	if ( si.days.contains(Week.FRI))
	            		friItem.add(si);
	            	if ( si.days.contains(Week.SAT))
	            		satItem.add(si);
	            	if ( si.days.contains(Week.SUN))
	            		sunItem.add(si);
	               
	            } catch (Exception e) {
	            	System.out.println("Error from ScheduleItem in CalendarView.java");
	            }
        	}
        }
        //sort each day item (monItem - sunItme) by the start time and end time
    	Collections.sort(monItem,new ScheduleItemComparator());
    	Collections.sort(tueItem,new ScheduleItemComparator());
    	Collections.sort(wedItem,new ScheduleItemComparator());
    	Collections.sort(thuItem,new ScheduleItemComparator());
    	Collections.sort(friItem,new ScheduleItemComparator());
    	Collections.sort(satItem,new ScheduleItemComparator());
    	Collections.sort(sunItem,new ScheduleItemComparator());
    }
    
    /**
     * update numDaysInWeek, startTime, endTime, and numHour
     * corresponding to the Advanced Filter settings and user's selections
     */
	public void updateData() {
		if ( Scheduler.schedView.getViewSettings().getViewLevel().getLevel() == ViewLevel.Level.DAILY)
			numDaysInWeek = 1;
		else
		numDaysInWeek = calcNumDaysInWeek(); 
		startTime = calcTime(0);
		endTime = calcTime(1);
		numHour =  countHour(View.advancedFilter.getStartTime(),View.advancedFilter.getEndTime());
	}    
	
	/**
	 * Count how many hours are in between the first parameter and the 2nd param.
	 * 1 hour is equal to 2 counts
	 * @param startHr - the beginning of the hour
	 * @param endHr - the ending of the hour
	 * @return number of hours between the 2 given hours.
	 */
	public int countHour(Time startHr,Time endHr){
		if ( endHr.compareTo(View.advancedFilter.getEndTime()) > 0 ){ //endHr is after AF endTime
			endHr = View.advancedFilter.getEndTime();
			System.out.println("endHr is after AF endTime");
		}
		if ( startHr.compareTo(View.advancedFilter.getStartTime()) < 0 ){// startHr is before AF startTime
			startHr = View.advancedFilter.getStartTime();
			System.out.println("startHr is before AF startTime");
		}
		int ret = startHr.compareTo(endHr);
		if (  ret < 0 ) {
			// 1 hour = 2 counts
			ret = (endHr.getHour() - startHr.getHour())*2;
			if ( startHr.getMinute() == 30 )
				ret = ret - 1;
			if ( endHr.getMinute() == 30 )
				ret = ret + 1;
		}
		return ret;
	}
	
	/**
	 * Calculate number of column to display in calendar view
	 * If daily view is selected, this method will return 1
	 * @return the number of column needed 
	 */
	protected int calcNumDaysInWeek(){
		if ( view.getViewSettings().getViewLevel().getLevel() == ViewLevel.Level.DAILY)
			return 1;
		else
			return View.advancedFilter.getDays().numDaysInWeek();
	}
	
	/**
	 * Converting a time object to an int 
	 * which correspond to the String of time, timeList
	 * @param i - 0 if converting for start time, 1 for end time
	 * @return index of timeList representing the given time
	 */
	protected int calcTime(int i){
		int hour, min, ret = 0;
		if ( i == 0 )
		{
			hour = View.advancedFilter.getStartTime().getHour();
			min  = View.advancedFilter.getStartTime().getMinute();
		}
		else
		{
			hour = View.advancedFilter.getEndTime().getHour();
			min  = View.advancedFilter.getEndTime().getMinute();
		}
		ret = hour * 2;
		if ( min == 30 )
			ret = ret + 1;
		
		return ret;
	}
	
	/**
	 * Converting a time object to an int 
	 * which correspond to the String of time, timeList
	 * @param t - the time object to be converted
	 * @return index of timeList representing the given time
	 */
	protected int calcTime(Time t){
		int hour = t.getHour();
		int min = t.getMinute();
		int ret = hour * 2;
		if ( min == 30 )
			ret = ret + 1;
		return ret;
		
	}
	
	/**
	 * return the day for daily calendar view in integer format
	 * Pre: d != null && (d == MON || d == TUE || d == WED ||
	 * 				d == THU || d == FRI || d == SAT || d == SUN)
	 * Post: return == 0 if d == MON
	 * 		retirn == 1 if d == TUE
	 * 		return == 2 if d == WED
	 * 		return == 3 if d == THU
	 * 		return == 4 if d == FRI
	 * 		return == 5 if d == SAT
	 * 		return == 6 if d == SUN
	 * 		return == -1 if d== null or invalid
	 * @return the day for daily calendar view in integer 
	 */
	public int getDayInt(Day d){
		if ( d == null )
			return -1;
		switch(d)
		{
			case MON: return 0;
			case TUE: return 1;
			case WED: return 2;
			case THU: return 3;
			case FRI: return 4;
			case SAT: return 5;
			case SUN: return 6;
		}
		return -1;
		
	}
	
	/**
	 * For testing
	 * return the day for daily calendar view in integer format
	 * Pre: d != null && (d == MON || d == TUE || d == WED ||
	 * 				d == THU || d == FRI || d == SAT || d == SUN)
	 * Post: return == 0 if d == MON
	 * 		retirn == 1 if d == TUE
	 * 		return == 2 if d == WED
	 * 		return == 3 if d == THU
	 * 		return == 4 if d == FRI
	 * 		return == 5 if d == SAT
	 * 		return == 6 if d == SUN
	 * 		return == -1 if d== null or invalid
	 * @return the day for daily calendar view in integer 
	 */
	public static int getDayIntTest(Day d){
		if ( d == null )
			return -1;
		switch(d)
		{
			case MON: return 0;
			case TUE: return 1;
			case WED: return 2;
			case THU: return 3;
			case FRI: return 4;
			case SAT: return 5;
			case SUN: return 6;
		}
		return -1;
		
	}
	
	/**
	 * return the calColumnList which contains a list of CalColumn obj
	 * for each column/day in the calendar view.
	 * Each CalColumn contains a list of CalCell representing each cell
	 * in the calendar view.
	 * @return list of CalColumn obj
	 */
	public ArrayList<CalColumn> getCalColumnList(){
		return this.calColumnList;
	}
	
	/**
	 * return the list ofScheduleItem containing selected the scheduleItems
	 * @return a list of scheduleItem
	 */
	public ArrayList<ScheduleItem> getSchedule(){
		return this.siList;
	}
	
	/**
	 * return the CalendarViewUI obj
	 * @return the CalendarViewUI obj
	 */
	public CalendarViewUI getCalViewUI(){
		return this.calViewUI;
	}

	/**
	 * Return the current numDaysInWeek
	 * @return numDaysInWeek
	 */
	public int getNumDaysInWeek(){
		return this.numDaysInWeek;
	}
	/**
	 * Return the start time in integer
	 * @return start time
	 */
	public int getStartTimeInt(){
		return this.startTime;
	}
	/**
	 * Return the end time in integer
	 * @return end time
	 */
	public int getEndTimeInt(){
		return this.endTime;
	}
	/**
	 * Return the number of hours
	 * @return number of hours
	 */
	public int getNumHourTotal(){
		return this.numHour;
	}
	/**
	 * Return dayNumCol array
	 * @return dayNumCol
	 */
	public int[] getDayNumCol(){
		return this.dayNumCol;
	}
	
	/**
	 * Return totalNumCol
	 * @return totalNumCol
	 */
	public int getTotalNumCol(){
		return this.totalNumCol;
	}
}
