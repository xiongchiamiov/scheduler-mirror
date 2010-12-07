package scheduler.top_menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ScheduleMenu extends JMenu
{
   public ScheduleMenu ()
   {
      super ("Scheduler");
      
      this.addClassItem       ();
      this.addInstructorItem  ();
      this.addRoomItem        ();
      this.addPreferencesItem ();
      this.add (new JSeparator());
      this.addFairnessItem    ();
      this.addQualityItem     ();
      this.add (new JSeparator());
      this.addGenerateItem    ();
   }
   
   private void addClassItem ()
   {
      this.add(new JMenuItem ("Class...")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               System.out.println ("Schedule->Class selected");
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
               new scheduler.schedule_menu.PrototypeGUI().setVisible(true);
            }
         }
      );
   }
   
   private void addRoomItem ()
   {
      this.add(new JMenuItem ("Room...")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               System.out.println ("Schedule->Room selected");
            }
         }
      );
   }
   
   private void addPreferencesItem ()
   {
      this.add(new JMenuItem ("Preferences...")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               new scheduler.preferences_ui.PreferencesUI().setVisible(true);
            }
         }
      );
   }
   
   private void addFairnessItem ()
   {
      this.add(new JMenuItem ("Fairness...")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               System.out.println ("Schedule->Fairness selected");
            }
         }
      );
   }

   private void addQualityItem ()
   {
      this.add(new JMenuItem ("Quality...")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               new scheduler.quality.Quality().setVisible(true);
            }
         }
      );
   }

   private void addGenerateItem ()
   {
      this.add(new JMenuItem ("Generate...")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               new scheduler.generate_ui.Generation().setVisible(true);
            }
         }
      );
   }

}
