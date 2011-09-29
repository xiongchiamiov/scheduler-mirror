package edu.calpoly.csc.scheduler.view.desktop.menu.file;

import javax.swing.*;

import edu.calpoly.csc.scheduler.Scheduler;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import edu.calpoly.csc.scheduler.model.db.pdb.DaysForClasses;
import edu.calpoly.csc.scheduler.model.schedule.Schedule;

import java.awt.*;
import java.awt.event.*;

import java.io.*;
import java.util.Collection;

import scheduler.*;
import edu.calpoly.csc.scheduler.model.db.cdb.*;
import edu.calpoly.csc.scheduler.model.db.idb.*;
import edu.calpoly.csc.scheduler.model.db.ldb.*;
import scheduler.db.preferencesdb.*;

/**
 * Handles the opening of the Scheduler files.
 *
 * @author Eric Liebowitz
 * @version 20jul10
 */
public class FileOpen
{
   /**
    * Displays the "Open File" dialog and acts accordingly to restore Scheduler
    * data from the selected file. 
    *
    * Data is expected to be found in the following order:
    *
    * <ul>
    *    <li>The Schedule</li>
    *    <li>The CDB</li>
    *    <li>The IDB</li>
    *    <li>The LDB</li>
    *    <li>TODO: The PDB</li>
    * </ul>
    *
    * @param fc The FileChooser to be used for opening the file
    */
   public FileOpen (JFileChooser fc)
   {
      if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
      {
         FileInputStream fis;
         ObjectInputStream ois = null;
         try
         {
            fis = new FileInputStream(fc.getSelectedFile());
            ois = new ObjectInputStream(fis);
         }
         catch (FileNotFoundException e)
         {
            System.err.println ("File could not be opened");
         }
         catch (IOException e)
         {
            System.err.println ("Some kind of IO exception");
         }

         readSchedule (ois);
         readLocalCDB (ois);
         readLocalIDB (ois);
         readLocalLDB (ois);
         readLocalPDB (ois);
      }
   }

   /**
    * Reads a "Schedule" object from a given object stream and changes the 
    * Scheduler's data to this reflex this new object.
    *
    * @param ois Object file stream to read the Schedule object from
    */
   private void readSchedule (ObjectInputStream ois)
   {
      Schedule s = null;
      try
      {
         Scheduler.setSchedule((Schedule)ois.readObject());
      }
      catch (Exception e)
      {
         System.err.println ("Error reading Schedule. Got exception:");
         e.printStackTrace();
      }
   }

   /**
    * Reads the local cdb from a given object stream and changes the 
    * Scheduler's data to reflect this.
    *
    * @param ois Object file stream to read the local cdb data from
    */
   private void readLocalCDB (ObjectInputStream ois)
   {
      try
      {
         Scheduler.setLocalCDB((Collection<Course>)ois.readObject());
      }
      catch (Exception e)
      {
         System.err.println ("Error reading local CDB. Got exception:");
         e.printStackTrace();
      }
   }

   /**
    * Reads the local idb from a given object stream and changes the Scheduler's
    * data to reflect this.
    *
    * @param ois Object file stream to read the local idb data from 
    */
   private void readLocalIDB (ObjectInputStream ois)
   {
      try
      {
         Collection<Instructor> ci = (Collection<Instructor>) ois.readObject();
         Scheduler.setLocalIDB(ci);
      }
      catch (Exception e)
      {
         System.err.println ("Error reading local IDB. Got exception:");
         e.printStackTrace();
      }
   }

   /**
    * Reads the local ldb from a given object stream and changes the Scheduler's
    * data to reflect this.
    *
    * @param ois Object file stream to read the local ldb data from 
    */
   private void readLocalLDB (ObjectInputStream ois)
   {
      try
      {
         Scheduler.setLocalLDB((Collection<Location>)ois.readObject());
      }
      catch (Exception e)
      {
         System.err.println ("Error reading local LDB. Got exception:");
         e.printStackTrace();
      }
   }

   /**
    * Reads the local pdb from a given object stream and changes the Scheduler's
    * data to reflect this. Currently, the following pieces of the PDB are read
    * in (in this order):
    *
    *  - DaysForClasses preferences
    *
    * @param ois Object file stream to read the local pdb data from 
    */
   private void readLocalPDB (ObjectInputStream ois)
   {
      try
      {
         Scheduler.setLocalPDB_DFC((Collection<DaysForClasses>)ois.readObject());
      }
      catch (Exception e)
      {
         System.err.println ("Error reading local PDB. Got exception:");
         e.printStackTrace();
      }
   }

}
