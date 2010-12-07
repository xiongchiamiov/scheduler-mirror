package scheduler.generate;

import scheduler.db.Time;

import java.util.Comparator;

/**
 * Compares two strings of the form:
 *
 * [number]_[Hour of day]:[Minute of day].
 *
 * The "number" part of the String will determine the order of itmes. In the
 * event of a tie, a Time object will be constructed, and the earlier Time 
 * will be given precedence over a later one. 
 *
 * @author Eric Liebowitz
 * @version 02oct10
 */
public class TimeSortByAvgDesireComparator implements Comparator
{
   public TimeSortByAvgDesireComparator ()
   {
      super ();
   }

   /**
    * Compares two strings of the form:
    *
    * [number]_[Hour of day]:[Minute of day].
    *
    * The "number" part of the String will determine the order of itmes. In the
    * event of a tie, a Time object will be constructed, and the earlier Time 
    * will be given precedence over a later one. 
    *
    * @param o1 The first item
    * @param o2 The second item
    *
    * @return -1 if o1 should come before o2, 0 if they're equal, or 1 if o1 
    *         should come after o2
    */
   public int compare (Object o1, Object o2)
   {
      String s1 = (String) o1, s2 = (String) o2;

      /*
       * Turn the "number" part of the string into a real Integer which can be
       * easily compared
       */
      Double desire1 = new Double(s1.substring(0, s1.indexOf('_')));
      Double desire2 = new Double(s2.substring(0, s2.indexOf('_')));

      /*
       * Double are sorted in ascending numerical order, but I want descending. 
       * Multiplying by -1 seems the easiest, fastest way to reverse the 
       * ordering.
       */
      int r = desire1.compareTo(desire2) * -1;
      /*
       * If there was a tie, extract the Time information from the strings and
       * compare the Time objects created from said information. (Whoever wrote
       * the Time constructor which took a String is my new best friend).
       */
      if (r == 0)
      {
         Time t1 = new Time(s1.substring(s1.indexOf('_') + 1));
         Time t2 = new Time(s2.substring(s2.indexOf('_') + 1));

         r = t1.compareTo(t2);
      }

      return r;
   }
}
