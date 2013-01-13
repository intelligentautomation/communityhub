package communityhub

import org.codehaus.groovy.grails.web.json.JSONObject
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.Interval
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.springframework.dao.DataAccessException

import com.iai.communityhub.AlertType
import com.iai.communityhub.HubUtils
import com.iai.communityhub.Result
import com.iai.communityhub.ServiceResponse
import com.iai.communityhub.dao.AlertDao
import com.iai.communityhub.dao.CapabilitiesCacheDao
import com.iai.communityhub.dao.OfferingPropertiesDao
import com.iai.communityhub.dao.RuleDao
import com.iai.communityhub.dao.ServiceDao
import com.iai.communityhub.model.Alert
import com.iai.communityhub.model.CapabilitiesCache
import com.iai.communityhub.model.OfferingProperties
import com.iai.communityhub.model.Rule
import com.iai.communityhub.model.Service
import com.iai.proteus.common.sos.GetCapabilities
import com.iai.proteus.common.sos.data.Field
import com.iai.proteus.common.sos.data.SensorData
import com.iai.proteus.common.sos.model.GetObservationRequest
import com.iai.proteus.common.sos.model.SensorOffering
import com.iai.proteus.common.sos.model.SosCapabilities
import com.iai.proteus.common.sos.model.TimeInterval
import com.iai.proteus.common.sos.util.SosUtil

class RulesService {
	
	def jdbcTemplate 

	/**
	 * Finds the rule for a "service down" alert for a given service, 
	 * or creates one if needed 
	 * 
	 * @param serviceId
	 * @return
	 */
	private Rule getServiceDownRule(int serviceId) {
		
		RuleDao daoRule = new RuleDao(jdbcTemplate);
		Rule rule = daoRule.findServiceDownRuleForService(serviceId);
		
		// if a rule is found, we return it  
		if (rule != null)
			return rule; 
		
		// if no rule is found, create rule for the given service
		rule = new Rule();
		rule.setType(AlertType.ALERT_SERVICE_DOWN.toString());
		rule.setServiceId(serviceId);
		
		Rule insertedRule = daoRule.insert(rule);
		
		return insertedRule;
	}
	
	/**
	 * Finds the rule for an "irregular data delivery" alert for a 
	 * given service, offering and property, or creates one if needed
	 *
	 * @param serviceId
	 * @param offering
	 * @param observedProperty
	 * @param options 
	 * @return
	 */
	private Rule getIrregularDataDeliveryRule(int serviceId, 
			String offering, String observedProperty, JSONObject options) {
		
		boolean onlyProperties = options.get('add-all-properties');
			
		RuleDao daoRule = new RuleDao(jdbcTemplate);
		// determine if we should query for rules that include the offering, 
		// or only rules that are specified across observed properties  
		String offeringToQueryFor = onlyProperties ? null : offering; 
		Rule rule = 
			daoRule.findIrregularDataDeliveryRule(serviceId, offeringToQueryFor, 
				observedProperty);
			
		// if a rule is found, we return it
		if (rule != null)
			return rule;
		
		// if no rule is found, create rule for the given service
		rule = new Rule();
		rule.setType(AlertType.ALERT_IRREGULAR_DATA_DELIVERY.toString());
		rule.setServiceId(serviceId);
		// only add the offering if the rule is specified on S/O/P, 
		// but skip it if the rule is specified on S/P
		if (!onlyProperties) {
			rule.setOffering(offering);
		}
		rule.setObservedProperty(observedProperty);
		
		Rule insertedRule = daoRule.insert(rule);
		
		return insertedRule;
	}

	/**
	 * Executes a rule
	 *
	 * @param rule
	 */
	private void executeAllRules(Collection<Rule> rules) {

		// storing capabilities used for rules 
		Map<Integer, SosCapabilities> capabilities = 
			new HashMap<Integer, SosCapabilities>();
			
		log.info("Executing " + rules.size() + " rules");
		
		// iterate through all rules 
		for (Rule rule : rules) { 
		
			AlertType alertType = AlertType.parse(rule.getType());
			switch (alertType) {
	
				case AlertType.ALERT_SERVICE_DOWN:
	
					executeServiceDownRule(rule);
	
					break;
	
				case AlertType.ALERT_IRREGULAR_DATA_DELIVERY:
				
					executeIrregularDataDeliveryRule(rule, capabilities);
	
					break;
	
				case AlertType.ALERT_USER_TEMPLATE:
	
					log.error("Not implemented yet.");
	
					break;
	
				default:
					log.error("Did not understand rule type");
			}
		}
	}
	

	/**
	 * Executes a rule 
	 * 
	 * @param rule
	 */
//	private void executeRule(Rule rule) {
//
//		AlertType alertType = AlertType.parse(rule.getType());
//		switch (alertType) {
//
//			case AlertType.ALERT_SERVICE_DOWN:
//
//				executeServiceDownRule(rule);
//
//				break;
//
//			case AlertType.ALERT_IRREGULAR_DATA_DELIVERY:
//
//				// there will always be a service ID
//				int serviceId = rule.getService();
//				// offering IDs and property IDs are optional
//				// (but there must be at least one)
//				String offering = rule.getOffering(); 
////				int offeringId = rule.getOffering();
//				String property = rule.getObservedProperty();
//
//				if (offering == null && property == null) {
//					log.error("Does not know how to handle rule, " +
//							"missing both offeringId and observed property, " +
//							"skipping.");
//					return;
//				}
//
//				Collection<Group> groups =
//						new GroupDao(jdbcTemplate).findGroupsForRule(rule.getId());
//
//				for (Group group : groups) {
//					println "Group: " + group.getId();
//				}
//
//				// get the service object
//				Service service =
//						new ServiceDao(jdbcTemplate).findUniqueObjectById("" + serviceId);
//
//				// we have an offering
//				if (offering != null) {
//
////					Offering offering =
////							new MyOfferingDao(jdbcTemplate).findUniqueObjectById("" + offeringId);
//
//					// AND an observed property
//					if (property != null) {
//
//						println "Query (IRREGULAR): " + service.getEndpoint() + "; for " +
//								offering + "; " + property;
//
//						executeRule(rule, service, offering, property);
//
//					}
//					// we only have an offering
//					else {
//
//						println "Query (IRREGULAR): " + service.getEndpoint() +
//								"; for ALL properties in offering: " + offering;
//
//					}
//				} else {
//
//					// there is no offering so we assume we have an
//					// observed property (already verified)
//
//					println "Query (IRREGULAR): " + service.getEndpoint() +
//							"; for ALL offerings for observed property: " +
//							property;
//							
//					executeRule(rule, service, property); 
//
//				}
//
//				break;
//
//			case AlertType.ALERT_USER_TEMPLATE:
//
//				log.error("Not implemented yet.");
//
//				break;
//
//			default:
//				log.error("Did not understand rule type");
//		}
//	}
	
	/**
	 * Executes a service down rule 
	 * 
	 * @param rule
	 */
	private void executeServiceDownRule(Rule rule) {
		
		// find the service associated with the rule
		ServiceDao daoService = new ServiceDao(jdbcTemplate);
		Service service;
		
		try {
			service = daoService.findUniqueObjectById("" + rule.getServiceId());
		} catch (DataAccessException e) {
			log.error("Data access exception: " + e.getMessage());
			return;
		}
		
		log.info("Executing rule: " + rule.getId());
		
		// try and fetch the capabilities document
		String capabilities = HubUtils.getCapabilitiesDocument(service);
		

		// only store Capabilities documents, not exceptions 
		boolean success = false;
		if (capabilities != null && !capabilities.contains("ExceptionReport")) {
			success = true;
		}
		
		if (success) {
		
			// create capabilities cache object
			CapabilitiesCache cache = new CapabilitiesCache();
			cache.setService(service.getId());
			cache.setCapabilities(capabilities);
			// insert capabilities cache object
			CapabilitiesCacheDao daoCache =
				new CapabilitiesCacheDao(jdbcTemplate);
			daoCache.insertOrUpdate(cache);
			
			log.info("Successfully fetched Capabilities document from: " +
				service.getEndpoint());
			
			// update service status 
			daoService.setAliveStatus(service, true);
			
		} else {
		
			// update service status
			daoService.setAliveStatus(service, false);
		
			// generate alert
			Alert alert = new Alert(); 
			alert.setServiceId(service.getId());
			alert.setType(rule.getType());
			alert.setDetail("We were not able to fetch the " +
				service.getType() + " Capabilities " + 
				"document from service endpoint " + service.getEndpoint());
			
			// insert the alert and automatically associate the alert
			// with relevant groups 
			AlertDao daoAlert = new AlertDao(jdbcTemplate);
			daoAlert.insert(alert, rule);
			
			log.info("Generated service down alert for rule ID: " + rule.getId());
		}
	}
	
	/**
	 * Executes an irregular data delivery rule 
	 * 
	 * @param rule
	 * @param capabilities Maps service IDs with parsed SosCapabilities objects 
	 */
	private void executeIrregularDataDeliveryRule(Rule rule, Map<Integer, SosCapabilities> capabilities) 
	{
		
		// there will always be a service ID
		int serviceId = rule.getServiceId();
		// offering IDs and property IDs are optional
		// (but there must be at least one)
		String offering = rule.getOffering();
		String property = rule.getObservedProperty();

		if (offering == null && property == null) {
			log.error("Does not know how to handle rule, " +
					"missing both offeringId and observed property, " +
					"skipping.");
			return;
		}

		// get the service object
		Service service =
				new ServiceDao(jdbcTemplate).findUniqueObjectById("" + serviceId);

		// get the capabilities object
		SosCapabilities caps = null;
		if (capabilities.containsKey(service.getId())) {
			// get already parsed Capabilities
			caps = capabilities.get(service.getId());
		} else {
			CapabilitiesCacheDao cacheDao =
				new CapabilitiesCacheDao(jdbcTemplate)
			CapabilitiesCache cache =
					cacheDao.findUniqueObjectById("" + service.getId());
			// parse the document
			caps =
				GetCapabilities.parseCapabilitiesDocument(cache.getCapabilities());
			// store the capabilities object
			capabilities.put(service.getId(), caps);
		}

		// we have an offering
		if (offering != null) {

			// AND an observed property
			if (property != null) {

				log.info "Query (IRREGULAR): " + service.getEndpoint() + "; for " +
						offering + "; " + property;

				executeRule(caps, rule, service, offering, property);

			}
			// we only have an offering
			else {

				log.info "Query (IRREGULAR): " + service.getEndpoint() +
						"; for ALL properties in offering: " + offering;

			}
			
		} else {

			// there is no offering so we assume we have an
			// observed property (already verified)

			log.info "Query (IRREGULAR): " + service.getEndpoint() +
					"; for ALL offerings for observed property: " +	property;
					
			executeRule(caps, rule, service, property);

		}

	}

	/**
	 * Executes a rule that is specified on S/O/P
	 * 
	 * @param capabilities 
	 * @param rule
	 * @param service
	 * @param offering
	 * @param observedProperty
	 */
	private void executeRule(SosCapabilities capabilities, Rule rule, 
		Service service, String offering, String observedProperty)
	{

		CapabilitiesCacheDao cacheDao = new CapabilitiesCacheDao(jdbcTemplate)
		CapabilitiesCache cache =
				cacheDao.findUniqueObjectById("" + service.getId());
		SosCapabilities caps = 
			GetCapabilities.parseCapabilitiesDocument(cache.getCapabilities());

		SensorOffering sensorOffering =	caps.getOffering(offering);

		// analyze 		
		analyzeIrregularDataDelivery(rule, service, sensorOffering, observedProperty);
	}
	
	/**
	 * Executes a rule that is specified on S/P
	 *
	 * @param capabilities 
	 * @param rule
	 * @param service
	 * @param offering
	 * @param observedProperty
	 */
	private void executeRule(SosCapabilities capabilities, Rule rule, 
		Service service, String observedProperty) 
	{
		
		// find active offerings/properties pairs
		OfferingPropertiesDao dao = new OfferingPropertiesDao(jdbcTemplate);
		Collection<OfferingProperties> offerings = 
			dao.findActiveForService(service.getId());

		// go through all offerings 
		for (SensorOffering offering : capabilities.getOfferings()) {
			// if we find one with the observed property, analyze 
			if (offering.getObservedProperties().contains(observedProperty)) {
				
				// check if we really should investigate this 
				// offering/observed property pair
				boolean analyze = includeOfferingProperty(offerings, 
					offering.getGmlId(), observedProperty);
				
				// analyze
				if (analyze) {
					 
					analyzeIrregularDataDelivery(rule, service, 
						offering, observedProperty);
				}
			}
		}

	}
	
	/**
	 * Returns true if the offeringId/observedProperty pair is included in the
	 * given offeringId/observedProperty collection  
	 * 
	 * @param offerings
	 * @param offeringId
	 * @param observedProperty
	 */
	private boolean includeOfferingProperty(Collection<OfferingProperties> offerings, 
		String offeringId, String observedProperty) 
	{
		boolean foundOffering = false;
		for (OfferingProperties offeringProperty : offerings) {
			// found the offering 
			if (offeringProperty.getOffering().equals(offeringId)) {
				foundOffering = true;
				// if the observed property matches, return True 
				if (offeringProperty.getObservedProperty().equals(observedProperty))
					return true;
			} else {
				/*
				 * We know that the @{link OfferingProperties} objects
				 * are ordered by offering. So if we have found the searched
				 * for offering, but have passed it, we can stop
				 */
				if (foundOffering) {
					break;
				}
			}
			
		}
		// default 
		return false;
	}
	
	/**
	 * Analyze irregular data delivery  
	 * 
	 * @param rule
	 * @param service
	 * @param sensorOffering
	 * @param observedProperty
	 */
	private void analyzeIrregularDataDelivery(Rule rule, Service service,  
		SensorOffering sensorOffering, String observedProperty) 
	{
		
		// TODO: fix
		DateTime to = new DateTime();
		DateTime from = to.minusHours(24);
		
		ServiceResponse serviceResponse =
			getSensorData(service.getEndpoint(), sensorOffering,
				observedProperty, from, to);
			
		log.info("Yes, we got response from: " + sensorOffering.getName());
			
		if (serviceResponse.getResult().equals(Result.RESULT_OK)) {
			
			SensorData sensorData = serviceResponse.getSensorData();
			
			// TODO: do not hard code column name
			String[] data = sensorData.getData(new Field("date_time"));
			
			Set<Duration> durations = new HashSet<Duration>();
			
			// TODO: this might have to be dynamic
			String zuluFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";
			DateTimeFormatter fmt =
				DateTimeFormat.forPattern(zuluFormat);
			
			for (int j = 1; j < data.length - 1; j++) {
		
				DateTime t1 = fmt.parseDateTime(data[j - 1]);
				DateTime t2 = fmt.parseDateTime(data[j]);
		
				Duration interval = new Duration(t1, t2);
		
				durations.add(interval);
			}
		
			// remove 'zero' durations if there are any,
			// might be caused by duplicates
			durations.remove(new Duration(0, 0));
		
//			for (Duration duration : durations) {
//				log.info("Duration: " + duration.getStandardMinutes());
//			}
			
			if (durations.size() > 1) {
				
				Alert alert = new Alert();
				alert.setServiceId(service.getId());
				alert.setValidFrom(from.toDate());
				alert.setValidTo(to.toDate());
				alert.setType(AlertType.ALERT_IRREGULAR_DATA_DELIVERY.toString());
				alert.setLatitude(sensorOffering.getLowerCornerLat());
				alert.setLongitude(sensorOffering.getLowerCornerLong());
				alert.setOffering(sensorOffering.getGmlId());
				alert.setObservedProperty(observedProperty);
				alert.setDetail("Varying or irregular data delivery identified.");
		
				AlertDao alertDao = new AlertDao(jdbcTemplate);
				alertDao.insert(alert, rule);
				
				log.warn("Alert generated for " + sensorOffering.getGmlId());
			}
		
		}

	}
	
	/**
	 * Retrieves sensor data to analyze
	 *
	 * @param serviceUrl
	 * @param sensorOffering
	 * @param observedProperty
	 * @param from
	 * @param to
	 * @return
	 */
	private ServiceResponse getSensorData(String serviceUrl, SensorOffering sensorOffering,
		String observedProperty, DateTime from, DateTime to)
	{

		List<String> commonFormats =
				SosUtil.commonResponseFormats(sensorOffering);

		if (commonFormats.size() > 0) {

			GetObservationRequest request =
					new GetObservationRequest(sensorOffering, observedProperty,
					commonFormats.get(0));

			// add the time interval
			Interval interval = new Interval(from, to);
			request.addTimeInterval(TimeInterval.fromJoda(interval));

//			System.out.println("Trying interval: " + interval);

			try {

				// fetch sensor data
				SensorData sensorData =
						SosUtil.getObservationData(serviceUrl, request, 60, 60);

				// return sensor data
				ServiceResponse ret =
						new ServiceResponse(Result.RESULT_OK, sensorData);
				// set the from and to times
				ret.setFrom(from);
				ret.setTo(to);

				return ret;

			} catch (Exception e) {

			}
		}

		return null;
	}
	
	private def createJson(List<Object[]> sensorData) {

		if (sensorData.size() > 0) {

			// header information
			Object[] columnsStr = sensorData.get(0);

			// to create json object
			List<Map<String, String>> jsonColumns =
					new ArrayList<Map<String, String>>();
			// to remember column names
			List<String> columnNames = new ArrayList<String>();
			// to remember column types
			List<String> columnTypes = new ArrayList<String>();

			for (String columnStr : columnsStr) {
				Map<String, String> column =
						new HashMap<String, String>();
				if (columnStr.contains(";")) {
					String[] parts = columnStr.split(";");
					column.put("name", parts[0]);
					column.put("type", parts[1]);
					// save column name and type
					columnNames.add(parts[0]);
					columnTypes.add(parts[1]);
				} else {
					column.put("name", columnStr);
					columnNames.add(columnStr);
				}
				jsonColumns.add(column);
			}

			// to create json object
			List<Map<String, Object>> jsonData =
					new ArrayList<Map<String, Object>>();

			System.out.println("ROWS: " + sensorData.size());

			List<?> jsonRowOuter =
					new ArrayList<?>();

			// let's iterate over the actual data
			for (int i = 1; i < sensorData.size(); i++) {
				Object[] row = sensorData.get(i);

				Map<String, Object> jsonValue =
						new HashMap<String, Object>();

				List<Map<String, Object>> jsonRow =
						new ArrayList<Map<String, Object>>();

				for (int j = 0; j < row.length; j++) {

					Map<String, Object> jsonItem =
							new HashMap<String, Object>();

					// find the column type
					String name = columnNames.get(j);
					jsonItem.put("column", name);
					Object value = row[j];
					jsonItem.put("value", value);

					jsonRow.add(jsonItem);
				}

				jsonRowOuter.add(jsonRow);
			}

			def json = [
						columns : jsonColumns,
						data : jsonRowOuter
					]

			return json;
		}

		return [];
	}

	/**
	 * Counts the offerings with the given observed property for the given service
	 * 	
	 * @param serviceId
	 * @param property
	 * @return
	 */
	public int countOfferingsWithObservedProperty(int serviceId, String property) {
		
		OfferingPropertiesDao dao = new OfferingPropertiesDao(jdbcTemplate);
		return dao.countOfferingsWithObservedProperty(serviceId, property);
	}
	
	

}
