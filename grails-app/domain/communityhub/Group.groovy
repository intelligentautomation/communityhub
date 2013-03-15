/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub

import communityhub.security.SecUser

/**
 * Representing a community group (that will have alert 
 * rules associated with it) 
 * 
 * @author Jakob Henriksson 
 *
 */
class Group {
	
	// name of the group
	String name = "Untitled"
	// description for the group
	String description = ""
	// when the group was created 
	Date dateCreated
	// when the group was last updated
	Date lastUpdated
	// the user who created the group
	SecUser createdBy
	// the administrator for the group
	SecUser admin
	// true of the group is considered active 
	boolean active = true
	// true if the group is public 
	boolean communal = true
	
	// the rules and alerts associated with this group 
	static hasMany = [rules:Rule, alerts:Alert]

    static constraints = {
		name(blank: false, nullable: false)
		description(blank: true, nullable: true)
		dateCreated()
		lastUpdated()
		createdBy(blank: false, nullable: false)
		admin(blank: false, nullable: false)
		active(blank: false, nullable: false)
		communal(blank: false, nullable: false)
		rules()
		alerts()
    }
	
	static mapping = {
		autoTimestamp true
		table 'hub_group'
		rules cascade: "all-delete-orphan"
		alerts cascade: "all-delete-orphan"  
//		rules sort: 'dateCreated'
	}
	
	static namedQueries = {
			activeGroups {
				eq('active', true)
				eq('communal', true)
			}
    }		
}
