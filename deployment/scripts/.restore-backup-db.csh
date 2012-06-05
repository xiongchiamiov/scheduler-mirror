#!/bin/tcsh
#
# Restore a backed up copy of DatabaseState.javaser.  If $1 is non-null, then
# it's used as the source backup file.  If $1 is null, then the default backup
# file is assumed to be ./DatabaseState-bkp.javaser.  If the specified or
# default backup file does not exist, the script exits with no action.
#

#
# Stop tomcat.  This is necessary to ensure that any active servlet copy of the
# database is forced out of servlet memory.
#
sudo service tomcat6 stop


#
# Do the copying.  Do so as the tomcat6 user so that the file is owned by
# tomcat6.
#
if ($1 != "") then
    # Copy from $1
    if (-e $f) then
        sudo -u tomcat6 cp $1 DatabaseState.javaser
    endif
else
    # Copy from the default backup file
    if (-e DatabaseState-bkp.javaser) then
        sudo -u tomcat6 cp DatabaseState-bkp.javaser DatabaseState.javaser
    endif
endif

#
# Change the permissions to ug+rw, o+r
#
sudo -u tomcat6 chmod 664 DatabaseState.javaser

#
# Restart tomcat
#
sudo service tomcat6 stop
