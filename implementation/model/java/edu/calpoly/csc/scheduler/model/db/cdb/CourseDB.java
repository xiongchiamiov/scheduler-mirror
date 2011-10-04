package edu.calpoly.csc.scheduler.model.db.cdb;

import java.util.Collection;
import java.lang.*;
import java.sql.*;
import java.util.*;

import edu.calpoly.csc.scheduler.model.db.SQLDB;
import edu.calpoly.csc.scheduler.model.db.pdb.DaysForClasses;

/**
 * The class representing the course database.
 *
 *
 * @author Jan Lorenz Soliman 
 *
 *
 **/

public class CourseDB extends Observable {

   /** A constant representing the course fields for a courses SQL database.  */
   public static final String courseFields = " (name, courseNum, wtus, scus, classType, maxEnrollment, labPairing, smartroom, overhead, laptop, prefix, hoursPerWeek, ctPrefix)";

   /** A collection of courses.  */
   protected ArrayList<Course> data;

   /** A collection of courses.  */
   protected ArrayList<Course> localData;

   /** A collection of the sections per course. */
   protected Collection<SectionsTuple> sectionsPerCourse;


   /**
    *  Construct the database and grab the
    *  information from the SQL database.
    **/
   public CourseDB() {
       this.localData = new ArrayList<Course>();
   }

   /**
    *  A tuple representing the number of sectons 
    *  for the courses.
    **/
   public static class SectionsTuple {

      /** String that represents the course id.  */
      private String id;

      /** Integer that represents the number of sections
       *  per course id.*/ 
      private int sections;

      /** Constructor for the Sections Tuple class 
       *  @param id The id of the course.
       *  @param sections The number of sections 
       *  */
      public SectionsTuple(String id, int sections) {
         this.id = id;
         this.sections = sections;
      }

      /**
       *  Method to set the id of the Tuple
       *  @param id The id of the course.
       * */
      public void setId(String id) {
         this.id = id;
      }

      /**
       *  Method to get the id of the Tuple
       *  @return The id of the course.
       * */
      public String getId() {
         return id;
      }


      /**
       *  Method to set the number of sections
       *  per tuple.
       *  @param sections The number of sections in the tuple.
       *
       **/
      public void setSections (int sections) {
         this.sections = sections;
      }

      /**
       *  Get the sections in the tuple
       *  @return The sections in the tuple.
       **/
      public int getSections () {
         return sections;
      }

   }
 
   /**
    * Changes the number of sections found in the database.
    * 
    * @param dataTable A table representing the number of sections per course.
    *
    */
   public void changeSections(Vector<Vector> dataTable) {
      Iterator iterator = dataTable.iterator();
      while (iterator.hasNext()) {
         //System.out.println ("HERE!");
         Vector row = (Vector) iterator.next();
         //System.out.println( (String) row.get(0));
         //System.out.println("Sections is " + Integer.parseInt((String)row.get(1)) );
         changeSections((String) row.get(0), Integer.parseInt((String)row.get(1)) );
      }

      setChanged();
      notifyObservers();
   }

   /** 
    *  Changes the number of sections per course
    *  @param id The id of the course. The id will have a L if it 
    *  is a lab.
    *  @param sections The number of sections for the course
    **/
   public void changeSections(String id, int sections) {
      //System.out.println("In CourseDB.changeSections");
      Course c;
      if (id.contains("L")) {
         String number = id.substring(3,6);
         //System.out.println("Number is " + number);
         c = this.getLocalCourse( Integer.parseInt(number), "Lab" );
      }
      else {
         String number = id.substring(3,6);
         //System.out.println("Number is " + number);
         c = this.getLocalCourse( Integer.parseInt(number), "Lecture" );
      }
      localData.remove(c);
      c.setSection(sections);
      localData.add(c);
      
   }

   /**
    *  Returns the collection of courses
    **/
   public ArrayList<Course> getData() {
      return data;
   }

   public void localToPermanent() {
       Iterator it = this.localData.iterator();

       while (it.hasNext()) {
           Course c = (Course) it.next();
           this.addCourse(c);
       }

   }

   public void permanentToLocal() {
       this.localData = new ArrayList<Course>(data);
   }

   /**
    *  Sets the data in the courseDB
    *  @param data The data to be set.
    **/
   public void setData(ArrayList<Course> data  ) {
      this.data = data;
      Collections.sort((List)data);
   }

   /**
    *  Sets the local data in the courseDB
    *  @param data The data to be set.
    **/
   public void setLocalData(Collection<Course> data  ) {
      this.localData = new ArrayList<Course>(data);
      Collections.sort((List)localData);
      this.setChanged();
      this.notifyObservers();
   }

   /**
    *  Returns the collection of courses in the local database
    **/
   public Collection<Course> getLocalData() {
      return this.localData;
   }


   /**
    *  Gets the local course by courseNum
    *  <pre>
    *  pre: 
    *         //
    *         // The course number is three digits long
    *         // 
    *         (this.validId(number)  )
    *           &&
    *
    *
    *         //
    *         // The string is either "Lab" or "Lecture" 
    *         //
    *         (courseType == "Lab" or courseType == "Lecture")
    *
    *  post:
    *         //
    *         // The returned course has the id of number and
    *         // course type of courseType
    *         //
    *         (course.id == number and course.courseType == courseType)
    *  </pre>
    *
    *  @param number The course number.
    *  @param courseType A string representing the course type
    *  @return the Course corresponding to the number and type.
    **/
   public Course getLocalCourse(int number, String courseType) {
      Iterator iterator;
      if (localData != null) {
         iterator = localData.iterator();
      }
      else {
         return null;
      }
      while (iterator.hasNext()) {
         Course c = (Course) iterator.next();
         //System.out.println ("Looking at course " + c);
         //System.out.println ("Comparing " + c.getId() + " to " + number );
         if (c.getId() == number && c.getCourseType().equals(courseType )) {
            return c;
         }
      }

      return null;
   }


   /**
    *  Gets the course by courseNum
    *  <pre>
    *  pre: 
    *         //
    *         // The course number is three digits long
    *         // 
    *         (this.validId(number)  )
    *           &&
    *
    *
    *         //
    *         // The string is either "Lab" or "Lecture" 
    *         //
    *         (courseType == "Lab" or courseType == "Lecture")
    *
    *  post:
    *         //
    *         // The returned course has the id of number and
    *         // course type of courseType
    *         //
    *         (course.id == number and course.courseType == courseType)
    *  </pre>
    *
    *  @param number The course number.
    *  @param courseType A string representing the course type
    *  @return the Course corresponding to the number and type.
    **/
   public Course getCourse(int number, String courseType) {
      Iterator iterator;
      if (data != null) {
         iterator = data.iterator();
      }
      else {
         return null;
      }
      while (iterator.hasNext()) {
         Course c = (Course) iterator.next();
         //System.out.println ("Looking at course " + c);
         //System.out.println ("Comparing " + c.getId() + " to " + number );
         if (c.getId() == number && c.getCourseType().equals(courseType )) {
            return c;
         }
      }

      return null;
   }

   /**
    * Checks if the id is three digits long
    *  <pre>
    *  pre: 
    *         //
    *         // The id is a number from 100 to 999 
    *         // 
    *         (id < 1000 && id > 99 )
    *
    *
    *  post:
    *
    *  </pre>
    * @param id The id of the course
    *
    **/
   public boolean validId(int id ) {
      int i;
      if (id == 0) {
         return false;
      }

      for (i = 0; id > 0; ++id  ) {
         id /= 10;
      }

      if (i != 3) {
         return false;
      }
      else {
         return true;
      }
   }


   /** Same as addCourse, but used for testing purposes.
    *  This connects to a test database.
    *  <pre>
    *  pre: 
    *         //
    *         // The course c is valid course
    *         // 
    *         (c != null && c.isValidCourse()  )
    *
    *           &&
    *
    *
    *         //
    *         // The course is not in the database
    *         //
    *         (c not in courseDB.data)
    *
    *  post:
    *         //
    *         // Only c is added to the database
    *         //
    *         (forall (Courses course)
    *             (course in courseDB'.data) iff
    *                ( (course == c) or 
    *                 (course in courseDB.data)))
    *  </pre>
    *
    *  @param c The Course to be added.
    ***/
   public void addCourseTest(Course c) throws CourseExistsException {
         if (data != null) {
            if (data.contains(c)) {
               System.out.println (data);
               throw new CourseExistsException();
            }
         }

         SQLDB sqldb = new SQLDB();
         sqldb.open("mysql://cedders.homelinux.net/jseallfilled");

         String labId = "NULL";
         if (c.getLabPairing() != null) {
            labId = c.getLabPairing().getId() + "";
         }
         String insert = "";
         insert = "( " + " '" + c.courseName + "', " + c.id + ", " + c.wtu + ", " + c.scu + ", '";
         insert = insert + c.courseType + "', " + c.maxEnrollment +  ", " + labId +  ", " + c.requiredEquipment.isSmartroom() + ", ";
         insert = insert + c.requiredEquipment.hasOverhead() + ", " + c.requiredEquipment.hasLaptopConnectivity() + ")";

         sqldb.insertStmt("courses", insert);
         CourseDB temp = sqldb.getCourseDB();
         this.data = temp.getData();
         setChanged();
         notifyObservers();
         sqldb.close();
   }

   /** Adds a given course to the course database. 
    *  <pre>
    *  pre: 
    *         //
    *         // The course c is valid course
    *         // 
    *         (c != null && c.isValidCourse()  )
    *
    *           &&
    *
    *
    *         //
    *         // The course is not in the database
    *         //
    *         (c not in courseDB.data)
    *
    *  post:
    *         //
    *         // Only c is added to the database
    *         //
    *         (forall (Courses course)
    *             (course in courseDB'.data) iff
    *                ( (course == c) or 
    *                 (course in courseDB.data)))
    *  </pre>
    *
    *  @param c The Course to be added.
    ***/
   public void addCourse(Course c) throws CourseExistsException {

         if (data != null) {
            if (data.contains(c)) {
               throw new CourseExistsException();
            }
         }
         //TODO FIX
         //SQLDB sqldb = Scheduler.schedDB;
         String labId = "NULL";
         if (c.getLabPairing() != null) {
            labId = c.getLabPairing().getId() + "";
         }
         String insert = "";
         insert = "( " + " '" + c.courseName + "', " + c.id + ", " + c.wtu + ", " + c.scu + ", '";
         insert = insert + c.courseType + "', " + c.maxEnrollment +  ", " + labId +  ", " + c.requiredEquipment.isSmartroom() + ", ";
         insert = insert + c.requiredEquipment.hasOverhead() + ", " + c.requiredEquipment.hasLaptopConnectivity() + ", '" +  c.getDepartment() + "', " +  c.getHoursPerWeek()  + ", '" + c.getCTPrefix() + "')";
         //insert = insert + c.requiredEquipment.hasOverhead() + ", " + c.requiredEquipment.hasLaptopConnectivity() + ", '" +  c.getPrefix() + "')";

         System.out.println(insert);

         //sqldb.insertStmt("courses", insert);

         Iterator it = c.getDFC().iterator();
         while (it.hasNext()) {
             DaysForClasses dfc = (DaysForClasses)it.next();
             String dfcInsert = "( '" + c.getId() + "', " + "'" + dfc.toString() + "')";
          //   sqldb.insertStmt("courses_to_preferences", dfcInsert);
         }

         //CourseDB temp = sqldb.getCourseDB();
         //addLocalCourse(c);
         //this.data = temp.getData();
         Collections.sort((List)data);
         setChanged();
         notifyObservers();

   }


   /** Adds a given course to the local course list. 
    *  <pre>
    *  pre: 
    *         //
    *         // The course c is valid course
    *         // 
    *         (c != null && c.isValidCourse()  )
    *
    *           &&
    *
    *
    *         //
    *         // The course is not in the database
    *         //
    *         (c not in courseDB.data)
    *
    *  post:
    *         //
    *         // Only c is added to the database
    *         //
    *         (forall (Courses course)
    *             (course in courseDB'.data) iff
    *                ( (course == c) or 
    *                 (course in courseDB.data)))
    *  </pre>
    *
    *  @param c The Course to be added.
    ***/
   public void addLocalCourse(Course c) throws CourseExistsException {

         if (localData != null) {
            if (localData.contains(c)) {
               throw new CourseExistsException();
            }
         }

         /*if (c.getCourseType().contains("Lecture")) {
            Lecture l = new Lecture(c);
            System.out.println(l);
            this.localData.add(l);
         }
         else {
            Lab l = new Lab(c);
            this.localData.add(l);
         }*/

         this.localData.add(c);
         Collections.sort((List)localData);
         //Scheduler.idb.updateAllLocalPreferences();
         setChanged();
         notifyObservers();
   }

    /**Course already exists exception thrown in the set methods.  */
    public static class CourseExistsException extends RuntimeException {
         /**
          * Constructor calls the exception constructor.
          * 
          */
         public CourseExistsException() {
            super();
         }
    }

   /**
    *  A method to get all of the lab courses in the Course Database.
    *  @return A Vector of labs in the course database.
    *  <pre>
    *  pre: 
    *
    *
    *  post:
    *         //
    *         // The return value contains only labs. 
    *         //
    *         (forall (Courses course in returnValue)
    *             (course.type == "lab")
    *  </pre>
    */
   public Vector<Course> getLabs() {
      Vector<Course> courses  = new Vector<Course>();
      Course empty = new Course("", 0, 0, 0, "", 0, 0, null, 
                                new RequiredEquipment(false, false, false), 
                                "", null);
      courses.add(empty);
      for (Course c: data  ) {
         if (c != null) {
            if (c.getCourseType().equals("Lab" ) ) {
               courses.add(c);
            }
         }
      }
      return courses;
   }


   /**
    *  A method to get all of the lab course names in the Course Database.
    *  @return A Vector of lab names in the course database.
    *  <pre>
    *  pre: 
    *
    *
    *  post:
    *         //
    *         // The return value contains only labs. 
    *         //
    *         (forall (Course c)
    *            if (course.type == "lab") {
    *               c.name in returnVal
    *            }
    *  </pre>
    */
   public Vector<String> getLabNames() {
      Vector<String> courses  = new Vector<String>();
      courses.add("");
      if (data != null) {
         for (Course c: data  ) {
            if (c != null) {
               if (c.getCourseType().equals("Lab" ) ) {
                  courses.add(c.toString());
               }
            }
         }
      }
      return courses;     
   }

   /**
    *  A method to get all of the lab course names in the local Course Database.
    *  @return A Vector of lab names in the course database.
    *  <pre>
    *  pre: 
    *
    *
    *  post:
    *         //
    *         // The return value contains only labs. 
    *         //
    *         (forall (Course c)
    *            if (course.type == "lab") {
    *               c.name in returnVal
    *            }
    *  </pre>
    */
   public Vector<String> getLocalLabNames() {
      Vector<String> courses  = new Vector<String>();
      courses.add("");
      /*
       * Eric was here: Changed "data" to "localData"
       */
      if (localData != null) 
      {
         for (Course c: localData) 
         {
            if (c != null) 
            {
               if (c.getCourseType().equals("Lab")) 
               {
                  System.err.println (c + " is a lab");
                  courses.add(c.toString());
               }
               else
               {
                  System.err.println (c + " isn't a lab");
               }
            }
         }
      }
      return courses;     
   }

   /** Returns the total number of sections  **/
   /*public int getTotalSections() {
      return 0;
   }*/

   /** The same as editCourse but for testing purposes. 
    *
    *  <pre>
    *  pre: 
    *         //
    *         // The course c is valid course
    *         // 
    *         (c != null && c.isValidCourse()  )
    *
    *
    *           &&
    *
    *         //
    *         // The course c is already in the database
    *         //
    *         (c in courseDB.data )
    *
    *
    *  post:
    *         //
    *         // No courses are added to the database 
    *         //
    *         (courseDB.data.size() == courseDB.data.size() )
    *
    *            &&
    *
    *         //
    *         // The database is the same as the earlier version except 
    *         // for the edited course.
    *         //
    *         (forall (Courses course)
    *             (course in courseDB'.data) iff
    *                ( (course == c) or 
    *                 (course in courseDB.data && course.id != c.id )))
    *
    *  </pre>
    *
    *  @param c The Course to be edited.
    * **/
   public void editCourseTest (Course c) {
      System.out.println("In CourseDB.editCourse");

      try {
         removeCourseTest(c);
      }
      catch (CourseDoesNotExistException e) {

      }
      try {
         addCourseTest(c);
      }
      catch (CourseExistsException e) {

      }
   }

   /** Edits a given course which is already in the course database. 
    *
    *  <pre>
    *  pre: 
    *         //
    *         // The course c is valid course
    *         // 
    *         (c != null && c.isValidCourse()  )
    *
    *
    *           &&
    *
    *         //
    *         // The course c is already in the database
    *         //
    *         (c in courseDB.data )
    *
    *
    *  post:
    *         //
    *         // No courses are added to the database 
    *         //
    *         (courseDB.data.size() == courseDB.data.size() )
    *
    *            &&
    *
    *         //
    *         // The database is the same as the earlier version except 
    *         // for the edited course.
    *         //
    *         (forall (Courses course)
    *             (course in courseDB'.data) iff
    *                ( (course == c) or 
    *                 (course in courseDB.data && course.id != c.id )))
    *
    *  </pre>
    *
    *  @param c The Course to be edited.
    * **/
   public void editCourse (Course c) {
      System.out.println("In CourseDB.editCourse");

      try {
         removeCourse(c);
      }
      catch (CourseDoesNotExistException e) {

      }
      try {
         addCourse(c);
      }
      catch (CourseExistsException e) {

      }
   }

   /** Edits a given course which is already in the local course list. 
    *
    *  <pre>
    *  pre: 
    *         //
    *         // The course c is valid course
    *         // 
    *         (c != null && c.isValidCourse()  )
    *
    *
    *           &&
    *
    *         //
    *         // The course c is already in the database
    *         //
    *         (c in courseDB.data )
    *
    *
    *  post:
    *         //
    *         // No courses are added to the database 
    *         //
    *         (courseDB.data.size() == courseDB.data.size() )
    *
    *            &&
    *
    *         //
    *         // The database is the same as the earlier version except 
    *         // for the edited course.
    *         //
    *         (forall (Courses course)
    *             (course in courseDB'.data) iff
    *                ( (course == c) or 
    *                 (course in courseDB.data && course.id != c.id )))
    *
    *  </pre>
    *
    *  @param c The Course to be edited.
    * **/
   public void editLocalCourse (Course c) {
      System.out.println("In CourseDB.editCourse");

      try {
         removeLocalCourse(c);
      }
      catch (CourseDoesNotExistException e) {

      }
      try {
         addLocalCourse(c);
      }
      catch (CourseExistsException e) {

      }
   }

    /**Course already exists exception thrown in the set methods.  */
    public static class CourseDoesNotExistException extends Exception {
         public CourseDoesNotExistException() {
         /**
          * Constructor calls the exception constructor.
          * 
          */
            super();
         }
    }


   /** The same as removeCourse() but for testing purposes. 
    *
    *  <pre>
    *  pre: 
    *         //
    *         // The course c is valid course
    *         // 
    *         (c != null && c.isValidCourse()  )
    *
    *
    *           &&
    *
    *         //
    *         // The course c is already in the database
    *         //
    *         (c in courseDB.data )
    *
    *  post:
    *         //
    *         // The database size is one smaller 
    *         //
    *         (courseDB.data.size() == courseDB`.data.size() - 1 )
    *
    *            &&
    *
    *         //
    *         // The database is the same as the earlier version except 
    *         // for the removed course.
    *         //
    *         (forall (Courses course)
    *             (course in courseDB'.data) iff
    *                ( (course != c)))
    *
    *  </pre>

    *
    *
    *  @param c The Course to be edited.
    * */
   public void removeCourseTest(Course c) throws CourseDoesNotExistException {
      //try {
      if (data != null) {

         if (!data.contains(c)) {
            throw new CourseDoesNotExistException();
         }
      }
         System.out.println("In CourseDB.removeCourse");
         SQLDB sqldb = new SQLDB();
         sqldb.open("mysql://cedders.homelinux.net/jseallfilled");
         String insert = "courseNum = " + c.getId() + " AND classType = '" + c.getCourseType() + "'";
         //sqldb.open();
         sqldb.removeStmt("courses", insert );
         CourseDB temp = sqldb.getCourseDB();
         this.data = temp.getData();
         //sqldb.close();
         setChanged();
         notifyObservers();
         sqldb.close();
      //}
      /*catch (NullPointerException e) {
         System.err.println("Trying to remove null");
      }*/
   }

   /** Removes a given, already-exiting course from the course database
    *
    *  <pre>
    *  pre: 
    *         //
    *         // The course c is valid course
    *         // 
    *         (c != null && c.isValidCourse()  )
    *
    *
    *           &&
    *
    *         //
    *         // The course c is already in the database
    *         //
    *         (c in courseDB.data )
    *
    *  post:
    *         //
    *         // The database size is one smaller 
    *         //
    *         (courseDB.data.size() == courseDB`.data.size() - 1 )
    *
    *            &&
    *
    *         //
    *         // The database is the same as the earlier version except 
    *         // for the removed course.
    *         //
    *         (forall (Courses course)
    *             (course in courseDB'.data) iff
    *                ( (course != c)))
    *
    *  </pre>

    *
    *
    *  @param c The Course to be edited.
    * */
   public void removeCourse(Course c) throws CourseDoesNotExistException {
      //try {
      if (data != null) {
         if (c == null) {
            
         }
         if (!data.contains(c)) {
            throw new CourseDoesNotExistException();
         }
      }
         //removeLocalCourse(c);
         System.out.println("In CourseDB.removeCourse");
         // TODO FIX
         //SQLDB sqldb = Scheduler.schedDB;
         String insert = "courseNum = " + c.getId() + " AND classType = '" + c.getCourseType() + "'";
         //sqldb.open();
         //sqldb.removeStmt("courses", insert );

         String remove = "courseid = " + c.getId();
         //sqldb.removeStmt("courses_to_preferences", remove);
         //CourseDB temp = sqldb.getCourseDB();
         //this.data = temp.getData();
         //sqldb.close();
         Collections.sort((List)data);
         setChanged();
         notifyObservers();
      //}
      /*catch (NullPointerException e) {
         System.err.println("Trying to remove null");
      }*/
   }


   /** Removes a given, already-exiting course from the local course list
    *
    *  <pre>
    *  pre: 
    *         //
    *         // The course c is valid course
    *         // 
    *         (c != null && c.isValidCourse()  )
    *
    *
    *           &&
    *
    *         //
    *         // The course c is already in the database
    *         //
    *         (c in courseDB.data )
    *
    *  post:
    *         //
    *         // The database size is one smaller 
    *         //
    *         (courseDB.data.size() == courseDB`.data.size() - 1 )
    *
    *            &&
    *
    *         //
    *         // The database is the same as the earlier version except 
    *         // for the removed course.
    *         //
    *         (forall (Courses course)
    *             (course in courseDB'.data) iff
    *                ( (course != c)))
    *
    *  </pre>

    *
    *
    *  @param c The Course to be edited.
    * */
   public void removeLocalCourse(Course c) throws CourseDoesNotExistException {
      //try {
      if (localData != null) {
         if (c == null) {
            
         }
         if (!localData.contains(c)) {
            throw new CourseDoesNotExistException();
         }
      }
      Iterator iterator = localData.iterator();
      int removeInd = -1;
      for (int i = 0; iterator.hasNext(); i++) {
         Course it = (Course) iterator.next();
         if (c.equals(it)) {
            removeInd = i;
            break;
         }
      }

      if (removeInd != -1) {
         
         localData.remove(removeInd);
         
      }
      Collections.sort((List)data);
      // TODO FIX
      
      //Scheduler.idb.updateAllLocalPreferences();
      setChanged();
      notifyObservers();
   }

   /**
    * Sets the local data to a given collection of data, and notifies any/all
    * observers of the change.
    *
    * @param data Data to set local data to
    *
    * By: Eric Liebowitz (22jul10)
    */
   public void setLocalWithThisFromFile (Collection<Course> data)
   {
      this.setLocalData(data);
      this.setChanged();
      this.notifyObservers();
   }
}
