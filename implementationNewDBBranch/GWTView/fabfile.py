from fabric.api import env, local, put, run

env.hosts = ['matt.schirle@scheduler.csc.calpoly.edu']

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
		put('war/*', '/var/lib/tomcat6/webapps/'+directory+'/')
	restart_tomcat()

