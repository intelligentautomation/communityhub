/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub

import communityhub.security.SecUser;

class SecTagLib {
	
	def springSecurityService
	
	def ifGroupAdmin = { attrs, body -> 
		if (springSecurityService.isLoggedIn()) {
			def group = attrs.group
			def user = SecUser.get(springSecurityService.principal.id)
			// make sure we have the right type 
			if (group instanceof Group) {
				// return the body if the logged in user is the admin of the
				// group 
				if (group.admin.equals(user))
					out << body()
			}
		}
	}

	def ifNotGroupAdmin = { attrs, body ->
		if (springSecurityService.isLoggedIn()) {
			def group = attrs.group
			def user = SecUser.get(springSecurityService.principal.id)
			// make sure we have the right type
			if (group instanceof Group) {
				// return the body if the logged in user is the admin of the
				// group
				if (!group.admin.equals(user))
					// logged in, but not admin
					out << body()
			}
		} else {
			// not logged in at all
			out << body();
		}
		
	}

}
