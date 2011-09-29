package edu.calpoly.csc.scheduler.view.desktop.menu.schedule.allInOne;

import java.util.Collection;
import java.util.Vector;
import javax.swing.*;

import edu.calpoly.csc.scheduler.Scheduler;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import edu.calpoly.csc.scheduler.model.db.pdb.SchedulePreference;
import edu.calpoly.csc.scheduler.view.desktop.MyView;

import java.awt.*;
import java.beans.*;

import scheduler.*;
import edu.calpoly.csc.scheduler.model.db.idb.*;
import edu.calpoly.csc.scheduler.model.db.cdb.*;
import edu.calpoly.csc.scheduler.model.db.ldb.*;

/**
 * For displaying the progress of generation to the user with a nice progress
 * bar. I don't completely understand the SwingWorker model, but the internet
 * said I needed it. I'm reasonably sure the contents of this class are sound,
 * but I <i>did</i> use some public fields to make things easier on myself. 
 * Beware. 
 *
 * In particular, this class is the one that actually starts the "generate"
 * process. In some magical way, it spawns the "generate" like a fork'd child
 * and is able to update its progress as the child goes. The "generate" process
 * is given this object so as to alter its progress as steps are completed. 
 *
 * @author Eric Liebowitz
 * @version 08jun10
 */
public class Progress extends SwingWorker<Void, Void>
{
   /** 
    * List of courses selected for generation 
    */
   private Vector<Course> cdb;
   /**
    * List of instructors selected for generation
    */
   private Vector<Instructor> idb;
   /**
    * List of locations selected for generation
    */
   private Vector<Location> ldb;
   /**
    * List of schedule preferences selected for generation
    */
   private Vector<SchedulePreference> pdb;
   /**
    * A frame to shove the progress bar in
    */
   private MyView pf;
   /**
    * The progress bar. The "generate" process will alter this. 
    */
   private JProgressBar pb;
   /**
    * Label of text to give some information about what's going on during
    * generation. The "generate" process will change this. 
    */
   public JLabel info;

   private int completed = 0;
   private int total;

   /**
    * Creates a progress bar whose total number of "thing to do" is equal to the
    * number of course sections in the database. Every section scheduled with 
    * increment the progress of the bar by one "tick". 
    *
    * @param cdb List of courses to generate with
    * @param idb List of instructors to generate with
    * @param ldb List of locations to generate with
    * @param pdb List of schedule preferences to generate with
    */
   public Progress (Vector<Course> cdb,
                    Vector<Instructor> idb,
                    Vector<Location> ldb,
                    Vector<SchedulePreference> pdb)
   {
      super ();
      this.cdb = cdb;
      this.idb = idb;
      this.ldb = ldb;
      this.pdb = pdb;

      /*
       * Create bounds for progress
       */
      this.total = 0;
      for (Course c: cdb)
      {
         this.total += c.getSection();
      }
      pb = new JProgressBar (0, 100);
      pb.setStringPainted(true);

      /*
       * Create text area for output
       */
      Box iBox = new Box(BoxLayout.X_AXIS);
      iBox.add(Box.createGlue());
      info = new JLabel ("_");
      iBox.add(info);
      iBox.add(Box.createGlue());

      /*
       * Put progress bar and info into a nice box
       */
      Box content = new Box(BoxLayout.Y_AXIS);
      content.add(pb);
      content.add(Box.createVerticalStrut(10));
      content.add(iBox);

      /*
       * Pack into frame and center it on screen
       */
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      pf = new MyView("Generating...");
      pf.add(content);
      pf.show(((dim.width - 200) / 2), 
              ((dim.height - 100) / 2));
      pf.setSize(250, 100);
      /*
       * Create action listener to update the progress bar
       */
      this.addPropertyChangeListener
      (
         new PropertyChangeListener ()
         {
            public void propertyChange(PropertyChangeEvent evt)
            {
               if (evt.getPropertyName().equals("progress"))
               {
                  pb.setValue((Integer)evt.getNewValue());
               }
            }
         }
      );
   }

   /**
    * I assume this does whatever I wanted to be done in the "background". IN 
    * this caes, that's the "generate" process
    */
   public Void doInBackground ()
   {
      Scheduler.schedule.generate (cdb, idb, ldb, pdb, this);
      return null;
   }

   /**
    * What should happen when the "background" process (generating) end. The 
    * progress bar should dissappear. 
    */
   protected void done ()
   {
      pf.setVisible(false);
   }

   /**
    * Updates the progress bar's value with new integer
    */
   public void completeOneTask ()
   {
      /*
       * Have to scale our percentage of completion to a value between 0 and 
       * 100, due to the limitations of the SwingWorker. The Progress Bar is
       * out of 100, so the value it will be updated with should be a good, 
       * up-to-date percentage value to display to the user.
       */
      this.completed ++;
      int progress = (int)((completed * 100) / (this.total + 0.0));
      this.setProgress(progress);
   }
}
