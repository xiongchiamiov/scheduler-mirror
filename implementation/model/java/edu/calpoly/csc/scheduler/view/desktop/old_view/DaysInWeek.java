package edu.calpoly.csc.scheduler.view.desktop.old_view;


/**
 * DaysInWeek contains a string of boolean 
 * to represent if that day is shown in a view schedule.
 * @author Sasiluk Ruangrongsorakai (sruangro@calpoly.edu)
 */


public class DaysInWeek {

	/** Day in a week: Monday - Sunday */
	protected boolean[] day;

	/**
	 * Construct this with a week containing Monday - Friday
	 */
	public DaysInWeek()
	{
		day = new boolean[7];
		day[0] = true;
		day[1] = true;
		day[2] = true;
		day[3] = true;
		day[4] = true;
		day[5] = false;
		day[6] = false;
	}
   
	/**
	 * Day specifiees a day (MON - SUN)
	 */
	public static enum Day {
    MON, TUE, WED,
    THU, FRI, SAT, SUN
   }

	/**
	 * set the given day with the given selected value
	 * pre: d != null &&
	 * 		(d == MON || d == TUE || d == WED || d == THU ||
	 *		 d == FRI || d == SAT || d == SUN )
	 * post:
	 * 		if ( d == MON )  	day[0] == s
	 *		elseif ( d == TUE ) day[1] == s
	 *		elseif ( d == WED ) day[2] == s
	 *		elseif ( d == THU ) day[3] == s
	 *		elseif ( d == FRI ) day[4] == s
	 * 		elseif ( d == SAT ) day[5] == s
	 * 		elseif ( d == SUN ) day[6] == s
	 */
	public void setDay(Day d,boolean s) throws NullDayException {
	   if ( d == null )
	      throw new NullDayException();
		switch(d)
		{
			case MON: 	day[0] = s; break;
			case TUE:	day[1] = s; break;
			case WED:	day[2] = s; break;
			case THU:	day[3] = s; break;
			case FRI:	day[4] = s; break;
			case SAT:	day[5] = s; break;
			case SUN:	day[6] = s; break;
		}
	}

	/**
	 * Return true if the give day is in a schedule view
	 * false otherwise
	 * pre: d != null &&
	 * 		(d == MON || d == TUE || d == WED || d == THU ||
	 *		 d == FRI || d == SAT || d == SUN )
	 * post:
	 * 		if ( d == MON ) 	return == day[0]
	 *		elseif ( d == TUE ) return == day[1]
	 *		elseif ( d == WED ) return == day[2]
	 *		elseif ( d == THU ) return == day[3]
	 *		elseif ( d == FRI ) return == day[4]
	 * 		elseif ( d == SAT ) return == day[5]
	 * 		elseif ( d == SUN )	return == day[6]
	 */
	public boolean isDaySelected(Day d) throws NullDayException {
	   if ( d == null )
	      throw new NullDayException();
		switch(d)
		{
			case MON: return	day[0];
			case TUE: return	day[1];
			case WED: return	day[2];
			case THU: return	day[3];
			case FRI: return	day[4];
			case SAT: return	day[5];
			case SUN: return	day[6];
			default:  throw new NullDayException();
		}
	}

	/**
	 * Calculate number of days selected  a week
	 * post: return >= 0 && return <= 7 &&
	 * 
	 * @return number of days in integer
	 */
	public int numDaysInWeek(){
		int i, count = 0;

		for (i = 0 ; i < day.length; i++ )
		{
			if ( day[i] )
				count++;
		}
		return count;
	}

	/**
	 * check if the given DaysInWeek object have the same 
	 * days selected 
	 * pre : diw != null
	 * post : 
	 * 		return == ( (day[0] == diw[0]) && (day[1] == diw[1]) 
	 * 		&& (day[2] == diw[2]) && (day[3] == diw[3]) &&
	 * 		(day[4] == diw[4]) && (day[5] == diw[5] && (day[6] == diw[6]))
	 * 
	 * @param diw - DaysInWeek object to compare 
	 * @return true if both objects have the same selected days,
	 *   		false - otherwise
	 */
	public boolean equal(DaysInWeek diw){
		if ( diw == null )
			return false;
	   boolean ret = true;
	   int i ;
	   for (i = 0; (i < day.length) && ret ; i++ )
	   {
	      if (!( diw.day[i] == this.day[i] ))
	         return false;
	   }
	   return ret;
	}
	/**
	 * Check 2 DaysInWeek to see if they have any of the same day turned on
	 * pre:
	 * 		diw != null
	 * post:
	 * 		return == ( (day[0] && (day[0] == diw[0])) || 
	 * 					(day[1] && (day[1] == diw[1])) ||
	 * 					(day[2] && (day[2] == diw[2])) ||
	 * 					(day[3] && (day[3] == diw[3])) ||
	 * 					(day[4] && (day[4] == diw[4])) || 
	 * 					(day[5] && (day[5] == diw[5])) ||
	 * 					(day[6] && (day[6] == diw[6])) )
	 * 
	 * @param diw - the DaysInWeek to be compared
	 * @return - true if both have any of the same day turned on,
	 * 			false otherwise
	 */
	
    public boolean semiEquals(DaysInWeek diw) {
    	if ( diw == null )
    		return false;
        for (int i = 0; (i < day.length)  ; i++ )
        {
            if ((diw.day[i] && day[i]))
                return true;
        }
        return false;
    }


	/**
	 * Return selection value for the DaysInWeek obj
	 * post: return == day
	 * @return day array
	 */
	public boolean[] getDays(){
	   return this.day;
	}

	public String toString(){
		String str = "{ ";
		int i;
        for (i = 0 ; i < day.length; i++ ) {
            if (day[i]){
            	switch(i){
            		case 0: str = str + "MON, "; break;
            		case 1: str = str + "TUE, "; break;
            		case 2: str = str + "WED, "; break;
            		case 3: str = str + "THU, "; break;
            		case 4: str = str + "FRI, "; break;
            		case 5: str = str + "SAT, "; break;
            		case 6: str = str + "SUN, "; break;
            	}
            }

        }
        str = str + "}";
        str = str.replaceAll(", }", " }");
        return str;
	}
}
