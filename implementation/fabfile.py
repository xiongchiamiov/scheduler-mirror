from fabric.api import env, local, put, run
from fabric.contrib.project import rsync_project as rsync

env.hosts = ['scheduler.csc.calpoly.edu']

def generate_build_xml():
	print('Generating build.xml...')
	local('webAppCreator -ignore edu.calpoly.csc.scheduler')

def build():
	print('Building...')
	local('ant clean')
	local('ant build')
	local('ant gwtc')

def test():
	print('Running JUnit tests...')
	local('ant test')

def restart_tomcat():
	print('Restarting tomcat...')
	run('sudo service tomcat6 restart')

def deploy(*directories):
	#build()
	if len(directories) == 1 and directories[0] == 'all':
		directories = ['AERO','BUS','CHEM','CM','CSC','EE','ENGL','FSN','GRC','IME','MU','RPTA']
	
	for directory in directories:
		print('Deploying %s...' % directory)
		local('''echo "#this required property tells the scheduler where to write its database file to.
		databasefilepath=/var/lib/tomcat6/webapps/%s/DatabaseState.javaser

		#this optional property tells the scheduler to feed databasefilepath to
		# the getServletContext().getRealPath(...) function. default is true.
		#applyServletPath=true
		applyServletPath=false" > war/WEB-INF/classes/scheduler/view/web/server/scheduler.properties''' % directory)
		rsync(local_dir='war/*', \
		      remote_dir='/var/lib/tomcat6/webapps/'+directory+'/', \
		      exclude='.svn', \
		      delete=True, \
		      extra_opts='--omit-dir-times --no-perms')
	restart_tomcat()

