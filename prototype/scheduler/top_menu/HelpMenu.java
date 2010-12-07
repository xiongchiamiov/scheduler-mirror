package scheduler.top_menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HelpMenu extends JMenu
{
   public HelpMenu ()
   {
      super ("Help");
      
      this.addContentsItem         ();
      this.add (new JSeparator ());
      this.addAboutItem            ();
   }
   
   private void addContentsItem ()
   {
      this.add(new JMenuItem ("Contents...")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               System.out.println ("Help->Contents selected");
            }
         }
      );
   }
   
   private void addAboutItem ()
   {
      this.add(new JMenuItem ("About...")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               System.out.println ("Help->About selected");
            }
         }
      );
   }
}
