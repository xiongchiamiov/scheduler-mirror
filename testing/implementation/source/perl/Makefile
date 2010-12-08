SHELL=/bin/bash
HOME=../../../..
TEST_CLASS_DIR=../java
CLASS_DIR=$(HOME)/implementation/source/java
TEST_DIR = t
EXEC_DIR=../../executables/JVM
PLIB=./plib
HARNESS_OUTPUT="harness_output"

blarg:
	screen -S asdf

test:
	@perl -I $(PLIB) Harness.pm $(CLASS_DIR) $(TEST_CLASS_DIR) $(EXEC_DIR) | \
		tee $(HARNESS_OUTPUT)

clean:
	@#
	@# Remove any/all compiler error output files
	@#
	@for f in $$(find ./ -name "*compile_errors*"); do \
		rm $$f; \
   done; 
	@#
	@# Remove any other temporary files generating during testing
	@#
	@for f in $(TEST_DIR)/*; do \
		if [[ ! $$f =~ \.t$$ && ! -d $$f ]]; then \
			rm $$f; \
		fi; \
	done;
	@#
	@# Remove the contents of the "executable" directory, where all built class
	@# files are stored. Only the "sql" directory should be preserved, as its
	@# necessary from test-to-test. I may choose to copy it over every time, thus
	@# make the removal of all these files a little easier. But for now, the "com"
	@# directory will have to stay put.
	@#
	@for f in $(EXEC_DIR)/*; do \
   	if [[ ! $$f =~ "com" ]]; then \
      	rm -rf $$f; \
      fi; \
	done;
	@#
	@# Remove all java TEST_ classes that were generated
	@#
	@for f in $(TEST_CLASS_DIR)/*; do \
		rm -rf $$f; \
	done; 
