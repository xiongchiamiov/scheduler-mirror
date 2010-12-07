#!/usr/bin/perl
use strict;
use warnings;
use 5.010;
use Test::More;

use ConstraintChecker qw(check);
use ClassMaker;
use Cwd qw(getcwd abs_path);
use Data::Dumper;
use FindBin qw($Bin);
use RandClassGenerator;

#################################
# SET DIRECTORY TO THIS ONE
#################################
my ($oldDir) = getcwd();
chdir ($Bin);

#################################
my ($cpath, $test_classDir, $dest) = @ARGV;

$ClassMaker::CPATHS = [$cpath];
$ClassMaker::DEST   = $dest;

my $javaFile = "$test_classDir/TEST_RandPoppedScheduler.java";
my @files = qw(cInfo iInfo lInfo);

SKIP:
{
   my $rtg = RandClassGenerator->new(
   {
      name => "randTest",
      jfile => $javaFile,
      cs   => 100,
      is   => 1000,
      ls   => 1000,
   });

   #
   # If the build failed, we can't check the output it should have dumped, as 
   # it won't be there (or -shouldn't- be)
   #
   eval 
   {
      $rtg->genClass(42);
      $rtg->runClass();
   };
   if (!ok(!$rtg->getError(), 
      "Built java class file and ran it w/ random preferences"))
   {
      skip ("Some error in test data generation. Error is: '".$rtg->getError().
         "'", 1);
   }

   #
   # If the build was good, we can go ahead and run our test(s) on the random
   # data we generated and dumped
   #
   ok(check($rtg->getDumpFile()), "Verifying constraints against schedule data");
}
done_testing();

chdir ($oldDir);
