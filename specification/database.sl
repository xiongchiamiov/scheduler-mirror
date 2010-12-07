(*
 * Module containing the components of the Scheduler Tool's Database
 *
 * Programmed by: Eric Liebowitz
 *)

module Database;
   import InstructorDB.*;
   import CourseDB.*;
   import LocationDB.*;
   import Preferences.*;
   export *;

   object Database = idb:InstructorDB           and 
                     cdb:CourseDB               and
                     ldb:LocationDB             and
                     pdb:PreferencesCollection
   description:
   (*
    * Top-level class which contains all databases pertaining to the scheduler:
    * instructors, classes, locations, and preferences
    *);
   end Database;

   object Time = hour:number and minute:number
   description:
   (*
    * Generic object for "Time" used by multiple modules of the scheduler
    *);
   end Time;

   (*
    * Verifies whether all our databases are valid or not. 
    *
    * NOTE: Each DB test function is located in its particular module file
    *)
   function isValidWholeDB (db:Database) -> boolean =
   (
      isValidInstructorDB (db.idb);
      isValidCourseDB     (db.cdb);
      isValidLocationDB   (db.ldb);
(*      isValidPreferenceDB (db.pdb);*)      
   );

end Database;
