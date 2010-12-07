all:	gui run clean

gui: 
	javac scheduler/*.java                  \
	scheduler/manual_edit/*.java            \
   scheduler/quality/*.java                \
   scheduler/top_menu/*.java               \
   scheduler/constraintviolation/*.java    \
   scheduler/preferences_ui/*.java         \
   scheduler/schedule_menu/*.java          \
   scheduler/splash/*.java                 \
   scheduler/view_ui/*.java 					 \
	scheduler/confirm_close/*.java

run:
	java scheduler/SchedulerUI

clean:
	rm scheduler/*.class
	rm scheduler/calander_view/*.class
	rm scheduler/manual_edit/*.class
	rm scheduler/quality/*.class
	rm scheduler/top_menu/*.class   
	rm scheduler/constraintviolation/*.class
	rm scheduler/preferences_ui/*.class
	rm scheduler/schedule_menu/*.class
	rm scheduler/splash/*.class               
	rm scheduler/view_ui/*.class
