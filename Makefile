#
# This is an "as-is" Makefile to be put be put in the root directory of a CSC
# 309 project.  It requires no modifications.
#
# This Makefile is used to update and fully compile a project.  It is intended
# primarily for use by project librarians to release a project to the
# projects/work directory.  However, it can be used by any project member to
# update and compile a complete project.
#
# This Makefile performs the following functions:
#
#    (1) runs cvs update
#    (2) runs the design Makefile to compile the javadoc documentation
#    (3) runs the implementation/source/java Makefile to compile the code
#    (4) runs the implementation/executables/JVM Makefile to build a
#        stand-alone executable jar file 
#
# In order for this Makefile to work properly, the three lower-level project
# Makefiles must be installed.  These are the Makefiles derived from the 309
# Makefile templates: design-Makefile, implementation-Makefile, and
# jvm-Makefile.  These template files are available on falcon at
#
#     ~gfisher/classes/309/lib/falcon-Makefiles
# 
#


#
# Update and build the complete project.
#
project:
	csh -q -c "svn update"
	csh -q -c "cd design; make"
	csh -q -c "cd implementation/source/java; make"
	csh -q -c "cd testing/implementation/source/java; make"
