package scheduler.menu.schedule.allInOne;

import java.util.Observer;
import java.util.Collections;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import scheduler.*;
import scheduler.generate.*;
import scheduler.db.*;
import scheduler.db.instructordb.*;
import scheduler.db.coursedb.*;
import scheduler.db.locationdb.*;
import scheduler.db.preferencesdb.*;
import scheduler.fair_qual.*;
import scheduler.menu.schedule.*;

/**
 * Displays lists of each database to be used in schedule generation.
 * The user may select from these lists any/all variables which they wish
 * to incorporate into a generated schedule.
 *
 * @author Eric Liebowitz
 * @version 08jun10
 */
public class AllInOne extends MyView implements Observer
{
   /* Instance variables ==>*/
   /**
    * For "serializable", I assume
    */
   public static final long serialVersionUID = 0;

   /**
    * Where the contents of this JFrame will be arranged
    */
   protected Box outermostVertBox;

   /**
    * The schedule's course database
    */
   protected CourseDB cdb;
   /**
    * The schedule's instructor database
    */
   protected InstructorDB idb;
   /**
    * The schedule's location database
    */
   protected LocationDB ldb;
   /**
    * The schedule's preference database
    */
   protected PreferencesDB pdb;

   /**
    * List of courses the user selects for generation
    */
   protected static CourseList cList = 
      new CourseList (BoxLayout.Y_AXIS, Scheduler.cdb);
   /**
    * List of instructors the user selects for generation
    */
   protected static InstructorList iList = 
      new InstructorList (BoxLayout.Y_AXIS, Scheduler.idb);
   /**
    * List of locations the user selects for generation
    */
   protected static LocationList lList = 
      new LocationList (BoxLayout.Y_AXIS, Scheduler.ldb);
   /**
    * List of preferences the user selects for generation
    */
   protected static PreferenceList pList = 
      new PreferenceList (BoxLayout.Y_AXIS, Scheduler.pdb);

   /**
    * Determines whether the Instructor's should be sorted before generation
    * or not
    */
   protected boolean asIs;
   /*<==*/

   /**
    * <pre>
    * Creates the Generate window. Adds the following comopnents to a JFrame:
    *
    *    SelectAllFields Button
    *    Course List
    *    Instructor List
    *    Location List
    *    Select All/Clear fields for all of the above lists
    *    Generate button
    *    Cancel button
    * </pre>
    */
   public AllInOne ()/*==>*/
   {
      super ("Generate a Schedule...");

      init ();
      createGUI();
   }/*<==*/

   /**
    * Instantiates the "outermostVertBox" and all 4 databases (cdb, idb, ldb, 
    * pdb). Adds this object as an observer of all of these databases. Inits
    * 'asIs' to false.
    */
   private void init ()/*==>*/
   {
      outermostVertBox = new Box (BoxLayout.Y_AXIS);
      cdb = Scheduler.cdb;
      idb = Scheduler.idb;
      ldb = Scheduler.ldb;
      pdb = Scheduler.pdb;
      
      cdb.addObserver(this);
      idb.addObserver(this);
      ldb.addObserver(this);
      pdb.addObserver(this);

      asIs = false;
   }/*<==*/

   /**
    * Adds the action to be executed when the "Genenerate" button gets clicked. 
    * All will be searched for whatever was selected, and those will be passed
    * to the appropriate list of selections. If no courses are selected, an 
    * error window is displayed and generation does not spawn. 
    *
    * When pressed, this will make the "AllInOne" window dissappear.
    *
    * @return the JButton with the above action associated with it. 
    */
   private JButton addGenerate ()/*==>*/
   {
      JButton button = new JButton("Generate");
      /*
       * HACK:
       * 
       * Since the closure below would think "this" as itself, saying 
       * "this.setVisible(false)" wouldn't work. So, this local variable will
       * be accessible to the closure. "setVisible" can then be easily called
       * on it.
       */
      final JFrame me = this;

      button.addActionListener
      (
         new ActionListener()
         {
            public void actionPerformed (ActionEvent e)
            {
               cList.gatherSelected();
               iList.gatherSelected();
               lList.gatherSelected();
               pList.gatherSelected();

               /*
                * Sort instructors unless the user said otherwise (natural 
                * sorting = ascending generosity)
                */
               if (!asIs)
               {
                  Collections.sort(iList.selections);
                  System.err.println ("Sorted selectioned:\n" + iList.selections);
               }

               if (cList.selections.isEmpty())
               {
                  JOptionPane.showMessageDialog(null, 
                     "You must select at least one course.", 
                     "Generate Error", 
                     JOptionPane.ERROR_MESSAGE);
                  return;
               }
               for (Course c: cList.selections)
               {
                  System.err.println ("COURSE: " + c);
                  System.err.println ("LAB: " + c.getLab());
               }

               /*
                * Close the window 
                */
               me.setVisible(false);
               /*
                * Generate and display progress
                */
               new Progress(cList.selections, 
                            iList.selections, 
                            lList.selections,
                            pList.selections).execute();
            }
         }
      );
      return button;
   }/*<==*/

   /**
    * Updates the lists when any of the observable targets change
    *
    * @param obs Object observed
    * @param obj I don't know
    */
   public void update (Observable obs, Object obj)/*==>*/
   {
      cList.refresh();
      iList.refresh();
      lList.refresh();
      pList.refresh();
   }/*<==*/

   /**
    * <pre>
    * Creates the windows GUI, placing everything where it needs to go. In 
    * particular, from top to bottom:
    * 
    *  - Adds a greeting message
    *  - Adds a "select all fields" button, for selecting everything at once
    *  - Horizontally, adds lists courses/instructor/locations/preferneces
    *  - Adds a "Cancel" and "Generate" button
    *
    * </pre>
    */
   private void createGUI ()/*==>*/
   {
      outermostVertBox.add(vSpace(15));
      outermostVertBox.add(generateMessagePrompt());
      outermostVertBox.add(Box.createHorizontalGlue());

      outermostVertBox.add(vSpace(15));
      outermostVertBox.add(addSelectAllFieldsButton());

      outermostVertBox.add(vSpace(15));
      outermostVertBox.add(addContents());

      outermostVertBox.add(vSpace(50));
      outermostVertBox.add(addBottomButtons());

      this.add(outermostVertBox);
      this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); 
   }/*<==*/

   /**
    * Creates a box contains a message to tell the user what to do with this 
    * window.
    *
    * @return A hBox with the message in it
    */
   private Box generateMessagePrompt ()/*==>*/
   {
      Box contents = new Box (BoxLayout.X_AXIS);
      contents.add(hSpace(15));
      contents.add(new JLabel("Select the elements you wish to include from the current schedule's database...", SwingConstants.LEFT));
      contents.add(hSpace(15));
      contents.add(Box.createHorizontalGlue());
      return contents;
   }/*<==*/

   /**
    * Adds the "SelectAllFields" button.
    *
    * @return A hBox with the "SelectAllFields" button in it
    */
   private Box addSelectAllFieldsButton ()/*==>*/
   {
      Box contents = new Box (BoxLayout.X_AXIS);
      JButton button = new JButton("Select All Fields");
      button.addActionListener(new ActionListener()
      {
         public void actionPerformed (ActionEvent e)
         {
            cList.table.selectAll();
            iList.table.selectAll();
            lList.table.selectAll();
            pList.table.selectAll();
         }
      });
      contents.add(Box.createGlue());
      contents.add(button);
      contents.add(Box.createGlue());
      return contents;
   }/*<==*/

   /**
    * Adds what really matters for generating: the list of all things to 
    * incorporate into the Schedule, and a row of options to alter generation's
    * behavior.
    *
    * @return a Y_AXIS-oriented box containing the above data.
    */
   private Box addContents ()/*==>*/
   {
      Box contents = new Box (BoxLayout.Y_AXIS);

      contents.add(addLists());
      contents.add(vSpace(30));
      contents.add(addAsIsCheckbox());

      return contents;
   }/*<==*/

   /**
    * Adds lists for Courses, Instructors, Locations, and Preference.
    *
    * NOTE: Preferences are currently not displayed, as nothing they contain
    *       can be used in generation yet. 
    *
    * @return An hBox with lists for instructors, locations, and courses
    */
   private Box addLists ()/*==>*/
   {
      Box contents = new Box (BoxLayout.X_AXIS);

      contents.add(Box.createGlue());
      contents.add(cList);
      contents.add(Box.createGlue());
      contents.add(iList);
      contents.add(Box.createGlue());
      contents.add(lList);
      contents.add(Box.createGlue());
      contents.add(pList);
      contents.add(Box.createGlue());


      return contents;
   }/*<==*/

   /** 
    * Adds the checkbox for whether the Instructors should be sorted according to
    * greediness or whether they should be sorted as the user sees them in the
    * list of Instructors.
    *
    * @return An hBox with the checkbox in it, left-justified
    */
   private Box addAsIsCheckbox ()/*==>*/
   {
      Box contents = new Box(BoxLayout.X_AXIS);

      final JCheckBox asIsBox = new JCheckBox ("Sort 'as is'");
      asIsBox.addActionListener
      (
         new ActionListener ()
         {
            /*
             * Change the state of the "asIs" variable according to the state of
             * of the checkbox
             */
            public void actionPerformed (ActionEvent e)
            {
               asIs = asIsBox.isSelected();
               System.err.println ("As is: " + asIs);
            }
         }
      );

      contents.add(asIsBox);
      contents.add(Box.createHorizontalGlue());

      return contents;
   }/*<==*/

   /**
    * Adds Generate and Cancel buttons
    *
    * @return A box containing Cancel and Generate buttons
    */
   private Box addBottomButtons ()/*==>*/
   {
      Box contents = new Box (BoxLayout.X_AXIS);

      contents.add(Box.createGlue());
      contents.add(addCancel());
      contents.add(hSpace(15));
      contents.add(addGenerate());
      contents.add(hSpace(15));
      return contents;
   }/*<==*/
   
   /**
    * Creates a cancel button
    *
    * @return a cancel button
    */
   private JButton addCancel ()/*==>*/
   {
      JButton button = new JButton("Cancel");
      button.addActionListener
      (
         new ActionListener()
         {
            public void actionPerformed (ActionEvent e)
            {
               dispose();
               System.out.println ("Canceling generation");
            }
         }
      );
      return button;
   }/*<==*/

   /**
    * Makes it easier to make horizontal space
    *
    * @return A horizontal component of the given size
    */
   private Component hSpace (int size)/*==>*/
   {
      return Box.createHorizontalStrut(size);
   }/*<==*/

   /**
    * Makes is easier to make vertical space
    *
    * @return A vertical component of the given size
    */
   private Component vSpace (int size)/*==>*/
   {
      return Box.createVerticalStrut(size);
   }/*<==*/
}
