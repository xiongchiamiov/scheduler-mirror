package edu.calpoly.csc.scheduler.model.db.idb;

import edu.calpoly.csc.scheduler.model.db.cdb.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.calpoly.csc.scheduler.model.db.Time;

/**
 * TimePreferenceCompare
 * 
 * @author Leland Garofalo
 */
public class TimePreferenceComparator implements Comparator {

    /**
     * Compare method to compare TimePreferences
     * @param obj1 Object TimePreference
     * @param obj2 Object TimePreference
     * @return int 1, 0, or -1 depending if obj1 is greater,equal,or less than obj2
     * <pre>
     * // ** Pre and Post conditions ** //
     *
     * <b><u>Pre:</u></b>
     *
     * // obj1 can not be null
     * obj1 != nil
     *
     * &&
     *
     * // obj2 can not be null
     * obj2 != nil
     *
     * &&
     *
     * // obj1 and obj2 must be of type TimePreference
     * obj1 instanceof(TimePreference) && obj2 instanceof(TimePreference)
     *
     *
     * // The return must be 1,0,-1
     * return <= 1 && return >= -1
     *
     *
     * </pre>
     */
    public int compare(Object o1, Object o2) 
    {
      TimePreference p1 = (TimePreference) o1;
      TimePreference p2 = (TimePreference) o2;
      if (p1.getDesire() > p2.getDesire())
      {
         return -1;
      }
      else
      {
         if (p1.getDesire() < p2.getDesire())
         {
            return 1;
         }
      }
      return 0;
    }
}
