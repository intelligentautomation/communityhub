/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub

import com.iai.proteus.common.TimeUtils
import com.iai.proteus.common.sos.GetCapabilities
import com.iai.proteus.common.sos.data.Field
import com.iai.proteus.common.sos.data.SensorData
import com.iai.proteus.common.sos.model.GetObservationRequest
import com.iai.proteus.common.sos.model.SensorOffering
import com.iai.proteus.common.sos.model.SosCapabilities
import com.iai.proteus.common.sos.model.TimeInterval
import com.iai.proteus.common.sos.util.SosUtil


/**
 * Service class for Alerts
 * 
 * @author Jakob Henriksson
 *
 */
class AlertService {
	

	/**
	 * Tries to retrieve the sensor data for the given alert 
	 * 
	 * @param alert
	 * @return
	 */
    def getSensorData(Alert alert) {
		
		String offering = alert.getOffering()
		String observedProperty = alert.getObservedProperty()
		
		Date from = alert.getValidFrom()
		Date to = alert.getValidTo()
		
		Service service = alert.getService()
		
		// get capabilities from cache 
		CapabilitiesCache cache = CapabilitiesCache.findByService(service)
		if (cache) {

			SosCapabilities capabilities =
					GetCapabilities.parseCapabilitiesDocument(cache.getCapabilities())
			
			// get sensor offering
			SensorOffering sensorOffering = 
				capabilities.getOfferingById(offering)

			// find common formats 
			List<String> commonFormats =
					SosUtil.commonResponseFormats(sensorOffering)

			// return error if there are no common formats 
			if (commonFormats.isEmpty()) {
				return [ status : "KO", 
					message: "No supported response format" ]
			}

			GetObservationRequest req =
					new GetObservationRequest(sensorOffering, 
						observedProperty, commonFormats.get(0))

			// add the time interval
			TimeInterval timeInterval = new TimeInterval(from, to)
			req.addTimeInterval(timeInterval)

			// fetch sensor data
			SensorData sensorData =
					SosUtil.getObservationData(service.getEndpoint(), 
						req, 60, 60)

			// TODO: do not hard code
			List<String[]> timestamps =
					sensorData.getData([new Field("date_time")])

			// collect array of time stamps  
			def timestampsData = timestamps.collect {
				TimeUtils.parseDefault(it[0]).getTime()
			}
			
			// return response 
			return [ status : "OK", data: timestampsData ]
			
		} else {
			// TODO: handle case where we do not have a cache 
		}
		
		// default error 
		return [ status : "KO", message : "Unknown error" ]
    }
	
}
