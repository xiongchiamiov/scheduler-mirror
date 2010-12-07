module Open; 
        import Database.*;
        import InstructorDB.*;
        import CourseDB.*;
        import LocationDB.*;
        import ViewSchedule.*;
        import Conflict.*;
        import ScheduleProject.*;
        import Generate.Schedule;
        import Edit.*;
        export *;



      object FileSpace is File*
    description: (*
        A FileSpace is an abstract model of a file space in the operating
        environment in which the Scheduler Tool is run.  The FileSpace is simply
        a collection of zero or more Files, with no other properties modeled
        here.
     *);
  end;

     object File is
    components: name:FileName and permissions:FilePermissions and
        file_type:FileType and data:FileData;
    description: (*
        A File is an abstraction of a file stored in the file space.  It has a
        name, permissions, type, and data.  These are the components sufficient
        to specify the behavior of Calendar Tool file operations.
    *);
  end File;

   
   object FileName is string
    description: (*
        The name of a file.  The string representation here is an abstraction
        of file names used in specific operating environments.  Implementations
        may obey any syntactic or semantic constraints imposed by a particular
        environment.
    *);
  end;

  object FilePermissions is is_readable:IsReadable and is_writable:IsWritable
    description: (*
        FilePermissions indicate whether a file is readable and/or writable.
    *);
  end;

  object IsReadable is boolean
    description: (*
        Flag indicating whether a file is readable, which is required to be
        true by the FileOpen operation.
    *);
  end;

  object IsWritable is boolean
    description: (*
        Flag indicating whether a file is writable, which is required to be
        true by the FileSave operation.
    *);
  end;

  object FileType is other_type:OtherType
    description: (*
        The type of file data is either SchedulerType data (which we care
        about), SettingsType data (which we also care about), or any other
        type of data (which we don't care about).
    *);
  end FileType;

  

     object FileData is ScheduleProject
    description: (*
        The abstract representation of scheduler-type FileData is a UserCalendar
        object.  Calendar Tool implementors may use any concrete file data
        representation that accurately holds all UserCalendar components.
    *);
  end FileData;


     object OtherType
    description: (*
        File data typing tag indicating that a file contains data other than
        calendar data created by the Calendar Tool.
    *);
  end OtherType;

 

   operation FileOpen 
    inputs: fs:FileSpace, fn:FileName, uws:UserWorkSpace;
    outputs: uws':UserWorkSpace;

    description: (*
        Open an existing scheduler file of the given name and put the data from
        that file in the workspace.
    *);

    precondition:
        (*
         * A file of the given name exists in the given file space, the file
         * is readable, and the file's data are of type calendar.
         *)
        exists (file in fs)
            (file.name = fn) and
            file.permissions.is_readable;

    postcondition:
        (*
         * The output workspace has a new schedule containing the file data of
         * the input file, and that schedule is current.  The user id of the
         * new schedule is that of the workspace, the options are the given
         * global options input, and the scedule does not require saving.  The
         * schedules in positions 1-last in the the input workspace are in
         * positions 2-last+1 in the output workspace.
         *)
        (exists (uc:ScheduleProject)
            (uc.schedule = uws'.schedule) and
            (exists (file in fs)
                (file.name = fn) and
                (uc = file.data)
            ) 
        );
  end FileOpen;

end Open;
