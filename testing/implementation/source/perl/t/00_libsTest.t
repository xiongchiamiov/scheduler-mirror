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
use Test::More tests => 20;

my $r = 1;
eval { $r &= use_ok("base", qw(Class::Accessor)); };
eval { $r &= use_ok("TestUtils", qw(:parsers)); };
eval { $r &= use_ok("SchedulerMimics::Instructor", ); };
eval { $r &= use_ok("Data::Dumper", ); };
eval { $r &= use_ok("SchedulerMimics::Time", ); };
eval { $r &= use_ok("Proc::Queue", qw(run_back running_now waitpids)); };
eval { $r &= use_ok("Test::More", ); };
eval { $r &= use_ok("warnings", ); };
eval { $r &= use_ok("Cwd", qw(getcwd abs_path)); };
eval { $r &= use_ok("FindBin", qw($Bin)); };
eval { $r &= use_ok("Linux::Cpuinfo", ); };
eval { $r &= use_ok("Thread::Pool", ); };
eval { $r &= use_ok("strict", ); };
eval { $r &= use_ok("ConstraintChecker", qw(check)); };
eval { $r &= use_ok("threads", ); };
eval { $r &= use_ok("base", qw (Class::Accessor)); };
eval { $r &= use_ok("fields", (@data)); };
eval { $r &= use_ok("fields", @data); };
eval { $r &= use_ok("base", qw(Exporter)); };
eval { $r &= use_ok("SchedulerMimics::ScheduleItem", ); };

BAIL_OUT("Could not 'use' all required modules'") if !$r;