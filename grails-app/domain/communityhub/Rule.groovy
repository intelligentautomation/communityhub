/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub

import communityhub.security.SecUser

/**
 * Representing an alert rule 
 *
 * @author Jakob Henriksson
 *
 */
class Rule {
	
	// the type of rule 
	// TODO: change to enum (AlertType) 
//	AlertType type
	String type
	// the service this rule is specified on
	Service service
	// the offering this rule is specified on, if any
	String offering
	// the observed property this rule is specified on, if any
	String observedProperty
	// the variable referred to in this rule, if any (custom)
	String variable
	// the value referred to in this rule, if any (custom)
	Double value
	// the date when this rule was created
	Date dateCreated
	// the date when the rule was last modified 
	Date lastUpdated
	// the user creating the rule 
	SecUser createdBy
	// true of the group is considered active 
	boolean active = true
	
	// many-to-many relationship with Groups
	static hasMany = [groups:Group]
	static belongsTo = Group
	
    static constraints = {
		type(blank: false, nullable: false)
		service(blank: false, nullable: false)
		offering(blank: true, nullable: true)
		observedProperty(blank: true, nullable: true)
		variable(blank: true, nullable: true)
		value(blank: true, nullable: true)
		dateCreated()
		lastUpdated()
		createdBy(blank: true, nullable: true)
		active(blank: false, nullable: false) 
    }
	
	static mapping = {
		autoTimestamp true
		table 'hub_rule'
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
