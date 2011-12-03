package edu.calpoly.csc.scheduler.model.schedule;

import java.io.Serializable;

/**
 * Represents a day of the week. To help unify the standards of what a Day is,
 * you can't actually use the constructor for this class. Instead, 7 statically
 * allocated days are provided to represent every day in the week. If you want
 * a day, just use the constant Day objects defined here. 
 * 
 * @author Eric Liebowitz
 * @version Oct 11, 2011
 */
public class Day implements Serializable
{  
   private static final long serialVersionUID = 3956064126215304948L;
   
   public static final Day SUN = new Day(0, "SUN");
   public static final Day MON = new Day(1, "MON");
   public static final Day TUE = new Day(2, "TUE");
   public static final Day WED = new Day(3, "WED");
   public static final Day THU = new Day(4, "THU");
   public static final Day FRI = new Day(5, "FRI");
   public static final Day SAT = new Day(6, "SAT");
   public static final Day[] ALL_DAYS = new Day[] { Day.SUN, Day.MON, Day.TUE, Day.WED, Day.THU, Day.FRI, Day.SAT };
   
   private int num;
   private String name;
   
   private Day (int num, String name) {
      this.num = num;
      this.name = name;
   }
   
   public boolean equals (Day d) { return this.getNum() == d.getNum(); }
   public int hashCode () { return this.num; }
   public int getNum () { return this.num; }
   public String toString () { return this.name; }
   public String getName() { return this.name; }
   public boolean equals(Object o)
   {
	if(o == null)
	{
	 return false;
	}
	if(this.getClass() != o.getClass())
	{
	 return false;
	}
	if(o == this)
	{
	 return true;
	}
	return this.equals((Day)o);
   }
}
