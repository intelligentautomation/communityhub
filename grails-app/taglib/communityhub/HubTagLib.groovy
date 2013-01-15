/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub

import org.springframework.dao.DataAccessException

import com.iai.communityhub.AlertType
import com.iai.communityhub.dao.ServiceDao
import com.iai.communityhub.model.Service
import com.iai.proteus.common.Labeling


class HubTagLib {
	
	def jdbcTemplate

	/**
	 * Returns the body if the "rule" attribute (which can be a 
	 * Rule or an Alert) has a type that matches the given "type" 
	 * attribute
	 *  
	 */
	def ifAlert = { attrs, body ->
		def type = attrs.type;
		def rule = attrs.rule;
		AlertType alertType = AlertType.parse(rule.getType());
		if (alertType.equals(AlertType.ALERT_SERVICE_DOWN) && 
			type.equals("service")) {
			out << body();
		} else if (alertType.equals(AlertType.ALERT_IRREGULAR_DATA_DELIVERY) && 
			type.equals("irregular")) {
			out << body();
		}
	}
	
	/**
	 * Returns the service URL that the given Rule object specified 
	 * in the "rule" attribute is specified on 
	 *  
	 */
	def getServiceUrl = { attrs, body ->
		def rule  = attrs.rule; 
		ServiceDao dao = new ServiceDao(jdbcTemplate);
		try {
			Service service = dao.findUniqueObjectById("" + rule.getServiceId());
			out << service.getEndpoint();
		} catch (DataAccessException e) {
			log.error("Data access exception: " + e.getMessage());
		}
	}
	
	/**
	 * Returns the service title for the given service id 
	 *
	 */
	def getServiceTitle = { attrs, body ->
		def serviceId = attrs.serviceId;
		ServiceDao dao = new ServiceDao(jdbcTemplate);
		try {
			Service service = dao.findUniqueObjectById("" + serviceId);
			out << service.getTitle();
		} catch (DataAccessException e) {
			log.error("Data access exception: " + e.getMessage());
		}
	}
	
	def isThisMonth = { attrs, body -> 
		def date = attrs.date;
		if (date != null) {
			Calendar calNow = Calendar.getInstance();
			calNow.setTime(new Date());
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			if (cal.get(Calendar.YEAR) == calNow.get(Calendar.YEAR) &&
				cal.get(Calendar.MONTH) == calNow.get(Calendar.MONTH)) {
				out << body();
			}
		} else {
			log.error("Missing argument to TagLib: isThisMonth");
		}
	}
	
	def isNotThisMonth = { attrs, body ->
		def date = attrs.date;
		if (date != null) {
			Calendar calNow = Calendar.getInstance();
			calNow.setTime(new Date());
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			if (cal.get(Calendar.YEAR) != calNow.get(Calendar.YEAR) ||
				cal.get(Calendar.MONTH) != calNow.get(Calendar.MONTH)) {
				out << body();
			}
		} else {
			log.error("Missing argument to TagLib: isNotThisMonth");
		} 
	}
	
	def inFuture = { attrs, body ->
		def date = attrs.date;
		if (date != null) {
			Calendar calNow = Calendar.getInstance();
			calNow.setTime(new Date());
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			if (cal.after(calNow)) {
				out << body();
			}
		}
	}

	def notInFuture = { attrs, body ->
		def date = attrs.date;
		if (date != null) {
			Calendar calNow = Calendar.getInstance();
			calNow.setTime(new Date());
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			if (!cal.after(calNow)) {
				out << body();
			} 
		}
	}

			
	def niceProperty = { attrs, body ->
		out << Labeling.labelProperty(body().toString().trim());
	}
	
}
