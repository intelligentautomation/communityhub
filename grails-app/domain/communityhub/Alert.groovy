/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub

/**
 * Representing an alert 
 * 
 * @author Jakob Henriksson 
 *
 */
class Alert {

	// the service this alert relates to  
	Service service
	// sensor offering 
	String offering
	// observed property 
	String observedProperty
	// when the alert was created
	Date dateCreated
	// when the alert was last updated (should not happen)
	Date lastUpdated
	// when the alert is valid from
	Date validFrom
	// when the alert is valid to 
	Date validTo
	// type of alert 
	// TODO: change to enum 
	String type
	// location (bounding box)
	BigDecimal latLower
	BigDecimal lonLower
	BigDecimal latUpper
	BigDecimal lonUpper
	// details about the alert 
	String detail
	
	// the groups the alert was generated for 
	// many to many relationship with Groups
	static hasMany = [groups:Group]
	static belongsTo = Group
	
    static constraints = {
		service(blank: false, nullable: false)
		offering(blank: true, nullable: true)
		observedProperty(blank: true, nullable: true)
		dateCreated()
		lastUpdated()
		validFrom(blank: true, nullable: true)
		validTo(blank: true, nullable: true)
		type(blank: false, nullable: false)
		latLower(scale: 12, blank: true, nullable: true)
		lonLower(scale: 12, blank: true, nullable: true)
		latUpper(scale: 12, blank: true, nullable: true)
		lonUpper(scale: 12, blank: true, nullable: true)
		detail(widget: 'textarea')
    }
	
	static mapping = {
		autoTimestamp true
		table 'hub_alert'
		detail(type: 'text')
		// default sort 
		sort dateCreated: "desc" 
	}
	
	static namedQueries = {
	        forGroup { g -> 
				groups {
					eq('id', g.id)
				}
	        }
    }
	
	/**
	 * Returns a 'prettified' version of the alert/rule type 
	 * 
	 * @return
	 */
	def getTypePretty() {
		// TODO: replace with this when we are using an enumeration for the type 
//		return type.toReadableString()
		String str = type.replaceAll("\\_", " ").toLowerCase()
		return str.substring(0, 1).toUpperCase() + str.substring(1)
	}
}
