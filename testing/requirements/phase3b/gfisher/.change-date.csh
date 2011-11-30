#!/bin/tcsh
foreach f (*.me)
    emacs -batch $f -l "./.change-date.el"
end
