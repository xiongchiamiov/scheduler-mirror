module fileExit;
import Generate.*;
import ScheduleProject.*;

object DefaultProject is ScheduleProject;

operation FileExit 
   inputs: sp:ScheduleProject;
   outputs: dsp:DefaultProject;

   precondition:;
   postcondition:
   (*
	  If there was an open project, it is now the default
	  opened project when the Scheduler is relaunched.
   *)
   if sp != nil
	then dsp = sp;
   description:
   (*
	  FileExit stores an open project, if one exists, into the
	  Scheduler's default project. The operation then terminates the 
	  Scheduler.
   *);
end FileExit;

end fileExit;
