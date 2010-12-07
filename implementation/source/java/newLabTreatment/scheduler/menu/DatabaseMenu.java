package scheduler.menu;

import scheduler.db.admin.admin_ui.*;
import scheduler.db.preferencesdb.preferences_ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import scheduler.Scheduler;

/**
 * Represents the GUI for the Database drop-down in the top-level menu bar. This
 * menu allows the user to interact with the remotely-accessed MYSQL database.
 *
 * @author Eric Liebowitz
 * @version 08jun10
 */
public class DatabaseMenu extends JMenu
{
   /**
    * <pre>
    * Creates the Database Menu with the following items within:
    *
    *  - "Courses"
    *  - "Instructors"
    *  - "Locations"
    *  - "Preferences"
    * </pre>
    */
   public DatabaseMenu ()
   {
      super ("Databases");

      addCourses();
      addInstructors();
      addLocations();
      addPreferences();

      this.add(new JSeparator());
      addSync();
   }

   /**
    * Adds the "Courses" menu option to the menu.
    *
    * Hooked = Yes.
    */
   private void addCourses ()/*==>*/
   {
      this.add(new JMenuItem("Courses")).addActionListener
      (
         new ActionListener() 
         {
            public void actionPerformed(ActionEvent e) 
            {
               if (Scheduler.schedDB.isConnected()) {
                  new CourseView().show(150, 150);
               }
               else {
                  JOptionPane.showMessageDialog(Scheduler.schedulerUI,
                   "You are not connected to the database.",
                   "Error",
                   JOptionPane.ERROR_MESSAGE);
               }
            }
         }
      );
   }/*<==*/
   
   /**
    * Adds the "Instructors" menu option to the menu.
    * 
    * Hooked = Yes.
    */
   private void addInstructors ()/*==>*/
   {
      this.add(new JMenuItem("Instructors")).addActionListener
      (
         new ActionListener() 
         {
            public void actionPerformed(ActionEvent e) 
            {
               if (Scheduler.schedDB.isConnected()) {
                  new InstructorViewAlt().show(150, 150);
               }
               else {
                  JOptionPane.showMessageDialog(Scheduler.schedulerUI,
                   "You are not connected to the database.",
                   "Error",
                   JOptionPane.ERROR_MESSAGE);
               }
            }
         }
      );
   }/*<==*/

  /**
   * Adds the "Locations" menu option to the menu.
   *
   * Hooked = Yes.
   */
private void addLocations ()/*==>*/
   {
      this.add(new JMenuItem("Locations")).addActionListener
      (
         new ActionListener() 
         {
            public void actionPerformed(ActionEvent e) 
            {
               if (Scheduler.schedDB.isConnected()) {
                  new LocationView().show(150, 150);
               }
               else {
                  JOptionPane.showMessageDialog(Scheduler.schedulerUI,
                   "You are not connected to the database.",
                   "Error",
                   JOptionPane.ERROR_MESSAGE);
               }
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Preferences" menu option to the menu.
    *
    * Hooked = Yes.
    */
   private void addPreferences ()/*==>*/
   {
      this.add(new JMenuItem("Preferences")).addActionListener
      (
         new ActionListener() 
         {
            public void actionPerformed(ActionEvent e) 
            {
               if (Scheduler.schedDB.isConnected()) {
                  new scheduler.db.preferencesdb.preferences_ui.PreferencesUI().show(150, 150);
               }
               else {
                  JOptionPane.showMessageDialog(Scheduler.schedulerUI,
                   "You are not connected to the database.",
                   "Error",
                   JOptionPane.ERROR_MESSAGE);
               }
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Sync" menu option to the menu, used to copy data from local DB
    * to global DB, and vice versa.
    *
    * Hooked = Yes.
    */
   private void addSync () /*==>*/
   {
      this.add(new JMenuItem("Sync")).addActionListener
      (
         new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               if (Scheduler.schedDB.isConnected()) {
                  new PublishData().show(150, 150);
               }
               else {
                  JOptionPane.showMessageDialog(Scheduler.schedulerUI,
                   "You are not connected to the database.",
                   "Error",
                   JOptionPane.ERROR_MESSAGE);
               }
            }
         }
      );
   }
}
