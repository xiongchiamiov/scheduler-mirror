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

   /* FIELDS ==>*/
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
    * Course Prefix (CPE vs CSC)
    */
   String prefix;

   /**
    * Course Type Prefix
    * 
    */
   String ctPrefix;

   /**
    * How many hours/week this course will be taught. Note that this is not
    * initialized directly in this method, but must be done in child classes.
    */
   protected int hoursPerWeek;

   /**
    * Prioritized list of DaysForClasses preferences which this course wishes 
    * to adhere to. (Translation: When you want this class taught [MWF, TR, 
    * MTRF, etc.]).
    *
    * Added by: Eric Liebowitz
    */
   private Stack<DaysForClasses> dfc;
   
   /** The other DaysForClasses variable. */
   protected DaysForClasses other_dfc;

   /*<==*/

   public Course() 
   {

   }

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
    * @param hoursPerWeek The hours per week of consecutive class.
    * @param ctPrefix The course type prefix.
	 */
   public Course (String courseName, int id, int wtu, int scu,
         String courseType, int maxEnrollment, int numOfSections,
         Course labPairing, RequiredEquipment requiredEquipment, String prefix, 
         DaysForClasses dfc, int hoursPerWeek, String ctPrefix)
   {
      init (courseName, id, wtu, scu, courseType, maxEnrollment, numOfSections,
            labPairing, requiredEquipment, prefix, dfc, hoursPerWeek, ctPrefix);
	}


	/**
	 * Constructs the Course with the almost complete required information.
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
         Course labPairing, RequiredEquipment requiredEquipment, String prefix, 
         DaysForClasses dfc)
   {
      init (courseName, id, wtu, scu, courseType, maxEnrollment, numOfSections,
            labPairing, requiredEquipment, prefix, dfc, 0, LEC);
   }

   /**
    * Unites the initialization of Course fields into a single method, for less
    * code duplication (though extremely long parameters lists still abound).
    *
    */
   private void init (String name, 
                      int id, 
                      int wtu, 
                      int scu,
                      String type, 
                      int maxEnrollment, 
                      int numOfSections,
                      Course labPairing, 
                      RequiredEquipment req, 
                      String prefix, 
                      DaysForClasses dfc, 
                      int hpw, 
                      String ctPrefix)
   {
		this.courseName = name;
		this.id = id;
		this.wtu = wtu;
		this.scu = scu;
		this.courseType = type;
		this.maxEnrollment = maxEnrollment;
		this.numOfSections = numOfSections;
		this.labPairing = labPairing;
		this.requiredEquipment = req;

      this.dfc= new Stack<DaysForClasses>();
      if (dfc == null) 
      {
         dfc = DaysForClasses.MTWRF;
      }
      this.dfc.push(dfc);

      this.hoursPerWeek = hpw;

      this.prefix = prefix;
      this.ctPrefix = ctPrefix;

   }

   /**
    * Returns a new course, whose fields are the same as the supplied course. 
    * In particular, "the same" means the "have the same reference" as. As of
    * right now (15nov10), this makes a <b>shallow copy</b> of the object, 
    * though all non-object fields will, of course, be copied. 
    *
    * Note: This particular constructor if pivotal to passing a copy of all
    *       to the schedule generator. In this way, the algorithm can do 
    *       whatever it likes to the Courses w/o fear of altering the 
    *       originals. 
    *
    * @param c Course to copy
    */
   public Course (Course c)
   {
      this.courseName         = c.getCourseName();
      this.id                 = c.getId();
      this.wtu                = c.getWTUs();
      this.scu                = c.getSCUs();
      this.courseType         = c.getCourseType();
      this.maxEnrollment      = c.getMaxEnrollment();
      this.numOfSections      = c.getNumberOfSections();
      this.requiredEquipment  = c.getRequiredEquipment().clone();
      this.prefix             = c.getPrefix();
      this.dfc                = c.getDFC();

      /*
       * Make a copy of the lab as well, so long as there is a lab to make a 
       * copy of.
       */
      Course lab = c.getLab();
      if (lab != null)
      {
         this.labPairing = new Course(lab);
      }

      this.hoursPerWeek       = c.getHoursPerWeek();
      this.ctPrefix           = c.getCTPrefix();

   }

   /**
    * Throws an UnsupportedOperationException object when called. Children 
    * classes must extend this method. It is only provided so that a call to
    * [some Course object].hasLab() can work, when the [some Course object] is
    * some other child class (such as a Lecture).
    */
   public boolean hasLab ()
   {
      return this.labPairing != null;
   }

   /**
    * Returns whether the given course is a lab.
    *
    * @return True if the course is a lab. False otherwise. 
    */
   public boolean isLab ()
   {
      return this.courseType.equals(LAB);
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
    * Overrrides the default compareTo method()
    * @param comp The course to compare to
    * @return An integer representation of the comparison.
    */
   public int compareTo(Course comp) 
   {
      if (comp == null || !(comp instanceof Course)) 
      {
         return -1;
      }

      if (this.getId() > comp.getId()) 
      {
         return 1;
      }
      else if (this.getId() < comp.getId()) 
      {
         return -1;
      }
      else 
      {
         if (this.courseType.contains("Lab") && comp.courseType.contains("Lecture")) 
         {
            return 1;
         }
         else 
         {
            return 0;
         }
      }
   }


   /** Returns the lab associated with this course.
    * @return The lab associated with this course.
    */
   public Course getLab ()
   {
      return this.labPairing;
   }

	/**
	 *  Returns the coursename as a string
	 *
	 *  @return The string representing the course Name
	 *
	 **/
	public String getCourseName() 
   {
		return courseName;
	}

	/**
	 *  Returns the course prefix as a string
	 *
	 *  @return The string representing the course Name
	 *
	 **/
	public String getPrefix() 
   {
		return prefix;
	}

	/**
	 *  Sets the prefix
	 *
	 *  @param pre The string representing the course Name
	 *
	 **/
	public void setPrefix(String pre) 
   {
		this.prefix = pre;
	}

	/**
	 *  Returns the course type prefix as a string
	 *
	 *  @return The string representing the course type prefix
	 *
	 **/
	public String getCTPrefix() 
   {
		return ctPrefix;
	}

	/**
	 *  Sets the prefix
	 *
	 *  @param pre The string representing the course Name
	 *
	 **/
	public void setCTPrefix(String ctpre) 
   {
		this.ctPrefix = ctpre;
	}

	/**
	 *  Returns the id of the class
	 *  @return The id of the course
	 **/
	public int getId() 
   {
		return id;
	}

	/**
	 *  Returns the wtus
	 *  @return The wtus earned for teaching the course
	 **/
	public int getWTUs() 
   {
		return wtu;
	}

	/**
	 * Get the scu for this course.
	 * 
	 * @return the scu for this course.
	 */
	public int getSCUs() 
   {
		return scu;
	}

	/**
	 * Returns the type of this course.
	 * @return the type of this course.
	 */
	public String getCourseType() 
   {
		return courseType;
	}

	/**
	 * Returns the maximum enrollment.
	 * @return the maximum enrollment.
	 */
	public int getMaxEnrollment() 
   {
		return maxEnrollment;
	}

	/**
	 * Returns the lab pairing.
	 * @return the lab pairing.
	 */
	public Course getLabPairing() 
   {
		return labPairing;
	}

	/**
	 *  Returns the work time unit
	 *  @return the work time unit
	 **/
	public int getWTU() 
   {
		return wtu;
	}

	/**
	 * Returns how many sections are available for this course.
	 * @return how many sections are available for this course.
'	 */
	public int sumOfSections () 
   {
		return this.numOfSections;
	}

	/**
	 * Returns the section for this course.
	 * 
	 * @return the section for this course.
	 */
	public int getSection() 
   {
		return this.numOfSections;
	}

	/**
	 * Returns the number of sections.
	 * @return the number of sections.
	 */
	public int getNumberOfSections() 
   {
		return numOfSections;
	}

	/**
	 * Returns the class container for required equipment in course.
	 * @return the class container for required equipment in course.
	 */
	public RequiredEquipment getRequiredEquipment() 
   {
		return requiredEquipment;
	}

   /**
    * Returns the bit which should be used to represent this Course in a BitSet.
    *
    * @return This Course's number
    */
   public int getBit () { return this.id; }

   /**
    * Returns the number of hours this course is taught a week
    */
   public int getHoursPerWeek ()
   {
      return this.hoursPerWeek;
   }

   /** TODO */
   public int getLengthOverDays (int numOfDays)
   {
      int r = -1;
      if (hoursDivideIntoDays(numOfDays))
      {
         r = ((this.hoursPerWeek * 2) / numOfDays);
      }
      return r;
   }

   /**
    * Determines whether a given number of days are suitable for teaching this
    * course over. In particular, this means that the hours/week this course
    * is taught divide evenly among the given number of days, and in such as 
    * manner that the Course will be taught at least one hour every day.
    *
    * @param numOfDays The number of days the class is to be taught.
    *
    * @return ((((this.hoursPerWeek * 2) % numOfDays) == 0) &&
    *            (this.hoursPerWeek * 2) / numOfDays) >  1));
    */
   public boolean hoursDivideIntoDays (int numOfDays)
   {
      int halfHours = (this.hoursPerWeek * 2);
      return (((halfHours % numOfDays) == 0) && ((halfHours / numOfDays) > 1));
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
   /*<==*/

   /* SETTERS ==>*/
	/**
	 * This method sets the section for a course.
	 * 
	 * @param num the section number.
	 */
	public void setSection(int num) 
   {
		this.numOfSections = num;
	}
   /*<==*/
}
