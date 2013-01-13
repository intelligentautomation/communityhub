package communityhub

import org.springframework.dao.DataAccessException

import com.iai.communityhub.HubUtils
import com.iai.communityhub.dao.CapabilitiesCacheDao
import com.iai.communityhub.dao.OfferingPropertiesDao
import com.iai.communityhub.dao.ServiceDao
import com.iai.communityhub.model.CapabilitiesCache
import com.iai.communityhub.model.OfferingProperties
import com.iai.communityhub.model.Service
import com.iai.proteus.common.sos.GetCapabilities
import com.iai.proteus.common.sos.model.SensorOffering
import com.iai.proteus.common.sos.model.SosCapabilities
import com.iai.proteus.common.sos.util.SosUtil

class ServiceService {

	def jdbcTemplate
	
	def addService(String type, String url) {
		
		if (url.trim().equals("")) {
			def error = "No service URL was provided, please try again";
			return [url : url, type : type, error : error]
		}
		
		Service service = new Service();
		service.setEndpoint(url.trim());
		service.setType(type);
		
		ServiceDao daoService = new ServiceDao(jdbcTemplate);
		
		// check if service already exists
		if (daoService.serviceExists(service)) {
			def error = "Service already exists";
			return [ url: url, type : type, error : error ]
		}
		
		// download Capabilities document
		String capabilities = HubUtils.getCapabilitiesDocument(service);
		if (capabilities != null) {
			
			// parse the capabilities document
			SosCapabilities sosCapabilities =
				GetCapabilities.parseCapabilitiesDocument(capabilities);

			// extract and set title
			String title = SosUtil.getServiceTitle(sosCapabilities);
			if (title != null)
				service.setTitle(title);
			else
				service.setTitle("Untitled " + type);
			
			// add the service
			// (AND update the object with the ID from the database,
			//  needed below)
			service = daoService.insert(service);
							
			// store Capabilities document in cache
			CapabilitiesCacheDao daoCache = new CapabilitiesCacheDao(jdbcTemplate);
			CapabilitiesCache cache = new CapabilitiesCache();
			cache.setService(service.getId());
			cache.setCapabilities(capabilities);
			daoCache.insert(cache);
			
			// populate offerings_properties table
			OfferingPropertiesDao daoOfferingsProperties =
				new OfferingPropertiesDao(jdbcTemplate);
				
			for (SensorOffering sensorOffering : sosCapabilities.getOfferings()) {
				for (String property : sensorOffering.getObservedProperties()) {
					
					OfferingProperties op = new OfferingProperties();
					op.setServiceId(service.getId());
					op.setOffering(sensorOffering.getGmlId());
					op.setObservedProperty(property);
					
					daoOfferingsProperties.insert(op);
				}
			}

			return service;	
		}
		
		return null;
	}
	
	/**
	 * Find bounding boxes for the service with the given id
	 * 
	 * @param id
	 * @return
	 */
    def Map<String, Object> findBoundingBoxesForService(int id) {
		
		try {
		
			CapabilitiesCacheDao daoCache =
				new CapabilitiesCacheDao(jdbcTemplate);
			CapabilitiesCache cache = daoCache.findForServiceId(id);
		
			SosCapabilities capabilities =
				GetCapabilities.parseCapabilitiesDocument(cache.getCapabilities());
				
			Map<String, Object> locations =
				getBoundingBoxes(capabilities);
				
			return locations;
			
		} catch (DataAccessException e) {
			log.error("Data access exception: " + e.getMessage());
		}
		
		return null;
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
		Map<String, Object> result = new HashMap<String, Object>();

		for (SensorOffering offering: capabilities.getOfferings()) {
			Map<String, Double> bbox = new HashMap<String, Double>();
			bbox.put("upper-lat", offering.getUpperCornerLat());
			bbox.put("upper-lon", offering.getUpperCornerLong());
			bbox.put("lower-lat", offering.getLowerCornerLat());
			bbox.put("lower-lon", offering.getLowerCornerLong());
			result.put(offering.getGmlId(), bbox);
		}

		return result;
	}

}
