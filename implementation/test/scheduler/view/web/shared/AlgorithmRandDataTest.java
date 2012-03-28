//package scheduler.view.web.shared;
//
////import scheduler.*;
////import scheduler.db.*;
////import scheduler.db.coursedb.*;
////import scheduler.db.instructordb.*;
////import scheduler.db.locationdb.*;
////import scheduler.db.preferencesdb.*;
////import scheduler.generate.Week;
//
//import scheduler.model.*;
//import scheduler.model.db.Time;
//import scheduler.model.db.idb.CoursePreference;
//import scheduler.model.db.idb.Instructor;
//import scheduler.model.db.idb.TimePreference;
//import scheduler.model.db.ldb.Location;
//import scheduler.model.db.pdb.DaysForClasses;
//import scheduler.model.schedule.Schedule;
//import scheduler.model.db.cdb.*;
//
//import java.io.FileNotFoundException;
//import java.io.PrintStream;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.Random;
//import java.util.Vector;
//
///**
// * Designed to run the Scheduler's generate method with a random set of data,
// * where "random" is determined by the algorithm with in the java.util.Random
// * class. Generated schedules will then dump their contents to a file, which
// * will be used later by Perl to verify the schedules' constraints. 
// *
// * @author Eric Liebowitz
// * @version 19dec10
// */
//public class AlgorithmRandDataTest
//{
//   /**
//    * Seed for predictable, random results
//    */
//
//   private static final int RAND_SEED = 42;
//   /**
//    * Global random [X] generator, seeded with RAND_SEED.
//    */
//   private static final Random rand = new Random(RAND_SEED);
//
//   /**
//    * The limit of how many courses can be generated, as only 3-digit numbers 
//    * are allowed for Course id's
//    */
//   private static final int C_LIMIT = 899;
//
//   /**
//    * The default office that generated instructors are given.
//    */
//   private static final Location OFFICE = new Location (0, 00);
//
//   /**
//    * Default occupancy maximum for generated locations.
//    */
//   private static final int MAX_OCCUPANCE = 35;
//
//   /**
//    * Location bldg number maximum (exclusive).
//    */
//   private static final int BLDG_LIMIT = 100;
//
//   /**
//    * Location room number maximum (exclusive).
//    */
//   private static final int ROOM_LIMIT = 1000;
//
//   /**
//    * Some commonly used DaysForClasses preferences which will be available for
//    * generated courses.
//    */
//   private static final DaysForClasses[] DFCS = 
//   {
////      new DaysForClasses("MWF", 5, 
////         new int[] { Week.MON, Week.WED, Week.FRI }),
////      new DaysForClasses("TR", 5, 
////         new int[] { Week.TUE, Week.THU }),
////      new DaysForClasses("MTRF", 5,
////         new int[] { Week.MON, Week.TUE, Week.THU, Week.FRI }),
////      new DaysForClasses("MTWRF", 5, 
////         new int[] { Week.MON, Week.TUE, Week.WED, Week.THU, Week.FRI }),
//   };
//
//   /**
//    * Runs the Scheduler, populating it with data, generating a schedule, and 
//    * outputting the results to a file. Takes 4 arguments from the command line,
//    * in order:
//    *
//    * <ul>
//    *    <li>Name of file to dump. Generated schedules will output data necessary
//    *        to verify their integrity.</li>
//    *    <li>Number of courses to generate.</li>
//    *    <li>Number of instructors to generate.</li>
//    *    <li>Number of locations to generate.</li>
//    * </ul>
//    *
//    * Providing a "0" for any of the latter 3 options will throw an error, as 
//    * there's really no point in testing these cases. (At least, the constraint
//    * verification done in Perl thus far only considers generated schedules that 
//    * work out well. It doesn't consider things TBA's or STAFF instructors).
//    *
//    * @param args The command line arguments. 
//    *
//    * @see scheduler.Scheduler#dumpAsPerlText(PrintStream) 
//    */
//   public static void main (String[] args)
//   {
//      /*
//       * Make sure the necessary arguments are there. 
//       */
//      if (args.length != 4)
//      {
//         System.err.println ("Error: Usage \"java Test_RandData [file] " +
//            "[num of courses] [num of instructors] [num of locations");
//         System.exit(1);
//      }
//
//      /*
//       * This is the order the arguments must be given on the command line. If
//       * the numberic arguments aren't good, we'll die right away
//       */
//      String file = args[0];
//      int cs = 0, is = 0, ls = 0;
//      try
//      {
//         cs = Integer.valueOf(args[1]);
//         is = Integer.valueOf(args[2]);
//         ls = Integer.valueOf(args[3]);
//      }
//      catch (NumberFormatException e)
//      {
//         System.err.println (e);
//         System.exit(1);
//      }
//      /*
//       * Ensure the numberic arguments are good
//       */
//      if (cs < 1 || is < 1 || ls < 1)
//      {
//         System.err.println ("Error: Numeric arguments must be > 0");
//         System.exit(1);
//      }
//
//      Schedule s = new Schedule ();
//      
//      /*
//       * Create and set the local databases
//       */
//      Vector<Course> c_list = makeRandCourses(cs);
//      Vector<Instructor> i_list = makeRandInstructors(is, c_list);
//      Vector<Location> l_list = makeRandLocations(ls);
////      s.setLocalCDB(c_list);
////      s.setLocalIDB(i_list);
////      s.setLocalLDB(l_list);
//
//      /*
//       * "null" is the Progress bar object, which isn't necessary for testing
//       * purposes
//       */
////      s.schedule.generate(new Vector<Course>(c_list), 
////                          new Vector<Instructor>(i_list), 
////                          new Vector<Location>(l_list),
////                          new Vector<SchedulePreference>(), 
////                          null);
//
//      /*
//       * Dump relavent data to the file provided on the command line
//       */
////      try
////      {
//////         s.dumpAsPerlText(new PrintStream(file));
////      }
////      catch (FileNotFoundException e)
////      {
////         System.err.println (e);
////      }
//   }
//
//   /**
//    * Creates a list of courses, each with its relavents pieces of data 
//    * "randomly" generated according to the RAND_SEED value. Each course's
//    * data will be generated according to the following rules:
//    *
//    * <ul>
//    *    <li>Sections will range from 1 - 10</li>
//    *    <li>Courses are taught 1 - 4 hours/week</li>
//    *    <li>Labs will have the same DFC as their lecture</li>
//    *    <li>All courses have a max enrollment of 35, no location requirements,
//    *        and the prefix "CPE".</li>
//    * </ul>
//    *
//    * The number of generated courses must be greater than 0 but less than 900
//    * (due to the 3-digit course id limitation).
//    *
//    * @param limit The number of courses to generate
//    * 
//    * @return list of Courses generated
//    */
//   private static Vector<Course> makeRandCourses (int limit) /*==>*/
//   {
//      Vector<Course> cs = new Vector<Course>();
//      /*
//       * Move the limit into the 3 digit range (if already there, this condition
//       * will ensure we don't exceed 3 digits)
//       */
//      if (limit > C_LIMIT)
//      {
//         System.err.println ("Error: Number of courses to generate must be " +
//            "between 1 and " + C_LIMIT + ", inclusive");
//         System.exit(1);
//      }
//      else
//      {
//         limit += 100; // First 3-digit number
//      }
//
//      /*
//       * No 2-digit course ids
//       */
//      while (limit > 99)
//      {
//         Course lec = null, lab = null;
//
//         /*
//          * Between 1 & 4 sections
//          */
//         int sections = rand.nextInt(4) + 1;
//
//         /*
//          * Once a lecture's made, selects its DFC and save it, in order to pass
//          * it on to a possible lab component
//          */
//         //lec = makeCourse (limit, Course.LEC, sections);
//
//         DaysForClasses dfc = chooseDFC();
//         //lec.setDFC(dfc);
//
//         /*
//          * Add the lec to the CDB
//          */
//         cs.add(lec);
//
//         /*
//          * Toss the coin for a lab component
//          */
//         if (rand.nextBoolean())
//         {
////            lab = makeCourse (limit, Course.LAB, sections);
////            lab.setDFC(dfc);
////            lec.setLab(lab);
//         }
//
//         limit --;
//      }
//
//      return cs;
//   }/*<==*/
//
//   /**
//    * Generates a course with "random" data. Its information will follow the 
//    * constraints detailed in the makeRandCourses method.
//    *
//    * @param id The ID for the course (3 digits)
//    * @param type The type (Course.LEC or Course.LAB)
//    * @param sections number of sections of the course
//    *
//    * @return A course w/ data generated which conforms to the rules specified 
//    *         in the makeRandCourses method. 
//    *
//    * @see #makeRandCourses(int)
//    */
//   private static Course makeCourse (int id, String type, int sections) /*==>*/
//   {
//      Course c = new Course ();
//
//      c.setName(Integer.toString(id));
////      c.setID(id);
////      c.setSection(sections);
// 
//      /* 
//       * Teach between 1 - 4 hours/week (which will also be applied to the
//       * courses SCU and WTU count)
//       */
//      int hpw = rand.nextInt(4) + 1;
////      c.setHPW(hpw);
////      c.setSCU(hpw);
////      c.setWTU(hpw);
//         
//      /*
//       * They'll all be "CPE" courses)
//       */
//      c.setType(type);
// //     c.setPrefix("CPE");
//
//      /*
//       * All courses have no required equipment
//       */
// //     c.setRequiredEquipment(new RequiredEquipment(false, false, false));
//
//      return c;
//   }/*<==*/
//
//   /**
//    * Randomly selects a DFC from the array "DFCS"
//    *
//    * @return a "random" DFC from the "DFCS" array
//    */
//   private static DaysForClasses chooseDFC ()/*==>*/
//   {
//      return DFCS[rand.nextInt(DFCS.length)];
//   }/*<==*/
//
//   /**
//    * Creates a list of instructors, each with its relavents pieces of data
//    * "randomly" generated. Each object's data will generated according to the
//    * following rules:
//    *
//    * <ul>
//    *    <li>First and last names will be random, 7-character strings</li>
//    *    <li>Wtus will range from 10 to 20 (inclusive)</li>
//    *    <li>Time preferences will be random from 0 to 10 (inclusive) for all
//    *        times (including 1a and such)</li>
//    *    <li>Course preference will range from 0 to 10 (inslucive)</li>
//    *    <li>The "id" is the same as first name</li>
//    *    <li>Every instructor will have the same office: its value doesn't 
//    *        matter where their office is.</li>
//    *    <li>Every instructor is <i>not</i> disabled.</li>
//    * </ul>
//    *
//    * @param limit Number of instructors to create
//    * @param c_list List of courses which this instructor will have preferences
//    *        for
//    *
//    * @return a list of Instructor's conforming to the above guidelines. 
//    */
//   /* makeRandInstructors ==>*/
//   private static Vector<Instructor> makeRandInstructors (int limit, 
//                                                          Vector<Course> c_list)
//   {
//      Vector<Instructor> is = new Vector<Instructor>();
//      
//      while (limit > 0)
//      {
//         /*
//          * 10 <= WTU <= 20
//          */
//         int wtu = rand.nextInt(11) + 10;
//         String first = randString(7), last = randString(7);
//
//         /*
//          * We'll make the instructor and then set his preferences afterwards
//          */
//         Instructor i = new Instructor (first, last, first, wtu, OFFICE);
//
//         /*
//          * The setters for the course and time preferences are a bit clunky, 
//          * so forgive the large object I've to create here.
//          */
//         ArrayList<CoursePreference> cPrefs = new ArrayList<CoursePreference>();
//         for (Course c: c_list)
//         {
//            cPrefs.add(new CoursePreference(c, rand.nextInt(11)));
//         }
////         i.setCoursePreferences(cPrefs);
//
//         /*
//          * Hash, keyed by days, which yields a hash map keyed by times, 
//          * which'll yield time preferences. 
//          *
//          * Yes, this is a horrible way to represent time preferences. I didn't
//          * code them. When/if it changes, this'll look prettier. 
//          */
//         HashMap<Integer, LinkedHashMap<Time, TimePreference>> tPrefs = 
//            new HashMap<Integer, LinkedHashMap<Time, TimePreference>>();
//         /*
//          * Consider every day of the work week
//          */
////         for (int day: new int[] { Week.MON, Week.TUE, Week.WED, Week.THU, Week.FRI })
////         {
////            tPrefs.put(day, new LinkedHashMap<Time, TimePreference>());
////            /*
////             * Consider every half-hour of the day. 
////             *
////             * An exception will break us out. (Yes, this was written poorly as 
////             * well. And I was the cause this time, sadly).
////             */
////            try
////            {
////               for (Time t = new Time (0, 0); /* Exception */ ; t.addHalf())
////               {
////                  /*
////                   * Create a preference for this time on this day (from 0 - 10)
////                   */
////                  tPrefs.get(day).put(new Time(t), 
////                     new TimePreference(new Time(t), rand.nextInt(11)));
////               }
////            }
////            catch (InvalidInputException e) { /* Do nothing */ }
////         }
////         i.setTimePreferences(tPrefs);
//
//         is.add(i);
//
//         limit --;
//      }
//      return is;
//   }/*<==*/
//
//   /**
//    * Generates random strings of a given length. Strings are made up of 
//    * characters which pass the "Character.isLetter" method
//    *
//    * @param length Length of the string to create
//    *
//    * @return a String, w/ random characters, of length "length"
//    */
//   private static String randString (int length) /*==>*/
//   {
//      char[] chars = new char[length];
//
//      for (int i = 0; i < length; i ++)
//      {
//         do
//         {
//            chars[i] = (char)rand.nextInt(256);
//         } while (!Character.isLetter(chars[i]));
//      }
//
//      return new String(chars);
//   }/*<==*/
//
//   /**
//    * Generates a list of locations with random building and room numbers. Each
//    * is generated under the following guidelines:
//    *
//    * <ul>
//    *    <li>Each bldg/room combination is unique.</li>
//    *    <li>Bldg numbers are between 0 (incl.) and BLDG_LIMIT, (excl.).</li>
//    *    <li>Room numbers are between 0 (incl.) and ROOM_LIMIT, (excl.).</li>
//    *    <li>Each location is a Course.LEC room. Since the type of room does 
//    *        not matter for generation, it makes no difference.</li>
//    *    <li>Each location provides no equipment.</li>
//    *    <li>Each location has a max occupancy of MAX_OCCUPANCE.</li>
//    * </ul>
//    *
//    * @param limit How many locations to generate.
//    *
//    * @return a list of Locations of length "limit" according to the guidelines
//    *         specified above.
//    */
//   private static Vector<Location> makeRandLocations (int limit) /*==>*/
//   {
//      Vector<Location> ls = new Vector<Location>();
//
//      while (limit > 0)
//      {
//         Location l = null;
////         do 
////         {
////            /*
////             * The location constructor works with strings
////             */
////            String bldg = String.valueOf(Math.abs(rand.nextInt() % BLDG_LIMIT));
////            String room = String.valueOf(Math.abs(rand.nextInt() % ROOM_LIMIT));
////
////            l = new Location (bldg, room, MAX_OCCUPANCE, Course.LEC, 
////               false, false, false, false);
////         } while (ls.contains(l));
//
//         ls.add(l);
//         limit --;
//      }
//
//      return ls;
//   }/*<==*/
//}
