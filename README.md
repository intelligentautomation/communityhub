# Community Hub

The Community Hub is a [Grails](http://grails.org "Grails") Web application that is intended to:

* Provide data products on top of sensor data
* Provide an alerting capability
* Provide sharability of discovered sensor data artifacts 
 
# Dependencies 

This module depends on the following 3rd party libraries: 

* [Proteus Common](https://github.com/intelligentautomation/proteus-common), Version 1.0, GNU Lesser General Public License (LGPL)

# Configuration 

In order to configure the Community Hub with a database, you address and the username and password needs to be specified. This is done in a file called "communityhub-config.groovy" which should be located on the classpath or in ${HOME}/.grails/. 

An example contents of this file could be: 

 environments {
 	development {
 		dataSource {
 			url = "jdbc:mysql://127.0.0.1/communityhub"
 			username = "USERNAME"
 			password = "PASSWORD"
 		}
 	}
 }

# License 

This software is released under the GNU Lesser General Public License (LGPL). See the file "LICENSE" for more details. 

# Acknowledgments

This software was initially developed by [Intelligent Automation, Inc.](http://www.i-a-i.com "IAI"), under NASA funding (contract no: NNX11CA19C). 
