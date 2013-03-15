/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub

import grails.converters.JSON
import grails.plugins.springsecurity.Secured

/**
 * Controller for viewing and managing services 
 * 
 * @author Jakob Henriksson 
 *
 */
class ServiceController {

	static allowedMethods = [ 
		index: 'GET', 
		list: 'GET', 
		create: 'GET', 
		view: 'GET', 
		save: 'POST', 
		delete: 'POST',
		boundingboxes: 'GET' 
	]
	
	def serviceService

	/**
	 * Index 
	 * 
	 * @return
	 */
	@Secured(['ROLE_ADMIN'])
	def index() {
		redirect(action: 'list', params: params)
	}
	
	/**
	 * List all services 
	 * 
	 * @return
	 */
	@Secured(['ROLE_ADMIN'])
	def list() {
		// find all active services 
		def services = Service.activeServices.list(params)
		[ services : services ]
	}
	
	/**
	 * Displays page to create a new service 
	 *
	 * @return
	 */
	@Secured(['ROLE_ADMIN'])
	def create() {
		
	}
	
	/**
	 * Saves a new service 
	 *
	 * @return
	 */
	@Secured(['ROLE_ADMIN'])
	def save() {
		
		// get the parameters
		def url = params.inputUrl
		def type = params.inputServiceType
		
		def errorCode = null

		// check for errors 
		if (url == null)
			errorCode = "default.communityhub.error.unknown"
		// check for empty end-point  
		else if (url.trim().equals(""))
			errorCode = "default.communityhub.service.error.emptyurl"
		// check if the service already exists
		else if (Service.findAllWhere(endpoint: url, active: true).size() > 0)
			errorCode = "default.communityhub.service.error.exists"

		// return with error if needed 
		if (errorCode != null) {
			// set message
			flash.error = errorCode
			// re-direct
			redirect(action: 'create', params: [url : url, type: type])
			return
		}

		// try to add service 		
		Service service = serviceService.addService(type, url)
		if (service) {
			// set message
			flash.message = "default.communityhub.service.success.added"
			// re-direct 
			redirect(action: 'view', id: service.id)
			return
		}
		
		// default
		errorCode = "default.communityhub.error.unknown"
		redirect(action: 'create', params: 
				[url : url, type: type, error: errorCode])
	}
	
	/**
	 * Show details of a service 
	 * 
	 * @param id
	 * @return
	 */
	@Secured(['ROLE_ADMIN'])
	def view(int id) {
		
		if (id > 0) {
			
			// find and return service 
			def service = Service.get(id)
			if (service)
				return [service : service]
		}

		// error 		
		response.sendError(404)
	}	
	
	/**
	 * Removes a service
	 *
	 * @param id service ID
	 * @return
	 */
	@Secured(['ROLE_ADMIN'])
	def delete(int id) {
		
		if (id > 0) {

			def service = Service.get(id)
			if (service) {
				// update service
				service.active = false
				service.save()
				// set message
				flash.message = "default.communityhub.service.success.deleted"
				// re-direct
				redirect(action: 'list')
				return
			}
		}
		
		// error
		response.sendError(404) 
	}
	
	/**
	 * Handles fetching of bounding boxes of offerings in a given service. 
	 * 
	 * Returns JSON
	 * 
	 *
	 * @return
	 */
	def boundingboxes(int id) {

		if (id > 0) {
			
			Map<String, Object> locations = 
				serviceService.findBoundingBoxesForService(id)
				
			if (locations != null) {
				
				def json = [ 
					"status" : "OK",
					"message" : "Success", 
					"data" : locations
				]

				render json as JSON
				return
			} 					
		}
		
		// error 
		([ status : "KO" ] as JSON).render response		
	}	
	
}
