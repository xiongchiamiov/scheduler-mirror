#!/bin/tcsh

foreach f (*-test.me)
    echo "Making $f:r"
    me2html $f >& /dev/null
end

cd ..
rsse gfisher thyme2:work/scheduler-vm/testing/requirements/phase3b
