package edu.calpoly.csc.scheduler.view.desktop.menu.schedule;

import javax.swing.*;

import edu.calpoly.csc.scheduler.Scheduler;
import edu.calpoly.csc.scheduler.model.schedule.ScheduleItem;
import edu.calpoly.csc.scheduler.view.desktop.MyView;

import java.awt.*;
import java.awt.event.*;
import java.util.Observer;
import java.util.Vector;

import scheduler.*;
import edu.calpoly.csc.scheduler.model.db.cdb.*;

/**
 * Displays a list of all courses which are TBA. These courses will have 
 * instructors ready to teach them, but lack a location and time to teach them
 * at.
 *
 * @author Eric Liebowitz
 * @version 14jul10
 */
public class TBA extends MyView
{
   private Box contentBox; 

   private JScrollPane tbaScrollList;

   private Box buttonBox;

   /**
    * Creates the TBA windows. Reaches into the Scheduler's "schedule" instance
    * variable (the "Schedule") to get its data. This is the piece of data
    * the window will be observing for changes. 
    */
   public TBA ()
   {
      super ("TBA's");
      init ();
      createGUI();
   }

   private void init ()
   {
      contentBox = new Box (BoxLayout.Y_AXIS);

      JList tbaList = new JList (Scheduler.schedule.getTBAs());
      tbaList.setLayoutOrientation(JList.VERTICAL);
      tbaScrollList = new JScrollPane (tbaList);

      buttonBox = new Box (BoxLayout.X_AXIS);

      //Scheduler.schedule.addObserver(this);
   }

   private void createGUI ()
   {
      contentBox.add(Box.createGlue());
      contentBox.add(Box.createVerticalStrut(15));
      contentBox.add(tbaScrollList);
      contentBox.add(Box.createVerticalStrut(5));

      contentBox.add(makeButtonRow());

      contentBox.add(Box.createVerticalStrut(5));
      contentBox.add(Box.createGlue());
      this.add(contentBox);
   }

   private Box makeButtonRow ()
   {
      buttonBox.add(Box.createGlue());
      buttonBox.add(closeButton());
      buttonBox.add(Box.createGlue());
      return buttonBox;
   }

   private JButton closeButton ()
   {
      JButton button = new JButton ("Close");
      button.addActionListener
      (
         new ActionListener()
         {
            public void actionPerformed (ActionEvent e)
            {
               dispose();
            }
         }
      );
      return button;
   }
}
