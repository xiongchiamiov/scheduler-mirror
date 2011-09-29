CPATH="./"
SRC="./"
CLASS_DST =   ../../executables/JVM
MAIN_CLASS="edu/calpoly/csc/scheduler/Scheduler"

FILES=`find $(SRC) -name "*.java" -type f`
FLAGS="-Xlint:deprecation -Xlint:unchecked"

all:
	@echo "Compiling..."
	@javac   -d $(CLASS_DST) \
				-sourcepath $(SRC) \
				-cp $(CPATH) \
				$(FLAGS) \
				$(FILES)
