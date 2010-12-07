use strict;
use warnings;
use 5.010;
use lib "./plib";
use Data::Dumper;
use Converter;

my ($file) = @ARGV or die "Need a filename\n";

#say Dumper &Converter::getI_Data($file) or die $!;

say "Talk to me about how I changed things, Alex. ";
say " " x 6 . "-Eric";
