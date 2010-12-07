(*
 * Module containing the components of the CourseDB
 *
 * Programmed by: Eric Liebowitz
 *)

module CourseDB;
   export *;

   object CourseDB = Course*;


      object Course = name:CourseName and wtu:number and scu:number 
         and courseType:string and maxEnrollment:number
         and numOfSections:number and LabPairing:CourseName
         and requiredEquipement:RequiredEquipment
      description:
      (*
         TODO
      *);
      end Course;

         object CourseName = major:string and num:number
         description: 
            (* 
             * A course's name ("CPE, CSC"...) and num (101, 102...)
             *);
         end;
    
   object RequiredEquipment = smartRoom:boolean and overHead:boolean
      and laptopConnectivity:boolean
   description:
   (*
    * Details the room-technology required by a course
    *);
   end;

   operation AddCourse
      inputs: c:Course, cdb:CourseDB;
      outputs: cdb':CourseDB;
      description:
      (*
       * Adds a given course to the course database
       *);

      precondition: 
      
         (*
          * "c" must be a valid course
          *)
         isValidCourse (c, cdb);

      postcondition:
         (*
          * Only "c" was added to "cdb"
          *)
         forall (c':Course)
            (c' in cdb') iff ((c' = c') or (c' in cdb));

   end AddCourse;

   operation EditCourse
      inputs: old:Course, new:Course, cdb:CourseDB;
      outputs: cdb':CourseDB;
      description:
      (* Edits a given course which is already in the course database*);

      precondition:
         (*
          * "old" and "new" cannot be the same
          *)
         (old != new)

            and

         (*
          * "old" is aleady in "cdb"
          *)
         (old in cdb)

            and

         (*
          * "new" must be a valid Course
          *)
         isValidCourse (new, cdb);

   end EditCourse;

   operation RemoveCourse
      inputs: c:Course, cdb:CourseDB;
      outputs: cdb':CourseDB;
      description:
      (* 
       * Removes a given, alredy-exiting course from the course database
       *);

      precondition:
         (*
          * "c" must be in "cdb"
          *)
         (c in cdb);

      postcondition:
         (*
          * The new database differs from the oldonly in the absence of "c"
          *)
         forall (c':Course)
            (c' in cdb') iff ((c' != c) and (c' in cdb));

   end RemoveCourse;

   (*
    * Determines whether a CourseDB is a valid one
    *)
   function isValidCourseDB (cdb:CourseDB) -> boolean =
   (
      forall (c:Course)
         (c in cdb) iff isValidCourse (c, cdb);
   );

   (*
    * Determines whether a given Course is a valid one
    *)
   function isValidCourse (c:Course, cdb:CourseDB) -> boolean = 
   (
      (*
       * "c" is not already in "cdb"
       *)
      (not (exists (c' in cdb) c'.name != c.name)) 
         and
      (not (exists (c' in cdb) c'.courseType != c.courseType))

         and

      (*
       * "c" has a name
       *)
      (c.name.major != nil)
         and
      (c.name.num != nil)

         and

      (*
       * "c" has a wtu and scu count
       *)
      (c.wtu != nil)
         and
      (c.scu != nil)

         and

      (*
       * "c" has a type
       *)
      (c.courseType != nil)

         and

      (*
       * "c" has at least one section
       *)
      (c.numOfSections > 0);
   );

   (*
    * Returns the total number of class sections in the entire CourseDB
    *)
   function sumOfSections (cdb:CourseDB) -> number =
   (
      if (#cdb = 0) 
         then 0
      else 
         cdb[1].numOfSections + sumOfSections (cdb[2:#cdb])
   );
end CourseDB;
