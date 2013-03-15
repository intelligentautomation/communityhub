/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub

import grails.converters.JSON


/**
 * Controller for version 1 of the Community Hub API 
 * 
 * @author Jakob Henriksson
 *
 */
class Apiv1Controller {
	
	static allowedMethods = [
		index: 'GET', 
		ping: 'GET', 
		groups: 'GET',
	]
	
	def version = "1.0"

	/**
	 * For documentation 
	 * 
	 * @return
	 */
    def index() { 
		
	}
	
	/**
	 * Returns a status message with supported API versions as JSON 
	 * 
	 * @return
	 */
	def ping() {
		def json = [ 
			"status" : "OK", 
			"versions" : [ version ] 
		]
		render json as JSON
	}
	
	/**
	 * Returns the defined community groups as JSON 
	 * 
	 * @return
	 */
	def groups() {
		
		def jsonGroups = Group.activeGroups.list().collect {
			[ id : it.id, name : it.name, description : it.description, 
			  dateCreated : it.dateCreated, lastUpdated : it.lastUpdated ]
		}
		
		def json = [ "status" : "OK", "version" : version, 
			"response": jsonGroups ]
		
		render json as JSON 
	}
}
