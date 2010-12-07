package scheduler.menu;

import scheduler.*;
import scheduler.menu.file.*;
import scheduler.menu.schedule.*;

import javax.swing.*;

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
      this.add(new TestingMenu());
   }
}
