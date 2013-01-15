/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub

import com.iai.communityhub.dao.RuleDao
import com.iai.communityhub.model.Rule

class IrregularDataDeliveryJob {
	
	def rulesService
	
	def jdbcTemplate

	static triggers = {
		// every 5 minutes
		// repeatCount: -1 = execute indefinitely
		simple name: "Irregular data delivery", repeatInterval: 300000l, repeatCount: -1
		// cron: s m h D M W
		cron name: "Irregular data delivery", startDelay: 10000, cronExpression: "0 0 0 * * ?"
	}

	/**
	 * Method to execute
	 *
	 * @return
	 */
	def execute() {
		log.info("Executing irregular data delivery job");
		
		RuleDao daoRule = new RuleDao(jdbcTemplate);
		Collection<Rule> rules = daoRule.findIrregularDataDeliveryRules();

		// execute the found rules 
		rulesService.executeAllRules(rules);
	}
}
