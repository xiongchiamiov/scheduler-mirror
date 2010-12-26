#!/usr/bin/perl -w
# POD ==>
=pod

=head1 SYNOPSIS

 ./perl buildLibsTest.pl plib t 

=head1 DESCRIPTION

This program generates a test under the "t" directory to test that all the 
modules used by the modules in this test suite are available on the current
system.

Goes through the directories specified on the command line, searching for 
*.pm and *.t files. Those which match are parsed for their "use" statments.
These statements are organized into "use_ok" tests from the Test::More module. 
These tests are printed into a file "t/00_libsTest.t" relative to the current 
directory.

Directory searches are B<not> recursive.

=head1 KNOWN ISSUES

This program wasn't written with maintainability in mind, and it wasn't 
thoroughly tested. But, there're a lot of inline comments for the reader to 
enjoy, so it's not a complete loss.

=head1 AUTHOR

Eric Liebowitz E<lt>leboyX@gmail.comE<gt>

=cut
#<== END POD
use strict;
use warnings;
use Data::Dumper;
#################################
# CONSTANTS
#################################
my $TEST_FILE = "t/00_libsTest.t";

#################################

#
# Open file now so that old content won't affect file scans
#
open (my $fh, ">", $TEST_FILE) or die $!;

#
# All *.pm and *.t files in the directories provided on the cmd line
#
my %uses;
for my $f (map { glob("$_/*.{pm,t}") } @ARGV)
{
   open (my $file, $f) or die $!;
   my $line = join("", <$file>);

   #
   # Remove all comments and POD documentation
   #
   $line =~ s/#.*//g;
   $line =~ s/__END__.*?//sg;
   $line =~ s/=pod.*?=cut//sg;

   #
   # Delimit on ";"
   #
   my @lines = split(/;/, $line);
   #
   # Find our "use [...]" statements". Note that I omit any "use #" statements.
   # The later "use_ok" can't do things like "use 5.010". I'll simply tack that
   # onto the beginning of the file we build.
   #
   for my $use (map { s/.*(use.*$)/$1/sg; $_ } grep { /use [^\d]/ } @lines)
   {
      #
      # Keep only uniq 'use' statements
      #
      $uses{$use} ++;
   }
}

#
# Meta-program the header for our test file
#
print $fh "#!/usr/bin/perl -w\n";
print $fh "#\n";
print $fh "# Automatically generated from modules present in the following ".
   "directories\n";
printf $fh "#  - $_\n" for @ARGV;
print $fh "#\n# DON'T think about putting 'use strict' in this. You'll break ".
   "everything\n#\n";
print $fh "use 5.010;\n";
#
# Use Test::More, and use our own knowledge to tell it how many tests will
# be run
#
print $fh "use Test::More tests => ".scalar keys (%uses).";\n\n";

#
# Separate the module from any arguments which followed it
#
print $fh "my \$r = 1;\n";
for (keys %uses)
{
   my ($use, $module, $line) = split(/ /, $_, 3);

   #
   # All I care about for testing purposes is that the args I request for export
   # are available (I always use 'qw' for imports things)
   #
   $line =~ s/.*(qw\(.*?\)).*/$1/s if $line;
   $line = "" unless $line;
   
   #
   # If '$line' is empty, a dangling ',' won't bother Perl
   #
   print $fh "eval { \$r &= use_ok(\"$module\", $line); };\n";
}

print $fh "\nBAIL_OUT(\"Could not 'use' all required modules'\") if !\$r;";
