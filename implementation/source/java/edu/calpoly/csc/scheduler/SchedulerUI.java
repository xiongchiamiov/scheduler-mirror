package edu.calpoly.csc.scheduler;

import javax.swing.*;

import edu.calpoly.csc.scheduler.view.desktop.MyView;
import edu.calpoly.csc.scheduler.view.desktop.menu.ScheduleMenuBar;

import java.awt.*;

import scheduler.menu.*;

/**
 * Represents the GUI for the Scheduler. 
 *
 * @author Eric Liebowitz
 * @version 08jun10
 */
public class SchedulerUI extends MyView
{
   /**
    * Creates the top-level menu bar
    */
   public SchedulerUI ()
   {
      super("Scheduler", JFrame.EXIT_ON_CLOSE);
   }

   /**
    * I don't know why this is here - Eric
    */
   public void compose() 
   { 
      this.setJMenuBar(new ScheduleMenuBar());
      this.pack();
   }

   public static void main (String[] args)
   {
      new SchedulerUI().show(150, 100);
   }
}
