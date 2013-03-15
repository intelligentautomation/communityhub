/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub

/**
 * Represents a cached Capabilities document 
 * 
 * 
 * @author Jakob Henriksson
 *
 */
class CapabilitiesCache {
	
	Service service
	String capabilities
	Date dateCreated
	Date lastUpdated 

    static constraints = {
		service(blank: false, nullable: false)
		capabilities(maxSize: 1073741824, blank: true, nullable: true)
    }
	
	static mapping = {
		autoTimestamp true
		table 'hub_capabilities_cache'
	}
}
