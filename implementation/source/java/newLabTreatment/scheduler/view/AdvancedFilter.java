package scheduler.view;

import java.util.Observable;
import scheduler.db.Time;
/****
 * AdvancedFilter contains Time objects to represent
 * the start and end time of the schedule view,
 * and DaysInWeek object to indicate days to display
 * in a weekly schedule view.
 * @author Sasiluk Ruangrongsorakai (sruangro@calpoly.edu)
 */
 
public class AdvancedFilter extends Observable{

   /** List of days to display in weekly view  */
	protected DaysInWeek days;
	
	/** Start time for displaying a view schedule */
	protected Time startTime;
	
	/** End time for displaying a view schedule */
	protected Time endTime;
	
    /**
     * Construct this with the default days and time.
     */
    public AdvancedFilter() {
		startTime = new Time(0,0);
		endTime = new Time(23,30);
		days = new DaysInWeek();
	}	
	
    /**
	 * set the startTime and endTime for a schedule view to display 
	 * Start time must be before the End time
     * pre:  start != NULL && end != NULL
     *       if (( !(start.getHour() < 24 && start.getHour() >= 0 
     *          && start.getMinute() < 60 && start.getMinute() >= 0) ) ||
     *          ( !(end.getHour() < 24 && end.getHour() >= 0 
     *          && end.getMinute() < 60 && end.getMinute() >= 0) ))
     *       then return false
     *       if (end.getHour()*60 + end.getMinute() <= start.getHour()*60 + start.getMinute())
     *       then  return false
     * post: this.endTime.getHour() == end.getHour() &&
     *       this.endTime.getMinute() == end.getMinute() &&
     *       this.startTime.getHour() == start.getHour() &&
     *       this.startTime.getMinute() == start.getMinute() 
     * @param t - new end time
     */
    public boolean setTime(Time start, Time end) {
    	boolean ret;
    	int minS, hourS, minE, hourE, preMinS, preHrS, preMinE, preHrE;
    	if ( start == null || end == null )
    		return false;
    	preMinS = startTime.getMinute();
    	preHrS = startTime.getHour();
    	preMinE = endTime.getMinute();
    	preHrE = endTime.getHour();
    	minS = start.getMinute();
    	hourS = start.getHour(); 
    	minE = end.getMinute();
    	hourE = end.getHour(); 
    	// adjust the minute
		if (minS < 15 && minS >= 0){
			minS = 0;
		}
		else if (minS >= 15 && minS < 45){
			minS = 30;
		}
		else if (minS >= 45 && minS <= 59 ){
			minS = 0;
			hourS = hourS + 1;
			if ( hourS == 24 )
				hourS = 0;
		}
		if (minE < 15 && minE >= 0 )
			minE = 0;
		else if (minE >= 15 && minE < 45)
			minE = 30;
		else if (minE >= 45 && minE <= 59){
			minE = 0;
			hourE = hourE + 1;
			if ( hourE == 24 )
				hourE = 0;
		}
    	
    	//check if the new start time is before the end time
		if ( (hourS < hourE)||( hourS == hourE && minS < minE) )
		   ret = true;
		else
		   return false;
		
		// check if the time is valid
		if ( startTime.setMinute(minS) && startTime.setHour(hourS) &&
				endTime.setMinute(minE) && endTime.setHour(hourE) )
		   ret = true;
		else {
			startTime.setMinute(preMinS); 
			startTime.setHour(preHrS);
			endTime.setMinute(preMinE);
			endTime.setHour(preHrE);
			return false;
		}
		return ret;
    }
    
	/**
    * set the given DaysInWeek object to the Advanced Filter
    * pre:  diw != NULL
    *       if ( !(diw.isDaySelected(DaysInWeek.Day.MON) || diw.isDaySelected(DaysInWeek.Day.TUE)
    *       || diw.isDaySelected(DaysInWeek.Day.WED) || diw.isDaySelected(DaysInWeek.Day.THU)
    *       || diw.isDaySelected(DaysInWeek.Day.FRI) || diw.isDaySelected(DaysInWeek.Day.SAT)
    *       || diw.isDaySelected(DaysInWeek.Day.SUN)))
    *       then return false;
    * post: this.days == diw
    * @param diw - new DaysInWeek obj
    * @return true for success, false otherwise
    */
    public boolean setDays(DaysInWeek diw){        
        int i;
        boolean ret = false;
        if ( diw == null ) 
           return false;
        // check if one of the day is selected
        for ( i = 0 ; i < diw.day.length && !ret; i++ )
        {
            if ( diw.day[i] )
               ret = true;
        }
		this.days = diw;
		return ret;
    }
	/**
	 * return the startTime for a schedule view 
    * post: return == this.startTime
	 * @return time obj - start time for a schedule view
	 */
	public Time getStartTime() {
		return this.startTime; 
	}
	
	/**
	 * return the endTime for a schedule view 
    * post: return == this.endTime
	 * @return time obj - end time for a schedule view
	 */
	public Time getEndTime() {
		return this.endTime;
	}	
	
	/**
	 * return the days as a DaysInWeek object 
	 * that are displayed for a weekly view
     * post: return == this.days
	 * @return DaysInWeek - to check what days are displaying in a view
	 */
	public DaysInWeek getDays (){
		return this.days;
	}
	
	public void updateObserver(){
		setChanged();
        notifyObservers();
	}
	
}
