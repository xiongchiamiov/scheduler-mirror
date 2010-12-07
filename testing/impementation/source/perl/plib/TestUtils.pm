package TestUtils;
use strict;
use warnings;
use 5.010;
use base qw(Exporter);

use Converter;
use JarMaker;

#################################
# EXPORTS
#################################
our @EXPORT = ();
our @EXPORT_OK = qw(genBuildRunClass);

###################################################################
#                           FUNCTIONS                             #
###################################################################

sub genBuildRunClass
{
   my %args = @_;

   my $converter = Converter->new($args{converterArgs});
   $converter->convert();

   my $jm = JarMaker->new
   (
      cpaths         => $args{cpaths},
      dest           => $args{dest},
      files          => $args{filesToBuild},
      jarName        => $args{jarName},
      mainClass      => $args{mainClass},
   );

   $jm->makeClassFiles("$args{errFile}.buildClass");
   $jm->makeJar("$args{errFile}.buildJar");
   $jm->runJar("$args{errFile}.runJar");
}
