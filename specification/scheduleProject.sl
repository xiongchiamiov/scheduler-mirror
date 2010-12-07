(*
 * Defines the object which contains all things pertaining to a schedule.
 * This object is what is saved/opened for a user to save/view schedules
 *
 * Programmed by: Eric Liebowitz
 *)

module ScheduleProject;
   import Database.Database;
   import Generate.Schedule;
   import Edit.*;
   import ViewSchedule.*;
   export *;

   object ScheduleProject = db:Database and schedule:Schedule 
      and term:string and year:number and needsToBeSaved:boolean
      and name:string and readable:boolean and writeable:boolean 
      and size:number and user:User
   description:
   (*
    * TODO
    *);
   end ScheduleProject;

   operation ProjectSave
      inputs: s:ScheduleProject;
      outputs: s':ScheduleProject;
      description:
      (*
       * Saves a given project, if necessary
       *);

      precondition:
         (*
          * Project must be in need of saving
          *)
         s.needsToBeSaved
         
            and
         
         (*
          * Project must be writeable at time of save attempt
          *)
         s.writeable

            and

         (*
          * Project must already have a name. If it does not
          * (an untitled/new project), the "Save As" operation 
          * will handle it
          *)
         s.name != nil;
         
      postcondition:
         (*
          * Project no longer needs to be saved
          *)
         s'.needsToBeSaved = false;
   end ProjectSave;

   operation ProjectSaveAs
      inputs: s:ScheduleProject, name:string;
      outputs: s':ScheduleProject;
      description:
      (*
       * Saves a given project with a given name, regardless
       * of whether it has already been saved or not. 
       *);

      precondition:
         (*
          * Schedule must be writeable
          *)
         s.writeable

            and

         (*
          * Name for project cannot be nil
          *)
         name != nil;

      postcondition:
         (*
          * The saved schedule's name = "name"
          *)
         s'.name = name;
   end ProjectSaveAs;


  object UserWorkSpace is
      components: schedule:Schedule and
      previous_state:PreviousState and clipboard:Clipboard and
      selection:Selection and context:SelectionContext and user:User;
      description: (*
         The Clipboard is used with the Edit cut, copy, and paste operations.
      *);
  end UserWorkSpace;

  object PreviousState is
      components: ScheduleProject*;
      description: (*
         PreviousState is the snapshot of Schedules before the most
         recently performed scheduling operation used by EditUndo.  The
         Schedule, Change, and Delete operations save the previous state to
         support Undo.
      *);
   end PreviousState;

   object User = id:string and type:string 
   description:
   (*
    * Information on the User object.
    *);
   end User;

end ScheduleProject;
