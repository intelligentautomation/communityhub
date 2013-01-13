package com.iai.communityhub.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.iai.communityhub.model.Alert;
import com.iai.communityhub.model.Group;
import com.iai.communityhub.model.GroupsAlertsXref;
import com.iai.communityhub.model.Rule;

public class AlertDao extends AbstractDao<Alert> {

    private final static String TABLE_NAME = "communityhub.alerts";

    private Group group; 
    
    /**
     * Constructor
     * 
     * @param jdbcTemplate
     */
    public AlertDao(JdbcTemplate jdbcTemplate) {
        super(new AlertRowMapper(), TABLE_NAME, jdbcTemplate);
        this.group = null;
    }
    
    /**
     * Create table 
     */
    @Override
    public void create() {
    	String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + 
    			"(" +  
    			"id INT PRIMARY KEY NOT NULL AUTO_INCREMENT, " +
    			"service_id INT NOT NULL, " + 
    			"timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " + 
    			"valid_from DATETIME DEFAULT NULL, " + 
    			"valid_to DATETIME DEFAULT NULL, " + 
    			"type VARCHAR(45) NOT NULL, " + 
    			"lat_lower DECIMAL(18,12), " + 
    			"lon_lower DECIMAL(18,12), " + 
    			"lat_upper DECIMAL(18,12), " + 
    			"lon_upper DECIMAL(18,12), " + 
    			"offering VARCHAR(128) DEFAULT NULL, " + 
    			"observed_property VARCHAR(128) DEFAULT NULL, " + 
    			"detail TEXT DEFAULT NULL" + 
    			")";
    	jdbcTemplate.execute(sql);
    }    
    
    /**
     * Constructor
     * 
     * @param jdbcTemplate
     * @param group
     */
    public AlertDao(JdbcTemplate jdbcTemplate, Group group) {
    	this(jdbcTemplate);
    	this.group = group;
    	
        // override queries
		sqlCount = 
				"SELECT COUNT(*) FROM " + TABLE_NAME + " alerts" + 
				" INNER JOIN communityhub.groups_alerts_xref xref" +
				" ON alerts.id=xref.alert_id" + 
				" INNER JOIN communityhub.groups groups" +
				" ON xref.group_id=groups.id" +
				" WHERE groups.id = " + group.getId();
    }
    
    /**
     * Fetches a limited set of most recent alerts
     * 
     * @param num
     * @return
     */
	public Collection<Alert> findRecentAlerts(int num) {
		String sql = "SELECT * FROM " + TABLE_NAME + 
				" ORDER BY timestamp DESC LIMIT " + num; 
		return jdbcTemplate.query(sql, rowMapper);
	}
	
    /**
     * Fetches alerts for a given group 
     * 
     * @param groupId
     * @return
     */
	public Collection<Alert> findAlertsForGroup(int groupId) {
		String sql = "SELECT * FROM " + TABLE_NAME + " alerts" + 
				" INNER JOIN communityhub.groups_alerts_xref xref" +
				" ON alerts.id=xref.alert_id" + 
				" INNER JOIN communityhub.groups groups" +
				" ON xref.group_id=groups.id" +
				" WHERE groups.id = " + groupId; 
		return jdbcTemplate.query(sql, rowMapper);
//		return findAlertsForGroup(groupId, ((long)18446744073709551610), 0);
	}	
	
    /**
     * Fetches alerts for a given group 
     * 
     * @param groupId
     * @param max
     * @param offset 
     * @return
     */
	public Collection<Alert> findAlertsForGroup(int groupId, long max, int offset) {
		String sql = "SELECT * FROM " + TABLE_NAME + " alerts" + 
				" INNER JOIN communityhub.groups_alerts_xref xref" +
				" ON alerts.id=xref.alert_id" + 
				" INNER JOIN communityhub.groups groups" +
				" ON xref.group_id=groups.id" +
				" WHERE groups.id = " + groupId + 
				" ORDER BY timestamp DESC LIMIT ? OFFSET ?"; 
		Object[] params = { max, offset };
		return jdbcTemplate.query(sql, params, rowMapper); 
	}
	
    /**
     * Fetches alerts for a given group within a given time range 
     * 
     * @param groupId
     * @param start
     * @param end
     * @return
     */
	public Collection<Alert> findAlertsForGroupInTimeRange(int groupId, Date start, Date end) {
		String sql = "SELECT * FROM " + TABLE_NAME + " alerts" + 
				" INNER JOIN communityhub.groups_alerts_xref xref" +
				" ON alerts.id=xref.alert_id" + 
				" INNER JOIN communityhub.groups groups" +
				" ON xref.group_id=groups.id" +
				" WHERE groups.id = " + groupId + 
				" AND timestamp >= ? AND timestamp < ?";
		Object[] params = { start, end };
		return jdbcTemplate.query(sql, params, rowMapper);
	}		
	
	/**
	 * Return max number of services starting from offset  
	 * 
	 * @param max
	 * @param offset
	 * @return
	 */
	@Override
	public Collection<Alert> list(int max, int offset) {
		if (group != null)
			return findAlertsForGroup(group.getId(), max, offset);
		return list(max, offset);
	}
	
    /**
     * Counts alerts for a given group 
     * 
     * @param groupId
     * @param max
     * @param offset 
     * @return
     */
	public Collection<Alert> countAlertsForGroup(int groupId) {
		String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " alerts" + 
				" INNER JOIN communityhub.groups_alerts_xref xref" +
				" ON alerts.id=xref.alert_id" + 
				" INNER JOIN communityhub.groups groups" +
				" ON xref.group_id=groups.id" +
				" WHERE groups.id = " + groupId;
		return jdbcTemplate.query(sql, rowMapper); 
	}			
	
	/**
	 * Insert an alert which was triggered by the given rule 
	 * 
	 * The purpose of having the rule as well is the ability to find 
	 * groups that are associated with the rule, such that we can 
	 * add a reference for the alert for those groups.  
	 * 
	 * @param alert
	 * @param rule
	 */
    public void insert(final Alert alert, Rule rule) {
    	final String sql = "INSERT INTO " + TABLE_NAME + 
    			"  (service_id, valid_from, valid_to, type, lat_lower, lon_lower, lat_upper, lon_upper, offering, observed_property, detail) VALUES " + 
    			" (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" ;
    	
    	KeyHolder keyHolder = new GeneratedKeyHolder();
    	jdbcTemplate.update(
    	    new PreparedStatementCreator() {
    	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
    	            PreparedStatement ps =
    	                connection.prepareStatement(sql, new String[] {"id"});
    	            int i = 1;
    	            ps.setInt(i++, alert.getServiceId());
    	            Date validFrom = alert.getValidFrom();
    	            if (validFrom != null) 
    	            	ps.setTimestamp(i++, new Timestamp(validFrom.getTime()));
    	            else
    	            	ps.setNull(i++, Types.TIMESTAMP);
    	            Date validTo = alert.getValidTo();
    	            if (validTo != null) 
    	            	ps.setTimestamp(i++, new Timestamp(validTo.getTime()));
    	            else
    	            	ps.setNull(i++, Types.TIMESTAMP);
    	            ps.setString(i++, alert.getType());
    	            if (alert.getLatLower() != null)
    	            	ps.setString(i++, "" + alert.getLatLower());
    	            else
    	            	ps.setNull(i++, Types.DECIMAL);
    	            if (alert.getLonLower() != null)
    	            	ps.setString(i++, "" + alert.getLonLower());
    	            else
    	            	ps.setNull(i++, Types.DECIMAL);
    	            if (alert.getLatUpper() != null)
    	            	ps.setString(i++, "" + alert.getLatUpper());
    	            else
    	            	ps.setNull(i++, Types.DECIMAL);
    	            if (alert.getLonUpper() != null)
    	            	ps.setString(i++, "" + alert.getLonUpper());
    	            else
    	            	ps.setNull(i++, Types.DECIMAL);
    	            ps.setString(i++, alert.getOffering());
    	            ps.setString(i++, alert.getObservedProperty());
    	            ps.setString(i++, alert.getDetail());
    	            return ps;
    	        }
    	    },
    	    keyHolder);
    	
    	int alertId = keyHolder.getKey().intValue();
    	
    	// find groups associated with the given rule 
    	GroupDao groupDao = new GroupDao(jdbcTemplate);
    	Collection<Group> groups = groupDao.findGroupsForRule(rule.getId());
    	
    	// update cross-reference table
    	GroupsAlertsXrefDao xrefDao = new GroupsAlertsXrefDao(jdbcTemplate);
    	
    	for (Group group : groups) {
    		GroupsAlertsXref xref = new GroupsAlertsXref();
    		xref.setGroup(group.getId());
    		xref.setAlert(alertId);
    		// insert into the cross-reference table 
    		xrefDao.insert(xref);
    	}
    }
    
    private static class AlertRowMapper implements RowMapper<Alert> {
        public Alert mapRow(ResultSet rs, int rowNum) throws SQLException {
        	Alert alert = new Alert();
            alert.setId(rs.getInt("id"));
            alert.setServiceId(rs.getInt("service_id"));
            alert.setTimestamp(new Date(rs.getTimestamp("timestamp").getTime()));
            Timestamp validFrom = rs.getTimestamp("valid_from");
            if (validFrom != null)
            	alert.setValidFrom(new Date(validFrom.getTime()));
            Timestamp validTo = rs.getTimestamp("valid_to");
            if (validTo != null)
            	alert.setValidTo(new Date(validTo.getTime()));
            alert.setType(rs.getString("type"));
            DecimalFormat format = new DecimalFormat();
            // parse BigDecimals
            format.setParseBigDecimal(true);
            String latLower = rs.getString("lat_lower");
            if (latLower != null && !latLower.equals("")) {
            	try {
            		alert.setLatLower(format.parse(latLower));
            	} catch (ParseException e) {
            		System.err.println("Parse exception: " + e.getMessage());
            	}
            }
            String lonLower = rs.getString("lon_lower");
            if (lonLower != null && !lonLower.equals("")) {
            	try {
            		alert.setLonLower(format.parse(lonLower));
            	} catch (ParseException e) {
            		System.err.println("Parse exception: " + e.getMessage());
            	}
            }
            String latUpper = rs.getString("lat_upper");
            if (latUpper != null && !latUpper.equals("")) {
            	try {
            		alert.setLatUpper(format.parse(latUpper));
            	} catch (ParseException e) {
            		System.err.println("Parse exception: " + e.getMessage());
            	}
            }
            String lonUpper = rs.getString("lon_upper");
            if (lonUpper != null && !lonUpper.equals("")) {
            	try {
            		alert.setLonUpper(format.parse(lonUpper));
            	} catch (ParseException e) {
            		System.err.println("Parse exception: " + e.getMessage());
            	}
            }
            alert.setOffering(rs.getString("offering"));
            alert.setObservedProperty(rs.getString("observed_property"));
            alert.setDetail(rs.getString("detail"));
            return alert;
        }
    }
}