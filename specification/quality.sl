(*
 * Module containing the components of the Scheduler's quality management tool
 *
 * Programmed by: Jason Mak
 *)

module ScheduleQuality;

   import Generate.*;
   import Preferences.*;
   import ViewSchedule.*;
   export *;

   object ScheduleQuality
      components: pvList:PreferenceViolationList, quality:integer,
	              low:integer, high:integer, avg:integer; 
      description:
      (*
         ScheduleQuality determine the schedule's overall "quality" based
		 on the amount and degree of schedule preference violations.
      *);
   end ScheduleQuality;

   object PreferenceViolationList = PreferenceViolation*;
      object PreferenceViolation
      components: pref:Preference, siList:ScheduleItem*, points:integer, type:pvType, imp:integer;
      description:
      (*
         A PreferenceViolation contains the preference violated, course
		 sections involved, and the calculated points for the violation.
      *);
      end PreferenceViolation;

   object pvType = "overlap" or "room" or "lab" or "days";

   operation FindPrefViolations
      inputs: prefDB:PreferencesCollection, siList:ScheduleItem*;
      outputs: pvList:PreferenceViolationList;
      precondition:
	  (*
	     There is at least one preference in the collection of preferences.
	  *)
      exists (pref:Preference) pref in prefDB;
      postcondition:
	  (*
	     Each preference violation in the preference violation database corresponds to a
		 preference in the collection of preferences and vice versa. Each preference violation
		 is assigned points.
	  *)
	  forall(pv in pvList)
	     exists (pref in prefDB) pref = pv.pref

	  and

	  forall(pref in prefDB)
	     (exists (pv in pvList) pv.pref = pref)

	  and

	  pvList = FindPoints(pvList);

      description:
      (*
         FindPrefViolations scans through all ScheduleItems and
         checks the PreferencesCollection for any violations. It
         calculates the points for each violation and puts them in a
         database.
      *);
   end FindPrefViolations;

   function FindPoints
      inputs: pvList:PreferenceViolationList;
      outputs: pvList':PreferenceViolationList;
      precondition:
	  (*
	     pvList is not null;    
	  *)
	  pvList != nil;
      postcondition:
	  (*
	    If the preference violation is an overlap, add
		one point per course overlap and multiply the total points by 
		the importance value;
      *)
	  forall (pv in pvList)
	  (if pv.type = "overlap"
	   then (pv.points = #pv.siList * pv.imp)	   
	   )
	  and
	  (*
	    If the preference violation is a room requirement, assign
		the product of the importance value and 2;
      *)
      forall(pv in pvList)
	  (if pv.type = "room"
	   then (pv.points = 2 * pv.imp)
	  )
	  and
	  (*
	    If the preference violation involves days offered, assign
		the product of the importance value and 2;
      *)
	  forall(pv in pvList)
	  (if pv.type = "days"
	   then (pv.points = 2 * pv.imp)
	  )
	  and 
	  (*
	    If the preference violation is a lecture/lab time proximity, multiply
		the difference in hours by the importance value.
      *)
	  forall(pv in pvList)
	  (if pv.type = "lab"
	   then pv.points = pv.imp; 
	  );
      description:
      (*
	   This function goes through the entire list of preference violations
	   and assigns points to each violation.
	  *);
   end FindPoints;

   operation ViewQuality
      inputs: pvList:PreferenceViolationList, quality:integer, high:integer, low:integer, avg:integer;
      outputs: sq:ScheduleQuality;
      precondition:
	  (*
	     Each preference violation in the preference violation database has
		 points greater than or equal to zero. The quality value of
		 the schedule is greater than or equal to zero.
	  *)
	  quality >= 0 
	  
	  and
	  
      forall(pv in pvList)
	     pv.points >= 0;
      
      postcondition:
	  (*
	     The Schedule Quality Window's fields are
		 initialized by the inputs.
	  *)
	  sq.quality = quality and sq.high = high and sq.low = low and sq.avg = avg;
      description:
      (*
         ViewQuality displays the Schedule Quality Window that
		 includes all the preference violations and the calculated 
		 schedule quality value.
      *);    
   end ViewQuality;


   operation CalculateQuality
      inputs: pvList:PreferenceViolationList;
      outputs: quality:integer;
      precondition:
	  (*
	     Each preference violation in the preference violation database has
		 points greater than or equal to zero. 
	  *) 
      forall(pv in pvList)
	     pv.points >= 0;
      
      postcondition:
	  (*
	     The quality value is the sum of all preference violation points.
	  *)
	  forall(pv in pvList)
	     quality = pv.points + quality;
	     
      description:
      (*
         CalculateQuality uses the points from each preference violation
		 in the preference violation database to determine a value for
		 the overall quality of the schedule.
      *);   
   end CalculateQuality;
end ScheduleQuality;
