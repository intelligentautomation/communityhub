/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub

import com.iai.communityhub.model.Group
import communityhub.security.SecUser;

class SecTagLib {
	
	def springSecurityService
	
	def isGroupAdmin = { attrs, body -> 
		if (springSecurityService.isLoggedIn()) {
			def group = attrs.group;
			def user = SecUser.get(springSecurityService.principal.id)
			// make sure we have the right type 
			if (group instanceof Group) {
				Group g = (Group)group;
				// return the body if the logged in user is the admin of the
				// group 
				if (g.getAdmin().equals(user.username))
					out << body();
			}
		}
	}

	def isNotGroupAdmin = { attrs, body ->
		if (springSecurityService.isLoggedIn()) {
			def group = attrs.group;
			def user = SecUser.get(springSecurityService.principal.id)
			// make sure we have the right type
			if (group instanceof Group) {
				Group g = (Group)group;
				// return the body if the logged in user is the admin of the
				// group
				if (!g.getAdmin().equals(user.username))
					// logged in, but not admin
					out << body();
			}
		} else {
			// not logged in at all
			out << body();
		}
		
	}

}
