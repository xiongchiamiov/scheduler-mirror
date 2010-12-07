package scheduler.menu.schedule.allInOne;

import scheduler.db.coursedb.*;
import scheduler.db.preferencesdb.*;

import java.util.Vector;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Represents a checkbox list of the preferences in a preference database. 
 *
 * Note: At the moment, only NoClassOverlap preferences are displayed
 *
 * @author Eric Liebowitz
 * @version 08jun10
 */
public class PreferenceList extends GenList<SchedulePreference>
{
   /**
    * Preference database from whence this list will grab its data to display.
    */
   private PreferencesDB pdb;

   /**
    * Creates a list of preferences. Column headers are "Preference Name". 
    *
    * @param axis Direction the contents of the box are to go (hor. or vert.)
    * @param pdb PreferencesDB you want backing this list.
    */
   public PreferenceList (int axis, PreferencesDB pdb)
   {
      super (axis, new Vector<String>(), "Preferences: ");
      this.pdb = pdb;

      Vector<String> colData = new Vector<String>();
      colData.add(" ");
      colData.add("Preference Name");
      this.table.getModel().setColumnIdentifiers(colData);

      refresh ();
   }

   /**
    * Populate the life of preferences. For each preference, its name is 
    * displayed. 
    */
   protected void populate ()
   {
      for (SchedulePreference sp: dummyData())//pdb.getLocalNoClassOverlaps())
      {
         Vector<Object> row = new Vector<Object>();

         row.add(this.selections.contains(sp));
         row.add(sp);

         this.table.getModel().addRow(row);
      }
   }

   /**
    * What should go into the "selections" list: a shallow copy of the 
    * Preference. 
    *
    * @param sp The SchedulePreference to copy
    *
    * @return A shallow copy of "sp"
    */
   protected SchedulePreference copy (SchedulePreference sp) { return sp; }

   private Vector<NoClassOverlap> dummyData ()
   {
      Vector<NoClassOverlap> r = new Vector<NoClassOverlap>();

      Course c1_lab = new Course ("First_L", 101, 1, 0, Course.LAB, 35, 1, null, 
                                  null, "CPE", DaysForClasses.MTWRF);
      Course c1_lec = new Course ("First", 101, 4, 4, Course.LEC, 35, 1, c1_lab, null,
                              "CPE", DaysForClasses.MTWRF);

      Course c2_lab = new Course ("Second_L", 102, 1, 0, Course.LAB, 35, 1, null, 
                                  null, "CPE", DaysForClasses.MTWRF);
      Course c2_lec = new Course ("Second", 102, 4, 4, Course.LEC, 35, 1, c2_lab, null,
                              "CPE", DaysForClasses.MTWRF);

      Course c3_lab = new Course ("Third_L", 101, 1, 0, Course.LAB, 35, 1, null, 
                                  null, "CPE", DaysForClasses.MTWRF);
      Course c3_lec = new Course ("Third", 103, 4, 4, Course.LEC, 35, 1, c3_lab, null,
                              "CPE", DaysForClasses.MTWRF);

      Vector<Course> courses = new Vector<Course>();
      courses.add (c1_lec);
      courses.add (c2_lec);
      courses.add (c3_lec);

      r.add(new NoClassOverlap("Dummy", 5, courses));
      return r;
   }
}
