package edu.calpoly.csc.scheduler.view.desktop.menu.file;

import javax.swing.*;

import edu.calpoly.csc.scheduler.Scheduler;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;

import java.awt.*;
import java.awt.event.*;

import java.io.*;

import scheduler.*;
import scheduler.db.instructordb.*;

/**
 * Handles the saving of the Scheduler files.
 *
 * @author Eric Liebowitz
 * @version 20jul10
 */
public class FileSave
{
   /**
    * Displays the "Save File" dialog and acts accordingly to get Scheduler
    * data from the selected file. Prompts the user for confirmation if the
    * file already exists.
    *
    * Data is to be written in the following order:
    *
    * <ul>
    *    <li>The Schedule</li>
    *    <li>The local CDB</li>
    *    <li>The local IDB</li>
    *    <li>The local LDB</li>
    *    <li>TODO: The local PDB</li>
    * </ul>
    */
   public FileSave (JFileChooser fc)
   {
      if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
      {
         File f = fc.getSelectedFile();

         FileOutputStream fos;
         ObjectOutputStream oos = null;

         try
         {
            fos = new FileOutputStream(f);
            oos = new ObjectOutputStream(fos);
         }
         catch (FileNotFoundException e)
         {
            System.err.println ("File not found?");
         }
         catch (IOException e)
         {
            System.err.println ("Some kind of IO exception prepping");
         }
   
         writeSchedule (oos);
         writeLocalCDB (oos);
         writeLocalIDB (oos);
         writeLocalLDB (oos);
         writeLocalPDB (oos);
   
         try
         {
            oos.close();
         }
         catch (IOException e)
         {
            System.err.println ("IO exception while closing file");
         }
      }
   }

   /**
    * Writes the Schedule object into a given object output stream
    *
    * @param oos Object output stream to write to
    */
   private void writeSchedule (ObjectOutputStream oos)
   {
      try
      {
         oos.writeObject(Scheduler.getSchedule());
      }
      catch (IOException e)
      {
         System.err.println ("Error writing Schedule to file");
         e.printStackTrace();
      }
   }

   /**
    * Writes the local Course database into a given output stream.
    *
    * @param oos Object output stream to write to
    */
   private void writeLocalCDB (ObjectOutputStream oos)
   {
      try 
      {
         System.err.println ("Writing localCDB");
         System.err.println (Scheduler.getLocalCDB());
         oos.writeObject(Scheduler.getLocalCDB());
      }
      catch (IOException e)
      {
         System.err.println ("Error writing local CDB to file");
         e.printStackTrace();
      }
   }

   /**
    * Writes the local Instructor database into a given output stream
    * 
    * @param oos Object output stream to write to
    */
   private void writeLocalIDB (ObjectOutputStream oos)
   {
      try
      {
         for (Instructor i: Scheduler.getLocalIDB())
         {
            System.err.println (i);
            System.err.println ("CPrefs: " + i.getCoursePreferences());
         }
         oos.writeObject(Scheduler.getLocalIDB());
      }
      catch (IOException e)
      {
         System.err.println ("Error writing local IDB to file");
         e.printStackTrace();
      }
   }

   /**
    * Writes the local Location database into a given output stream
    * 
    * @param oos Object output stream to write to
    */
   private void writeLocalLDB (ObjectOutputStream oos)
   {
      try
      {
         oos.writeObject(Scheduler.getLocalLDB());
      }
      catch (IOException e)
      {
         System.err.println ("Error writing local LDB to file");
         e.printStackTrace();
      }
   }

   /**
    * Writes the local PreferencesDB into a given output stream. Currently, the
    * following parts of the PDB are written to the file (in this order):
    *
    *  - Local DaysForClasses data
    *
    * @param oos Object output stream to write to
    */
   private void writeLocalPDB (ObjectOutputStream oos)
   {
      try
      {
         oos.writeObject(Scheduler.pdb.getLocalDaysForClasses());
      }
      catch (IOException e)
      {
         System.err.println ("Error writing local PDB to file");
         e.printStackTrace();
      }
   }
}
