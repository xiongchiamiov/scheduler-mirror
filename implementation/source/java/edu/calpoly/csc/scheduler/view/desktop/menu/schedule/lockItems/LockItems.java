package edu.calpoly.csc.scheduler.view.desktop.menu.schedule.lockItems;

import java.util.Observer;
import java.util.Observable;
import javax.swing.*;

import edu.calpoly.csc.scheduler.Scheduler;
import edu.calpoly.csc.scheduler.view.desktop.MyView;

import java.awt.*;
import java.awt.event.*;

import scheduler.*;
import edu.calpoly.csc.scheduler.model.schedule.*;

/**
 * Displays a table of all ScheduleItems in the current schedule (excluding any
 * that are TBA, of course), and allows the user to "lock" certain ones to force
 * the Scheduler to keep them where they are when the schedule is regenerated. 
 *
 * @author Eric Liebowitz
 * @version 03sep10
 */
public class LockItems extends MyView implements Observer
{
   private Box contentBox;
   private SiList siList;

   public LockItems ()
   {
      super ("Lock Schedule Items");
      Scheduler.getSchedule().addObserver(this);
      //siList = new SiList(Scheduler.getSchedule().getScheduleItems());

      contentBox = new Box (BoxLayout.Y_AXIS);
      //contentBox.add(siList);
      contentBox.add(Box.createVerticalStrut(15));
      contentBox.add(buttons());
      contentBox.add(Box.createVerticalStrut(15));

      this.add(contentBox);
   }

   private Box buttons ()
   {
      Box buttonRow = new Box (BoxLayout.X_AXIS);

      buttonRow.add(Box.createGlue());
      buttonRow.add(closeButton());
      buttonRow.add(Box.createHorizontalStrut(15));

      return buttonRow;
   }

   private JButton closeButton ()
   {
      JButton button = new JButton ("Close");
      button.addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               dispose ();
            }
         }
      );
      return button;
   }

   public void update (Observable observee, Object obj)
   {
      System.err.println ("Updated locked");
      //this.siList.refresh();
      System.err.println ("Done w/ locked");
   }
}
