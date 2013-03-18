# Community Hub

The Community Hub is a [Grails](http://grails.org "Grails") Web application that is intended to:

* Provide data products on top of sensor data
* Provide an alerting capability
* Provide sharability of discovered sensor data artifacts

## Requirements 

The following are the requirements: 

* Grails version 2.2.1
* JDK 1.6 or JDK 1.7 

## Grails plugins used

* [Resources](http://grails.org/plugin/resources "Resources")
* [Less Resources](http://grails.org/plugin/less-resources "Less Resources")
* [jQuery](http://grails.org/plugin/jquery "jQuery")
* [Twitter Bootstrap](https://github.com/groovydev/twitter-bootstrap-grails-plugin/ "Twitter Bootstrap")
* [Spring Security Core](http://grails.org/plugin/spring-security-core "Spring Security Core")
* [Quartz](http://grails.org/plugin/quartz "Quartz")
 
## Dependencies 

This software depends on the following 3rd party libraries: 

* [Proteus Common](https://github.com/intelligentautomation/proteus-common), Version 1.0, GNU Lesser General Public License (LGPL)

## Configuration 

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

## License 

This software is released under the GNU Lesser General Public License (LGPL). See the file "LICENSE" for more details. 

## Acknowledgments

This software was initially developed by [Intelligent Automation, Inc.](http://www.i-a-i.com "IAI"), under NASA funding (contract no: NNX11CA19C). 

There are icons used in this software from the
[Fugue Icon set](http://p.yusukekamiyamane.com/icons/search/fugue/) and are   (C) 2012 Yusuke Kamiyamane, used under
[Creative Commons
Attribution 3.0 License](http://creativecommons.org/licenses/by/3.0/).

