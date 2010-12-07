package ClassGenerator;
use strict;
use warnings;
use 5.010;
use base qw(Class::Accessor);

#################################
# FIELDS
#################################
my @data;
my @other_data;
BEGIN
{
   @data = qw(name jfile cfile ifile lfile);
   @other_data = qw(cm dumpFile error);
}
use fields (@data, @other_data);

sub accessor_name_for { "get\u$_[1]" }
sub mutator_name_for  { "set\u$_[1]" }
ClassGenerator->mk_accessors(@data, @other_data);

################################
# IMPORTS
#################################
use ClassMaker;
use Converter;
use ConstraintChecker qw(check);
use Cwd qw(getcwd abs_path);
use Data::Dumper;
use Test::More;

###################################################################
#                             METHODS                             #
###################################################################

sub new
{
   my ($self, $args) = @_;

   if (!ref($self))
   {
      $self = fields::new($self);
   }
   for (@data)
   {
      $self->{$_} = $args->{$_} // die "Need '$_'";
   }

   #
   # Since the main class must be based off the name of the java file, I'll just
   # strip off the ".java" extension (along with the directory leading up to 
   # it) and use that as the class name (as it should be).
   #
   my ($javaClass) = ($self->{jfile} =~ /(?:.*\/)?(.*)\.java$/);

   $self->{cm} = ClassMaker->new
   (
      files     => ["$self->{jfile}"],
      mainClass => "$javaClass",
   );
   $self->{dumpFile} = abs_path("dump_$self->{name}");

   $self;
}

sub convertFilesToClass
{
   my ($self) = @_;

   Converter->new(
   {
      cfile => $self->{cfile},
      ifile => $self->{ifile},
      lfile => $self->{lfile},
      name  => "$self->{jfile}",
      dumpFile => $self->{dumpFile},
   })->convert();
}

sub genClass
{
   my ($self) = @_;

   $self->convertFilesToClass();

   $self->{error} = $@;
}

sub buildClass
{
   my ($self) = @_;

   eval 
   {
      $self->{cm}->makeClassFiles(abs_path("$self->{name}.buildClass"));
   };

   $self->{error} = $@;
}

sub runClass
{
   my ($self) = @_;

   eval
   {
      $self->{cm}->runClass(abs_path("$self->{name}.runClass"));
   };
   $self->{error} = $@;
}

1;
