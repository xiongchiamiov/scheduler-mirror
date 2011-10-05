package edu.calpoly.csc.scheduler.view.desktop.menu;

import javax.swing.*;

import edu.calpoly.csc.scheduler.view.desktop.menu.file.*;
import edu.calpoly.csc.scheduler.view.desktop.menu.schedule.*;

import edu.calpoly.csc.scheduler.model.Scheduler;

/**
 * Represent the top-level-GUI's menu bar. 
 *
 * @author Eric Liebowitz
 * @version 08jun10
 */
public class ScheduleMenuBar extends JMenuBar
{
   private Scheduler model;
   /**
    * <pre>
    * Creates a menu bar with the following items:
    *
    *  - "File"
    *  - "Edit"
    *  - "Schedule"
    *  - "View"
    *  - "Database"
    * </pre>
    */
   public ScheduleMenuBar (Scheduler s)
   {
      super ();

      this.model = s;
      
      this.add(new FileMenu());
      this.add(new EditMenu());
      this.add(new ScheduleMenu(s));
      this.add(new ViewMenu());
      this.add(new DatabaseMenu());
   }
}
