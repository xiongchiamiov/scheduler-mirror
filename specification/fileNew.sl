module fileNew;
import Generate.*;
import ScheduleProject.*;
import Open.*;
export *;

operation FileNew
   inputs: uws:UserWorkSpace;
   outputs: uws':UserWorkSpace;

   description: (*
     Add a new empty schedule to the workspace and make it current.
   *);

   precondition: ;

   postcondition:
     (*
      *)
      
     (exists (uc:ScheduleProject)
         (uc.schedule = uws'.schedule) 

     );

end FileNew;
end fileNew;
