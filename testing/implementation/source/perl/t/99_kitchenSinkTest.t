#!/usr/bin/perl
use strict;
use warnings;
use 5.010;
use threads;
use Test::More;       # How we'll run tests

#################################
# CONSTANTS
#################################
our $C_LIMIT = 2;
our $C_DIV   = 1;
our $I_LIMIT = 2;
our $I_DIV   = 1;
our $L_LIMIT = 2;
our $L_DIV   = 1;

#################################
# IMPORTS
#################################
#
# Before we begin testing, get the number of CPUs we have to work with
#
my $CPUS;
BEGIN
{
   use Linux::Cpuinfo;
   $CPUS = Linux::Cpuinfo->new()->num_cpus();
}

use ConstraintChecker qw(check);
use Cwd qw(getcwd abs_path);
use Data::Dumper;
use FindBin qw($Bin);
use Thread::Pool;       # Makes it easy to multi-thread execution

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

###################################################################
#                           FUNCTIONS                             #
###################################################################

#
# Generates all the java files. We'll make threads to do the work in 
# parallel. Since this is simply the generation step, the work will be nearly
#
# $_[0] = Hash of values to limit how courses, instructors, and locations are
#         used
#
# Returns a reference to a list of test names (which are also the names of the
# files to which schedule data is dumped in this directory).
#
# runClasses ==>
sub runClass
{
   my (%args) = @_;
   my (@r);

   #
   # Workers will operate based on how many courses, instructors, and locations
   # you wish them to generate.
   #
   my $pool = Thread::Pool->new(
   {
      optimize => "cpu",
      do => sub
      {
         my ($cs, $is, $ls, $name) = @_;
         my $file  = abs_path("$name");

         diag ("Running '$name'");
         system ("java -cp $test_class_dir Test_RandData ".
            "$file $cs $is $ls 1> $file.out 2> $file.err");
      },
      workers => $CPUS,
      minjobs => $CPUS, # Always have jobs available for workers
   });


   for my $cs (@{$args{c_count}})
   {
      for my $is (@{$args{i_count}})
      {
         for my $ls (@{$args{l_count}})
         {
            my $name = "test_c${cs}_i${is}_l${ls}";
            $pool->job($cs, $is, $ls, $name);
            push (@r, $name);
         }
      }
   }

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

   my $pool = Thread::Pool->new(
   {
      optimize => "cpu",
      do => sub
      {
         diag ("Checking '$_[0]'");
         ok(check($_[0]), "$_[0] passed");
      },
      workers => $CPUS,
      minjobs => $CPUS, # Always have jobs for our workers
   });

   for my $file (@{$files})
   {
      #
      # If the dumped file does not exist, something bad happened
      #
      if (-e $file)
      {
         $pool->job($file);
      }
      else
      {
         fail ("$file failed");
      }
   }
}#<==

