package scheduler.menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Represents the GUI for the Edit drop-down in the top-level menu bar. 
 *
 * @author Eric Liebowitz
 * @version 08jun10
 */
public class EditMenu extends JMenu
{
   /**
    * <pre>
    * Creates the Edit Menu with the following items within:
    *
    *  - "Cut"
    *  - "Copy"
    *  - "Paste"
    *  - "Delete"
    *  - "Find"
    * </pre>
    */
   public EditMenu ()
   {
      super ("Edit");

      addCut();
      addCopy();
      addPaste();
      addDelete();
      this.add(new JSeparator());

      addFind();
   }

   /**
    * Adds the "Cut" menu option to the menu.
    *
    * Hooked = No.
    */
   private void addCut ()/*==>*/
   {
      this.add(new JMenuItem("Cut")).addActionListener
      (
         new ActionListener() 
         {
            public void actionPerformed(ActionEvent e) 
            {
               System.err.println ("In Edit-->Cut");
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Copy" menu option to the menu.
    *
    * Hooked = No.
    */
   private void addCopy ()/*==>*/
   {
      this.add(new JMenuItem("Copy")).addActionListener
      (
         new ActionListener() 
         {
            public void actionPerformed(ActionEvent e) 
            {
               System.err.println ("In Edit-->Copy");
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Paste" menu option to the menu.
    *
    * Hooked = No.
    */
   private void addPaste ()/*==>*/
   {
      this.add(new JMenuItem("Paste")).addActionListener
      (
         new ActionListener() 
         {
            public void actionPerformed(ActionEvent e) 
            {
               System.err.println ("In Edit-->Paste");
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Delete" menu option to the menu.
    *
    * Hooked = No.
    */
   private void addDelete ()/*==>*/
   {
      this.add(new JMenuItem("Delete")).addActionListener
      (
         new ActionListener() 
         {
            public void actionPerformed(ActionEvent e) 
            {
               System.err.println ("In Edit-->Delete");
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Find" menu option to the menu.
    *
    * Hooked = No.
    */
   private void addFind ()/*==>*/
   {
      this.add(new JMenuItem("Find...")).addActionListener
      (
         new ActionListener() 
         {
            public void actionPerformed(ActionEvent e) 
            {
               System.err.println ("In Edit-->Find...");
            }
         }
      );
   }/*<==*/

}
