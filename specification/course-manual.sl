
module ManualEdit; 
        import Database.*;
        import InstructorDB.*;
        import CourseDB.*;
        import LocationDB.*;
        import ViewSchedule.*;
        import Conflict.*;
        export *;


object EditCourseItem
		components: course:Course*, location:Location*, instructor:Instructor*,day:Day*, start_time:StartTime, end_time:EndTime;
		description: (*EditCourseItem represents a temp course to be edited*);
	end EditCourseItem;
	


operation CourseEditPopup
        inputs: c:ScheduleItem;
        outputs: e:EditCourseItem;
        precondition:
                        (*
                                Course is not null
                        *)
		c != nil
                and
                (c.lock);
        postcondition:
                        (* editCourse with course information *);
        description:  (* Display the given courses information *)
                 c.course = e.course
                         and
                 c.location = e.location
                         and
                 c.start_time = e.start_time
                         and
                 c.end_time = e.end_time;     
    end CourseEditPopup;


operation ManualEditCourse
        inputs: c:ScheduleItem, ec: EditCourseItem;
        outputs: s:ScheduleItem, cvList:ConstraintViolationList;
        precondition:
                        (*
                                Course is not null
                        *)
                c != nil
                and
                ec != nil
                and 
                (c.lock);
        postcondition:
                        (* editCourse with course information *)
                 c.course = ec.course
                         and
                 c.location = ec.location
                         and
                 c.start_time = ec.start_time
                         and
                 c.end_time = ec.end_time
                        ;

        description:  (* Display the given courses information *);
    end ManualEditCourse;


operation LockAll
        inputs: c:ScheduleItem*;
        outputs: s:ScheduleItem*;
        precondition:
                        (*      
                                Course is not null
                        *)
               c != nil;
        postcondition:
                        (* Courses are locked*)
               forall(si:ScheduleItem)
		( (si.lock)) iff ( (si in s) and (si in c) );
        description: (*display the given courses information*);
    end LockAll;

operation LockCourse
        inputs: c:ScheduleItem;
        outputs: s:ScheduleItem;
        precondition:
                        (*
                                Course is not null
                        *)
               c != nil;
        postcondition:
                        (* Courses are locked*)
             
               ;
        description: (*Display the given courses information*);
    end Lock;

end ManualEdit;

