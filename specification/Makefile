#
# This is here for the sole purpose of making SpecL compilation easier. Feel 
# free to add your own tags to the file for your own use. 
#
# Reason this is here: in order to get your "export" and "import" operations
# to work, you need to compile all files involved at the same time on the 
# command line. Thus, the "database.sl" file needs to be compiled with all the
# other "DB" files and the "preference" file for everything to stitch together
# nicely
#
# Cheers,
# Eric
#

SPECL=~eliebowi/bin/specl
DB=database.sl instructorDB.sl courseDB.sl locationDB.sl preference.sl

all: generate database project

project:
	$(SPECL) scheduleProject.sl generate.sl $(DB)

generate:
	$(SPECL) generate.sl $(DB)

database:
	$(SPECL) $(DB)
