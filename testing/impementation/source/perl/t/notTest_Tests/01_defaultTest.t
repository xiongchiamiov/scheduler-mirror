use strict;
use warnings;
use 5.010;

use ConstraintChecker qw(check);
use Cwd qw(getcwd abs_path);
use Data::Dumper;
use SpawningClassGenerator;
use Test::More;

#################################
# GO TO THIS FILE'S DIR FOR EASIER PATHS
#################################
use FindBin qw($Bin);
my ($oldDir) = getcwd();
chdir ($Bin);

#################################
# CONSTANTS
#################################
my $CFILE = "commonTestData/cInfo";
my $IFILE = "commonTestData/default_iInfo";
my $LFILE = "commonTestData/lInfo";

###################################################################
#                            TEST CODE                            #
###################################################################
my ($cpath, $test_classDir, $dest) = @ARGV;
$ClassMaker::CPATHS = [$cpath];
$ClassMaker::DEST   = $dest;

my $className = "TEST_DefaultPrefSchedule";
my $javaFile = "$test_classDir/$className.java";
my $dumpFile = abs_path("dump_$className");

#
# If the steps taken to generate our Java file fail, we'll have to skip the
# schedule's verification
#
SKIP:
{
   my $cg = SpawningClassGenerator->new(
   {
      name => "$className",
      jfile => $javaFile,
      cfile => $CFILE,
      ifile => $IFILE,
      lfile => $LFILE,
   });

   eval { $cg->genClass(); };
   if (!ok(!$@, "Built java class file '$javaFile'"))
   {
      skip ("Build error: $@", 2);
   }

   eval { $cg->runClass(); };
   if (!ok(!$@, "Ran class file '$className'"))
   {
      skip ("Run error: $@", 1);
   }

   pass("Finished test");
}

done_testing ();

chdir ($oldDir);


