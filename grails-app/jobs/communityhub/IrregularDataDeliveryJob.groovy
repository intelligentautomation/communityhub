/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub

/**
 * A Quartz job for checking for irregular data delivery alerts
 * 
 * @author Jakob Henriksson
 *
 */
class IrregularDataDeliveryJob {
	
	def rulesService
	
	static triggers = {
		// every 5 minutes
		// repeatCount: -1 = execute indefinitely
//		simple name: "Irregular data delivery", repeatInterval: 300000l, repeatCount: -1
		// cron: s m h D M W
		cron name: "Irregular data delivery job", 
			startDelay: 10000, 
			cronExpression: "0 0 0 * * ?"
	}

	/**
	 * Method to execute
	 *
	 * @return
	 */
	def execute() {
		log.info("Executing irregular data delivery job")
		
		// find all irregular data delivery rules 
		def rules = Rule.findAllWhere(type : 
			AlertType.IRREGULAR_DELIVERY.toString())

		// execute the found rules 
		rulesService.executeAllRules(rules)
	}
}
