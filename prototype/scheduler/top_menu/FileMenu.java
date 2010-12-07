package scheduler.top_menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FileMenu extends JMenu 
{
   public FileMenu ()
   {
      super ("File");
      
      this.addNewItem ();
      this.addOpenItem ();
      this.addRecentItem ();
      this.addSaveItem ();
      this.addSaveAsItem ();
      this.addPrintItem ();
      this.addPreviewItem ();
      this.add (new JSeparator());
      this.addExitItem ();
   }

   protected void addNewItem ()
   {
      this.add (new JMenuItem("New Schedule...")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               System.out.println ("File->New selected");
            }
         }
      );
   }

   protected void addOpenItem ()
   {
      this.add (new JMenuItem("Open Schedule...")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               new scheduler.schedule_menu.Open().setVisible(true);
               System.out.println ("File->Open selected");
            }
         }
      );
   }

   protected void addRecentItem ()
   {
      JMenu recent = new JMenu ("Open Recent");

      recent.add (new JMenuItem("First file")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               System.out.println ("File->OpenRecent->FirstFile selected");
            }
         }
      );
      
      recent.add(new JMenuItem ("Second file")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               System.out.println ("File->OpenRecent->SecondFile selected");
            }
         }
      );
      this.add(recent);
   }

   protected void addSaveItem ()
   {
      this.add (new JMenuItem ("Save")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               System.out.println ("File->Save selected");

            }
         }
      );
   }

   protected void addSaveAsItem ()
   {
      this.add (new JMenuItem ("Save As...")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               System.out.println ("File->SaveAs selected");
            }
         }
      );
   }

   protected void addPrintItem ()
   {
      this.add (new JMenuItem ("Print...")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               System.out.println ("File->Print selected");
            }
         }
      );
   }

   protected void addPreviewItem ()
   {
      this.add (new JMenuItem ("Print Preview...")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               System.out.println ("File->PrintPreview selected");
            }
         }
      );
   }

   protected void addExitItem ()
   {
      this.add (new JMenuItem ("Exit")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               new scheduler.confirm_close.ConfirmClose().setVisible(true);
            }
         }
      );
   }
}
