package scheduler.top_menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FullMenu extends JMenuBar
{
   private FileMenu      fileMenu;
   private EditMenu      editMenu;
   private ViewMenu      viewMenu;
   private ScheduleMenu  scheduleMenu;
   private HelpMenu      helpMenu;
   private PrototypeMenu prototypeMenu;

   public FullMenu ()
   {
      super ();

/*      fileMenu =      new FileMenu      ();
      editMenu =      new EditMenu      ();
      viewMenu =      new ViewMenu      ();
      scheduleMenu =  new ScheduleMenu  ();
      helpMenu =      new HelpMenu      ();
      prototypeMenu = new PrototypeMenu ();*/
      
      this.add(new FileMenu ());
      this.add(new EditMenu ());
      this.add(new ViewMenu ());
      this.add(new ScheduleMenu ());
      this.add(new HelpMenu ());
      this.add(new PrototypeMenu ());
   }
}
