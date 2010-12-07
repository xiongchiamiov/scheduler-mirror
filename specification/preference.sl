(*
 * Module containing the components of the Scheduler Preference Database
 *
 * Leland Garofalo
 *)

module Preferences;

export *;

object PreferencesCollection = Preference*;
   object Preference
      components: t:Type and v:Violatable and i:Importance and n:Name and d:Data;
   end Preference;


   object Type = string;
   object Name = string;
   object Data = string;
   object Violatable = number;
   object Importance = number;   
   
   operation addPreference
     inputs: db:PreferencesCollection, p:Preference;
	  outputs: db':PreferencesCollection;
	 
     precondition: (p != nil);
	  postcondition: exists (p' in db')
            (p = p') ;

	  description: (*
	     addPreference takes information entered by user and adds the 
		  listed information to the preference database.
         *);
   end addPreference;
	
   operation deletePreference
      inputs: db:PreferencesCollection, p:Preference;
      outputs: db':PreferencesCollection;
      precondition: (p != nil) and exists (p' in db) (p' = p);
      postcondition:  forall (p' in db')
            (p != p') ;
   end deletePreference;
      
   operation editName
      inputs: p:Preference, n':Name;
	  outputs: p':Preference;
	  precondition: (n'!= nil) ;
	  postcondition: (p'.n = n');
   end editName;
   
   operation editData
      inputs: p:Preference, d':Data;
	  outputs: p':Preference;
	  precondition: (d'!= nil) ;
	  postcondition: (p'.d = d');
   end editData;
   
   operation editViolatable
      inputs: p:Preference, v':Violatable;
	  outputs: p':Preference;
	  precondition: (v'!= nil) ;
	  postcondition: (p'.v = v');
   end editViolatable;
   
   operation editImportance
      inputs: p:Preference, i':Importance;
	  outputs: p':Preference;
	  precondition: (i'!= nil) ;
	  postcondition: (p'.i = i');
   end editImportance;

   operation editType
      inputs: p:Preference, t':Type;
	  outputs: p':Preference;
	  precondition: (t'!= nil) ;
	  postcondition: (p'.t = t');
   end editType;

   function isValidPreferenceDB (db:PreferencesCollection) -> boolean =
	(
      forall(p in db) (p != nil);
	);
   

end Preferences;

