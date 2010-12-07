package RandClassGenerator;
use strict;
use warnings;
use 5.010;
use base qw(ClassGenerator);

#################################
# FIELDS
#################################
my @data;
my @constructorData;
BEGIN
{
   @data = qw(is cs ls);
   @constructorData = (@data, "name", "jfile");
}
use fields (@data);
RandClassGenerator->mk_accessors(@data);

################################
# IMPORTS
#################################
use RandDataGenerator;
use Data::Dumper;

###################################################################
#                             METHODS                             #
###################################################################

sub new
{
   my ($class, $args) = @_;

   my $self = fields::new($class);
   for (@constructorData)
   {
      $self->{$_} = $args->{$_} // die "Need '$_'";
   }

   ($args->{cfile}, $args->{ifile}, $args->{lfile}) = 
      map { "$args->{name}_$_" } qw(cInfo iInfo lInfo);

   $self->SUPER::new($args);
}

sub genClass
{
   my ($self, $seed) = @_;
   srand($seed);

   my $rdg = RandDataGenerator->new
   (
      courses     => $self->{cs},
      instructors => $self->{is},
      locations   => $self->{ls},
   );
   $rdg->makeData($self->getCfile(), $self->getIfile(), $self->getLfile());

   $self->SUPER::genClass();
}

1;
