package scheduler.view.web.shared;

import java.util.ArrayList;

/* An ArrayList which contains a string for conflict error messages.  Allows a 
 * list of ScheduleItemGWT objects and an error message to be returned from the
 * server with one server call.
 */
public class ScheduleItemList extends ArrayList<OldScheduleItemGWT>
{ 
 public String conflict = "";
}
