package edu.calpoly.csc.scheduler.view.desktop.sched_display;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

import java.util.Vector;

import edu.calpoly.csc.scheduler.model.db.cdb.*;
import edu.calpoly.csc.scheduler.model.db.idb.*;
import edu.calpoly.csc.scheduler.model.db.ldb.*;

import edu.calpoly.csc.scheduler.model.schedule.*; 

/**
 * Displays the contents of a generated schedule in a list format.
 * 
 * @author Eric Liebowitz
 * @version Oct 3, 2011
 */
public class SchedTableModel extends DefaultTableModel
{
   private static final String[] COL_DATA = 
   {
      "Dept.",
      "Class",
      "First Name",
      "Last Name",
      "Days",
      "Start",
      "End",
      "Bldg",
      "Room",
   };
   
   Schedule s;
   Vector<ScheduleItem> scheduleItems;
   
   public SchedTableModel (Schedule s)
   {
      super ();
      
      this.s = s;
      this.scheduleItems = s.getScheduleItems();
      
      this.setColumnIdentifiers(COL_DATA);
      
      System.out.println (getColumnName(0));
      for (ScheduleItem si: this.scheduleItems)
      {
         addItem(si);
      }
   }
   
   protected void addItem (ScheduleItem si)
   {
      if (!scheduleItems.contains(si))
      {
         Vector<String> data = new Vector<String>();
         
         Course c = si.getCourse();
         Instructor i = si.getInstructor();
         Location l = si.getLocation();
         
         data.add(c.getDepartment());
         data.add(c.toString());
         data.add(i.getFirstName());
         data.add(i.getLastName());
         data.add(si.getDays().toString());
         data.add(si.getStart().toString());
         data.add(si.getEnd().toString());
         data.add(l.getBuilding());
         data.add(l.getRoom());
         
         this.addRow(data);
      }
   }
}
