(*
 * Module containing the components of the LocationDB
 *
 * Programmed by: Eric Liebowitz
 *)

module LocationDB;
   export *;

   object LocationDB = Location*;

      (*
       * Determines whether a LocationDB is a valid one
       *)
      function isValidLocationDB (ldb:LocationDB) -> boolean = 
      (
         forall (l:Location)
            (l in ldb) iff isValidLocation (l, ldb);
      );

      object Location = building:number and room:number and maxOccupancy:number
         and type:string and providedEqiupment:ProvidedEquipment
         and disabilitiesCompliance:boolean
      description: 
      (* 
         TODO
       *);
      end Location;
   
   object ProvidedEquipment = smartRoom:boolean and overHead:boolean
      and laptopConnectivity:boolean
   description:
   (*
    * Details the technology with which a given room is equipped
    *);
   end;
   
   (*
    * Determines whether a Location object is a valid one
    *)
   function isValidLocation (l:Location, ldb:LocationDB) -> boolean =
   (
      (*
       * "l" must not already be in "ldb"
       *)
      (not (exists (l' in ldb) 
         (l'.building = l.building and l'.room = l.room)))

         and

      (*
       * "l" must have a building, room, maxOccupancy, and type
       *)
      (l.building != nil)     and 
      (l.room != nil)         and 
      (l.maxOccupancy != nil) and
      (l.type != nil);
   );

   operation AddLocation
      inputs: l:Location, ldb:LocationDB;
      outputs: ldb':LocationDB;
      description: (* Adds a given location to the location database *);

      precondition: 
      
         (*
          * "l" must be a valid Location
          *)
         isValidLocation (l, ldb);

      postcondition:
         (*
          * Only "l" was added to "ldb"
          *)
         forall (l' in ldb')
            (l' in ldb') iff ((l' = l) or (l' in ldb));
   end AddLocation;

   operation EditLocation
      inputs: old:Location, new:Location, ldb:LocationDB;
      outputs: ldb':LocationDB;
      description: 
      (*
       * Edits a given, already-existing Location in the database
       *);

      precondition:
         (*
          * "old" and "new" cannot be the same
          *)
         (old != new)

            and

         (*
          * "new" must be a valid location
          *)
         isValidLocation (new, ldb);

      postcondition:
         (*
          * A location is in the output database iff it was already there
          * to begin with, iff it was the new user added, and iff it is
          * not the old instructor that was changed
          *)
         forall (l':Location)
            (l' in ldb') iff (((l' = new) or (l' in ldb)) and (l' != old));

   end EditLocation;

   operation RemoveLocation
      inputs: l:Location, ldb:LocationDB;
      outputs: ldb':LocationDB;
      description:
      (*
       * Removes a given, already existing locatino from the
       * location database
       *);

      precondition:
         (*
          * "l" must be in "ldb"
          *)
         (l in ldb);

      postcondition:
         (*
          * The new database differs from teh old only in the absence of "l"
          *)
         forall (l':Location)
            (l' in ldb) iff ((l' != l) and (l' in ldb));

   end RemoveLocation;
end LocationDB;
