/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub

import grails.converters.JSON

/**
 * Controller for alerts 
 * 
 * @author Jakob Henriksson 
 *
 */
class AlertController {
	
	static allowedMethods = [ 
		index: 'GET', 
		view: 'GET', 
		sensorData: 'GET' 
	]

	def alertService 
	
	/**
	 * Lists recent alerts
	 * 
	 * @param id
	 * @return
	 */
    def index(int id) { 
		// return the most recent 5 alerts 
		[alerts: Alert.list(max: 5, sort: "id", order: "desc")]
	}

	/**
	 * Show details about a particular alert
	 *
	 * @param id
	 * @return
	 */
	def view(int id) {
		
		if (id > 0) {
			
			def alert = Alert.get(id)
			if (alert) {
				return [ alert : alert ]
			}
		}

		// error 		
		response.sendError(404)
	}
	
	/**
	 * Returns sensor data as JSON 
	 * 
	 */
	def sensorData() {
		
		def alertId = params.int('alert_id');
		if (alertId != null) {
			
			def alert = Alert.get(alertId)
			if (alert) {
				// get data 
				def res = alertService.getSensorData(alert)
				// return as JSON 
				(res as JSON).render response
				return 
			}
		}
		
		// default 
		([ status : "KO", 
			message : "Unkonwn error"] as JSON).render response
	}	
}
