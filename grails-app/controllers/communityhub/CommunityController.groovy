package communityhub

import grails.converters.JSON
import grails.plugins.springsecurity.Secured

import org.springframework.dao.DataAccessException

import com.iai.communityhub.AlertType
import com.iai.communityhub.SecUser
import com.iai.communityhub.dao.AlertDao
import com.iai.communityhub.dao.CapabilitiesCacheDao
import com.iai.communityhub.dao.GroupDao
import com.iai.communityhub.dao.GroupsRulesXrefDao
import com.iai.communityhub.dao.OfferingPropertiesDao
import com.iai.communityhub.dao.RuleDao
import com.iai.communityhub.dao.ServiceDao
import com.iai.communityhub.model.Alert
import com.iai.communityhub.model.CapabilitiesCache
import com.iai.communityhub.model.Group
import com.iai.communityhub.model.GroupsRulesXref
import com.iai.communityhub.model.Rule
import com.iai.communityhub.model.Service
import com.iai.proteus.common.sos.GetCapabilities
import com.iai.proteus.common.sos.model.SensorOffering
import com.iai.proteus.common.sos.model.SosCapabilities
import com.iai.proteus.common.sos.util.SosUtil


class CommunityController {

	def rulesService
	
	def jdbcTemplate 
	def springSecurityService
	
	grails.gsp.PageRenderer groovyPageRenderer
	

	/**
	 * 
	 * @param id
	 * @return
	 */
	def index(int id) {

		GroupDao daoGroup = new GroupDao(jdbcTemplate);
		Collection<Group> groups = daoGroup.findAll();
		
		// if no group is selected, but there are groups, select the first one
		// by default 
		if (id == 0 && groups != null && groups.size() > 0) {
			id = ((Group)groups.iterator().next()).getId();
		}
		
		if (id > 0) {
			
			try {
				
				// fetch the current group 
				Group group = daoGroup.findUniqueObjectById("" + id);
					
				AlertDao daoAlert = new AlertDao(jdbcTemplate);
				Collection<Alert> alerts = daoAlert.findAlertsForGroup(id, 3, 0);
				
				return [ id: id, groups: groups, group: group, alerts : alerts ] 
				 
			} catch (DataAccessException e) {
				log.error("Data access exception: " + e.getMessage()); 
				response.sendError(500);
				return;
			}

		}
		
		return [ id: id, groups : groups]
	}
	
	/**
	 * Shows rules for various community groups 
	 *
	 * @param id
	 * @return
	 */
	def rules(int id) {

		GroupDao daoGroup = new GroupDao(jdbcTemplate);
		Collection<Group> groups = daoGroup.findAll();
		
		// if no group is selected, but there are groups, select the first one
		// by default
		if (id == 0 && groups != null && groups.size() > 0) {
			id = ((Group)groups.iterator().next()).getId();
		}
		
		if (id > 0) {
			
			try {
				
				// fetch the current group
				Group group = daoGroup.findUniqueObjectById("" + id);
					
				RuleDao daoRule = new RuleDao(jdbcTemplate);
				Collection<Rule> rules = daoRule.findRulesForGroup(id);
				
				return [ id: id, groups: groups, group: group,
						rules: rules]
				 
			} catch (DataAccessException e) {
				log.error("Data access exception: " + e.getMessage());
				response.sendError(500);
				return;
			}

		}
		
		return [ id: id, groups : groups]
	}
	
	/**
	 * Create a new group
	 * 
	 * @return
	 */
	@Secured(['ROLE_ADMIN', 'ROLE_USER'])
	def create() {
		
		if (request.method == "POST") {
			
			// get the parameters 
			def name = params.inputName;
			def description = params.inputDescription;

			if (name.trim().equals("")) {
				def error = "The group must have a non-empty name, please try again";
				return [name : name, description : description, error : error] 
			}
			
			if (description != null && description.trim().equals(""))
				description = null;
			
			Group group = new Group();
			group.setName(name);
			group.setDescription(description);
			// set user and administrator
			def user = SecUser.get(springSecurityService.principal.id)
			group.setCreatedBy(user.username);
			group.setAdmin(user.username);
			
			GroupDao dao = new GroupDao(jdbcTemplate);
			dao.insert(group);
	
			def success = "The group was successfully created";
			return [name : name, description : description, success : success ];
		}
		
	}
	
	/**
	 * Edit a new group 
	 * 
	 * @param id Group id 
	 * @return
	 */
	def edit(int id) {
		
		if (request.method == "POST") {

			// get the service parameter and make sure it's an integer
			def name = params.inputName;
			// get the service parameter and make sure it's an integer
			def description = params.inputDescription;
			
			GroupDao dao = new GroupDao(jdbcTemplate);
			try {
				
				// get and update record  
				Group group = dao.findUniqueObjectById("" + id);
				group.setName(name);
				group.setDescription(description);
				dao.update(group);
				
				def success = "Group updated";
				return [ id : id, group : group, success : success ];
				
			} catch (DataAccessException e) {
				log.error("Data access exception: " + e.getMessage());
			}
			
			def error = "A failure occurred";
			return [ id : id, error : error ];
		}
		
		if (id > 0) {
			GroupDao dao = new GroupDao(jdbcTemplate);
			try {
				
				Group group = dao.findUniqueObjectById("" + id);
				
				return [ id : id, group : group];
				
			} catch (DataAccessException e) {
				log.error("Data access exception: " + e.getMessage());
			}
		}
		
		return response.sendError(404);
	}
	
	/**
	 * View for adding rules to a group
	 *
	 * @param id
	 * @return
	 */
	def add(int id) {

		// get the service parameter (an integer)
		def serviceId = params.int('service');

		ServiceDao dao = new ServiceDao(jdbcTemplate);
		Collection<Service> services = dao.findAll();

		if (id > 0) {
			
			GroupDao daoGroup = new GroupDao(jdbcTemplate);
			try {
				
				Group group = daoGroup.findUniqueObjectById("" + id)
				return [ services : services, group : group, serviceId : serviceId ]
				
			} catch (DataAccessException e) {
				log.error("Data access exception: " + e.getMessage());
			}
		}

		return response.sendError(404);
	}
	
	/**
	 * For creating a rule
	 *
	 * @return
	 */
	def ajaxCreateRule() {

		def json = request.JSON;

		// get the service parameter and make sure it's an integer
		def groupId = json.group;

		Group group = new Group();
		group.setId(groupId);
			
		def details = json.details;
		
		println "Group Id: " + groupId;

		// handle according to service type
		switch (json.type) {

			case "service":

				def serviceId = details.serviceId;
							
				if (serviceId != null) {
					println "Service Id: " + serviceId;
					 
					// get or create the rule
					Rule rule = rulesService.getServiceDownRule(serviceId);
					
					// associate the rule with the group
					GroupsRulesXrefDao daoXref =
						new GroupsRulesXrefDao(jdbcTemplate);
					daoXref.insert group, rule;
				}
			
				break;
				
			case "irregular":
			
				def serviceId = details.serviceId;
				def offering = details.offering;
				def property = details.property;
				
				def options = details.options;
				
				if (serviceId != null && offering != null && property != null) {
					
					// get or create the rule
					Rule rule =
						rulesService.getIrregularDataDeliveryRule(serviceId,
							offering, property, options);
						
//					System.out.println("RULE ID: " + rule.getId());

					// associate the rule with the group
					GroupsRulesXrefDao daoXref =
						new GroupsRulesXrefDao(jdbcTemplate);
					daoXref.insert group, rule;
				}
			
				break;

		}
		
		([ status : "OK" ] as JSON).render response
	}
	
	/**
	 * Executes all 'service down' rules
	 *
	 * @return
	 */
	def ajaxExecuteServiceDownRules() {
		
		RuleDao daoRule = new RuleDao(jdbcTemplate);
		Collection<Rule> rules = daoRule.findServiceDownRules();

		// execute the rules in the background
		runAsync {
			rulesService.executeAllRules(rules);
		}
		
		def json = [ status : "OK", noRules : rules.size() ];
		render json as JSON;
	}
	
	/**
	 * Executes all 'irregular data delivery' rules
	 *
	 * @return
	 */
	def ajaxExecuteIrregularDataDeliveryRules() {
		
		RuleDao daoRule = new RuleDao(jdbcTemplate);
		Collection<Rule> rules = daoRule.findIrregularDataDeliveryRules();
		
		// execute the rules in the background
		runAsync {
			rulesService.executeAllRules(rules);
		}
		
		def json = [ status : "OK", noRules : rules.size() ];
		render json as JSON;
	}


	/**
	 * Renders a template with a list of sensor offerings
	 *
	 */
	def ajaxOfferings() {

		// get the service parameter (an integer)
		def serviceId = params.int('service_id');

		OfferingPropertiesDao dao = new OfferingPropertiesDao(jdbcTemplate);
		Collection<String> offerings =
			dao.findDistinctActiveOfferings(serviceId);

		// render the view with the specified model
		render(view : "/community/t_ajax_offerings",
				model: [ offerings : offerings ] );
	}

	/**
	 * Renders a template with a list of observed properties
	 *
	 * @return
	 */
	def ajaxProperties() {

		// get the service parameter (an integer)
		def serviceId = params.int('service_id');
		// get the offering parameter
		def offeringId = params.offering_id;

		OfferingPropertiesDao dao = new OfferingPropertiesDao(jdbcTemplate);
		Collection<String> properties =
				dao.findActiveObservedProperties(serviceId, offeringId);
				
		// render the view with the specified model
		render(view : "/community/t_ajax_properties",
				model: [ properties : properties ] );
	}
	
	/**
	 * Returns JSON with the number of offerings that contain the given
	 * observed property for the given service
	 *
	 * @return
	 */
	def ajaxOfferingsWithSameProperty() {

		// get the service parameter (an integer)
		def serviceId = params.int('service_id');
		// get the observed property
		def property = params.observed_property;

		int num =
			rulesService.countOfferingsWithObservedProperty(serviceId, property);
		
		def json = [ "status" : "OK", "number" : num ];
		
		render json as JSON
	}

	/**
	 * Returns JSON with information regarding if we support the data formats
	 * served up by the given sensor offering  
	 * 
	 * @return
	 */
	def ajaxCheckSupportedFormats() {
		
		// get the service parameter (an integer)
		def serviceId = params.int('service_id');
		// get the offering parameter
		def offeringId = params.offering_id;
		
		CapabilitiesCacheDao daoCache =	new CapabilitiesCacheDao(jdbcTemplate);
		try {
			
			CapabilitiesCache cache = daoCache.findForServiceId(serviceId);
			SosCapabilities caps =
				GetCapabilities.parseCapabilitiesDocument(cache.getCapabilities());

			SensorOffering sensorOffering = caps.getOfferingById(offeringId);
			List<String> commonFormats = 
				SosUtil.commonResponseFormats(sensorOffering);
				
			if (commonFormats.size() > 0) {
				([ status : "OK" ] as JSON).render response
				return
			}
			
		} catch (DataAccessException e) {
			log.error("Data access exception: " + e.getMessage());
		}
		
		([ status : "KO" ] as JSON).render response
	}
	
	/**
	 * Removes a rule from a group
	 *
	 * @return
	 */
	def ajaxRemoveRule() {
		
		// get the group parameter (an integer)
		def groupId = params.int('group_id');
		if (!groupId) {
			log.error("Error reading 'group_id' parameter");
			([ status : "KO" ] as JSON).render response
			return;
		}

		// get the rule parameter (an integer)
		def ruleId = params.int('rule_id');
		if (!ruleId) {
			log.error("Error reading 'ruleId' parameter");
			([ status : "KO" ] as JSON).render response
			return;
		}

		RuleDao ruleDao = new RuleDao(jdbcTemplate);
		
		try {
			
			Rule rule = ruleDao.findUniqueObjectById("" + ruleId);
			// do we need to remove the rule itself?
			switch (rule.getType()) {
				case AlertType.ALERT_SERVICE_DOWN.toString():
					// do nothing
					break;
				case AlertType.ALERT_IRREGULAR_DATA_DELIVERY.toString():
					// do nothing
					break;
				case AlertType.ALERT_USER_TEMPLATE.toString():
					// TODO: maybe do something
					break;
			}

			// remove the reference between the group and the rule
			GroupsRulesXref xref = new GroupsRulesXref();
			xref.setGroup(groupId);
			xref.setRule(ruleId);
			GroupsRulesXrefDao daoXref = new GroupsRulesXrefDao(jdbcTemplate);
			daoXref.remove(xref);
			log.info("Removed Group-Rule reference");
			
			// return success
			([ status : "OK" ] as JSON).render response
			return;
			
		} catch (DataAccessException e) {
			log.error("Data access exception: " + e.getMessage());
		}
		
		// something went wrong
		([ status : "KO" ] as JSON).render response
		return;
	}
	
	
	/**
	 * Deletes a community group
	 * 
	 * (Actually, does not delete, but makes it inactive) 	
	 */
	def ajaxDeleteGroup(int id) {

		if (request.method == "POST") {
				 
			try {
				
				GroupDao daoGroup = new GroupDao(jdbcTemplate);
				// find the group
				Group group = daoGroup.findUniqueObjectById("" + id);
				// delete the group
				daoGroup.delete(group);
				
				([ status : "OK" ] as JSON).render response
				return;
				
			} catch (DataAccessException e) {
				log.error("Data access exception: " + e.getMessage());
			}
		}
		
		// default 
		([ status : "KO" ] as JSON).render response
	}
	
	/**
	 * Generates an alerts feed for a group
	 *
	 * @param id
	 * @return
	 */
	def feed(int id) {
		
		if (id > 0) {
			
			GroupDao daoGroup = new GroupDao(jdbcTemplate);
			Group group = daoGroup.findUniqueObjectById("" + id);
			
			AlertDao daoAlert = new AlertDao(jdbcTemplate);
			Collection<Alert> alerts =
				daoAlert.findAlertsForGroup(group.getId(), 50, 0);
				
			return render(view: "/community/feed",
				model: [group: group, alerts: alerts],
				contentType: "application/atom+xml",
				encoding: "UTF-8")
		}
		
		return response.sendError(404);
	}
	
	/**
	 * Generates an alerts feed for a group
	 *
	 * @param id
	 * @return
	 */
	def rss(int id) {
		
		if (id > 0) {
			
			GroupDao daoGroup = new GroupDao(jdbcTemplate);
			Group group = daoGroup.findUniqueObjectById("" + id);
			
			AlertDao daoAlert = new AlertDao(jdbcTemplate);
			Collection<Alert> alerts =
				daoAlert.findAlertsForGroup(group.getId(), 50, 0);
				
			return render(view: "/community/rss",
				model: [group: group, alerts: alerts],
				contentType: "application/rss+xml",
				encoding: "UTF-8")
		}
		
		return response.sendError(404);
	}

}
