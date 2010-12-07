SOURCE      = implementation/source/java
DESTINATION = documentation/API
CLASS       = implementation/executables/JVM

PACKAGES = \
   scheduler \
   scheduler.db \
   scheduler.db.admin \
   scheduler.db.admin.admin_ui \
   scheduler.db.coursedb \
   scheduler.db.instructordb \
   scheduler.db.locationdb \
   scheduler.db.preferencesdb \
   scheduler.db.preferencesdb.preferences_ui \
   scheduler.fair_qual \
   scheduler.fair_qual.fair_qual_ui \
   scheduler.generate \
   scheduler.menu \
   scheduler.menu.file \
   scheduler.menu.schedule \
   scheduler.menu.schedule.allInOne \
   scheduler.menu.schedule.lockItems \
   scheduler.view \
   scheduler.view.conflicts \
   scheduler.view.conflicts.conflicts_ui \
   scheduler.view.view_ui

DOC_TITLE = "Documentation for the Scheduler Project"
WINDOW_TITLE = "Documentation for the Scheduler Project"

all:
	cd ../../; \
	javadoc \
   	-sourcepath $(SOURCE) \
      -d $(DESTINATION) \
      -doctitle $(DOC_TITLE) \
      -windowtitle $(WINDOW_TITLE) \
      -private \
      -author \
      $(PACKAGES)
