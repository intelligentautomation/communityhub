/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub

import grails.plugins.springsecurity.Secured

import com.iai.communityhub.dao.ServiceDao
import com.iai.communityhub.model.Service
import com.iai.communityhub.paging.Paginator

class AdminController {
	
	def jdbcTemplate

	@Secured(['ROLE_ADMIN'])
    def index() { 
		
	}
	
}
