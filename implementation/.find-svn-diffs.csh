#!/bin/tcsh

foreach f (`find . -name '*.java'`)
    svn log -r \{2012-04-02\}:\{"2012-04-09 23:59"\} $f > .svn-log.out
    diff .svn-log-separator-line .svn-log.out > .separator-line.diff
    if (! -z .separator-line.diff) then
	echo $f
    endif
end
