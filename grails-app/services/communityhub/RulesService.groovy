/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub

import org.codehaus.groovy.grails.web.json.JSONObject
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.Interval
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

import com.iai.communityhub.HubUtils
import com.iai.communityhub.Result
import com.iai.communityhub.ServiceResponse
import com.iai.proteus.common.sos.GetCapabilities
import com.iai.proteus.common.sos.data.Field
import com.iai.proteus.common.sos.data.SensorData
import com.iai.proteus.common.sos.model.GetObservationRequest
import com.iai.proteus.common.sos.model.SensorOffering
import com.iai.proteus.common.sos.model.SosCapabilities
import com.iai.proteus.common.sos.model.TimeInterval
import com.iai.proteus.common.sos.util.SosUtil

class RulesService {
	
	/**
	 * Finds the rule for a "service down" alert for a given service, 
	 * or creates one if needed 
	 * 
	 * @param service
	 * @return
	 */
	Rule getServiceDownRule(Service service) {
		// find or create the rule 
		// TODO: use enumeration type instead of string 
		def rule = Rule.findOrCreateWhere(service: service, 
				type : AlertType.SERVICE_DOWN.toString())
		rule.save()
		return rule
	}
	
	/**
	 * Finds the rule for an "irregular data delivery" alert for a 
	 * given service, offering and property, or creates one if needed
	 *
	 * @param service
	 * @param offering
	 * @param observedProperty
	 * @param options 
	 * @return
	 */
	Rule getIrregularDataDeliveryRule(Service service, 
			String offering, String observedProperty, JSONObject options) {
		
		boolean onlyProperties = options.get('add-all-properties')
			
		// determine if we should query for rules that include the offering, 
		// or only rules that are specified across observed properties  
		String offeringToQueryFor = onlyProperties ? null : offering
		
		// TODO: use enumeration type instead of string		
		def rule = Rule.findWhere(service: service, 
			offering: offeringToQueryFor, 
			observedProperty: observedProperty, 
			type: AlertType.IRREGULAR_DELIVERY.toString())
		
		// if a rule is found, we return it
		if (rule)
			return rule
		
		// if no rule is found, create rule for the given service
		rule = new Rule()
		rule.service = service
		rule.type = AlertType.IRREGULAR_DELIVERY.toString()
		// only add the offering if the rule is specified on S/O/P, 
		// but skip it if the rule is specified on S/P
		if (!onlyProperties) 
			rule.offering = offering
		rule.observedProperty = observedProperty
		rule.save(flush: true)
				
		return rule
	}

	/**
	 * Executes a collection of rules
	 *
	 * @param rules
	 */
	public void executeAllRules(Collection<Rule> rules) {

		// storing capabilities used for rules 
		Map<Integer, SosCapabilities> capabilities = 
			new HashMap<Integer, SosCapabilities>()
			
		log.info("Executing " + rules.size() + " rules")
		
		// iterate through all rules 
		for (Rule rule : rules) { 
		
			def alertType = AlertType.parse(rule.type)
			switch (alertType) {
	
				case AlertType.SERVICE_DOWN:
				
					executeServiceDownRule(rule)
	
					break
	
				case AlertType.IRREGULAR_DELIVERY:
				
					executeIrregularDataDeliveryRule(rule, capabilities)
	
					break
	
				case AlertType.CUSTOM:
	
					log.error("Not implemented yet.")
	
					break
	
				default:
					log.error("Did not understand rule type")
			}
		}
	}
	
	
	/**
	 * Executes a service down rule 
	 * 
	 * @param rule
	 */
	void executeServiceDownRule(Rule rule) {
		
		// find the service associated with the rule
		Service service = rule.service
		
		log.info("Executing rule: " + rule.id)
		
		// try and fetch the capabilities document
		String capabilities = 
			HubUtils.getCapabilitiesDocument(service.getEndpoint())

		// only store Capabilities documents, not exceptions 
		boolean success = false
		if (capabilities != null && !capabilities.contains("ExceptionReport")) {
			success = true
		}
		
		// if we were able to contact the service, update the capabilities
		// cache and update aliveness flag on service 
		if (success) {
		
			// create capabilities cache object
			CapabilitiesCache cache = CapabilitiesCache.findByService(service)
			if (!cache) {
				// create a new object if it did not exist
				cache = new CapabilitiesCache()
				cache.service = service
			}
			// set or update capabilities cache
			cache.capabilities = capabilities
			cache.save(flush: true)
			
			log.info("Successfully fetched Capabilities document from: " +
				service.getEndpoint())
			
			service.alive = true
			service.save(insert: false)
			
		} 
		// if we could not contact the service, update the service object and 
		// then generate an alert 
		else {
			
			println "Will generate alert..."
		
			// update service status
			service.alive = false
			service.save(flush: true, insert: false)
			
			println "Saved service..."
		
			// crate the alert
			def alert = new Alert()
			alert.service = service
			alert.type = rule.type
			// valid from NOW
			Date now = new Date()
			alert.validFrom = now
			// valid to now + 5 minutes 
			alert.validTo = HubUtils.addTimeToDate(now, Calendar.MINUTE, 5)
			// alert details 
			alert.detail = "We were not able to fetch the " +
				service.getType() + " Capabilities " + 
				"document from service endpoint " + service.getEndpoint()
			if (!alert.save(flush: true)) {
				alert.errors.each {
					println it 
				}
			}
//			alert.save(flush: true)
			
			println "Created an alert..."

			// associate the generated alert with the appropriate groups 
			associateAlertWithGroups(alert, rule)
			
			log.info("Generated service down alert for rule ID: " + rule.id)
		}
	}
	
	/**
	 * Executes an irregular data delivery rule 
	 * 
	 * @param rule
	 * @param capabilities Maps service IDs with parsed SosCapabilities objects 
	 */
	void executeIrregularDataDeliveryRule(Rule rule, Map<Integer, SosCapabilities> capabilities) 
	{
		
		// offering IDs and property IDs are optional
		// (but there must be at least one)
		String offering = rule.getOffering()
		String property = rule.getObservedProperty()

		if (offering == null && property == null) {
			log.error("Does not know how to handle rule, " +
					"missing both offeringId and observed property, " +
					"skipping.")
			return
		}

		// get the service object
		Service service = rule.service

		// get the capabilities object
		SosCapabilities caps = null
		if (capabilities.containsKey(service.id)) {
			// get already parsed Capabilities
			caps = capabilities.get(service.id)
		} else {
			CapabilitiesCache cache = CapabilitiesCache.findByService(service)
			// parse the document
			caps =
				GetCapabilities.parseCapabilitiesDocument(cache.getCapabilities())
			// store the capabilities object
			capabilities.put(service.id, caps)
		}

		// we have an offering
		if (offering != null) {

			// AND an observed property
			if (property != null) {

				log.info "Query (IRREGULAR): " + service.getEndpoint() + "; for " +
						offering + "; " + property

				executeRule(caps, rule, service, offering, property)

			}
			// we only have an offering
			else {

				log.info "Query (IRREGULAR): " + service.getEndpoint() +
						"; for ALL properties in offering: " + offering

				// TODO: implement 
			}
			
		} else {

			// there is no offering so we assume we have an
			// observed property (already verified)

			log.info "Query (IRREGULAR): " + service.getEndpoint() +
					"; for ALL offerings for observed property: " +	property
					
			executeRule(caps, rule, service, property)

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
	void executeRule(SosCapabilities capabilities, Rule rule, 
		Service service, String offering, String observedProperty)
	{

		SensorOffering sensorOffering =	capabilities.getOfferingById(offering)

		// analyze 		
		analyzeIrregularDataDelivery(rule, service, sensorOffering, observedProperty)
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
	void executeRule(SosCapabilities capabilities, Rule rule, 
		Service service, String observedProperty) 
	{
		
		// find active offerings/properties pairs
		def offerings = 
			OfferingProperties.findAllWhere(service: service, active: true) {
				sort: "offering" 
			}

		// go through all offerings 
		for (SensorOffering offering : capabilities.getOfferings()) {
			// if we find one with the observed property, analyze 
			if (offering.observedProperties.contains(observedProperty)) {
				
				// check if we really should investigate this 
				// offering/observed property pair
				boolean analyze = includeOfferingProperty(offerings, 
					offering.getGmlId(), observedProperty)
				
				// analyze
				if (analyze) {
					 
					analyzeIrregularDataDelivery(rule, service, 
						offering, observedProperty)
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
	boolean includeOfferingProperty(Collection<OfferingProperties> offerings, 
		String offeringId, String observedProperty) 
	{
		boolean foundOffering = false
		for (OfferingProperties offeringProperty : offerings) {
			// found the offering 
			if (offeringProperty.offering.equals(offeringId)) {
				foundOffering = true
				// if the observed property matches, return True 
				if (offeringProperty.observedProperty.equals(observedProperty))
					return true
			} else {
				/*
				 * We know that the @{link OfferingProperties} objects
				 * are ordered by offering. So if we have found the searched
				 * for offering, but have passed it, we can stop
				 */
				if (foundOffering) {
					break
				}
			}
			
		}
		// default 
		return false
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
		
		// TODO: update 
		
		// TODO: fix
		DateTime to = new DateTime()
		DateTime from = to.minusHours(24)
		
		ServiceResponse serviceResponse =
			getSensorData(service.getEndpoint(), sensorOffering,
				observedProperty, from, to)
			
		log.info("Yes, we got response from: " + sensorOffering.getName())
			
		if (serviceResponse.getResult().equals(Result.RESULT_OK)) {
			
			SensorData sensorData = serviceResponse.getSensorData()
			
			// TODO: do not hard code column name
			String[] data = sensorData.getData(new Field("date_time"))
			
			Set<Duration> durations = new HashSet<Duration>()
			
			// TODO: this might have to be dynamic
			String zuluFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'"
			DateTimeFormatter fmt =	DateTimeFormat.forPattern(zuluFormat)
			
			for (int j = 1; j < data.length - 1; j++) {
		
				DateTime t1 = fmt.parseDateTime(data[j - 1])
				DateTime t2 = fmt.parseDateTime(data[j])
		
				Duration interval = new Duration(t1, t2)
		
				durations.add(interval)
			}
		
			// remove 'zero' durations if there are any,
			// might be caused by duplicates
			durations.remove(new Duration(0, 0))
		
//			for (Duration duration : durations) {
//				log.info("Duration: " + duration.getStandardMinutes());
//			}
			
			if (durations.size() > 1) {
				
				Alert alert = new Alert()
				alert.service = service
				alert.validFrom = from.toDate()
				alert.validTo to.toDate()
				alert.type = AlertType.IRREGULAR_DELIVERY.toString()
				alert.latLower = sensorOffering.getLowerCornerLat()
				alert.latUpper = sensorOffering.getUpperCornerLat()
				alert.lonLower = sensorOffering.getLowerCornerLong()
				alert.lonUpper = sensorOffering.getUpperCornerLong()
				alert.offering = sensorOffering.getGmlId()
				alert.observedProperty = observedProperty
				alert.detail = "Varying or irregular data delivery identified"
				alert.save(flush: true)
				
				// associate the generated alert with the appropriate groups  			
				associateAlertWithGroups(alert, rule)
				
				log.warn("Alert generated for " + sensorOffering.getGmlId())
			}
		
		}

	}
	
	/**
	 * Associates the alert with the groups that are setup with the given 
	 * rule 
	 * 
	 * @param alert
	 * @param rule
	 */
	void associateAlertWithGroups(Alert alert, Rule rule) {

		// find the groups that are associated with the given rule
		def groups = Group.withCriteria {
			rules {
				eq('id', rule.id)
			}
		}
				
		// associate the alert with each found group
		groups.each { it.addToAlerts(alert) }
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
	ServiceResponse getSensorData(String serviceUrl, SensorOffering sensorOffering,
		String observedProperty, DateTime from, DateTime to)
	{

		List<String> commonFormats =
				SosUtil.commonResponseFormats(sensorOffering)

		if (commonFormats.size() > 0) {

			GetObservationRequest request =
					new GetObservationRequest(sensorOffering, observedProperty,
					commonFormats.get(0))

			// add the time interval
			Interval interval = new Interval(from, to)
			request.addTimeInterval(TimeInterval.fromJoda(interval))

//			System.out.println("Trying interval: " + interval);

			try {

				// fetch sensor data
				SensorData sensorData =
						SosUtil.getObservationData(serviceUrl, request, 60, 60)

				// return sensor data
				ServiceResponse ret =
						new ServiceResponse(Result.RESULT_OK, sensorData)
				// set the from and to times
				ret.setFrom(from)
				ret.setTo(to)

				return ret

			} catch (Exception e) {

			}
		}

		// default 
		return null
	}
	
//	private def createJson(List<Object[]> sensorData) {
//
//		if (sensorData.size() > 0) {
//
//			// header information
//			Object[] columnsStr = sensorData.get(0);
//
//			// to create json object
//			List<Map<String, String>> jsonColumns =
//					new ArrayList<Map<String, String>>();
//			// to remember column names
//			List<String> columnNames = new ArrayList<String>();
//			// to remember column types
//			List<String> columnTypes = new ArrayList<String>();
//
//			for (String columnStr : columnsStr) {
//				Map<String, String> column =
//						new HashMap<String, String>();
//				if (columnStr.contains(";")) {
//					String[] parts = columnStr.split(";");
//					column.put("name", parts[0]);
//					column.put("type", parts[1]);
//					// save column name and type
//					columnNames.add(parts[0]);
//					columnTypes.add(parts[1]);
//				} else {
//					column.put("name", columnStr);
//					columnNames.add(columnStr);
//				}
//				jsonColumns.add(column);
//			}
//
//			// to create json object
//			List<Map<String, Object>> jsonData =
//					new ArrayList<Map<String, Object>>();
//
//			System.out.println("ROWS: " + sensorData.size());
//
//			List<?> jsonRowOuter =
//					new ArrayList<?>();
//
//			// let's iterate over the actual data
//			for (int i = 1; i < sensorData.size(); i++) {
//				Object[] row = sensorData.get(i);
//
//				Map<String, Object> jsonValue =
//						new HashMap<String, Object>();
//
//				List<Map<String, Object>> jsonRow =
//						new ArrayList<Map<String, Object>>();
//
//				for (int j = 0; j < row.length; j++) {
//
//					Map<String, Object> jsonItem =
//							new HashMap<String, Object>();
//
//					// find the column type
//					String name = columnNames.get(j);
//					jsonItem.put("column", name);
//					Object value = row[j];
//					jsonItem.put("value", value);
//
//					jsonRow.add(jsonItem);
//				}
//
//				jsonRowOuter.add(jsonRow);
//			}
//
//			def json = [
//						columns : jsonColumns,
//						data : jsonRowOuter
//					]
//
//			return json;
//		}
//
//		return [];
//	}

	/**
	 * Counts the offerings with the given observed property for the given service
	 * 	
	 * @param serviceId
	 * @param property
	 * @return
	 */
//	public int countOfferingsWithObservedProperty(int serviceId, String property) {
//		
//		OfferingPropertiesDao dao = new OfferingPropertiesDao(jdbcTemplate);
//		return dao.countOfferingsWithObservedProperty(serviceId, property);
//	}
}
