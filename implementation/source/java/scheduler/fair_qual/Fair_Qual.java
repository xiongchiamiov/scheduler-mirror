package scheduler.fair_qual;


import java.util.*;
import java.io.*;

import edu.calpoly.csc.scheduler.Scheduler;
import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.idb.CoursePreference;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.schedule.Schedule;
import edu.calpoly.csc.scheduler.model.schedule.ScheduleItem;

import scheduler.generate.*;
import scheduler.*;
import scheduler.db.instructordb.*;
import scheduler.db.coursedb.*;
import scheduler.db.locationdb.*;
import scheduler.db.*;

/***
 * Fair_Qual contains methods to judge a schedule
 * against preferences and determine the Fairness
 * and Quality of the given schedule.
 *
 * @author Alex Lindt (alindt@calpoly.edu)
 *
 */

public class Fair_Qual implements Observer, Serializable
{

   /** Contains all instructor's who have a fairness value computed */
   private ArrayList<Instructor> instructor_list;

   /** Holds fairness values for the instructors in instructor_list */
   private ArrayList<Integer> fairness;
   
   /** Holds quality values for the instructors in instructor_list */
   private ArrayList<Integer> quality;

   /** Holds the number of assigned classes and times for a given isntructor */
   private ArrayList<Integer> hits;

   /** Average fairness value. */
   private float f_avg;

   /** Standard deviation of fairness values */
   private double f_dev;

   /** Lowest fairness value */
   private int f_low;

   /** Highest fairness value */
   private int f_high;

   /** The index being looked at by FairUI */
   private int current_index;

   /** Each instructor's schedule */
   private Schedule schedule;

   /**
    * Sets all variables and attempts to fill instructor_list with the
    * current schedule (if any).
    */
   public Fair_Qual() {
      initSettings();
      schedule = Scheduler.schedule;

      try{ updateLists(); }
      catch(Exception e) {}

      Scheduler.schedule.addObserver(this);
   }

   /**
    * Sets all variables and sets canned data for testing purposes
    *
    * @param cur_schedule current list of all generated schedules
    */
   public Fair_Qual(Schedule cur_schedule) {
      initSettings();
      schedule = cur_schedule;
      updateLists();
   }

   /**
    * Variable declarations
    */
   private void initSettings(){
      instructor_list = new ArrayList<Instructor>();
      fairness = new ArrayList<Integer>();
      quality = new ArrayList<Integer>();
      hits = new ArrayList<Integer>();
      f_avg = 0;
      f_low = 0;
      f_high = 0;
      f_dev = 0;
      current_index = 0;
      schedule = null;
   }

   /**
    * Updates the lists when it's observable targets change
    *
    * @param obs Object observed
    * @param obj I don't know
    */
   public void update(Observable obs, Object obj)//-->
   {
      //may need again in the future, not sure atm.
      //if(Generate.class.isInstance(obs)){
         schedule = Scheduler.schedule;
         updateLists();
         generateFairnessStats(); //generate associated statistics
      //}
   }

   /**
    * Synchronizes lists with the updated schedule
    */
   private void updateLists() 
   {
      LinkedList<Instructor> instructors = schedule.getInstructorList();
      clearLists();

      int beg=0,end=0;

      for(int i=0 ; i < instructors.size() ; i++) 
      {
         instructor_list.add(new Instructor(instructors.get(i)));
         fairness.add(0);
         quality.add(0);
         hits.add(0);
      }

      for(ScheduleItem cs: schedule.s)
      {
         for(int i=0 ; i < instructor_list.size() ; i++) 
         {
            if(cs.i.equals(instructor_list.get(i)))
            {
               /* 
                * incremented by 2, 1 for the course preference, and 1 for the 
                * time preference 
                */
               hits.set(i, hits.get(i)+2);

               /*
                * TODO: TimePreference representation has changed drastically, 
                *       impacting how fair/qual should be evaluated, along with
                *       the generate algorithm. To resolve this until the author
                *       (Alex), has time, the following changes are made below:
                *
                *       THE TIME PREFERENCE USED IN CALC'ING FAIRNESS IS JUST 5.
                */
                /*
                * sets fairness to an intermediate value, summing together the instructor's
                * preferences for all assigned courses and times. Currently assumes all
                * assigned classes are 1 hour. This may need to be improved in the future.
                */               
               fairness.set(i, fairness.get(i) + 
                               instructor_list.get(i).getPreference(cs.c) + 5);
            }
         }
      }

      generateFairness();
      generateFairnessStats();
   }


   /**
    * Calculates the fairness and
    * associated statistics (avg,low,high,etc) for
    * all instructors with a generated schedule.<p> "fairness_value"
    * represents the fairness, and has a range from
    * 1-10. a fairness_value of 1 means the schedule for
    * the schedule is most fair, while 10 is the least
    * fair.
    * <p><p>
    * pre: <code> schedule.getInstructorList().size() >= 0 </code>
    * <p><p>       
    * post:
    * <code>forall(stored_schedules)
    *    stored_schedules[i].instructor.fairness = sum(10 - each time/course preference
    *                                                  assigned in instructor's schedule) /
    *                                                 (total assignments)</code>
    */
   private void generateFairness() {
      for(int i=0 ; i<fairness.size() ; i++) {
         fairness.set(i, fairness.get(i)/hits.get(i));
      }
   }

   /**
    * generates associated statistics for overall data, such as
    * highest, lowest, or average fairness. Other statistics are
    * computed ass well.
    */
   private void generateFairnessStats(){
      int lowest=0;
      int highest=0;
      int sum=0;
      int temp=0;
      
      for(int i=0 ; i < fairness.size() ; i++) {
         temp = fairness.get(i);
         if(i==0) {
            lowest = temp;
            highest = temp;
            sum  = temp;
         }
         if(fairness.get(i) < lowest) {
            lowest = temp;
         }
         if(fairness.get(i) > highest) {
            highest = temp;
         }
         sum += fairness.get(i);
      }

      if(fairness.size() > 0) {
         f_low = lowest;
         f_high = highest;
         f_avg = (sum/(fairness.size()+1));
         f_dev = calculateDeviation();
      }
   }

   /**
    * calculates standard deviation
    * <p><p>
    * pre: <code> fairness.size() > 1 </code>
    * <p><p>
    * post: <code> dev = standardDeviation(ofAllStoredFairnessValues) </code>
    *
    * @return dev the standard deviation
    */
   private double calculateDeviation(){
      double dev = 0;

      if(fairness.size() > 1) {
         for(int i=0 ; i<fairness.size() ; i++) {
            dev += (fairness.get(i)-f_avg)*(fairness.get(i)-f_avg);
         }
         dev /= (fairness.size()-1);
         dev = java.lang.Math.sqrt(dev);
      }
      return dev;
   }

   /**
    * generateQuality will calculate the fairness and
    * associated statistics (avg,low,high,etc) for
    * all instructors with a generated schedule.<p> "quality_value"
    * represents the quality, and has a range from
    * 1-10. a quality_value of 1 means the schedule for
    * the schedule is of the most quality, while 10 is the least
    * quality.
    * <p><p>
    * pre: <code> schedule.getInstructorList().size() </code>
    * <p><p>       
    * post:
    * <code>forall(stored_schedules)
    *    stored_schedules[i].instructor.quality = (1 / (sum of all quality violations))*10
    *
    */   
   public void generateQuality() {

      int quality_value = -1;  //end result
      int hits = 0;            //incremented for every time slot the instructor has a class for
      int temp = 0;            //stores intermediate values


      //for all instructors
      for(int j = 0 ; j < instructor_list.size() ; j++) {
         //for all hours in the day
         for(int i = 0 ; i < 24 ; i++) {

            /*
             * TODO: TimePreference representation has changed drastically, 
             *       impacting how fair/qual should be evaluated, along with
             *       the generate algorithm. To resolve this until the author
             *       (Alex), has time, the following changes are made below:
             *
             *       THE TIME PREFERENCE USED IN CALC'ING IS JUST 5.
             */
            temp = 5;
            //temp = instructor_list.get(j).getPreference(new Time(i,0));

            if(temp > 0) {
               hits++;
               quality_value += temp;
            }
         }
         quality_value /= hits;
         quality.set(j,quality_value);
      }
   }

   /**
    * setFairness will modify the fairness value of a
    * given schedule and set it to the new_fairness
    * value. The function will return true if the fairness
    * value is successfully set.
    * <p><p> 
    * pre:
    * <code>(to_modify != NULL) && (new_fairness > 0 && new_fairness < 11)</code>
    * <p><p>  
    * post:
    * <code>return (to_modify.fairness = new_fairness)</code>
    *
    * @param to_modify the schedule being modified.
    * @param new_fairness the value to set the fairness as.
    * @return true if to_modify.fairness is successfully changed. false if otherwise
    */
   public boolean setFairness(Instructor instructor, int new_fairness) throws Instructor.NullUserIDException{

      for(int i = 0 ; i < instructor_list.size() ; i++) {
         if(instructor_list.get(i).getId().compareTo(instructor.getId()) == 0) {
            fairness.set(i, new_fairness);
            generateFairnessStats();
            return true;
         }
      }
      return false;
   }

   /**
    * setQuality will modify the quality  value of a
    * given schedule and set it to the new_quality
    * value. The function will return true if the quality
    * value is successfully set.
    * <p><p>
    * pre:
    * <code>(to_modify != NULL) && (new_quality > 0 && new_quality < 11)</code>
    * <p><p>
    * post:
    * <code>return (to_modify.quality = new_quality)</code>
    *
    * @param to_modify the schedule being modified.
    * @param new_ quality the value to set the quality as.
    * @return true if to_modify.quality is successfully changed. false if otherwise
    */
   public boolean setQuality(Instructor instructor, int new_quality) throws Instructor.NullUserIDException {

      for(int i = 0 ; i < instructor_list.size() ; i++) {
         if(instructor_list.get(i).getId().compareTo(instructor.getId()) == 0) {
            quality.set(i, new_quality);
            return true;
         }
      }
      return false;
   }
   
   /**
    * Takes in an isntructor and sums up the
    * values of all of their course and time preferences. That
    * value represents that professor's Generosity, where higher
    * values mean the professor was less generous, while lower
    * values mean the professor was more generous.
    * <p><p>
    * pre:
    * <code>inst != null;</code>
    * <p><p>
    * post:
    * <code>forall(preferences) { generosity += preference.desire; }</code>
    *
    * @param inst the instructor to determine generosity for
    * @param courses classes with specified preferences
    * @return the sum of all (time/class).desire()
    */
   public static int getGenerosity(Instructor inst, LinkedList<Course> courses) {
      CoursePreference pref;
      int generosity = 0;

      for(int i=0;i<courses.size();i++) {
         pref = null;
         //pref = Scheduler.idb.getPreference(inst, courses.get(i));
         pref = new CoursePreference(courses.get(i),inst.getPreference(courses.get(i)));
         if(pref != null) {
            generosity += pref.getDesire();
         }
      }

      return generosity;
   }

   /** returns all the instructor's schedule item indices */
   public ArrayList<Integer> getScheduleItemIndices(int index) {
      if(instructor_list.isEmpty()){ return null; }

      ArrayList<Integer> indices = new ArrayList<Integer>();

      for(int j = 0 ; j < schedule.s.size() ; j++) {
         if(schedule.s.get(j).i.equals(instructor_list.get(index))){
            indices.add(j);
         }
      }

      return indices;
   }
   
   /** returns the average fairness value */
   public float getAvg(){ return f_avg; }
   
   /** returns the lowest fairness value */
   public int getLow(){ return f_low; }
   
   /** returns the highest fairness value */
   public int getHigh(){ return f_high; }

   /** returns the current index being viewed by the UI */
   public int getIndex(){ return current_index; }

   /** returns the number of instructor's stored */
   public int getSize(){ return instructor_list.size(); }

   /** sets the index being viewed by the UI */
   public boolean setIndex(int index){
      if(index>instructor_list.size() || index<0) {
         return false;
      }
      current_index = index;
      return true;
   }

   /** returns the instructor at a given index */
   public Instructor getInstructor(int index){
      if(index>instructor_list.size() || instructor_list.isEmpty()) {
         return null;
      }
      return instructor_list.get(index);
   }

   /** returns the instructor at the current_index */
   public Instructor getCurrentInstructor(){
      if(current_index>instructor_list.size() || instructor_list.isEmpty()) {
         return null;
      }      
      return instructor_list.get(current_index);
   }

   /** returns the fairness value at a given index */
   public int getFairness(int index){
      if(index>fairness.size() || fairness.isEmpty()) {
         return -1;
      }
      return fairness.get(index);
   }

   /** returns the fairness value at the current_index */
   public int getCurrentFairness(){
      if(fairness.isEmpty()) {
         return -1;
      }
      return fairness.get(current_index);
   }

   /** returns the quality value at a given index */
   public int getQuality(int index){
      if(index>quality.size() || quality.isEmpty()) {
         return -1;
      }
      return quality.get(index);
   }

   /** returns the quality value at the current_index */
   public int getCurrentQuality(){
      if(quality.isEmpty()) {
         return -1;
      }
      return quality.get(current_index);
   }

   /** returns the schedule */
   public Schedule getSchedule(){ return schedule; }

   /** returns the standard deviation of stored fairness values */
   public double getDev(){ return f_dev; }

   /**
    * Calculates upper bound, any fairness value higher than
    * the upper bound is statistically considered to be an
    * outlier.
    *
    * @return Upper_bound average + 2*std_deviation
    */
   public double getUpperBound(){ return f_avg + 2*f_dev; }

   /**
    * Calculates lower bound, any fairness value lower than
    * the lower bound is statistically considered to be an
    * outlier.
    *
    * @return Lower_bound average - 2*std_deviation
    */
   public double getLowerBound(){ return f_avg - 2*f_dev; }

   /**
    * Helper function to empty all current lists
    */
   public void clearLists() {
      while(!instructor_list.isEmpty()) { instructor_list.remove(0); }
      while(!fairness.isEmpty()       ) { fairness.remove(0);        }
      while(!quality.isEmpty()        ) { quality.remove(0);         }
      while(!hits.isEmpty()           ) { hits.remove(0);            }
   }

   public Fair_Qual testSaving() {
      String filename = "fair_qual.ser";
      FileOutputStream fos = null;
      ObjectOutputStream out = null;
      Fair_Qual return_me = null;     
 
      try {
         fos = new FileOutputStream(filename);
         out = new ObjectOutputStream(fos);
         f_avg = 48;
         out.writeObject(this);
         out.close();
         System.out.println("f_avg (value before save): " + f_avg);
      }
      catch(IOException ex) {
         ex.printStackTrace();
      }
      

      FileInputStream fis = null;
      ObjectInputStream in = null;

      f_avg = -10000;

      System.out.println("f_avg (modified before load): " + f_avg);

      try {
         fis = new FileInputStream(filename);
         in = new ObjectInputStream(fis);
         return_me = (Fair_Qual)in.readObject();
         in.close();
      }
      catch(IOException ex) {
         ex.printStackTrace();
      }
      catch(ClassNotFoundException ex) {
         ex.printStackTrace();
      }

      return return_me;

   }

}
