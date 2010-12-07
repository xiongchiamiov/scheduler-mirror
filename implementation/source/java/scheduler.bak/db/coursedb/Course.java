package scheduler.db.coursedb;

import java.io.Serializable;
import java.util.Vector;
import java.util.Stack;

import scheduler.db.preferencesdb.DaysForClasses;
/**
 * This class will contain the information necessary to represent a course
 * available to the instructor, such as its name and required equipment.
 * 
 * @author Cedric Wienold
 *
 **/
public class Course implements Serializable, Comparable<Course>
{
   /**
    * For serializing
    */
   public static final int serialVersionUID = 42;
	/**
	 * This class specifies the required equipment for a course.
	 */
	public class RequiredEquipment implements Cloneable, Serializable
   {
      /** Whether the course requires a smartroom or not. */
		private boolean smartroom;
      /** Whether the course requires an overhead. */
		private boolean overhead;
      /** Whether the course requires laptop connectivity.*/
		private boolean laptopconnectivity;


		/**
		 * This constructor will make a class holding current required
		 * equipment.
		 * 
		 * @param isSmartRoom whether this need be a smart room.
		 * @param hasOverhead whether this need have an overhead.
		 * @param hasLaptopConnectivity whether this need have laptop
		 * 								connectivity.
		 */
		public RequiredEquipment (boolean isSmartRoom, boolean hasOverhead,
				boolean hasLaptopConnectivity) {
			this.smartroom = isSmartRoom;
			this.laptopconnectivity = hasLaptopConnectivity;
			this.overhead = hasOverhead;
		}

		/**
		 * Returns whether this need be a smart room.
		 * 
		 * @return whether this need be a smart room.
		 */
		public boolean isSmartroom() {
			return smartroom;
		}

		/**
		 * Returns whether this need have an overhead.
		 * 
		 * @return whether this need have an overhead.
		 */
		public boolean hasOverhead() {
			return overhead;
		}

		/**
		 * Returns whether this need have laptop connectivity.
		 * 
		 * @return whether this need have laptop connectivity.
		 */
		public boolean hasLaptopConnectivity() {
			return laptopconnectivity;
		}

      /**
       * Standard cloning method.
       *
       * By: Eric Liebowitz
       */
     public RequiredEquipment clone ()
     {
        try
        {
           return (RequiredEquipment)super.clone();
        }
        catch (CloneNotSupportedException e)
        {
           System.err.println (e);
        }
        return null;
     }
	}

   /**
    * Constant that should be used for the "lecture" course type
    */
   public static final String LEC = "Lecture";
   /**
    * Constant that should be used for the "lab" course type
    */
   public static final String LAB = "Lab";

	/**
	 * Name of course.
	 */
	String courseName;

	/**
	 * Id of course
	 *
	 */
	int id;

	/**
	 * Work-Time Units.
	 */
	int wtu;

	/**
	 * Student Course Units.
	 */
	int scu;

	/**
	 * Type of course.
	 */
	String courseType;

	/**
	 * Maximum enrollment of course.
	 */
	int maxEnrollment;

	/**
	 * Number of sections for course.
	 */
	int numOfSections;

	/**
	 * Corresponding lab.
	 */
	Course labPairing;

	/**
	 * Required equipment in course.
	 */
	RequiredEquipment requiredEquipment;


   /**
    * Course Prefix
    */
   String prefix;

   /**
    * Prioritized list of DaysForClasses preferences which this course wishes 
    * to adhere to. (Translation: When you want this class taught [MWF, TR, 
    * MTRF, etc.]).
    *
    * Added by: Eric Liebowitz
    */
   private Stack<DaysForClasses> dfc;

	/**
	 * Constructs the Course with the complete required information.
	 *
	 * @param courseName the name of this course.
	 * @param id the id of this course.
	 * @param wtu the work-time units of this course.
	 * @param scu the scu of this course.
	 * @param courseType the course type.
	 * @param maxEnrollment the maximum enrollment.
	 * @param numOfSections the numer of sections for this course.
	 * @param labPairing the lab pairing, if required.
	 * @param requiredEquipment the required equipment for htis course.
         * @param prefix The prefix of the course
         * @param dfc The DaysForClasses preference for the course.
	 */
	public Course (String courseName, int id, int wtu, int scu,
			String courseType, int maxEnrollment, int numOfSections,
			Course labPairing, RequiredEquipment requiredEquipment, String prefix, DaysForClasses dfc)
   {
		this.courseName = courseName;
		this.id = id;
		this.wtu = wtu;
		this.scu = scu;
		this.courseType = courseType;
		this.maxEnrollment = maxEnrollment;
		this.numOfSections = numOfSections;
		this.labPairing = labPairing;
		this.requiredEquipment = requiredEquipment;
      this.prefix = prefix;
      this.dfc= new Stack<DaysForClasses>();

      if (dfc == null) 
      {
         dfc = DaysForClasses.MTWRF;
      }
      this.dfc.push(dfc);
	}

   /**
    * <pre>
    * Initialization which happens for all Course objects. Currently, the 
    * following things are done:
    *
    *  - DaysForClasses.MTWRF is added to "this.dfc"
    * 
    * </pre>
    *
    * Written by: Eric Liebowitz
    */
   private void init ()
   {
      this.dfc.add(DaysForClasses.MTWRF);
   }

   public int compareTo(Course comp) {
      if (comp == null || !(comp instanceof Course)) {
         return -1;
      }

      if (this.getId() > comp.getId()) {
         return 1;
      }
      else if (this.getId() < comp.getId()) {
         return -1;
      }
      else {
         if (this.courseType.contains("Lab") && comp.courseType.contains("Lecture")) {
            return 1;
         }
         else {
            return 0;
         }
      }

   }

	/**
	 * This constructor will create a course with all required information.
	 * This constructor is more verbose than the other option.
	 * 
	 * @param courseName the name of this course.
	 * @param id the id of this course.
	 * @param wtu the work-time units of this course.
	 * @param scu the scu of this course.
	 * @param courseType the course type.
	 * @param maxEnrollment the maximum enrollment.
	 * @param numOfSections the numer of sections for this course.
	 * @param labPairing the lab pairing, if required.
	 * @param smartroom whether this has a smart room.
	 * @param overhead whether this has an overhead.
	 * @param connectivity whether this need laptop connectivity.
	 */
	/*public Course (String courseName, int id, int wtu, int scu,
			String courseType, int maxEnrollment, int numOfSections,
			Course labPairing, boolean smartroom, boolean overhead, boolean connectivity ) {
		this.courseName = courseName;
		this.id = id;
		this.wtu = wtu;
		this.scu = scu;
		this.courseType = courseType;
		this.maxEnrollment = maxEnrollment;
		this.numOfSections = numOfSections;
		this.labPairing = labPairing;
		this.requiredEquipment = new RequiredEquipment(smartroom, overhead, connectivity);
      this.prefix = "CPE";
	}*/

	/**
	 * This constructor will create a course with all required information.
	 * This constructor is most verbose option.
	 * 
	 * @param courseName the name of this course.
	 * @param id the id of this course.
	 * @param wtu the work-time units of this course.
	 * @param scu the scu of this course.
	 * @param courseType the course type.
	 * @param maxEnrollment the maximum enrollment.
	 * @param numOfSections the numer of sections for this course.
	 * @param labPairing the lab pairing, if required.
	 * @param smartroom whether this has a smart room.
	 * @param overhead whether this has an overhead.
	 * @param connectivity whether this need laptop connectivity.
         * @param prefix The prefix for the course.
         * @param dfc The DaysForClasses preference for the course.
	 */
	public Course (String courseName, int id, int wtu, int scu,
			String courseType, int maxEnrollment, int numOfSections,
			Course labPairing, boolean smartroom, boolean overhead, boolean connectivity, 
         String prefix, DaysForClasses dfc ) 
   {
		this.courseName = courseName;
		this.id = id;
		this.wtu = wtu;
		this.scu = scu;
		this.courseType = courseType;
		this.maxEnrollment = maxEnrollment;
		this.numOfSections = numOfSections;
		this.labPairing = labPairing;
		this.requiredEquipment = new RequiredEquipment(smartroom, overhead, connectivity);
      this.prefix = prefix;

      this.dfc= new Stack<DaysForClasses>();
      if (dfc == null) 
      {
         dfc = DaysForClasses.MTWRF;
      }
      this.dfc.push(dfc);
	}

   /**
    * Returns a new course, whose fields are an exact copy of the given course
    *
    * @param c Course to copy
    */
   public Course (Course c)
   {
      this.courseName = c.getCourseName();
      this.id = c.getId();
      this.wtu = c.getWTUs();
      this.scu = c.getSCUs();
      this.courseType = c.getCourseType();
      this.maxEnrollment = c.getMaxEnrollment();
      this.numOfSections = c.getNumberOfSections();
      this.labPairing = (c.hasLab()) ? new Course(c.getLabPairing()) : null;
      this.requiredEquipment = c.getRequiredEquipment().clone();
      this.prefix = c.getPrefix();
      this.dfc = c.getDFC();
   }

   /**
    * Returns whether the given course is a lab.
    *
    * @return True if the course is a lab. False otherwise. 
    */
   public boolean isLab ()
   {
      return this.courseType.equals("Lab");
   }

   /**
    * Tells whether this course is a lecture.
    *
    * @return true if this course is a lecture. False othwerise. 
    */
   public boolean isLecture ()
   {
      return this.courseType.equals(LEC);
   }

	/**
	 *  Returns whether the given course has a lab.
	 *
	 *  @return True if the class has a lab and false if not.
	 **/
	public boolean hasLab() {
		return (labPairing != null );
	}

	/**
	 *  Returns the coursename as a string
	 *
	 *  @return The string representing the course Name
	 *
	 **/
	public String getCourseName() {
		return courseName;
	}

	/**
	 *  Returns the course prefix as a string
	 *
	 *  @return The string representing the course Name
	 *
	 **/
	public String getPrefix() {
		return prefix;
	}

	/**
	 *  Sets the prefix
	 *
	 *  @param pre The string representing the course Name
	 *
	 **/
	public void setPrefix(String pre) {
		this.prefix = pre;
	}

	/**
	 *  Returns the id of the class
	 *  @return The id of the course
	 **/
	public int getId() {
		return id;
	}

	/**
	 *  Returns the wtus
	 *  @return The wtus earned for teaching the course
	 **/
	public int getWTUs() {
		return wtu;
	}

	/**
	 * Get the scu for this course.
	 * 
	 * @return the scu for this course.
	 */
	public int getSCUs() {
		return scu;
	}

	/**
	 * Returns the type of this course.
	 * @return the type of this course.
	 */
	public String getCourseType() {
		return courseType;
	}

	/**
	 * Returns the maximum enrollment.
	 * @return the maximum enrollment.
	 */
	public int getMaxEnrollment() {
		return maxEnrollment;
	}

	/**
	 * Returns the number of sections.
	 * @return the number of sections.
	 */
	public int getNumberOfSections() {
		return numOfSections;
	}

	/**
	 * Returns the lab pairing.
	 * @return the lab pairing.
	 */
	public Course getLabPairing() {
		return labPairing;
	}

	/**
	 *  Returns the work time unit
	 *  @return the work time unit
	 **/
	public int getWTU() {
		return wtu;
	}

	/**
	 * Returns whether this is a valid course.
	 */
	public boolean isValidCourse (Course course) {
		return false;
	}

	/**
	 * Returns whether the current database is valid.
'	 */
	public boolean isValidCourseDB () {
		return false;
	}

	/**
	 * Returns how many sections are available for this course.
	 * @return how many sections are available for this course.
'	 */
	public int sumOfSections () {
		return this.numOfSections;
	}

	/**
	 * Returns the class container for required equipment in course.
	 * @return the class container for required equipment in course.
	 */
	public RequiredEquipment getRequiredEquipment() {
		return requiredEquipment;
	}

	/**
	 * Returns the string representation of this course.
	 * @return the string representation of this course.
	 */
	public String toString()
	{
		return this.prefix + this.id;
	}

	/**
    * Determines whether two courses are equal. Currently, the following fields
    * are considered: 
    *
    *  - id
    *  - courseType
    *
	 * @return whether this course is equal to the given object.
	 */
	public boolean equals (Object o)
	{
		Course toCmp = (Course) o;
		return (this.id == toCmp.getId() && 
              this.courseType.equals(toCmp.getCourseType()));
	}
	
   /**
    * Returns a key which is the hashCode of the course's ID added to the
    * hashCode of its type and the number of its sections. Added to maintain the
    * contract between "equals" and "hashCode" method as stated in the 
    * documentation for the hashCode method in the Object class in the Java API.
    *
    * @return this.id + this.courseType.hashCode()
    *
    * Added by: Eric Liebowitz
    */
   public int hashCode ()
   {
      return this.id + this.courseType.hashCode();
   }
    

	/**
	 * This method sets the section for a course.
	 * 
	 * @param num the section number.
	 */
	public void setSection(int num) {
		this.numOfSections = num;
	}
	
	/**
	 * Returns the section for this course.
	 * 
	 * @return the section for this course.
	 */
	public int getSection() {
		return this.numOfSections;
	}

   /**
    * Returns this courses list of DaysForClasses preferences.
    *
    * Written by: Eric Liebowitz
    *
    * @return this courses list of DaysForClasses preferences.
    */
   public Stack<DaysForClasses> getDFC ()
   {
      return this.dfc;
   }

   /**
    * Adds a DaysForClasses preference to the front of the Fector of 
    * DaysForClasses associated w/ this Course. 
    *
    * Written by: Eric Liebowitz
    * 
    * @param dfc The DaysForClasses to add to the beginning of this course's
    *        dfc vector
    *
    * @return the new "dfc" vector
    */
   public Vector<DaysForClasses> addToFrontDFC(DaysForClasses dfc)
   {
      if (dfc != null)
      {
         if (this.dfc == null)
         {
            this.dfc = new Stack<DaysForClasses>();
         }
         this.dfc.push(dfc);
      }
      else
      {
         throw new NullPointerException();
      }
      return this.dfc;
   }

   /**
    * Returns the bit which should be used to represent this Course in a BitSet.
    *
    * @return This Course's number
    */
   public int getBit () { return this.id; }
}
