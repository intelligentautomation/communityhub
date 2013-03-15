/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub

import grails.converters.JSON
import grails.plugins.springsecurity.Secured

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

import javax.validation.GroupSequence;

import com.iai.proteus.common.sos.GetCapabilities
import com.iai.proteus.common.sos.model.SensorOffering
import com.iai.proteus.common.sos.model.SosCapabilities
import com.iai.proteus.common.sos.util.SosUtil
import communityhub.security.SecUser

/**
 * Controller for Community Groups
 * 
 * @author Jakob Henriksson 
 *
 */
class CommunityController {
	
	static allowedMethods = [ 
		index: 'GET', 
		list: 'GET', 
		create: 'GET', 
		view: 'GET', 
		feed: 'GET', 
		report: 'GET',
		edit: 'GET', 
		config: 'GET',
		add: 'GET',   
		save: 'POST', 
		update: 'POST', 
		delete: 'POST', 
		createRule: 'POST', 
		removeRule: 'POST',  
		
		ajaxOfferings: 'GET', 
		ajaxProperties: 'GET', 
		ajaxOfferingsWithSameProperty: 'GET', 
		ajaxCheckSupportedFormats: 'GET',
		
		ajaxExecuteServiceDownRules: 'POST', 
		ajaxExecuteIrregularDataDeliveryRules: 'POST', 
	]	
	
	DateFormat formatYear = new SimpleDateFormat("yyyy")
	DateFormat formatMonth = new SimpleDateFormat("MMM")
	
	def rulesService
	
	def springSecurityService
	
	/**
	 * Index 
	 * 
	 * @return
	 */
	def index() {
		redirect(action: 'list', params: params)
	}
	
	/**
	 * List all services 
	 * 
	 * @return
	 */
	def list() {
		// find all active community groups
		def groups = Group.activeGroups.list(params)
		[ groups : groups ]
	}	
	
	/**
	 * Displays page to create a new group
	 * 
	 * @return
	 */
	@Secured(['ROLE_ADMIN', 'ROLE_USER'])
	def create() {
		
	}
	
	/**
	 * Save a group 
	 * 
	 * @return
	 */
	@Secured(['ROLE_ADMIN', 'ROLE_USER'])
	def save() {
		
		// get the parameters 
		def name = params.inputName
		def description = params.inputDescription
		
		def errorCode = null

		// check for errors
		if (name.trim().equals("")) 
			errorCode = "default.communityhub.group.error.empty"

		if (description != null && description.trim().equals(""))
			description = null

		// return with error if needed
		if (errorCode != null) {
			// set error message
			flash.error = errorCode
			// direct 
			redirect(action: 'create', params: 
				[name : name, description: description])
			return			
		}
		
		// create group 	
		def group = new Group()
		group.name = name
		group.description = description
		// set user and administrator
		def user = SecUser.get(springSecurityService.principal.id)
		group.createdBy = user
		group.admin = user
		group.save(flush: true)

		// set message
		flash.message = "default.communityhub.group.success.created"
		// re-direct 		
		redirect(action: 'view', id: group.id)
	}
	
	/**
	 * View a community group 
	 * 
	 * @param id
	 * @return
	 */
	def view(int id) {
		
		if (id > 0) {
			def group = Group.get(id)
			if (group) {

				// get alerts for the group  
				def alerts = Alert.createCriteria().list(params) {
					groups {
						eq('id', group.id)
					}
				}
				
				return [ group : group, alerts : alerts ]
			}
		}
		
		// error 		
		response.sendError(404)
	}
	
	/**
	 * Edit a new group 
	 * 
	 * @param id Group id 
	 * @return
	 */
	@Secured(['ROLE_ADMIN', 'ROLE_USER'])
	def edit(int id) {
		if (id > 0) {
			def group = Group.get(id)
			if (group) {
				// check that the user is the administrator 
				def user = SecUser.get(springSecurityService.principal.id)
				// the user is the administrator
				if (user.equals(group.admin))
					return [ group : group ]
				
				// set message
				flash.error = "default.communityhub.group.error.edit"
				// re-direct
				redirect(action: "view", id : id)
				return
			}
		}
		
		// error 		
		response.sendError(404)
	}
	
	/**
	 * Update a group
	 * 
	 * @param id
	 * @return
	 */
	def update(int id) {
		
		if (id > 0) {
			
			def group = Group.get(id)
			if (group) {
			
				// get the service parameter and make sure it's an integer
				def name = params.inputName
				// get the service parameter and make sure it's an integer
				def description = params.inputDescription
				
				// update information 
				group.name = name
				group.description = description
				// save
				group.save()
				
				flash.message = "default.communityhub.group.success.updated"
				// re-direct
				redirect(action: 'view', id: group.id) 
				return
			}
		}
		
		// error
		response.sendError(404)
	}
	
	/**
	 * Deletes a new group (marks it as inactive) 
	 * 
	 * @param id Group id 
	 * @return
	 */
	@Secured(['ROLE_ADMIN', 'ROLE_USER'])
	def delete(int id) {
		if (id > 0) {
			def group = Group.get(id)
			if (group) {
				// check that the user is the administrator
				def user = SecUser.get(springSecurityService.principal.id)
				// the user is the administrator 
				if (user.equals(group.admin)) {
					// update flag
					group.active = false
					// save
					group.save()
					// set message
					flash.message = "default.communityhub.group.success.deleted"
					// re-direct to home 
					redirect(action: 'list')
					return
				}
				
				// set error message
				flash.error = "default.communityhub.group.error.delete"
				// re-direct to home 
				redirect(action: 'list')
				return
			}
		}
		// general error 
		flash.error = "default.communityhub.error.unknown"
		// re-direct to home
		redirect(action: 'list') 		
	}	
	
	/**
	 * Displays the main configuration page for group, where users 
	 * (administrators) can add/remove rules for community groups 
	 *
	 * @param id
	 * @return
	 */
	@Secured(['ROLE_ADMIN', 'ROLE_USER'])
	def config(int id) {

		if (id > 0) {
			
			def group = Group.get(id)
			if (group) {
				// check that the user is the administrator
				def user = SecUser.get(springSecurityService.principal.id)
				// the user is the administrator 
				if (user.equals(group.admin)) {
					// get the rules of this group
					def rules = group.rules
					return [ group: group, rules: rules ]
				}

				// set error message
				flash.error = "default.communityhub.group.error.config"
				// re-direct
				redirect(action: "view", id: id)
				return
			}
		}
		
		// error
		response.sendError(404)
	}	
	
	/**
	 * Displays page for adding rules to a group
	 *
	 * @param id
	 * @return
	 */
	@Secured(['ROLE_ADMIN', 'ROLE_USER'])
	def add(int id) {

		// get the service parameter (an integer)
		def serviceId = params.int('service')

		if (id > 0) {
			
			def group = Group.get(id)
			if (group) {
				// check that the user is the administrator
				def user = SecUser.get(springSecurityService.principal.id)
				// the user is the administrator 
				if (user.equals(group.admin)) {
					def services = Service.activeServices.list()
					return [group : group, serviceId : serviceId, services : services]
				}
				
				// set error message
				flash.error = "default.communityhub.group.error.config"
				// re-direct
				redirect(action: "view", id: id)
				return
			}
		}

		// error 
		return response.sendError(404)
	}	
	
	/**
	 * Generates an alerts feed for a group
	 *
	 * @param id
	 * @return
	 */
	def feed(int id) {
		
		if (id > 0) {

			def group = Group.get(id)
			if (group) {
				
				def alerts = Alert.forGroup(group).list(max: 50, 
					sort: "dateCreated", order: "asc")
				
				return render(view: "/community/feed",
					model: [group: group, alerts: alerts],
					contentType: "application/atom+xml",
					encoding: "UTF-8")
			}			
		}
		
		return response.sendError(404)
	}
	
	/**
	 * Generates an alerts feed for a group
	 *
	 * @param id
	 * @return
	 */
	def rss(int id) {
		
		if (id > 0) {

			def group = Group.get(id)
			if (group) {
				
				def alerts = Alert.forGroup(group).list(max: 50, 
					sort: "dateCreated", order: "asc")
				
				return render(view: "/community/rss",
					model: [group: group, alerts: alerts],
					contentType: "application/rss+xml",
					encoding: "UTF-8")				
			}			
		}
		
		return response.sendError(404)		
	}
	
	/**
	 * Generates a report for a community group 
	 * 
	 * @param id
	 * @return
	 */
	def report(int id) {
		
		if (id > 0) {
			
			boolean usingDefault = false
			
			def year = params.year
			def month = params.month
			
			Calendar calendar = GregorianCalendar.getInstance()
			// default, this month, reset values 
			calendar.set(Calendar.DAY_OF_MONTH, 1)
			calendar.set(Calendar.HOUR_OF_DAY, 0)
			calendar.set(Calendar.MINUTE, 0)
			calendar.set(Calendar.SECOND, 0)
			
			// parsing the year 
			if (year != null) {
				try {
					Date date = formatYear.parse(year)
					// set the calendar 
					calendar.setTime(date)
				} catch (ParseException e) {
					usingDefault = true
				}
			}
			
			// parsing the month
			if (month != null) {
				try {
					Date date = formatMonth.parse(month)
					// set the calendar
					Calendar c1 = Calendar.getInstance()
					c1.setTime(date)
					calendar.set(Calendar.MONTH, c1.get(Calendar.MONTH))
				} catch (ParseException e) {
					usingDefault = true
					// it's bizarre that I have to do this
					Date date = formatMonth.parse(formatMonth.format(new Date()))
					Calendar c1 = Calendar.getInstance()
					c1.setTime(date)
					calendar.set(Calendar.MONTH, c1.get(Calendar.MONTH))
				}
			}

			Date start = calendar.getTime()

			// calculate next and previous months for browsing
			calendar.add(Calendar.MONTH, -1)
			Date datePrev = calendar.getTime()
			calendar.add(Calendar.MONTH, 2)
			Date dateNext = calendar.getTime()
						
			// used as end-date when querying for alerts  
			Date end = dateNext.clone()
			
			def group = Group.get(id)
			if (group) {

				// get alerts from the group within the appropriate time frame
				def alerts = Alert.createCriteria().list(params) {
					groups {
						eq('id', group.id)
						between('dateCreated', start, end)
					}
				}				
				
				def mapAlerts = [:]
				def mapServiceDown = [:]
				def mapCountIrregularOfferings = [:]
				
				for (Alert alert : alerts) {
					
					int serviceId = alert.service.id
					String offeringId = alert.getOffering()
					
					// alerts 
					def l = mapAlerts.get(serviceId)
					if (l) {
						l.add(alert)
					} else {
						l = new ArrayList<Alert>()
						l.add(alert)
						mapAlerts.put(serviceId, l)
					}
					
					// service down calculations 
					if (alert.type.equals(AlertType.SERVICE_DOWN.toString())) {
					    def d = mapServiceDown.get(serviceId)
					    if (d != null) {
							// add X minutes to count
							mapServiceDown.put(serviceId, d + 5)
					    } else {
							// initialize ALERT_SERVICE_DOWN
							mapServiceDown.put(serviceId, 5)
					    }
					}
					
					// count irregular data delivery offerings
					if (alert.getType().equals(AlertType.IRREGULAR_DELIVERY.toString())) {
					    def o = mapCountIrregularOfferings.get(offeringId)
					    if (o != null) {
							mapCountIrregularOfferings.put(offeringId, o + 1)
					    } else {
							// initialize ALERT_IRREGULAR_DATA_DELIVERY
							mapCountIrregularOfferings.put(offeringId, 1)
					    }
					}
					
				}
				
				def sortDesc = { a, b -> b.value <=> a.value }
				
				return [ group : group, alerts : alerts, 
						mapAlerts : mapAlerts, 
						// sort: descending
						mapServiceDown : mapServiceDown.sort(sortDesc),
						// sort: descending
						mapCountIrregularOfferings : mapCountIrregularOfferings.sort(sortDesc), 
						date : start, datePrev : datePrev, dateNext : dateNext, 
						usingDefault : usingDefault ]
			}
				
			return []
		}
		
		response.sendError(404)
	}	
	
	
	
	/**
	 * Creates a rule for a group 
	 *
	 * @return
	 */
	def createRule() {

		def json = request.JSON
		// get the service parameter and make sure it's an integer
		def groupId = json.group
		def details = json.details
		
		// get the group
		def group = Group.get(groupId)

		// handle according to service type
		switch (json.type) {

			case "service":

				def serviceId = details.serviceId
							
				if (serviceId != null) {
					
					def service = Service.get(serviceId)
					if (group && service) {
						// get or create the rule
						Rule rule = rulesService.getServiceDownRule(service)
						println "Rule: " + rule
						// associate the rule with the group
						group.addToRules(rule).save()
					}
				}
			
				break
				
			case "irregular":
			
				def serviceId = details.serviceId
				def offering = details.offering
				def property = details.property
				
				def options = details.options
				
				if (serviceId != null && offering != null && property != null) {
					
					def service = Service.get(serviceId)
					if (group && service) {
						// get or create the rule
						Rule rule =
							rulesService.getIrregularDataDeliveryRule(service,
								offering, property, options)
							
						// associate the rule with the group
						group.rules.add(rule)
						group.save()
					}
				}
			
				break
		}
		
		([ status : "OK" ] as JSON).render response
	}
	
	/**
	 * Removes a rule from a group 
	 * 
	 * @param id
	 * @return
	 */
	def removeRule(int id) {
		
		if (id > 0) {
			
			def group = Group.get(id)
			if (group) {
		
				// get the rule parameter (an integer)
				def ruleId = params.int('rule_id')
				if (!ruleId) {
					log.error("Error reading 'rule_id' parameter")
					// set error message
					flash.error = "default.communityhub.error.unknown"
					// re-direct
					redirect(action: "config", id: id)
					return
				}
				
				def rule = Rule.get(ruleId)
				if (rule) {
					
					// remove the rule from the group
					rule.removeFromGroups(group)
					
					// set message
					flash.message = "default.communityhub.group.success.removerule"
					// re-direct
					redirect(action: "config", id: id)
					return					
				}
				
				// set error message
				flash.error = "default.communityhub.error.unknown"
				// re-direct
				redirect(action: "config", id: id)
				return
			}
		}
		
		// error
		response.sendError(404)
	}
	
	
	
	/**
	 * Executes all 'service down' rules
	 *
	 * @return
	 */
	def ajaxExecuteServiceDownRules() {
		
		// execute the rules in the background
		runAsync {
			
			// get all 'service down' rules
			def rules = Rule.findAllWhere(type : AlertType.SERVICE_DOWN.toString())
			
			rulesService.executeAllRules(rules)
		}
		
		def json = [ status : "OK" ]
		render json as JSON
	}
	
	/**
	 * Executes all 'irregular data delivery' rules
	 *
	 * @return
	 */
	def ajaxExecuteIrregularDataDeliveryRules() {
		
		// get all 'irregular data delivery' rules
		def rules = Rule.findAllWhere(type : AlertType.IRREGULAR_DELIVERY.toString())
		
		// execute the rules in the background
		runAsync {
			rulesService.executeAllRules(rules)
		}
		
		def json = [ status : "OK", noRules : rules.size() ]
		render json as JSON
	}


	/**
	 * Renders a template with a list of sensor offerings
	 *
	 */
	def ajaxOfferings() {

		// get the service parameter (an integer)
		def serviceId = params.int('service_id')
		
		def service = Service.get(serviceId)
		if (service) {

			// find all unique offerings 
			def offerings =	OfferingProperties.findAllWhere(service: service, 
				active: true)*.offering.unique()

			// render the view with the specified model
			render(view : "/community/t_ajax_offerings",
					model: [ offerings : offerings ] )
			return
		}
		
		// error
		response.sendError(404)
	}

	/**
	 * Renders a template with a list of observed properties
	 *
	 * @return
	 */
	def ajaxProperties() {

		// get the service parameter (an integer)
		def serviceId = params.int('service_id')
		// get the offering parameter
		def offeringId = params.offering_id

		def service = Service.get(serviceId)
		if (service) {
			def properties = OfferingProperties.findAllWhere(service: service, 
				offering : offeringId, active: true)*.observedProperty
			
			// render the view with the specified model
			render(view : "/community/t_ajax_properties",
					model: [ properties : properties ] )
			return
		}
		
		// error
		response.sendError(404)
	}
	
	/**
	 * Returns JSON with the number of offerings that contain the given
	 * observed property for the given service
	 *
	 * @return
	 */
	def ajaxOfferingsWithSameProperty() {

		// get the service parameter (an integer)
		def serviceId = params.int('service_id')
		// get the observed property
		def property = params.observed_property
		
		def service = Service.get(serviceId)
		if (service) {
			// count 
			def num = OfferingProperties.countByServiceAndObservedProperty(service, property)
			// return response 
			def json = [ "status" : "OK", "number" : num ]
			render json as JSON
			return
		}
		
		// error 
		([ status : "KO" ] as JSON).render response
	}

	/**
	 * Returns JSON with information regarding if we support the data formats
	 * served up by the given sensor offering  
	 * 
	 * @return
	 */
	def ajaxCheckSupportedFormats() {
		
		// get the service parameter (an integer)
		def serviceId = params.int('service_id')
		// get the offering parameter
		def offeringId = params.offering_id

		def service = Service.get(serviceId) 
		if (service) {
			def cache = CapabilitiesCache.findWhere(service: service)
			if (cache.capabilities) {
				// parse capabilities 
				SosCapabilities caps = 
					GetCapabilities.parseCapabilitiesDocument(cache.capabilities)
				// get sensor offering object 
				SensorOffering sensorOffering = caps.getOfferingById(offeringId)
				// retrieve supported formats 
				List<String> commonFormats = 
					SosUtil.commonResponseFormats(sensorOffering)
				// return "OK" if there is a common format 
				if (commonFormats.size() > 0) {
					([ status : "OK" ] as JSON).render response
					return
				}
			}
		}
		
		// error 
		([ status : "KO" ] as JSON).render response
	}
	

	
}
