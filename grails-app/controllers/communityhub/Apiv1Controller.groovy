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
		alerts: 'GET', 
	]
	
	def version = "1.0"

	/**
	 * Displays documentation 
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
	 * Returns the public community groups as JSON 
	 * 
	 * @return
	 */
	def groups() {
		
		// get groups 
		def groups = Group.activeGroups.list()
		
		// create JSON structure for groups 
		def jsonGroups = groups.collect {
			[ id : it.id, name : it.name, description : it.description, 
			  dateCreated : it.dateCreated, lastUpdated : it.lastUpdated ]
		}
		
		// create complete response structure 
		def json = [ "status" : "OK", "version" : version, 
			"groups": jsonGroups ]
		
		// return with the response 
		render json as JSON 
	}
	
	/**
	 * Returns a list of alerts for a given community group (via its id)
	 * 
	 * @param id the group id 
	 * @return
	 */
	def alerts(int id) {
		if (id > 0) {
			def group = Group.get(id)
			if (group) {
				
				// get alerts for the group  
				def alerts = Alert.createCriteria().list(params) {
					groups {
						eq('id', group.id)
					}
				}
				
				// create JSON structure for alerts
				def jsonAlerts = alerts.collect {
					[ id : it.id, type : it.type, detail : it.detail, 
					  dateCreated : it.dateCreated, lastUpdated : it.lastUpdated, 
					  validFrom : it.validFrom, validTo : it.validTo,
					  latLower : it.latLower, latUpper : it.latUpper, 
					  lonLower : it.lonLower, lonUpper : it.lonUpper,
					  serviceEndpoint : it.service.endpoint, 
					  sensorOfferingId : it.offering, 
					  observedProperty : it.observedProperty ]
				}
				
				// create complete response structure 
				def json = [ "status" : "OK", "version" : version, 
					"alerts": jsonAlerts ]
				
				// return with the response 
				render json as JSON
				return				
			}

			// return with an error			
			([ status : "KO", error: "A group with the id " + id + " cannot be found" ] as JSON).render response
			return
		}
		
		// return with an error 
		([ status : "KO", error: "The group id has to be a positive integer" ] as JSON).render response		
	} 
}
