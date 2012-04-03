from fabric.api import env, local, put, run

env.hosts = ['scheduler.csc.calpoly.edu']

def generate_build_xml():
	print('Generating build.xml...')
	local('webAppCreator -ignore edu.calpoly.csc.scheduler')

def build():
	print('Building...')
	local('ant clean')
	local('ant build')
	local('ant gwtc')

def restart_tomcat():
	print('Restarting tomcat...')
	run('sudo service tomcat6 restart')

def deploy(*directories):
	#build()
	for directory in directories:
		print('Deploying %s...' % directory)
		local('''echo "#this required property tells the scheduler where to write its database file to.
		databasefilepath=/var/lib/tomcat6/webapps/%s/DatabaseState.javaser

		#this optional property tells the scheduler to feed databasefilepath to
		# the getServletContext().getRealPath(...) function. default is true.
		#applyServletPath=true
		applyServletPath=false" > war/WEB-INF/classes/scheduler/view/web/server/scheduler.properties''' % directory)
		put('war/*', '/var/lib/tomcat6/webapps/'+directory+'/')
	restart_tomcat()

