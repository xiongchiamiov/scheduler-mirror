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

use_ok("base", qw(Class::Accessor));
use_ok("TestUtils", qw(:parsers));
use_ok("SchedulerMimics::Instructor", );
use_ok("Data::Dumper", );
use_ok("SchedulerMimics::Time", );
use_ok("Proc::Queue", qw(run_back running_now waitpids));
use_ok("Test::More", );
use_ok("warnings", );
use_ok("Cwd", qw(getcwd abs_path));
use_ok("FindBin", qw($Bin));
use_ok("Linux::Cpuinfo", );
use_ok("strict", );
use_ok("ConstraintChecker", qw(check));
use_ok("base", qw (Class::Accessor));
use_ok("fields", (@data));
use_ok("fields", @data);
use_ok("base", qw(Exporter));
use_ok("SchedulerMimics::ScheduleItem", );
use_ok("Test::SharedFork", );
