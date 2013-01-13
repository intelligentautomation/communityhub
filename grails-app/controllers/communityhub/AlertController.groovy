package communityhub

import grails.converters.JSON

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

import org.springframework.dao.DataAccessException

import com.iai.communityhub.AlertType
import com.iai.communityhub.dao.GroupDao
import com.iai.communityhub.dao.AlertDao
import com.iai.communityhub.dao.ServiceDao
import com.iai.communityhub.model.Alert
import com.iai.communityhub.model.Group
import com.iai.communityhub.model.Service
import com.iai.communityhub.paging.Paginator


class AlertController {

	def alertService 
	
	def jdbcTemplate
	
	private DateFormat formatYear = new SimpleDateFormat("yyyy");
	private DateFormat formatMonth = new SimpleDateFormat("MMM");
	
	/**
	 * List recent alerts
	 * 
	 * @param id
	 * @return
	 */
    def index(int id) { 
		
		AlertDao dao = new AlertDao(jdbcTemplate);
		Collection<Alert> alerts = dao.findRecentAlerts(5);
				
		return [id : id, alerts : alerts]
	}

	/**
	 * List alerts for a given group 
	 * 
	 * @param id
	 * @return
	 */
	def view(int id) {
		
		GroupDao daoGroup = new GroupDao(jdbcTemplate);
		Collection<Group> groups = daoGroup.findAll();
		
		// if no group is selected, but there are groups, select the first one
		// by default
		if (id == 0 && groups != null && groups.size() > 0) {
			id = ((Group)groups.iterator().next()).getId();
		}
		
		if (id > 0) {
			
			// get the requested page, default to 1 if there is a problem
			Integer page = params.int('page')
			if (!page)
				page = 1;
				
			// fetch the current group
			try {
				
				Group group = daoGroup.findUniqueObjectById("" + id);
			
				def max = 5;
				
				AlertDao dao = new AlertDao(jdbcTemplate, group);
				def paginator = new Paginator<Alert>(dao, max);
				
				try {
					paginator.setCurPage(page);
				} catch (IllegalArgumentException e) {
					log.error("Illegal argument: " + e.getMessage());
					paginator.setCurPage(paginator.numPages);
				}
				
				return [ id: id, groups: groups, group: group,
					paginator : paginator ]

			} catch (DataAccessException e) {
				log.error("Data access exception: " + e.getMessage());
			}

		}
		
		return [ id: id, groups : groups]
	}
	
	/**
	 * Show details about a particular alert
	 *
	 * @param id
	 * @return
	 */
	def id(int id) {
		
		AlertDao daoAlert = new AlertDao(jdbcTemplate);
		
		if (id > 0) {
			try {
				Alert alert = daoAlert.findUniqueObjectById("" + id);
				
				ServiceDao daoService = new ServiceDao(jdbcTemplate);
				Service service = 
					daoService.findUniqueObjectById("" + alert.getServiceId());
				
				return [id : id, alert : alert, service : service]
				
			} catch (DataAccessException e) {
				log.error("Data access exception: " + e.getMessage());
			}
		}
		
		response.sendError(404);
	}
		
	def ajaxGetSensorData = {
		
		def alertId = params.int('alert_id');
		
		if (alertId != null) {

			AlertDao daoAlert = new AlertDao(jdbcTemplate);
			
			try {
				
				Alert alert = daoAlert.findUniqueObjectById("" + alertId);
				
				def res = alertService.getSensorData(alert);
				
				(res as JSON).render response 
				
			} catch (DataAccessException e) {
				log.error("Data access exception: " + e.getMessage());
			}
		}
		
		([ status : "KO", message : "Unkonwn error"] as JSON).render response
	}
	
	def report(int id) {
		
		if (id > 0) {
			
			boolean usingDefault = false;
			
			def year = params.year;
			def month = params.month;
			
			Calendar calendar = GregorianCalendar.getInstance();
			// default, this month, reset values 
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			
			// parsing the year 
			if (year != null) {
				try {
					Date date = formatYear.parse(year);
					// set the calendar 
					calendar.setTime(date);
				} catch (ParseException e) {
					usingDefault = true;
				}
			}
			
			// parsing the month
			if (month != null) {
				try {
					Date date = formatMonth.parse(month);
					// set the calendar
					Calendar c1 = Calendar.getInstance();
					c1.setTime(date);
					calendar.set(Calendar.MONTH, c1.get(Calendar.MONTH));
				} catch (ParseException e) {
					usingDefault = true;
					// it's bizarre that I have to do this
					Date date = formatMonth.parse(formatMonth.format(new Date()));
					Calendar c1 = Calendar.getInstance();
					c1.setTime(date);
					calendar.set(Calendar.MONTH, c1.get(Calendar.MONTH));
				}
			}

			Date start = calendar.getTime();

			// calculate next and previous months for browsing
			calendar.add(Calendar.MONTH, -1);
			Date datePrev = calendar.getTime();
			calendar.add(Calendar.MONTH, 2);
			Date dateNext = calendar.getTime();
						
			// used as end-date when querying for alerts  
			Date end = dateNext.clone();
			
			GroupDao daoGroup = new GroupDao(jdbcTemplate);
			try {
				
				Group group = daoGroup.findUniqueObjectById("" + id);
				
				AlertDao daoAlert = new AlertDao(jdbcTemplate);
				Collection<Alert> alerts = 
					daoAlert.findAlertsForGroupInTimeRange(id, start, end);
				
				def mapAlerts = [:];
				def mapServiceDown = [:];
				def mapCountIrregularOfferings = [:];
				
				for (Alert alert : alerts) {
					
					int serviceId = alert.getServiceId();
					String offeringId = alert.getOffering();
					
					// alerts 
					def l = mapAlerts.get(serviceId);
					if (l) {
						l.add(alert);
					} else {
						l = new ArrayList<Alert>();
						l.add(alert);
						mapAlerts.put(serviceId, l);
					}
					
					// service down calculations 
					if (alert.getType().equals(AlertType.ALERT_SERVICE_DOWN.toString())) {
					    def d = mapServiceDown.get(serviceId);
					    if (d != null) {
							// add X minutes to count
							mapServiceDown.put(serviceId, d + 5);
					    } else {
							// initialize ALERT_SERVICE_DOWN
							mapServiceDown.put(serviceId, 5);
					    }
					}
					
					// count irregular data delivery offerings
					if (alert.getType().equals(AlertType.ALERT_IRREGULAR_DATA_DELIVERY.toString())) {
					    def o = mapCountIrregularOfferings.get(offeringId);
					    if (o != null) {
							mapCountIrregularOfferings.put(offeringId, o + 1);
					    } else {
							// initialize ALERT_IRREGULAR_DATA_DELIVERY
							mapCountIrregularOfferings.put(offeringId, 1);
					    }
					}
					
				}
				
				def sortDesc = { a, b -> b.value <=> a.value };
				
				return [ group : group, alerts : alerts, 
						mapAlerts : mapAlerts, 
						// sort: descending
						mapServiceDown : mapServiceDown.sort(sortDesc),
						// sort: descending
						mapCountIrregularOfferings : mapCountIrregularOfferings.sort(sortDesc), 
						date : start, datePrev : datePrev, dateNext : dateNext, 
						usingDefault : usingDefault ];
				
			} catch (DataAccessException e) {
			
			}
			
			return [];
		}
		
		response.sendError(404);
	}
}
