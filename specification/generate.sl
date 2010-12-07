(*
 * Module for generation: Details the generate operation, complete with pre 
 * and post conditions. Also, the objects output (Schedule=ScheduleItem
 * are defined here
 *
 * Programmed by: Eric Liebowitz && Jan Soliman
 *)
module Generate;
   import InstructorDB.*;
   import CourseDB.*;
   import LocationDB.*;
   import Database.*;
   import ViewSchedule.*;
   import ScheduleProject.*;
   export *;

   operation Generate
      inputs: db:Database, user:User;
      outputs: schedule:Schedule;
      description:
      (*
       * Generates a schedule, given the database of Instructors, Courses, 
       * Locations, and Instructor time/class preferences
       *);

       precondition:
         (*
          * Ensure that we have a valid database
          *)
         isValidWholeDB (db)

            and

         (*
          * There must be at least one Course in the CourseDB
          * (there is no check for Instructor/Location, as STAFF and TBA
          *  will be used by default, w/o needing to check)
          *)
         #(db.cdb) > 0

            and

         (* And, user can't be a student*)

         user.type != "Student";

      postcondition:

         (*
          * Number of ScheduleItems = Number of course sections
          * (signifying that each course section was given a time slot)
          *)
         #schedule = sumOfSections (db.cdb);

   end Generate;

   object Schedule = ScheduleItem*;
   

end Generate;
