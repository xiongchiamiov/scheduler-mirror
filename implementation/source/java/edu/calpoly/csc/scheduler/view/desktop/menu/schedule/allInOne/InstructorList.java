package edu.calpoly.csc.scheduler.view.desktop.menu.schedule.allInOne;

import scheduler.db.instructordb.*;

import java.util.Vector;
import javax.swing.*;

import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.idb.InstructorDB;

/**
 * Represents a checkbox list of the instructors in an instructor database. Note
 * that the instructors listed are those present in the -local- database, and 
 * not the remotely-global one.
 * 
 * @author Eric Liebowitz
 * @version 23jun10
 */
public class InstructorList extends GenList<Instructor>
{
   /**
    * Instructor database from when this list will grab its data to display
    */
   private InstructorDB idb;

   /**
    * Creates a list of instructors. Column headers are "Name".
    *
    * @param axis Direction the contents of the box are to go (hor. or vert.)
    * @param idb InstructorDB you want backing this list
    */
   public InstructorList (int axis, InstructorDB idb)
   {
      super (axis, new Vector<String>(), "Instructors: ");
      this.idb = idb;

      Vector<String> colData = new Vector<String>();
      colData.add(" ");
      colData.add("Name");
      this.table.getModel().setColumnIdentifiers(colData);

      refresh();
   }

   /**
    * Populates the list of instructors. For each instructor, his/her name are
    * displayed.
    */
   protected void populate ()
   {
      for (Instructor i: idb.getLocalData())
      {
         Vector<Object> row = new Vector<Object>();

         row.add(this.selections.contains(i));
         row.add(i);

         this.table.getModel().addRow(row);
      }
   }

   /**
    * What should go into the "selections" list: a deep copy of the Instructor.
    *
    * @param i The Instructor to copy
    * 
    * @return A deep copy of "i"
    */
   protected Instructor copy (Instructor i) { return new Instructor(i); }
}
