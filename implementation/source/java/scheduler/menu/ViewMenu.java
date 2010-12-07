package scheduler.menu;

import scheduler.fair_qual.fair_qual_ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Represents the GUI for the View drop-down in the top-level menu bar. This
 * menu allows the user to manipulate how he/she views information about the
 * current schedule. 
 *
 * @author Eric Liebowitz
 * @version 08jun10
 */
public class ViewMenu extends JMenu
{
   /**
   * <pre>
   * Creates the Database Menu with the following item within:
   * 
   *  - "Course"
   *  - "Instructor"
   *  - "Location"
   *  - "Conflicts"
   *  - "Fairness"
   *  - "Quality"
   *  - "Advanced"
   * </pre>
   */
   public ViewMenu ()
   {
      super ("View");

      addCourse();
      addInstructor();
      addLocation();
      this.add(new JSeparator());
      
      addConflicts();
      addFairness();
      addQuality();     
      this.add(new JSeparator());

      addAdvanced();
   }

   /**
    * Adds the "Course" menu option to the menu. 
    * 
    * Hooked = No.
    */
   private void addCourse ()/*==>*/
   {
      this.add(new JMenuItem("Course")).addActionListener
      (
         new ActionListener() 
         {
            public void actionPerformed(ActionEvent e) 
            {
               System.err.println ("In View-->Course");
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Instructor" menu option to the menu.
    * 
    * Hooked = No.
    */
   private void addInstructor ()/*==>*/
   {
      this.add(new JMenuItem("Instructor")).addActionListener
      (
         new ActionListener() 
         {
            public void actionPerformed(ActionEvent e) 
            {
               System.err.println ("In View-->Instructor");
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Location" menu option to the menu.
    * 
    * Hooked = No.
    */
   private void addLocation ()/*==>*/
   {
      this.add(new JMenuItem("Location")).addActionListener
      (
         new ActionListener() 
         {
            public void actionPerformed(ActionEvent e) 
            {
               System.err.println ("In View-->Location");
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Advanced" menu option to the menu.
    *
    * Hooked = No.
    */
   private void addAdvanced ()/*==>*/
   {
      this.add(new JMenuItem("Advanced Filter...")).addActionListener
      (
         new ActionListener() 
         {
            public void actionPerformed(ActionEvent e) 
            {
               System.err.println ("In View-->Advanced Filter...");
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Conflics" menu option to the menu.
    * 
    * Hooked = No.
    */
   private void addConflicts ()/*==>*/
   {
      this.add(new JMenuItem("Conflicts")).addActionListener
      (
         new ActionListener() 
         {
            public void actionPerformed(ActionEvent e) 
            {
               System.err.println ("In View-->Conflicts");
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Fairness" menu option to the menu.
    *
    * Hooked = Yes.
    */
   private void addFairness ()/*==>*/
   {
      this.add(new JMenuItem("Fairness")).addActionListener
      (
         new ActionListener() 
         {
            public void actionPerformed(ActionEvent e) 
            {
               System.err.println ("In View-->Fairness");
               new FairUI().show(150, 150);
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Quality" menu option to the menu.
    * 
    * Hooked = No.
    */
   private void addQuality ()/*==>*/
   {
      this.add(new JMenuItem("Quality")).addActionListener
      (
         new ActionListener() 
         {
            public void actionPerformed(ActionEvent e) 
            {
               System.err.println ("In View-->Quality");
            }
         }
      );
   }/*<==*/
}

