package Time;
use strict;
use warnings;
use 5.010;
use base qw(Class::Accessor);

#################################
# IMPORTS
#################################
use Data::Dumper;

#################################
# FIELDS
#################################
my @data;
BEGIN { @data = qw(h m) }
use fields (@data);

Time->mk_accessors(@data);
sub accessor_name_for { "get\u$_[1]"; }
sub mutator_name_for  { "set\u$_[1]"; }

###################################################################
#                          METHODS                                #
###################################################################

sub new
{
   my ($class, $time) = @_;

   my $self = fields::new($class);

   ($self->{h}, $self->{m}) = split(/:/, $time);

   $self;
}

sub getId
{
   my ($self) = @_;
   "$self->{h}:$self->{m}";
}

sub addHalf
{
   my ($self) = @_;

   #
   # HACK: Need to keep the "0" tagged onto the hour. Since Perl will treat 
   #       strings and numbers the same, we can use sprintf to pad a number
   #       with that 0 when necessary. 
   #
   $self->{h} = sprintf("%02s", ++$self->{h}) if $self->{m} > 29;
   $self->{m} = ($self->{m} > 29) ? "00" : "30";
}

sub equals
{
   my ($self, $t) = @_;

   return (($self->{h} == $t->{h}) and ($self->{m} == $t->{m}));
}

1;
