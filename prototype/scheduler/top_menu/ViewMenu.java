package scheduler.top_menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ViewMenu extends JMenu
{
   public ViewMenu ()
   {
      super ("View");
      this.addCourseItem     ();
      this.addInstructorItem ();
      this.addRoomItem       ();
      this.addConflictsItem  ();
      
   }

   private void addCourseItem ()
   {
      this.add(new JMenuItem ("Course...")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               new scheduler.view_ui.CourseViewSetting().setVisible(true);
            }
         }
      );
   }
   
   private void addInstructorItem ()
   {
      this.add(new JMenuItem ("Instructor...")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               new scheduler.view_ui.InstructorViewSetting().setVisible(true);
            }
         }
      );
   }

   private void addRoomItem ()
   {
      this.add(new JMenuItem ("Location...")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               new scheduler.view_ui.LocationViewSetting().setVisible(true);
            }
         }
      );
   }

   private void addConflictsItem ()
   {
      this.add(new JMenuItem ("Conflicts...")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               new scheduler.constraintviolation.ConstraintViolation().setVisible(true);
            }
         }
      );
   }
}
