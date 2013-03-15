/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub

import com.iai.communityhub.HubUtils
import com.iai.proteus.common.sos.GetCapabilities
import com.iai.proteus.common.sos.model.SensorOffering
import com.iai.proteus.common.sos.model.SosCapabilities
import com.iai.proteus.common.sos.util.SosUtil

/**
 * Service for service (hmmm...) 
 * 
 * @author Jakob Henriksson
 *
 */
class ServiceService {

	/**
	 * Adds a service 
	 * 
	 * @param type
	 * @param url
	 * @return
	 */
	def addService(String type, String url) {
		
		// try and download Capabilities document
		String capabilities = HubUtils.getCapabilitiesDocument(url.trim())
		if (capabilities != null) {
			
			// create the service object 
			def service = new Service()
			service.endpoint = url.trim()
			service.type = type
			service.alive = true
		
			// parse the capabilities document
			SosCapabilities sosCapabilities =
				GetCapabilities.parseCapabilitiesDocument(capabilities)

			// extract and set title if we can (otherwise default will be used)
			String title = SosUtil.getServiceTitle(sosCapabilities)
			if (title != null)
				service.title = title
				
			// save service object
			service.save(flush:true)
			
			// create and save Capabilities document in cache
			def cache = new CapabilitiesCache()
			cache.service = service
			cache.capabilities = capabilities
			cache.save()
			
			// populate offering-properties 
			for (SensorOffering sensorOffering : sosCapabilities.getOfferings()) {
				for (String property : sensorOffering.getObservedProperties()) {
					// create object 
					OfferingProperties op = new OfferingProperties()
					op.service = service
					op.offering = sensorOffering.getGmlId()
					op.observedProperty = property
					// save the object 
					op.save()
				}
			}

			// return the service 
			return service
		}
		
		// default 
		return null
	}
	
	/**
	 * Find bounding boxes for the service with the given id
	 * 
	 * @param id
	 * @return
	 */
    def Map<String, Object> findBoundingBoxesForService(int id) {

		// find the service 
		Service service = Service.get(id)
		if (service != null) {

			// find the cached capabilities document 	
			CapabilitiesCache cache = CapabilitiesCache.findByService(service)
			if (cache != null) {
				// parse the cached capabilities document 
				SosCapabilities capabilities =
					GetCapabilities.parseCapabilitiesDocument(cache.getCapabilities())
				// return the bounding boxes 
				Map<String, Object> locations = getBoundingBoxes(capabilities)
				
				return locations
			}
		}

		// default 					
		return null
    }
	
	/**
	 * Finds and returns the bounding boxes of all offerings that have
	 * bounding boxes
	 *
	 * @param capabilities
	 * @return
	 */
	private Map<String, Object> getBoundingBoxes(SosCapabilities capabilities) {
		/// Offering Id -> Object (containing bounding box)
		Map<String, Object> result = new HashMap<String, Object>()

		for (SensorOffering offering: capabilities.getOfferings()) {
			Map<String, Double> bbox = new HashMap<String, Double>()
			bbox.put("upper-lat", offering.getUpperCornerLat())
			bbox.put("upper-lon", offering.getUpperCornerLong())
			bbox.put("lower-lat", offering.getLowerCornerLat())
			bbox.put("lower-lon", offering.getLowerCornerLong())
			result.put(offering.getGmlId(), bbox)
		}

		return result
	}

}
