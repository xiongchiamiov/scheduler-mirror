#
# This is a template Makefile to be put in the design directory of a 309
# project.  Goto the lines labeled "FILL IN HERE" to customize this template
# for your project.
#
# In order for this Makefile to work properly, your project directory must be
# set up per the 309 SOP, Volume 1.  In particular, you must have the following
# directories defined:
#
#   DIRECTORY                     CONTENTS
#   =====================================================================
#   design/javadoc                root directory for the HTML files generated
#                                 by javadoc
#
#   implementation/source/java    root directory for the Java source files of
#                                 your project, organized into packages
#
# You must also have the following files defined:
#
#   FILE                          CONTENTS
#   =====================================================================
#   design/javadoc/overview.html  Descriptive overview of your entire project.
#                                 This will be placed by javadoc at the top
#                                 level of the generated HTML files.
#
#   implementation/source/java/   Descriptive overview for each package defined
#      PKG_DIR/package.html       in each implementation PKG_DIR.
#


# CSC 309 java lib directory
LIB309 =        /home/gfisher/classes/309/lib/JVM

# Implementation source directory
SOURCE =        implementation/source/java

# Javadoc destination directory
DESTINATION =   design/javadoc

# File containing top-level javadoc overview for your project
OVERVIEW_FILE = design/javadoc/overview.html

### FILL IN HERE:

# On the lines following "PACKAGES = \" below, fill in the names of all the
# packages for your project.  Start each line with one tab character and end
# each line except the last with a space followed by a backslash.
PACKAGES = \
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
	scheduler.generate.generate_ui \
	scheduler.file \
	scheduler.file.file_ui \
	scheduler.edit \
	scheduler.edit.edit_ui \
	scheduler.view \
	scheduler.view.view_ui \
	scheduler.help \
	scheduler.help.help_ui \
	scheduler.scheduler_ui


### FILL IN HERE:
# Between the double quote characters below, fill in the document title for
# your project.  This is the title that will appear in the top-level javadoc
# overview documentation.
DOC_TITLE = "Documentation for the Scheduler Project"

### FILL IN HERE:
# Between the double quote characters below, fill in the title you want to
# appear in the banner of the javadoc browser window.  This will be used as the
# HTML <title> command by javadoc.
WINDOW_TITLE = "Documentation for the Scheduler Project"

# Generate the javadoc files.
doc:
	cd ..; \
	javadoc \
	  -sourcepath $(SOURCE) \
	  -classpath $(LIB309) \
	  -d $(DESTINATION) \
	  -overview $(OVERVIEW_FILE) \
	  -doctitle $(DOC_TITLE) \
	  -windowtitle $(WINDOW_TITLE) \
     -private \
     -author \
		$(PACKAGES)
