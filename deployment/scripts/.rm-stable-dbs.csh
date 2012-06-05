#!/bin/tcsh
#
# Remove the DatabaseState.javaser files from all of the 'x' deployments.
#

foreach f (AERO BUS CHEM CM CSC EE ENGL FSN GRC IME MU PHYS RPTA)
    cd $f
    sudo rm -f DatabaseState.javaser
    cd ..
end
