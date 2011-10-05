package edu.calpoly.csc.scheduler.view.desktop.menu.schedule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.Vector;

import edu.calpoly.csc.scheduler.view.desktop.menu.schedule.generate.*;

import edu.calpoly.csc.scheduler.model.Scheduler;

/**
 * Represents the GUI for the Scheudle drop-down in the topl-evel menu bar. This
 * menu allows the user to interact with the data particular to the current
 * Schedule. 
 *
 * @author Eric Liebowitz
 * @version 08jun10
 */
public class ScheduleMenu extends JMenu
{
   private Scheduler model;
   
   /**
    * <pre>
    * Creates the Schedule menu with the following items within:
    *
    *  - "Courses"
    *  - "Instructors"
    *  - "Locations"
    *  - "Preferences"
    *  - "Schedule"
    *  - "TBAs"
    *  - "AllInOne"
    * </pre>
    */
   public ScheduleMenu (Scheduler s)
   {
      super ("Schedule");

      this.model = s;
      
      addCourse();
      addInstructor();
      addLocation();
      addPreference();
      this.add(new JSeparator());
      
      addSchedule();
      addChangeHours();
      addTBAs();
      this.add(new JSeparator());

      addGenerate();
   }

   /**
    * Adds the "Courses" menu option to the menu. Opens a window at 150x150.
    *
    * Hooked = Yes.
    */
   private void addCourse ()/*==>*/
   {
      this.add(new JMenuItem("Courses")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Instructors" menu option. Opens a window at 150x150.
    *
    * Hooked = Yes.
    */
   private void addInstructor ()/*==>*/
   {
      this.add(new JMenuItem("Instructors")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Locations" menu option. Opens a window at 150x150.
    *
    * Hooked = Yes.
    */
   private void addLocation ()/*==>*/
   {
      this.add(new JMenuItem("Locations")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Preferences" menu option. Opens a window at 150x150.
    *
    * Hooked = Yes.
    */
   private void addPreference ()/*==>*/
   {
      this.add(new JMenuItem("Preferences")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Schedule" menu option. Opens a window at 150x150.
    *
    * Hooked = Yes.
    */
   private void addSchedule ()/*==>*/
   {
      
      this.add(new JMenuItem("Schedule")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               new ScheduleMenuItem().show(150, 150);
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "ChangeHours" menu option. Opens a window at 150x150.
    *
    * Hooked = Yes.
    */
   private void addChangeHours()/*==>*/
   {
      
      this.add(new JMenuItem("Change Hours")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               
            }
         }
      );
   }

   /**
    * Adds the "TBA's" menu option. Opens a window at 150x150.
    * 
    * Hooked = Yes.
    */
   private void addTBAs ()/*==>*/
   {
      this.add(new JMenuItem("TBA's")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
          
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "AllInOne" menu option. Opens a window at 150x150. To clarify, 
    * this window is where you'll probably go to generate schedules. All 
    * schedule-specific information is displayed in the window this spawns. 
    *
    * Hooked = Yes. 
    */
   private void addGenerate ()/*==>*/
   {
      /*
       * Make a single object so we don't go adding lots of the same observer.
       * (See AllInOne's constructor so see where observers are added for 
       * a better description).
       */
      final GenerateWindow gw = new GenerateWindow
      (
         model.getCourseDB(),
         model.getInstructorDB(),
         model.getLocationDB()
      );
      this.add(new JMenuItem("Generate")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               gw.show(150, 150);
            }
         }
      );
   }/*<==*/
}
