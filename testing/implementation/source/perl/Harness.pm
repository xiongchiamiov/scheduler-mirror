package Harness;
use strict;
use warnings;
use 5.010;
use base qw(TAP::Harness);
use lib "./plib";

use Data::Dumper;
use Cwd qw(abs_path);
use File::Find qw(find);

#################################
# RECEIVING ARGS FROM COMMAND LINE
#################################
my ($cpath, $test_classDir, $dest) = map { abs_path($_) } @ARGV;

###################################################################
#                            TESTING                              #
###################################################################

#
# Note that this harness assumes the Scheduler's class files have already been
# built and put into the appropriate directory. This is the Makefile's 
# reponsibility. 
#
my $harness = Harness->new(
{
   lib => [qw(./ ./plib)],          # Libraries tests will have in @INC
   color => 1,                      # Color output
   #failures => 1,                   # Only print failed tests
   verbosity => 1,                  # Print everything
   #
   # Pass 1 arg(s) to each test via @ARGV: 
   #  - Absolute path to the top-level directory where java class files are
   #
   test_args => [$dest],
});

#
# When ready for heavy testing, just do a "glob(t/*.t)"
#
$harness->runtests(glob("t/99*.t"));
