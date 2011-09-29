FLAGS="-Xlint:deprecation -Xlint:unchecked"

CPATH="./"

SRC="./"
FILES=`find $(SRC) -name "*.java" -type f`

# Directory for generated executable files
CLASS_DST =   ../../executables/JVM

MAIN_CLASS="edu/calpoly/csc/scheduler/Scheduler"

all:
	@echo "Compiling..."
	@javac   -d $(CLASS_DST) \
				-sourcepath $(SRC) \
				-cp $(CPATH) \
				$(FLAGS) \
				$(FILES)
