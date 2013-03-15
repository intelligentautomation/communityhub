/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub

/**
 * A Quartz job for checking for service down alerts
 * 
 * @author Jakob Henriksson
 *
 */
class ServiceDownJob {
	
	def rulesService 
	
	static triggers = {
		// TODO: make this configurable 
		// every 5 minutes 
		// repeatCount: -1 = execute indefinitely 
		simple name: "Service down job", 
			repeatInterval: 300000l, 
			repeatCount: -1
	}

	/**
	 * Method to execute 
	 * 
	 * @return
	 */
	def execute() {
		log.info("Executing service down job")
		
		// find all service down rules
		def rules = Rule.findAllWhere(type : AlertType.SERVICE_DOWN.toString())

		// execute the found rules 
		rulesService.executeAllRules(rules)
	}
}
