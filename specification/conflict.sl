(*
 * Module containing the Scheduler's constraint management tool
 *
 * Programmed by: Jason Mak
 *)

module Conflict;

   import Generate.*;
   import LocationDB.*;
   import ScheduleQuality.*;
   import ViewSchedule.*;
   export *;

   object ConstraintManager
      components: cvList:ConstraintViolationList, oldSI:ScheduleItem, newSI:ScheduleItem;
      description:
      (*
         An object within the Scheduler that keeps track of a
		 ScheduleItem before and after an administrator has manually
		 edited it. The object consists of constraint violations
		 that result from the manual edit. An administrator 
		 can view the last manual edit, constraint violations,
		 and resolutions through the ConstraintManager. 
      *);
   end ConstraintManager;
   
   object ConstraintViolationList = ConstraintViolation*;
      object ConstraintViolation 
	  components: si:ScheduleItem, type:LocOrInstr; 
      description:
      (*
         A constraint violation consists of a ScheduleItem that has
		 its location or instructor double-booked. 
      *);
      end ConstraintViolation;

   object LocOrInstr = "location" or "instructor";

   function FindConstraintViolations
      inputs: newSI:ScheduleItem, sched:Schedule;
	  outputs: cvList:ConstraintViolationList;
      precondition: 
      (*
         The ScheduleItem modified by the manual edit is in the schedule.
      *)
      newSI in sched;
      postcondition: 
      (*
         Each constraint violation has a corresponding ScheduleItem
		 with a double-booked location or a double-booked instructor.
		 The type field of each constraint violation should match
		 the type of constraint violation.
      *)
      forall(cv in cvList)
	     exists (si in sched) (si != cv.si and 
				 ((cv.si.location = si.location and cv.type = "location") 
				  or (cv.si.instructor = si.instructor and cv.type = "instructor"))); 
      description:
      (*
         FindConstraintViolations uses a modified ScheduleItem following a 
		 a manual edit to check the schedule for constraint violations. This
		 function is to be called following a manual edit operation specified in
		 the Course-Manual module.
      *);
   end FindConstraintViolations;

   function ResolveConstraintViolations
      inputs: sched:Schedule, cvList:ConstraintViolationList;
      outputs: sched':Schedule;
      precondition:
	  (*
         Each constraint violation in the list of constraint violations 
		 has its ScheduleItem in the actual schedule.    
      *)
      forall(cv in cvList)
         exists(si in sched) cv.si = si;

      postcondition: 
      (*
         Each ScheduleItem associated with each constraint violation
		 has either its instructor changed to STAFF or its course 
		 changed to TBA based on the type of constraint violation. 
	  *)
      forall(si in sched')
	     forall(cv in cvList)
	     if (si = cv.si)
	     then ((cv.type = "location" and si.location = nil) 
		  or (cv.type = "instructor" and si.instructor = nil));

      description:
      (*
         ResolveConstraintViolations resolves all constraint violations in the Schedule 
		 by changing location(s) to TBA (nil) and/or instructor(s) to STAFF (nil).      
         This function called immediately after FindConstraintViolations
		 following a manual edit.
	  *); 
   end ResolveConstraintViolations;

   operation ViewConflicts
      inputs: oldSI:ScheduleItem, newSI:ScheduleItem, cvList:ConstraintViolationList;
      outputs: cm:ConstraintManager;
      precondition:
      (*
         The new ScheduleItem is not equal to the old ScheduleItem 
		 because it has been modified by a manual edit.
      *)
      newSI != oldSI;
      postcondition: 
      (*
         The Constraint Manager components are updated with the input data
		 and displayed in the Constraint Manager Window.       
      *)
      cm.oldSI = oldSI and cm.newSI = newSI and cm.cvList = cvList;
   	  description:
      (*
         ViewConflicts takes the last manual edit and the list of resolved 
		 constraint violations and displays them in the Constraint Manager Window.
      *);
   end ViewConflicts;

   object ConflictView 
      components: cvList:ConstraintViolationList, 
                  pvList:PreferenceViolationList, sched:Schedule;
      description:
      (*
	     View of a schedule's conflicts through red markings
	     for constraint violations and yellow markings for preference violations.
      *);  
   end ConflictView;

   function GetConflictView
      inputs: sched:Schedule, cvList:ConstraintViolationList, 
	          pvList:PreferenceViolationList;
      outputs: cView:ConflictView;
   precondition:
   (*
	  Each constraint violation has its affected ScheduleItem 
	  in the schedule.
   *)
   forall(cv in cvList)
      exists (si in sched) cv.si = si

   and
   
   (*
	  Each preference violation has its affected ScheduleItem(s) 
	  in the schedule.
   *)
   forall(pv in pvList)
      forall(psi in pv.siList)	
         exists (si in sched) psi = si;
   postcondition:
   (*
	  The components of ConflictView are initialized with
	  the input data.
   *)
   cView.pvList = pvList and cView.cvList = cvList and cView.sched = sched;
   description:
   (*
	  GetConflictView uses the preference violations and constraint violations
	  found by the Scheduler to create a ConflictView object for the schedule.
      This function is used by ViewSchedule to add conflict markings to
	  to the schedule view.
   *);
   end GetConflictView;

   object ConflictInfo
      components: si:ScheduleItem, cvList:ConstraintViolationList,
	              pvList:PreferenceViolationList;
      description:
	  (*
	     ConflictInfo consists of a ScheduleItem and lists of any conflicts 
		 (preference or constraint violations) that involve it.
	  *);
   end ConflictInfo;

   function GetConflictInfo
      inputs: si:ScheduleItem, cvList:ConstraintViolationList,
	          pvList:PreferenceViolationList;
      outputs: cInfo:ConflictInfo;
	  precondition:
	  (*
	     si is not null.
	   *)
	  si != nil;
      postcondition:
	  (*
	     The ScheduleItem component of the cInfo is initialized with
		 the input.
	  *)
	  cInfo.si = si

	  and 
	  (*
	     cInfo.si is equivalent to a ScheduleItem component in every 
		 preference violation in the generated cInfo.pvList.
      *)
	  forall(pv in cInfo.pvList)
	     exists(psi in pv.siList) psi = cInfo.si

	  and
	  (*
	     cInfo.is equivalent to the ScheduleItem component in
		 every constraint violation in the generated cInfo.cvList.
	  *)
      forall(cv in cInfo.cvList)
	     cv.si = cInfo.si;

      description:
	  (*
	     This function uses all the preference violations and constraint violations
		 found by the Scheduler to generate conflict information
		 for a specific ScheduleItem. The function selects the conflicts
		 that involve the ScheduleItem to create a ConflictInfo object. This
		 object is used by ViewSchedule to display detailed information
		 about a conflict in a ViewInfoDialog popup. 
	   *);
   end GetConflictInfo;	  
end Conflict;
