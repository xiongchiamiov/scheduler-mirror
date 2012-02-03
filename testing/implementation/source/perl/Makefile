SHELL=/bin/bash
HOME=../../../..
TEST_CLASS_DIR=../java
CLASS_DIR=$(HOME)/implementation/source/java
TEST_DIR = t
EXEC_DIR=../../executables/JVM
PLIB=./plib
HARNESS_OUTPUT="harness.out"

#
# Directories to search for modules. Those found will have their "use" 
# statements compiled into the 00_libsTest.t file to make sure that a given
# system provides the modules necessary for testing
#
TEST_ZERO_MODULE_DIRS = t \
								plib \
								plib/SchedulerMimics 

#
# Finds all the java files in the scheduler packaged directory (ignoring svn 
# directories)
#
BASE_FILES=`find $(CLASS_DIR) -path "*svn*" -prune -o -name "*java" -path "*scheduler/*" -print`

#
# Compiles all base files (including the test classes)
#
base:	
	javac -classpath $(CLASS_DIR) -d $(EXEC_DIR) $(BASE_FILES) 1> base.out 2>&1
	javac -classpath $(CLASS_DIR) -d $(EXEC_DIR) $(TEST_CLASS_DIR)/*.java 1> test.out 2>&1

libs_test:
	perl buildLibsTest.pl $(TEST_ZERO_MODULE_DIRS)

test:	base 
	perl -I $(PLIB) Harness.pm $(CLASS_DIR) $(TEST_CLASS_DIR) $(EXEC_DIR) 2>&1 | \
		tee $(HARNESS_OUTPUT)

background_test:	
	@screen -S scheduler_test -d -m make test

clean:
	@#
	@# Remove any/all compiler and other output files
	@#
	@find ./ -name "*.out" -delete -o -name "*.err" -delete -o -name "test_*" -delete
	@#
	@# Remove the contents of the "executable" directory, where all built class
	@# files are stored. Only the sql directory should be preserved, as its
	@# necessary from test-to-test. I may choose to copy it over every time, thus
	@# make the removal of all these files a little easier. But for now, the "com"
	@# directory will have to stay put.
	@#
	@for f in $(EXEC_DIR)/*; do \
   	if [[ ! $$f =~ "com" ]]; then \
      	rm -rf $$f; \
      fi; \
	done;
