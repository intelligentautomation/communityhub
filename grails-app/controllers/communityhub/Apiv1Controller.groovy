package communityhub

import grails.converters.JSON

import com.iai.communityhub.dao.GroupDao
import com.iai.communityhub.model.Group

class Apiv1Controller {
	
	def jdbcTemplate
	
	def version = "1.0";

	/**
	 * For documentation 
	 * 
	 * @return
	 */
    def index() { 
		
	}
	
	/**
	 * Ping 
	 * 
	 * @return
	 */
	def ping() {
		def json = [ "status" : "OK" ];
		render json as JSON
	}
	
	/**
	 * Returns the groups 
	 * 
	 * @return
	 */
	def groups() {
		
		GroupDao daoGroup = new GroupDao(jdbcTemplate);
		Collection<Group> groups = daoGroup.findAll();
		
		def jsonGroups = groups.collect {
			[ id : it.id, name : it.name, description : it.description, 
			  created : it.created, createdBy : it.createdBy, admin : it.admin ]
		}
		
		def json = [ "status" : "OK", "version" : version, 
			"response": jsonGroups ]
		
		render json as JSON 
	}
}
