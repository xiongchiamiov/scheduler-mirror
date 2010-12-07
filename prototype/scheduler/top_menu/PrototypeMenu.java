package scheduler.top_menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PrototypeMenu extends JMenu 
{
   public PrototypeMenu ()
   {
      super ("Prototype");
      this.addNewItem  ();
      this.addOpenItem ();
   }

   protected void addNewItem ()
   {
      this.add (new JMenuItem("Calendar View...")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               new scheduler.view_ui.CalanderView().setVisible(true);
            }
         }
      );
   }

   protected void addOpenItem ()
   {
      this.add (new JMenuItem("Manual Edit...")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               new scheduler.manual_edit.ManualEdit().setVisible(true);
            }
         }
      );
   }
}
