package edu.calpoly.csc.scheduler.view.desktop.menu;

import edu.calpoly.csc.scheduler.view.desktop.menu.file.*;
import edu.calpoly.csc.scheduler.view.desktop.menu.schedule.*;

import javax.swing.*;

import edu.calpoly.csc.scheduler.view.desktop.menu.file.FileMenu;
import edu.calpoly.csc.scheduler.view.desktop.menu.schedule.ScheduleMenu;

/**
 * Represent the top-level-GUI's menu bar. 
 *
 * @author Eric Liebowitz
 * @version 08jun10
 */
public class ScheduleMenuBar extends JMenuBar
{
   /**
    * <pre>
    * Creates a menu bar with the following items:
    *
    *  - "File"
    *  - "Edit"
    *  - "Schedule"
    *  - "View"
    *  - "Database"
    *  - "Testing"
    * </pre>
    */
   public ScheduleMenuBar ()
   {
      super ();

      this.add(new FileMenu());
      this.add(new EditMenu());
      this.add(new ScheduleMenu());
      this.add(new ViewMenu());
      this.add(new DatabaseMenu());
      //this.add(new TestingMenu());
   }
}
