/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub

/**
 * Representing a service (e.g. an SOS)
 * 
 * @author Jakob Henriksson
 *
 */
class Service {
	
	// the service endpoint 
	String endpoint
	// title of the service 
	String title = "Untitled"
	// the type of service (SOS, WMS, etc.)
	// TODO: change to enum
//	ServiceType type = ServiceType.SOS	
	String type = "SOS"
	// when the service was created 
	Date dateCreated
	// when the service object was last updated 
	Date lastUpdated
	// true if the service is considered active, false otherwise
	boolean active = true
	// true if the serivce is considered alive/functioning, false otherwise 
	boolean alive = false

    static constraints = {
		endpoint(blank: false, nullable: false)
		title(blank: false, nullable: false)
		type(blank: false, nullable: false)
		dateCreated()
		lastUpdated()
		active(blank: false, nullable: false)
		alive(blank: false, nullable: false)
    }
	
	static mapping = {
		autoTimestamp true
		table 'hub_service'
	}
	
	static namedQueries = {
			// returns the active services 
			activeServices {
				eq('active', true)
			}
    }	
}
