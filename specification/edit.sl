(*
 * Module containing the components of the Scheduler Tool's Edit DropDown
 *
 * Programmed by: Jan Lorenz Soliman
 *)

module Edit;
   import ScheduleProject.*;
   export *;

   object Clipboard is
     components: string;
     description: (* 
         The clipboard holds text for the cut and copy operations.
     *);
   end Clipboard;

   object Selection is
     components: start_position:integer and end_position:integer and
         context:string;
     description: (*
         The  Selection is the starting and ending index of the selection. 
         *);
   end Selection;

   object SelectionContext is
     components: string;
     description: (*
         SelectionContext is the text context in which the user makes a selection.

     *);
   end SelectionContext;
   
  operation EditCut
    inputs: uws:UserWorkSpace;
    outputs: uws':UserWorkSpace;

    description: (*
        The currently selected text segment is copied into the clipboard and
        removed from the workspace.
    *);

    precondition:
        (*
         * The selection is not empty.
         *)
        uws.selection != nil;

    postcondition:
        (*
         * The clipboard of the output workspace equals the selection.  The
         * selection context of the output workspace has the selection removed.
         * The selection of the output workspace is nil.
         *)
        (uws'.clipboard = uws.context[uws.selection.start_position:uws.selection.end_position])

             and

        (uws'.context = uws.context[1:uws.selection.start_position-1] +
                        uws.context[uws.selection.start_position+1:
                                    #(uws.context)])

             and

        (uws'.selection = nil);

  end EditCut;

  operation EditCopy
    inputs: uws:UserWorkSpace;
    outputs: uws':UserWorkSpace;

    description: (*
        The currently selected text segment is copied into the clipboard;
    *);

    precondition:
        (*
         * The selection is not empty.
         *)
        uws.selection != nil;

    postcondition:
        (*
         * The clipboard of the output workspace equals the selection.  The
         * context and selection of the output workspace are unchanged.
         *)
        (uws'.clipboard = uws.context[
            uws.selection.start_position:uws.selection.end_position])

             and

        (uws'.context = uws.context)

             and

        (uws'.selection = uws.selection);

  end EditCopy;


   operation EditPaste
     inputs: uws:UserWorkSpace;
     outputs: uws':UserWorkSpace;

     description: (*
         Paste the contents of the clipboard into the currently selected
         start position, replacing any selected text from start to end position.
     *);

     precondition:
         (*
          * The clipboard is not empty.
          *)
         uws.clipboard != nil;

     postcondition:
         (*
          * The context in the output workspace is the string consiting of
          * everything up to the selection, followed by the clipboard, followed
          * by everything after the selection.  The selection of the output
          * workspace is nil and the clipboard is unchanged.
          *)
         (uws'.context = uws.context[1:uws.selection.start_position-1] +
                         uws.clipboard +
                         uws.context[uws.selection.start_position+1:
                                     #(uws.context)])
              and
         (uws'.selection = nil)

              and

         (uws'.clipboard = uws.clipboard);

   end EditPaste;

   operation EditFind
     inputs: uws:UserWorkSpace, find:string;
     outputs: uws':UserWorkSpace;

     description: (*
          Searches for a string in the schedule files.
     *);

     precondition: (*
          The input string is not empty.
     *)
     find != nil;

     postcondition: (*
          The string found is equal to the one inputed. The text is selected on screen.
     *)
      (uws'.context = uws.context[1:uws.selection.start_position-1] +
                      uws.context[uws.selection.start_position+1: #(uws.context)]);

   end EditFind;

   operation EditSelectAll
     inputs: uws:UserWorkSpace;
     outputs: uws':UserWorkSpace;

     description: (*
          Selects all of the text on the screen.
     *);

     precondition: (*
          The input string is not empty.
     *)
     uws != nil;

     postcondition: (*
          The string found is equal to the one inputed. The text is selected on screen.
     *)
         (uws'.context = uws.context[1:#(uws.context)])
              and
         (uws'.selection = nil);

   end EditSelectAll;

end Edit;
