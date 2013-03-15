/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub

import grails.plugins.springsecurity.Secured

class AdminController {
	
	def jdbcTemplate

	@Secured(['ROLE_ADMIN'])
    def index() { 
		
	}
	
}
