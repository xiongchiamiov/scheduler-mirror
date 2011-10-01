package edu.calpoly.csc.scheduler;


import scheduler.db.*;
import edu.calpoly.csc.scheduler.model.db.cdb.*;
import edu.calpoly.csc.scheduler.model.db.idb.*;
import edu.calpoly.csc.scheduler.model.db.ldb.*;
import scheduler.view.*;


import javax.swing.*;

import edu.calpoly.csc.scheduler.model.db.SQLDB;
import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.cdb.CourseDB;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.idb.InstructorDB;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import edu.calpoly.csc.scheduler.model.db.ldb.LocationDB;
import edu.calpoly.csc.scheduler.model.db.pdb.DaysForClasses;
import edu.calpoly.csc.scheduler.model.db.pdb.PreferencesDB;
import edu.calpoly.csc.scheduler.model.schedule.Schedule;
import edu.calpoly.csc.scheduler.view.desktop.MyView;
import edu.calpoly.csc.scheduler.view.desktop.old_view.View;

import java.awt.*;
import java.awt.event.*;
import java.util.Properties;
import java.io.PrintStream;
import java.io.Serializable;

import java.util.Collection;
import java.util.Vector;

/**
 * Top level class which will launch the GUI's main loop. Contains commonly used
 * used information, such as databases and the current schedule. 
 *
 * @author Eric Liebowit
 * @version 08jun10
 */
public class Scheduler implements Serializable
{
   /* Global, database variables ==>*/

   /** Whole database */
   public static SQLDB schedDB;

   /** Course database */
   public static CourseDB cdb;

   /** Instructor database */
   public static InstructorDB idb;

   /** Location database */
   public static LocationDB ldb;

   /** Preference database */
   public static PreferencesDB pdb;

   /** Scheduler viewing module */
   public static View schedView;

   public static SchedulerUI schedulerUI;

   public static Time startTime = new Time("7:00");
   public static Time endTime = new Time("22:00");

   /*<==*/

   /* Scheudle ==>*/
   /** The very-sacred Schedule */
   public static Schedule schedule = new Schedule ();
   /*<==*/

   /**
    * Initializes all databases. 
    */
   public Scheduler ()
   {
      if(!System.getProperty("java.version").startsWith("1.6")
       && !System.getProperty("java.version").startsWith("1.7")) {
         JOptionPane.showMessageDialog(schedulerUI,
          "The Scheduler requires Java " +
           "6 or higher to run. To download the latest version of Java, " +
           "visit http://java.com/en/.",
          "Java Runtime Environment out of date",
          JOptionPane.ERROR_MESSAGE);
         System.exit(1);
      }       

      schedDB = new SQLDB ();
      schedDB.open();

      /** Preference database must be created first, so they can appear in the courseDB.
       *  -Jan Lorenz Soliman
       */
      pdb = schedDB.getPreferencesDB  ();
      cdb = schedDB.getCourseDB       ();
      idb = schedDB.getInstructorDB   ();
      ldb = schedDB.getLocationDB     ();
   }

   public void spawn (int x, int y)
   {
      schedulerUI = new SchedulerUI();

      if(!System.getProperty("java.version").startsWith("1.6") && 
         !System.getProperty("java.version").startsWith("1.7")) 
       {
         JOptionPane.showMessageDialog(schedulerUI,
          "The Scheduler requires Java " +
           "6 or higher to run. To download the latest version of Java, " +
           "visit http://java.com/en/.",
          "Java Runtime Environment out of date",
          JOptionPane.ERROR_MESSAGE);
         System.exit(1);
      }
      try
      {
         /*
          * Stop changing this - Eric
          */
         UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
      }
      catch (Exception e)
      {
         System.err.println ("Couldn't get the cool UI");
      }

      schedView = new View(this);
      schedView.initSettings();

      schedulerUI.compose();

      schedulerUI.show(x, y);
   }

   /**
    * Debug function
    *
    **/
   public static void debug(String message) 
   {
      //Set to true if debugging
      boolean check = false;

      if (check) 
      {
         System.out.println (message);
      }

   }

   /**
    * I don't know why this is here - Eric
    */
   public MyView getView() 
   {
      return schedulerUI;
   }

   /**
    * Returns the almighty schedule
    *
    * @return the Schedule
    */
   public static Schedule getSchedule ()
   {
      return schedule;
   }

   /**
    * Returns the local Course database
    * 
    * @return the local course database
    */
   public static Collection<Course> getLocalCDB ()
   {
      return cdb.getLocalData();
   }

   /**
    * Returns the local Instructor database
    *
    * @return the local Instructor database
    */
   public static Collection<Instructor> getLocalIDB ()
   {
      return idb.getLocalData();
   }

   /**
    * Returns the local Location database
    *
    * @return the local Location database
    */
   public static Collection<Location> getLocalLDB ()
   {
      return ldb.getLocalData();
   }

   /**
    * Changes the Schedule to a new one and notifies views as necessary to 
    * display it.
    *
    * @param s New schedule to set
    */
   public static void setSchedule (Schedule s)
   {
      schedule.replaceWithThisFromFile(s);
   }

   /**
    * Changes the local CDB to a new one from a file. Should notify views as 
    * necessary.
    *
    * @param data Data to set CDB to
    */
   public static void setLocalCDB (Collection<Course> data)
   {
      System.err.println ("Opening local cdb");
      System.err.println (data);
      cdb.setLocalWithThisFromFile(data);
      System.err.println ("Done opening cdb");
   }
   
   /**
    * Changes the local IDB to a new one from a file. Should notify views as 
    * necessary.
    *
    * @param data Data to set IDB to
    */
   public static void setLocalIDB (Collection<Instructor> data)
   {
      System.err.println ("Opening local idb");
      System.err.println (data);
      idb.setLocalWithThisFromFile(data);
   }

   /**
    * Changes the local LDB to a new one from a file. Should notify views as 
    * necessary.
    *
    * @param data Data to set LDB to
    */
   public static void setLocalLDB (Collection<Location> data)
   {
      System.err.println ("Opening local ldb");
      System.err.println (data);
      ldb.setLocalWithThisFromFile(data);
   }

   /**
    * Changes the local PDB's DaysForClasses to those from a file. Should 
    * notify views as necessary.
    *
    * NOTE: Naming convention breaks here, as the PDB is split up differently
    *       from the other DB's.
    *
    * @param data Data to set PDB to
    */
   public static void setLocalPDB_DFC (Collection<DaysForClasses> data)
   {
      System.err.println ("Opening local pdb dfc's");
      System.err.println (data);
      pdb.setLocalDFCWithThisFromFile(data);
   }

   /**
    * Dumps relevant information to a given PrintStream (most often a file) in 
    * a format which is easy for Perl to parse and use. Currently, the 
    * following methods are called to dump:
    * <ul>
    *    <li>this.idb.dumpLocalAsPerlText</li>
    *    <li>this.schedule.dumpAsPerlText</li>
    * </ul>
    *
    * @param ps PrintStream to dump text to.
    */
   public void dumpAsPerlText (PrintStream ps)
   {
      this.idb.dumpLocalAsPerlText(ps);
      this.schedule.dumpAsPerlText(ps);
   }

   /**
    * What runs everything
    */
   public static void main (String[] args)
   {
      new Scheduler().spawn(150, 100);
   }
}
