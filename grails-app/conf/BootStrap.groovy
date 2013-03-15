import org.apache.commons.lang.StringUtils

import communityhub.security.SecRole
import communityhub.security.SecUser
import communityhub.security.SecUserSecRole

class BootStrap {

	def jdbcTemplate
	
	def springSecurityService
	
    def init = { servletContext ->
		
		String.metaClass.intro = { len ->
			return StringUtils.abbreviate(delegate, len) ?: ''
		}

		GString.metaClass.intro = { len ->
			return StringUtils.abbreviate(delegate.toString(), len)
		}
		
		/*
		 * Create tables if needed 
		 * 
		 */
//		new CapabilitiesCacheDao(jdbcTemplate).create();
//		new GroupDao(jdbcTemplate).create();
//		new GroupsAlertsXrefDao(jdbcTemplate).create();
//		new GroupsRulesXrefDao(jdbcTemplate).create();
//		new AlertDao(jdbcTemplate).create();
//		new OfferingPropertiesDao(jdbcTemplate).create();
//		new RuleDao(jdbcTemplate).create();
//		new ServiceDao(jdbcTemplate).create();
		
		/*
		 * Create users 
		 */
		
		def userRole = SecRole.findByAuthority('ROLE_USER') ?: new SecRole(authority: 'ROLE_USER').save(failOnError: true)
		def adminRole = SecRole.findByAuthority('ROLE_ADMIN') ?: new SecRole(authority: 'ROLE_ADMIN').save(failOnError: true)
		
		// admin user 
		def adminUser = SecUser.findByUsername('admin') ?: new SecUser(
				username: 'admin',
				password: 'admin',
				enabled: true).save(failOnError: true)

		// demo user: demo
		def demoUser = SecUser.findByUsername('demo') ?: new SecUser(
			username: 'demo',
			password: 'demo',
			enabled: true).save(failOnError: true)

		// demo user: ioos 
		def ioosUser = SecUser.findByUsername('ioos') ?: new SecUser(
			username: 'ioos',
			password: 'ioos',
			enabled: true).save(failOnError: true)
	
		// the admin user belongs to the admin role
		if (!adminUser.authorities.contains(adminRole)) {
			SecUserSecRole.create adminUser, adminRole
		}

		// the demo user(s) belongs to the user role
		if (!demoUser.authorities.contains(userRole)) {
			SecUserSecRole.create demoUser, userRole
		}
		if (!ioosUser.authorities.contains(userRole)) {
			SecUserSecRole.create ioosUser, userRole
		}

    }
    def destroy = {
    }
}
