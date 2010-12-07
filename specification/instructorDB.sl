(*
 * Module containing the components of the InstructorDB
 *
 * Programmed by: Eric Liebowitz
 *)

module InstructorDB;
   import Database.Time;
   export *;

   object InstructorDB = Instructor*;

      (*
       * Determines whether an InstructorDB is a valid one
       *)
      function isValidInstructorDB (idb:InstructorDB) -> boolean =
      (
         forall (i:Instructor)
            (i in idb) iff isValidInstructor (i, idb);
      );

      object Instructor = name:InstructorName and id:UserID and wtu:WTU
         and office:Office and coursePref:CoursePreference* 
         and timePref:TimePreference*
      description:
      (*
         TODO
      *);
      end Instructor;

         object InstructorName = first:string and last:string
         description: (* The name of a registered user *);
         end;

         object UserID = id:string
         description: (* The unique id of a registered user *);
         end;

         object WTU = num:number
         description: (* number of maximum work-time units *);
         end;

         object Office = building:number and room:number
         description: (* Location of an instructor's office *);
         end;

         object CoursePreference = class:number and pref:number
         description: (* Describes an instructor's desire for a particular class *);
         end;

         object TimePreference = time:Time and pref:number
         description: 
         (*
          * Describes an instructor's desire for a 
          * particular time interval 
          *);
         end;

      (*
       * Determines whether an Instructor object is a valid one
       *)
      function isValidInstructor (i:Instructor, idb:InstructorDB) -> boolean =
      (
         (*
          * The id is not empty
          *)
         i.id != nil

            and

         (* 
          *There is no other user record with an id like this one 
          *)
         (not (exists (i' in idb) i'.id = i.id))
 
            and

         (*
          * The name is not empty
          *)
         i.name.first != nil and i.name.last != nil
  
           and
 
         (*
          * The office is not empty
          *)
         i.office.building != nil and i.office.room != nil;
      );

      operation AddInstructor
         inputs: i:Instructor, idb:InstructorDB;
         outputs: idb':InstructorDB;
         description:
         (* Adds a given instructor to the instructor database. *);

         precondition: 
         
            (*
             * "i" must be a valid Instructor
             *)
            isValidInstructor (i, idb);

         postcondition:
            (*
             * Only "i" was added to "idb"
             *)
            forall (i':Instructor)
               (i' in idb') iff ((i' = i) or (i' in idb));
      end AddInstructor;

      operation EditInstructor
         inputs: old:Instructor, new:Instructor, idb:InstructorDB;
         outputs: idb':InstructorDB;
         description:
         (* Edits a given instructor which is already in the database *);

         precondition:
            (*
             * "old" and "new" cannot be the same
             *)
            old != new
   
               and
   
            (*
             * "old" is already in "idb"
             *)
            old in idb
         
               and
            
            (*
             * "new" must be a valid Instructor
             *)
            isValidInstructor (new, idb);

         postcondition:
            (*
             * An instructor record is in the output database iff it was
             * already there to begin with, iff it was the new user added, and
             * iff it is not the old instructor that was changed
             *)
            forall (i':Instructor)
               (i' in idb') iff (((i' = new) or (i' in idb)) and (i' != old));

      end EditInstructor;

      operation RemoveInstructor
         inputs: i:Instructor, idb:InstructorDB;
         outputs: idb':InstructorDB;
         description:
            (* 
             * Removes a given instructor from the database. This does not deal 
             * with admin removal. This is handled elsewhere.
             *);
   
         precondition:
            (*
             * "i" must be in "idb"
             *)
            (i in idb);
   
         postcondition:
            (*
             * The new database differs from the old only in the absence of "i"
             *)
            forall (i':Instructor)
               (i' in idb') iff ((i' != i) and (i' in idb));

      end RemoveInstructor;
end InstructorDB;
