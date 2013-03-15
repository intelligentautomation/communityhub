/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub

import com.iai.proteus.common.Labeling


class HubTagLib {
	
	/**
	 * Returns the body if the "rule" attribute (which can be a 
	 * Rule or an Alert) has a type that matches the given "type" 
	 * attribute
	 *  
	 */
	def ifAlert = { attrs, body ->
		def type = attrs.type
		def rule = attrs.rule
		AlertType alertType = AlertType.parse(rule.type)
		if (alertType.equals(AlertType.SERVICE_DOWN) && 
			type.equals("service")) {
			out << body()
		} else if (alertType.equals(AlertType.IRREGULAR_DELIVERY) && 
			type.equals("irregular")) {
			out << body()
		}
	}
	
	/**
	 * Returns the service title for the given service id 
	 *
	 */
	def getServiceTitle = { attrs, body ->
		def serviceId = attrs.serviceId
		def service = Service.get(serviceId)
		if (service) {
			out << service.title
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
		out << Labeling.labelProperty(body().toString().trim())
	}
		
}
