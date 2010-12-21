package ClassMaker;
use strict;
use warnings;
use 5.010;
use base qw (Class::Accessor);

my @data;
BEGIN 
{
   @data = qw(mainClass files) 
};
use fields(@data);
ClassMaker->mk_accessors(@data);

sub accessor_name_for { "get\u$_[1]" }
sub mutator_name_for  { "set\u$_[1]" }

use File::Path qw(make_path);
use Cwd;

#################################
# GLOBAL VARIABLES
#################################
our ($CPATHS, $DEST);

#################################

sub new 
{
   my ($class, %args) = @_;

   my $self = fields::new($class);

   for (@data)
   {
      $self->{$_} = $args{$_} // die "Need '$_'";
   }
   
   $self;
}

sub makeClassFiles
{
   my ($self, $errFile) = @_;

   make_path($DEST);

   my $cpaths = join(" ", map { "-classpath $_" } @{$CPATHS});
   
   system ("javac -g -J-Xms1024m -J-Xmx1024m $cpaths -d $DEST @{$self->{files}} ".
      "1> $errFile 2>&1");

   #
   # Die if UNIX reports an error (a non-zero return)
   #
   die "Build failed. See '$errFile' for details.\n" if ($? >> 8);
   1;
}

sub runClass
{
   my ($self, $errFile) = @_;

   my $oldDir = getcwd();
   chdir ($DEST);

   system ("java $self->{mainClass} 1> $errFile 2>&1");
   #
   # Die if UNIX reports an error (a non-zero return)
   #
   if ($? >> 8)
   {
      die "Could not run '$self->{mainClass}'. See '$errFile' for details\n";
   }

   chdir ($oldDir);

   1;
}

1;
