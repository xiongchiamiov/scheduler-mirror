(****
 *
 * Module View defines the objects and operations related to the different
 * schedule views available to user; course, location, and instructor schedule views. 
 * There are operation to filter the information displaying on the schedule.
 * Operation is available to view availability of instructors and locations at a particular time
 *
 *)


module ViewSchedule;
	import Database.Time;
	import InstructorDB.*;
	import CourseDB.*;
	import LocationDB.*;
	import Conflict.*;
	import ScheduleQuality.*;
	import Generate.*;
	export *;



    object View
        components: vs:ViewSetting and adv_fil:AdvancedFilter and items:ScheduleItem* and conf_view:ConflictView;
        description: (* a viewing page that displays a schedule with ScheduleItems and their information based on the options from the ViewSetting ( ViewMode, ViewLevel, FilterOption) and AdvancedFilter *);
    end View;
	
(**********************View Type*****************)
	object ViewType
		components: CourseScheduleView or LocationScheduleView or InstructorScheduleView;
		description: (* There are two specialization of ViewType - courses view and locations view and instructor view *);
	end ViewType ;
	
	object CourseScheduleView
		components: Course*;
		description: (* Scheduler displays the given courses on a schedule view *);
	end CourseScheduleView;
	
	object LocationScheduleView
		components: Location*;
		description: (* Scheduler displays the given location on a schedule view*);
	end LocationScheduleView;
	
	object InstructorScheduleView
		components: Instructor*;
		description: (* Scheduler displays the given instructor on a schedule view*);
	end InstructorScheduleView;
	
(***********************View Mode******************)
    object ViewMode
		components: CalendarMode or ListMode;
        description: (*There are two specialization of ViewMode - calendar mode and list mode*);
    end ViewMode;
	
    object CalendarMode;
	object ListMode;

(***********************View Level******************)
    object ViewLevel
		components: DailyView or WeeklyView;
        description: (*There are two specialization of ViewLevel - daily and weekly*);
    end ViewLevel; 

    object DailyView
        components: Day;
        description: (*Schedule displays a daily schedule of the Day*);
    end DailyView;
	
	object Day = string;
	object WeeklyView;

(**********************Filter Option*********************)
    object FilterOption
        components: 
			CourseNameFilter and CourseNumberFilter and SectionFilter and CourseWTUFilter and CourseTypeFilter and 
			MaxEnrollmentFilter and LabPairingFilter and CourseRequiredEquipmentFilter and InstructorFilter and 
			InstructorIDFilter and InstructorOfficeFilter and InstructorWTUFilter and InstructorDisabilitiesFilter and
			BuildingFilter and RoomFilter and LocationMaxOccupancyFilter and RoomTypeFilter and 
			LocationDisabilitiesComplianceFilter and StartTimeFilter and EndTimeFilter and DaysFilter;
        description: (*Scheduler displays the information on a schedule view if the FilterOption element's value is true*);
    end FilterOption;

   object CourseNameFilter = boolean;
   object CourseNumberFilter = boolean;
   object SectionFilter = boolean;
   object CourseWTUFilter = boolean;
   object CourseTypeFilter = boolean;
   object MaxEnrollmentFilter = boolean;
   object LabPairingFilter = boolean;
   object CourseRequiredEquipmentFilter = boolean;
   object InstructorFilter = boolean;
   object InstructorIDFilter = boolean;
   object InstructorOfficeFilter = boolean;
   object InstructorWTUFilter = boolean;
   object InstructorDisabilitiesFilter = boolean;
   object BuildingFilter = boolean;
   object RoomFilter = boolean;
   object LocationMaxOccupancyFilter = boolean;
   object RoomTypeFilter = boolean;
   object LocationDisabilitiesComplianceFilter = boolean;
   object StartTimeFilter = boolean;
   object EndTimeFilter = boolean;
   object DaysFilter = boolean;
	
(********************Advanced Filter Option*************)
    object AdvancedFilter
        components: StartTimeView and EndTimeView and Day*; 
        description: (*Scheduler displays a schedule starting from the time specified in the StartTimeView until the time specified in the EndTimeView. For viewing a weekly schedule, Scheduler displays a schedule for the selected Days*);
    end AdvancedFilter;

    object StartTimeView = Time;
    object EndTimeView = Time;
	
(*===============================================================================================================================*)

(******************View Setting**************************)

    object ViewSetting
        components: ViewType and ViewMode and ViewLevel and FilterOption;
        description: (*Scheduler uses the attributes from the ViewType, ViewMode, ViewLevel, and FilterOption to display a schedule view. *)
        (*    
        Scheduler uses these options from ViewMode, ViewLevel, FilterOption to display a schedule.
        InfoDialogue is used to display information of an item on a schedule.
        *);
    end ViewSetting;

(*===============================================================================================================================*)

(******************View Popup**************************)

	object PopupView
        components: si:ScheduleItem*;
        description: (*The PopupView displays the CourseNumber, Section, Building, Room, Instructor, and Day information of the ScheduleItem.*)
        (*
        If CourseNumberFilter, SectionFilter, BuildingFilter, RoomFilter, InstructorFilter, or DaysFilter are true or selected by the user, scheduler displays CourseNameItem, SectionItem, BuildingItem, RoomItem, InstructorItem, or DaysItem on the popup screen
        *);
    end PopupView;
	
(**************ScheduleItem************************)
	object ScheduleItem
		components: course:Course*, location:Location*, instructor:Instructor*,day:Day*, start_time:StartTime, end_time:EndTime, Section, lock:Lock;
		description: (*ScheduleItem represents a course generated from the Scheduler*);
	end ScheduleItem;
	
	object StartTime = Time;
	object EndTime = Time;
	object Section = number;
	object Lock = boolean;
	
(*===============================================================================================================================*)

(******************View Info Dialog**************)

    object ViewInfoDialog
        components: si:ScheduleItem and conf:ConflictInfo;
        description: (*ViewInfoDialog displays the details information for a schedule item*);
    end ViewInfoDialog;	
	
(*===============================================================================================================================*)

(******************View Availability****************)
    object ViewAvailability
        components: InstructorAvailability and LocationAvailability and ScheduleItem;
        description: (*Display the available instructors and locations at the specific time of the ScheduleItem*);
    end ViewAvailability;

    object InstructorAvailability 
        components: Instructor*;
        description: (*The available instructor*);
    end InstructorAvailability;

    object LocationAvailability
        components: Location*;
        description: (*The available location*);
    end LocationAvailability;
(*===============================================================================================================================*)

(***********************Operation Schedule*************)
    operation ViewingPopup
        inputs: si:ScheduleItem*, fo:FilterOption;
        outputs: pv:PopupView;
        precondition:
            (si != nil) and
            (fo != nil) and
            (#(si) > 3)
			
			(* 
				scheduleItem is more than 3 objects
				FilterOption is not null
			*);
        postcondition:
            (pv != nil) and
			(
			   forall (si':ScheduleItem)
			   	(si' in pv.si) iff ( si' in si ) 
			);
        description: (*Display the given ScheduleItem(s)' information based on the FilterOption on a small popup window*);
    end ViewingPopup;

    operation ViewingInfoDialog
        inputs: si:ScheduleItem, fo:FilterOption, cvList:ConstraintViolationList,
	          pvList:PreferenceViolationList;
        outputs: id:ViewInfoDialog;
        precondition:
			(si != nil) and
			(fo != nil)
			(*
				ScheduleItem is not null
				FilterOption is not null
			*);
        postcondition:
			(id != nil) and
			(
			   forall (si':ScheduleItem)
			   	(si' = id.si) iff ( si' = si ) 
			)
			and
			(id.conf = GetConflictInfo(si, cvList, pvList));

			(* Displaying all of the information of the ScheduleItem *)
        description: (*Displaying the given ScheduleItem's full information on the dialog window*);
    end ViewingInfoDialogue;
    
    operation AdvancingFilter
        inputs: v:View, af:AdvancedFilter;
        outputs: v':View;
        precondition:
			(v != nil) and
			(af != nil)
			(*
				AdvancedFilter is not null
				View is not null
			*);
        postcondition:
			(v' != nil) and
			(
				forall (af':AdvancedFilter)
					(af' = v'.adv_fil) iff (af' = af)
			)
			(*
				AdvancedFilter is not null
				AdvancedFilter is applied to the View, v'
			*);
        description: (*Change the AdvancedFilter of the View and regenerate a schedule view to correspond with the new AdvancedFilter options*);
    end AdvancingFilter;
     
    operation ViewingAvailability
        inputs: si:ScheduleItem;
        outputs: i:Instructor* and l:Location*;
        precondition:
			(si != nil)
			(*
				ScheduleItem is not null
			*);
        postcondition:
			(	forall(instr:Instructor)
					not(instr in si.instructor) iff (instr in i)
			) and
			(	forall(loc:Location)
					not(loc in si.location) iff (loc in l)
			)
			(*
				Instructor is not the instructor of ScheduleItem
				Instructors are not busy during that time
				Location is not the location of ScheduleItem
				Locations are not busy during that time
			*);
        description: (*Display available instructors and locations for a class specified in the ScheduleItem during the time specified in the ScheduleItem*); 
    end ViewingAvailability;
	
	operation Viewing
		inputs: vs:ViewSetting and items:Schedule and af:AdvancedFilter and cvList:ConstraintViolationList and pvList:PreferenceViolationList;
		outputs: v:View;
		precondition:
			(af != nil )and
			(items != nil) and
			(vs != nil)
			;
		postcondition:
			(v != nil) and
			(
				forall (item:ScheduleItem)
				   (item in v.items) iff (item in items) 
			)
			and
			v.vs = vs
			and
			v.adv_fil = af
			and
			v.conf_view = GetConflictView(items, cvList, pvList) 
	
			(*and
			forall(item in items)
			  *) 
			
				
			(*ScheduleItems are replaced in the view
			ViewSetting is applied to the view
			AdvancedFilter is replaced in the view*)
			
			;
		description: (*Display a course schedule view for the given courses with the given information specified in the ViewSetting, FilterOption, and AdvancedFilter*);
		end Viewing;

end ViewSchedule;


