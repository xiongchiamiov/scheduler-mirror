package edu.calpoly.csc.scheduler.view.desktop;

import javax.swing.*;

import edu.calpoly.csc.scheduler.view.desktop.sched_display.SchedTableModel;
import edu.calpoly.csc.scheduler.view.desktop.menu.ScheduleMenuBar;

import edu.calpoly.csc.scheduler.model.schedule.Schedule;
/**
 *
 * @author jasonkilroy
 * @version Oct 3, 2011
 */
public class SchedulerUI extends JFrame
{
   Box content = Box.createVerticalBox();
   
   public SchedulerUI ()
   {
      this.setJMenuBar(new ScheduleMenuBar());
      
      this.add(new JScrollPane(new JTable(new SchedTableModel(new Schedule()))));     
      
      this.pack();
   }
   
   public static void main (String[] args)
   {
      new SchedulerUI().setVisible(true);
   }
}
