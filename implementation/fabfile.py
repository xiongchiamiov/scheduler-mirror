from fabric.api import env, local, put, run
from fabric.contrib.project import rsync_project as rsync

env.hosts = ['scheduler.csc.calpoly.edu']
departments = ['AERO','BUS','CHEM','CM','CSC','EE','ENGL','FSN','GRC','IME','MU','RPTA']

def generate_build_xml():
	print('Generating build.xml...')
	local('webAppCreator -ignore edu.calpoly.csc.scheduler')

def build():
	print('Building...')
	local('ant clean')
	local('ant build')
	local('ant gwtc')

def test(domain='localhost:8080'):
	print('Running JUnit tests...')
	local('ant build && ant test')
	print('Running Selenium tests...')
	local('echo "domain=http://%s" > ../Selenium/test/scheduler/view/web/shared/selenium.properties' % domain)
	local('cd ../Selenium && ant build && ant test')

def restart_tomcat():
	print('Restarting tomcat...')
	run('sudo service tomcat6 restart')

def deploy(*directories):
	#build()
	if len(directories) == 1:
		if directories[0] == 'all':
			# We need a copy since we're modifying the list.
			directories = list(departments)
			directories.extend([department+'x' for department in departments])
		elif directories[0] == 'all-dev':
			directories = [department+'x' for department in departments]
		elif directories[0] == 'all-stable':
			directories = departments
	
	local('mkdir -p test/scheduler/view/web/share')
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
		      extra_opts='--omit-dir-times --no-perms')
	restart_tomcat()

def nuke_databases():
	print('Deleting serialized database files...')
	run('rm /var/lib/tomcat6/webapps/**/*.javaser')

