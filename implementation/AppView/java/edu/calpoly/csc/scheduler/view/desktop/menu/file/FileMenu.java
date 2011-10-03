package edu.calpoly.csc.scheduler.view.desktop.menu.file;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Represents the GUI for the File drop-down in the top-level menu bar.
 *
 * @author Eric Liebowitz
 * @version 08jun10
 */
public class FileMenu extends JMenu
{
   /**
    * The Schedule Data File extension
    */
   protected static final String extension = ".sdf";

   private JFileChooser fc;

   /**
    * <pre>
    * Creates the File menu with the following within:
    *
    *  - "New"
    *  - "Open"
    *  - "Close"
    *  - "Save"
    *  - "Save As"
    *  - "Print"
    *  - "Exit"
    * </pre>
    */
   public FileMenu ()
   {
      super ("File");
      
      fc = new JFileChooser ();
      fc.setFileFilter(new SchedulerFileFilter());

      addNew();
      addOpen();
      addClose();
      this.add(new JSeparator());

      addSave();
      addSaveAs();
      this.add(new JSeparator());
      
      addPrint();
      this.add(new JSeparator());

      addExit();
   }

   /**
    * Adds the "New" menu option to the menu.
    *
    * Hooked = Yes.
    */
   private void addNew ()/*==>*/
   {
      this.add(new JMenuItem("New")).addActionListener
      (
         new ActionListener() 
         {
            public void actionPerformed(ActionEvent e) 
            {
               new FileNewUI().show(150, 150);
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Open" menu option to the menu.
    *
    * Hooked = Yes.
    */
   private void addOpen ()/*==>*/
   {
      this.add(new JMenuItem("Open")).addActionListener
      (
         new ActionListener() 
         {
            public void actionPerformed(ActionEvent e) 
            {
               new FileOpen(fc);
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Close" menu option to the menu.
    *
    * Hooked = No.
    */
   private void addClose ()/*==>*/
   {
      this.add(new JMenuItem("Close")).addActionListener
      (
         new ActionListener() 
         {
            public void actionPerformed(ActionEvent e) 
            {
               System.err.println ("In File-->Close");
            }
         }
      );
   }/*<==*/

   /** 
    * Adds the "Save" menu option to the menu.
    *
    * Hooked = Yes.
    */
   private void addSave ()/*==>*/
   {
      this.add(new JMenuItem("Save")).addActionListener
      (
         new ActionListener() 
         {
            public void actionPerformed(ActionEvent e) 
            {
               new FileSave(fc);
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Save As" menu option to the menu.
    *
    * Hooked = No.
    */
   private void addSaveAs ()/*==>*/
   {
      this.add(new JMenuItem("Save As")).addActionListener
      (
         new ActionListener() 
         {
            public void actionPerformed(ActionEvent e) 
            {
               System.err.println ("In File-->Save As");
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Print" menu option to the menu.
    *
    * Hooked = No.
    */
   private void addPrint ()/*==>*/
   {
      this.add(new JMenuItem("Print")).addActionListener
      (
         new ActionListener() 
         {
            public void actionPerformed(ActionEvent e) 
            {
               System.err.println ("In File-->Print");
            }
         }
      );
   }/*<==*/

   /**
    * Adds the "Exit" menu option to the menu. 
    *
    * Hooked = Yes.
    */
   private void addExit ()/*==>*/
   {
      this.add(new JMenuItem("Exit")).addActionListener
      (
         new ActionListener() 
         {
            public void actionPerformed(ActionEvent e) 
            {
               System.exit(0);
            }
         }
      );
   }/*<==*/
}
