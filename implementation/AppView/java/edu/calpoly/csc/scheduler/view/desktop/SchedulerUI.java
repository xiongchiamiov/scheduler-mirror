package edu.calpoly.csc.scheduler.view.desktop;

import javax.swing.*;

import edu.calpoly.csc.scheduler.view.desktop.menu.ScheduleMenuBar;

/**
 *
 * @author jasonkilroy
 * @version Oct 3, 2011
 */
public class SchedulerUI extends JFrame
{
   public SchedulerUI ()
   {
      this.setJMenuBar(new ScheduleMenuBar());
      this.pack();
   }
   
   public static void main (String[] args)
   {
      new SchedulerUI().setVisible(true);
   }
}
