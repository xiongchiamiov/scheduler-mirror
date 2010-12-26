#!/usr/bin/perl
use strict;
use warnings;
use 5.010;
use Test::More;       # How we'll run tests
use Test::SharedFork; # Lets fork'd tests cooperate

#################################
# CONSTANTS
#################################
our $C_LIMIT = 2;#100;
our $C_DIV   = 1;#10;
our $I_LIMIT = 2;#1000;
our $I_DIV   = 1;#100;
our $L_LIMIT = 2;#1000;
our $L_DIV   = 1;#100;

#################################
# IMPORTS
#################################
#
# Before we begin testing, get the number of CPUs we have to work with
#
my $cpu;
BEGIN
{
   use Linux::Cpuinfo;
   $cpu = Linux::Cpuinfo->new();
}
use Proc::Queue qw(run_back running_now waitpids), 
                size => $cpu->num_cpus();

use ConstraintChecker qw(check);
use Cwd qw(getcwd abs_path);
use Data::Dumper;
use FindBin qw($Bin);

#################################
# SET DIRECTORY TO THIS ONE
#
# (Where this file is actually located)
#################################
my ($oldDir) = getcwd();
chdir ($Bin);

#################################
# GLOBALS
#################################
#
# Receive our cmd line args
#
my ($test_class_dir) = @ARGV;

###################################################################
#                             TESTING                             #
###################################################################

diag (); # Give us a clean line to start on

#
# Run the test class with different numerical arguments
#
&verifyOutput(&runClass
(
   c_count => [grep { ($_ % $C_DIV) == 0 } 1..$C_LIMIT],
   i_count => [grep { ($_ % $I_DIV) == 0 } 1..$I_LIMIT],
   l_count => [grep { ($_ % $L_DIV) == 0 } 1..$L_LIMIT],
));

done_testing();

chdir ($oldDir);

###################################################################
#                           FUNCTIONS                             #
###################################################################

#
# Generates all the java files. We'll fork off children to do the work in 
# parallel. Since this is simply the generation step, the work will be nearly
# instantaneous ('cause Perl's fast). But, in keeping with the other portion of
# this test which uses children, I figured spawning processes couldn't hurt us.
#
# $_[0] = Hash of values to limit how many RCG's are created:
#
#   c_limit => # of courses
#   i_limit => # of instructors
#   l_limit => # of locations
#
# Returns a reference to a list of RCGs
#
# runClasses ==>
sub runClass
{
   my (%args) = @_;
   my (@r);

   for my $cs (@{$args{c_count}})
   {
      for my $is (@{$args{i_count}})
      {
         for my $ls (@{$args{l_count}})
         {
            my $name = "test_c${cs}_i${is}_l${ls}";
            my $file  = abs_path("$name");

            run_back 
            {
               diag ("Running '$name'");
               system ("java -cp $test_class_dir Test_RandData ".
                  "$file $cs $is $ls 1> $file.out 2> $file.err");
            };
            push (@r, $file);
         }
      }
   }

   #
   # Knock-off any/all straggling children
   #
   waitpid (-1, 0) while (running_now());

   return \@r;
}#<==

#
# Verifies the output of every class, making sure generated schedules comply 
# to scheduling constraints. 
#
# $_[0] = Hash w/ PID-RCG key-value pairs
# $_[1] = Hash of failed RCG's, w/ the RCG's as its values and the corresponding
#         PID of the failed process as their keys
#
# verifyOutput ==>
#
sub verifyOutput
{
   my ($files) = @_;

   for my $file (@{$files})
   {
      if (-e $file)
      {
         run_back
         {
            diag ("Checking file '$file'");
            ok(check($file), "$file passed");
         }
      }
      else
      {
         fail ("$file failed");
      }
   }
   waitpid(-1, 0) while (running_now());
}#<==
