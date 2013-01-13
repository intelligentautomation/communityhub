package communityhub

import grails.converters.JSON
import grails.plugins.springsecurity.Secured

import org.springframework.dao.DataAccessException

import com.iai.communityhub.HubUtils
import com.iai.communityhub.dao.CapabilitiesCacheDao
import com.iai.communityhub.dao.OfferingPropertiesDao
import com.iai.communityhub.dao.ServiceDao
import com.iai.communityhub.model.CapabilitiesCache
import com.iai.communityhub.model.OfferingProperties
import com.iai.communityhub.model.Service
import com.iai.communityhub.paging.Paginator

class ServiceController {
	
	def jdbcTemplate
	
	def serviceService

	/**
	 * List all services 
	 * 
	 * @return
	 */
	@Secured(['ROLE_ADMIN'])
	def index() {
		
		// get the requested page, default to 1 if there is a problem
		Integer page = params.int('page')
		if (!page)
			page = 1;
			
		def max = 5;
		
		ServiceDao dao = new ServiceDao(jdbcTemplate);
		def paginator = new Paginator<Service>(dao, max);
		
		try {
			paginator.setCurPage(page);
		} catch (IllegalArgumentException e) {
			log.error("Illegal argument: " + e.getMessage());
			paginator.setCurPage(paginator.numPages);
		}
		
		return [ paginator : paginator ]
	}
	
	/**
	 * Show details of a service 
	 * 
	 * @param id
	 * @return
	 */
	@Secured(['ROLE_ADMIN'])
	def view(int id) {
		
		if (id > 0) {
			
			try {
				
				ServiceDao daoService = new ServiceDao(jdbcTemplate);
				Service service = daoService.findUniqueObjectById("" + id);
			
				return [service : service];
				
			} catch (DataAccessException e) {
				log.error("Data access exception: " + e.getMessage());
			}
			
		}
		
		response.sendError(404);
	}
	
	/**
	 * Handles fetching of bounding boxes of offerings
	 *
	 * @return
	 */
	def ajax_bboxes(int id) {

		if (id > 0) {
			
			Map<String, Object> locations = 
				serviceService.findBoundingBoxesForService(id);
				
			if (locations != null) {
				
				def json = [ 
					"status" : "OK",
					"message" : "Success", 
					"data" : locations
				]

				render json as JSON
				return
			} 					
		}
		
		println "CAN"
		
		response.sendError(404);		
	}
		

	/**
	 * Add a new service 
	 *
	 * @return
	 */
	@Secured(['ROLE_ADMIN'])
	def add() {
		
		if (request.method == "POST") {
			
			// get the parameters
			def url = params.inputUrl;
			def type = params.inputServiceType;

			Service service = serviceService.addService(type, url);
			if (service != null) {
				def success = "The service was successfully added";
				return [url : url, type: type, title : service.getTitle(), success : success ];
			}
			
			def error = "There was an error fetching the capabilities document";
			return [url : url, type: type, error : error ];
		}

		def error = "Request method not supported";
		return [ error : error ]		
	}
	
	/**
	 * Removes a service
	 *
	 * @param id service ID
	 * @return
	 */
	@Secured(['ROLE_ADMIN'])
	def remove(int id) {
		
		if (id > 0) {
			
			System.out.println("OK, let's do it");
			
			ServiceDao daoService = new ServiceDao(jdbcTemplate);
			try {
				Service service = daoService.findUniqueObjectById("" + id);
				System.out.println("Service: " + service);
				// de-activate the service 
				int res = daoService.setActiveStatus(service, false);
				if (res > 0) {
					// return success
					([status: "OK"] as JSON).render response
					return;
				}
			} catch (DataAccessException e) {
				log.error("Could not find service with id: " + id);
			}
		}
		
		([status: "KO"] as JSON).render response
	}
}
