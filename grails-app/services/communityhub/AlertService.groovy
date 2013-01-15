/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub

import org.springframework.dao.DataAccessException

import com.iai.communityhub.dao.CapabilitiesCacheDao
import com.iai.communityhub.dao.ServiceDao
import com.iai.communityhub.model.Alert
import com.iai.communityhub.model.CapabilitiesCache
import com.iai.communityhub.model.Service
import com.iai.proteus.common.TimeUtils
import com.iai.proteus.common.sos.GetCapabilities
import com.iai.proteus.common.sos.data.Field
import com.iai.proteus.common.sos.data.SensorData
import com.iai.proteus.common.sos.model.GetObservationRequest
import com.iai.proteus.common.sos.model.SensorOffering
import com.iai.proteus.common.sos.model.SosCapabilities
import com.iai.proteus.common.sos.model.TimeInterval
import com.iai.proteus.common.sos.util.SosUtil


class AlertService {
	
	def jdbcTemplate

	/**
	 * Tries to retrieve the sensor data for the given alert 
	 * 
	 * @param alert
	 * @return
	 */
    def getSensorData(Alert alert) {
		
		String offering = alert.getOffering();
		String observedProperty = alert.getObservedProperty();
		
		Date from = alert.getValidFrom();
		Date to = alert.getValidTo();
		
		try {
			
			// get capabilities 
			CapabilitiesCacheDao daoCache =
					new CapabilitiesCacheDao(jdbcTemplate);
			CapabilitiesCache cache = daoCache.findForServiceId(alert.getServiceId());

			SosCapabilities capabilities =
				GetCapabilities.parseCapabilitiesDocument(cache.getCapabilities());
			
			// get service 
			ServiceDao daoService = new ServiceDao(jdbcTemplate);
			Service service =
				daoService.findUniqueObjectById("" + alert.getServiceId());

			// get sensor offering 
			SensorOffering sensorOffering = capabilities.getOfferingById(offering);
			
			List<String> commonFormats =
				SosUtil.commonResponseFormats(sensorOffering);
				
			if (commonFormats.isEmpty()) {
				return [ status : "KO", message: "No supported response format" ];
			}
			
			GetObservationRequest req =
				new GetObservationRequest(sensorOffering, observedProperty,
						commonFormats.get(0));

			// add the time interval
			TimeInterval timeInterval = new TimeInterval(from, to);
			req.addTimeInterval(timeInterval);
			
			// fetch sensor data
			SensorData sensorData =
					SosUtil.getObservationData(service.getEndpoint(), req, 60, 60);

			// TODO: do not hard code 	
			List<String[]> timestamps = 
				sensorData.getData([new Field("date_time")]);
			
			return timestamps.collect { 
				TimeUtils.parseDefault(it[0]).getTime()
			}; 
				
		} catch (DataAccessException e) {
			log.error("Data access exception: " + e.getMessage()); 
		} catch (Exception e) {
			log.error("Exception while fetching sensor data: " + e.getMessage());
		}
		
		return [ status : "KO", message : "Unknown error" ];
    }
	
}
