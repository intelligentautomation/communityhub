/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub

import com.iai.communityhub.dao.RuleDao
import com.iai.communityhub.model.Rule

class ServiceDownJob {
	
	def rulesService 
	
	def jdbcTemplate

	static triggers = {
		// every 5 minutes 
		// repeatCount: -1 = execute indefinitely 
		simple name: "Service down", repeatInterval: 300000l, repeatCount: -1
	}

	/**
	 * Method to execute 
	 * 
	 * @return
	 */
	def execute() {
		log.info("Executing service down job");
		
		RuleDao daoRule = new RuleDao(jdbcTemplate);
		Collection<Rule> rules = daoRule.findServiceDownRules();

		// execute the found rules 
		rulesService.executeAllRules(rules);
	}
}
