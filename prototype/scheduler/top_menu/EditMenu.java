package scheduler.top_menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EditMenu extends JMenu
{
   public EditMenu ()
   {
      super ("Edit");
      this.addUndoItem        ();
      this.addRedoItem        ();
      this.addCutItem         ();
      this.addCopyItem        ();
      this.addPasteItem       ();
      this.addFindItem        ();
      this.addSelectAllItem   ();
      this.add(new JSeparator ());
      this.addPreferencesItem ();
   }

   private void addUndoItem ()
   {
      this.add(new JMenuItem ("Undo")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               System.out.println ("Edit->Undo selected");
            }
         }
      );
   }

   private void addRedoItem()
   {
      this.add(new JMenuItem ("Redo")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               System.out.println ("Edit->Redo selected");
            }
         }
      );
   }

   private void addCutItem ()
   {
      this.add(new JMenuItem ("Cut")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               System.out.println ("Edit->Cut selected");
            }
         }
      );
   }

   private void addCopyItem ()
   {
      this.add(new JMenuItem ("Copy")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               System.out.println ("Edit->Copy selected");
            }
         }
      );
   }

   private void addPasteItem ()
   {
      this.add(new JMenuItem ("Paste")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               System.out.println ("Edit->Paste selected");
            }
         }
      );
   }

   private void addFindItem ()
   {
      this.add(new JMenuItem ("Find...")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               System.out.println ("Edit->Find selected");
            }
         }
      );
   }

   private void addSelectAllItem ()
   {
      this.add(new JMenuItem ("Select All")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               System.out.println ("Edit->SelectAll selected");
            }
         }
      );
   }

   private void addPreferencesItem ()
   {
      this.add(new JMenuItem ("Preferences...")).addActionListener
      (
         new ActionListener ()
         {
            public void actionPerformed (ActionEvent e)
            {
               System.out.println ("Edit->Preferences selected");
            }
         }
      );
   }
}
