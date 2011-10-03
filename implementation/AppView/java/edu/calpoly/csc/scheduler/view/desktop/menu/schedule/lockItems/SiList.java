package edu.calpoly.csc.scheduler.view.desktop.menu.schedule.lockItems;

import edu.calpoly.csc.scheduler.model.schedule.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.schedule.ScheduleItem;
import edu.calpoly.csc.scheduler.view.desktop.menu.schedule.allInOne.GenList;

import java.awt.*;
import java.util.Vector;
import java.util.LinkedHashMap;
import java.lang.UnsupportedOperationException;

/**
 * Displays a list of ScheduleItems, providing checkboxes to allow the user 
 * to "lock" whichever ones they want. An action listener will update each
 * ScheduleItem the moment its "locked" checkbox is [de]selected. Labs are not
 * displayed: locking a lecture automatically locks its corresponding lab.
 *
 * @author Eric Liebowitz
 * @version 03sep10
 */
public class SiList extends GenList<ScheduleItem>
{
   /**
    * The list of ScheduleItems to display. Note that the keys are Strings. This
    * is due to the fact that Courses w/ different section #'s will hash to the 
    * same value. I tack on the section number to the Course's string-
    * -representation in order to guarantee unique keys
    */
   private LinkedHashMap<String, ScheduleItem> siList;

   /**
    * Creates a list of ScheduleItems, with check boxes to select whether they
    * should be "locked" or not. Comes with "Select All" and "Clear" buttons
    */
   public SiList (Vector<ScheduleItem> siList)/*==>*/
   {
      /*
       * Passed empty string vector b/c I can't constructor the vector before
       * calling super. I'm just fooling the constructor for a bit
       */
      super (BoxLayout.Y_AXIS, new Vector<String>(), "Schedule Items: ");

      /*
       * Now, I can freely construct the column headers and set the table's 
       * columns
       */
      Vector<String> colData = new Vector<String>();
      colData.add("SI [Not Seen]"); // Will not be visible to the user
      colData.add("Locked");
      colData.add("Course");
      colData.add("Section");
      colData.add("Professor");
      colData.add("Start");
      colData.add("End");
      colData.add("Days");
      this.table.getModel().setColumnIdentifiers(colData);
   
      addLockedListener();

      refresh ();
   }/*<==*/

   /**
    * Initializes the list of SI's by associating its course with the SI 
    * object. This'll make it easier to lookup selections in the table, as the
    * course in the table can be used to lookup the SI it applies to. 
    *
    * @param siList List of ScheduleItems to use
    */
   private void initSiList (Vector<ScheduleItem> siList)/*==>*/
   {
      //this.siList = new LinkedHashMap<Course, ScheduleItem>();
      for (ScheduleItem si: siList)
      {
         //this.siList.put(si.c + si.c.getSection() + "", si);
      }
      initSiList (siList);
      //siList_2 = new Vector<ScheduleItem>(siList);
   }/*<==*/

   /**
    * Sets up a listener to update an SI with the value of the "locked" field
    * for its corresponding row. Note that the listener added will only be 
    * invoked if "evt.getType == TableModelEvent.UPDATE".
    */
   private void addLockedListener ()/*==>*/
   {
      this.table.getModel().addTableModelListener
      (
         new TableModelListener ()
         {
            public void tableChanged (TableModelEvent evt)
            {
               if (evt.getType() == TableModelEvent.UPDATE)
               {
                  updateLock(evt);
               }
            }
         }
      );
   }/*<==*/

   /**
    * When set as a listener, will update the SI's "locked" field according to
    * the status of the "locked" item in the SI's corresponding row. T
    *
    * @param evt TableModelEvent associated with the event which triggered
    *        this listener
    */
   private void updateLock (TableModelEvent evt)/*==>*/
   {
      String s = getCourseKeyAtRow(evt.getFirstRow());
      Boolean b = getLockedStatusAtRow(evt.getFirstRow());

      //this.siList.get(c).locked = b;
      //if (c.hasLab())
      {
         //this.siList.get(c.getLabPairing()).locked = b;
      }
   }/*<==*/

   /**
    * <pre>
    * Populates the table of ScheduleItems. The following aspects of each item
    * are displayed horizontally, from left-to-right, as follows:
    *
    *  - Locked
    *  - Course (this is the actual, Course object)
    *  - Section
    *  - Professor
    *  - Start
    *  - End
    *  - Days
    *
    * Labs are not displayed, though their information is still contained in 
    * "siList".
    *
    * Note that the order of SI's displayed is whatever order the iterator for
    * Vector returns. This guarantee will be used to locate ScheduleItems
    * in the table
    * </pre>
    */
   protected void populate ()/*==>*/
   {
      for (ScheduleItem si: this.siList.values())
      {
         /*
          * Don't display labs
          */
         if (si.c.isLecture())
         {
            Vector<Object> row = new Vector<Object>();

            row.add(si.locked);
            row.add(si.c);
            row.add(si.c.getSection());
            row.add(si.i.getId());
            row.add(si.start);
            row.add(si.end);
            row.add(si.days);

            this.table.getModel().addRow(row);
         }
      }
   }/*<==*/

   /**
    * Refreshed the list of ScheduleItems contained in this object, along with 
    * everything the "GenList" does for its refresh.
    */
   public void refresh ()/*==>*/
   {
      //TODO: FILL LIST
//      initSiList (Scheduler.getSchedule().getScheduleItems());
      super.refresh();
   }/*<==*/

   /**
    * Throws UnsupportedOperationException. Present to satisfy parent class'
    * requirement, as it defines an abstract "copy(T)" method, and I don't want
    * to make this class abstract.
    *
    * @param si ScheduleItem to "copy"
    * 
    * @throws UnsupportedOperationException
    */
   protected ScheduleItem copy (ScheduleItem si)/*==>*/
   {
      throw new UnsupportedOperationException();
   }/*<==*/

   /**
    * Goes through the list of "things" and adds whichever ones are checked to 
    * the "selections" list.
    */
   protected void gatherSelected ()/*==>*/
   {
      selections.clear();
      for (int r = 0; r < table.getRowCount(); r ++)
      {
         if (table.getValueAt(r, 0) == Boolean.TRUE)
         {
            selections.add(copy((Course)table.getValueAt(r, 1)));
         }
      }
   }/*<==*/

   /**
    * What should go into the "selections" list: a shallow copy of the selected
    * ScheduleItems
    *
    * @param c Key used to lookup ScheduleItem to copy
    * 
    * @return A shallow copy of the value "c" yields in the SiList
    */
   protected ScheduleItem copy (Course c)/*==>*/
   {
      return siList.get(c);
   }/*<==*/

   /**
    * Sizes the "check" column (the first one) such that it fits its column
    * header. Currently, this call "packColumn (0, 0)", which'll just make it
    * dynamically size according to the size of the window. This doesn't look
    * <i>great</i> but it'll get me by until I feel like fixing it later. 
    */
   public void packCheckColumn ()/*==>*/
   {
      packColumn (0, 0);
   }/*<==*/

   /**
    * Returns the "locked" status of an SI at a given row.
    *
    * @param r The row to look at
    *
    * @return The "locked" status of the SI at row "r"
    */
   protected Boolean getLockedStatusAtRow (int r)/*==>*/
   {
      return (Boolean)this.table.getModel().getValueAt(r, 0);
   }/*<==*/

   /**
    * Returns the Course associated with a given row in the table.
    *
    * @param r The row to look at
    * 
    * @return The Course in row "r"
    */
   protected String getCourseKeyAtRow (int r)/*==>*/
   {
      Course c = (Course)this.table.getModel().getValueAt(r, 1);

      return "" + c + c.getSection();
   }/*<==*/
}
