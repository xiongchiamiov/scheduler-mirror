#!/usr/bin/perl -w
#
# Automatically generated from modules present in the following directories
#  - t
#  - plib
#  - plib/SchedulerMimics
#
# DON'T think about putting 'use strict' in this. You'll break everything
#
use 5.010;
use Test::More tests => 19;

my $r = 1;
$r &= use_ok("base", qw(Class::Accessor));
$r &= use_ok("TestUtils", qw(:parsers));
$r &= use_ok("SchedulerMimics::Instructor", );
$r &= use_ok("Data::Dumper", );
$r &= use_ok("SchedulerMimics::Time", );
$r &= use_ok("Proc::Queue", qw(run_back running_now waitpids));
$r &= use_ok("Test::More", );
$r &= use_ok("warnings", );
$r &= use_ok("Cwd", qw(getcwd abs_path));
$r &= use_ok("FindBin", qw($Bin));
$r &= use_ok("Linux::Cpuinfo", );
$r &= use_ok("strict", );
$r &= use_ok("ConstraintChecker", qw(check));
$r &= use_ok("base", qw (Class::Accessor));
$r &= use_ok("fields", (@data));
$r &= use_ok("fields", @data);
$r &= use_ok("base", qw(Exporter));
$r &= use_ok("SchedulerMimics::ScheduleItem", );
$r &= use_ok("Test::SharedFork", );

BAIL_OUT("Could not 'use' all required modules'") if !$r
