/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub

/**
 * Represents observed properties in offerings 
 * 
 * @author Jakob Henriksson
 *
 */
class OfferingProperties {
	
	// the service 
	Service service
	// the offering ID
	String offering
	// the observed property ID
	String observedProperty
	// true if this offering-property is active, false otherwise 
	boolean active = true

    static constraints = {
		service(blank: false, nullable: false)
		offering(blank: false, nullable: false)
		observedProperty(blank: false, nullable: false)
		active(blank: false, nullable: false)
    }
	
	static mapping = {
		table 'hub_offering_properties'		
	}
	
	static namedQueries = {
			// returns the active services 
			activeOfferings {
				eq('active', true)
			}
    }	
	
}
