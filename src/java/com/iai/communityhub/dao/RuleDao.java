package com.iai.communityhub.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;

import org.codehaus.groovy.syntax.Types;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.iai.communityhub.AlertType;
import com.iai.communityhub.HubUtils;
import com.iai.communityhub.model.Rule;

public class RuleDao extends AbstractDao<Rule> {

    private final static String TABLE_NAME = "communityhub.rules";

    /**
     * Constructor 
     * 
     * @param jdbcTemplate
     */
    public RuleDao(JdbcTemplate jdbcTemplate) {
        super(new RuleRowMapper(), TABLE_NAME, jdbcTemplate);
    }
    
    /**
     * Create table 
     */
    @Override
    public void create() {
    	String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + 
    			"(" +  
    			"id INT PRIMARY KEY NOT NULL AUTO_INCREMENT, " +
    			"type VARCHAR(45) NOT NULL, " + 
    			"service_id INT NOT NULL, " + 
    			"offering VARCHAR(128) DEFAULT NULL, " +
    			"observed_property VARCHAR(128) DEFAULT NULL, " +
    			"variable VARCHAR(128) DEFAULT NULL, " + 
    			"value DOUBLE DEFAULT NULL, " + 
    			"active TINYINT NOT NULL DEFAULT 1, " +
    			"created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " + 
    			"created_by VARCHAR(45) NOT NULL DEFAULT 'admin'" + 
    			")";
    	jdbcTemplate.execute(sql);
    }    
    
    /**
     * Fetches groups for a given rule 
     * 
     * @param groupId
     * @return
     */
	public Collection<Rule> findRulesForGroup(int groupId) {
		String sql = "SELECT * FROM " + TABLE_NAME + " rules " + 
				" INNER JOIN communityhub.groups_rules_xref xref" +
				" ON rules.id=xref.rule_id" + 
				" INNER JOIN communityhub.groups groups" +
				" ON xref.group_id=groups.id" +
				" WHERE groups.id = " + groupId; 
		return jdbcTemplate.query(sql, rowMapper); 
	}		
	
	/**
	 * Returns service down rules 
	 * 
	 * @return
	 */
	public Collection<Rule> findServiceDownRules() {
		return findRulesOfType(AlertType.ALERT_SERVICE_DOWN.toString());
	}
	
	/**
	 * Returns irregular data delivery rules 
	 * 
	 * @return
	 */
	public Collection<Rule> findIrregularDataDeliveryRules() {
		return findRulesOfType(AlertType.ALERT_IRREGULAR_DATA_DELIVERY.toString());
	}	
	
	/**
	 * Returns service down rules 
	 * 
	 * @param serviceId
	 * @return
	 */
	public Collection<Rule> findRulesOfType(String type) {
		String sql = "SELECT * FROM " + TABLE_NAME + 
				" WHERE type = ? AND active = 1";
	    Object[] params = { type };
	    return jdbcTemplate.query(sql, params, rowMapper);
	}	
		
	/**
	 * Query for "service down" rules for a given service Id 
	 * and returns the first one ordered by ID
	 * 
	 * Returns the rule if found, null otherwise 
	 * 
	 * @param serviceId
	 * @return
	 */
	public Rule findServiceDownRuleForService(int serviceId) {
		String sql = "SELECT * FROM " + TABLE_NAME + " " + 
				" WHERE service_id = ? AND type = 'ALERT_SERVICE_DOWN' " + 
				" ORDER BY id";
	    Object[] params = {serviceId};
	    try {
	    	return jdbcTemplate.queryForObject(sql, params, rowMapper);
	    } catch (DataAccessException e) {
	    	System.err.println("Data access exception: " + e.getMessage());
	    }
	    return null;
	}
	
	/**
	 * Query for "service down" rules for a given service Id 
	 * and returns the first one ordered by ID
	 * 
	 * Returns the rule if found, null otherwise 
	 * 
	 * @param serviceId
	 * @return
	 */
	public Rule findIrregularDataDeliveryRule(int serviceId, 
			String offering, String observedProperty) 
	{
		String nullCheckOnOffering = 
				HubUtils.createNullCheckedClause("offering", offering);
		
		String sql = "SELECT * FROM " + TABLE_NAME +  
				" WHERE service_id=? " + 
				" AND " + nullCheckOnOffering +  
				" AND observed_property=? " + 
				" AND type='" +
				AlertType.ALERT_IRREGULAR_DATA_DELIVERY.toString() + 
				"' " +  
				" ORDER BY id";
		
	    Object[] params = {serviceId, offering, observedProperty };
	    try {
	    	return jdbcTemplate.queryForObject(sql, params, rowMapper);
	    } catch (DataAccessException e) {
	    	System.err.println("Data access exception: " + e.getMessage());
	    }
	    return null;
	}	
	
	/**
	 * Insert an alert which was triggered by the given rule 
	 * 
	 * @param alert
	 * @param rule
	 */
    public Rule insert(final Rule rule) {
    	
    	final String sql = "INSERT INTO " + TABLE_NAME + 
    			"  (type, service_id, offering, observed_property, variable, value, active, created, created_by) VALUES " + 
    			" (?, ?, ?, ?, ?, ?, ?, ?, ?)" ;
    	
    	KeyHolder keyHolder = new GeneratedKeyHolder();
    	jdbcTemplate.update(
    	    new PreparedStatementCreator() {
    	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
    	            PreparedStatement ps =
    	                connection.prepareStatement(sql, new String[] {"id"});
    	            int i = 1; 
    	            ps.setString(i++, rule.getType());
    	            ps.setInt(i++, rule.getServiceId());
    	            // offering
    	            String offering = rule.getOffering();
    	            if (offering != null && !offering.trim().equals(""))
    	            	ps.setString(i++, offering);
    	            else
    	            	ps.setNull(i++, Types.STRING);
    	            // observed property 
    	            String property = rule.getObservedProperty();
    	            if (property != null && !property.trim().equals(""))
    	            	ps.setString(i++, property);
    	            else
    	            	ps.setNull(i++, Types.STRING);
    	            
    	            ps.setString(i++, rule.getVariable());
    	            // value 
    	            Double value = rule.getValue();
    	            if (value != null)
    	            	ps.setDouble(i++, rule.getValue());
    	            else
    	            	ps.setNull(i++, Types.DECIMAL_NUMBER);
    	            // active
    	            ps.setBoolean(i++, rule.isActive());
    	            ps.setTimestamp(i++, new Timestamp(rule.getCreated().getTime()));
    	            // created by 
    	            String createdBy = rule.getCreatedBy(); 
    	            if (createdBy != null && !createdBy.trim().equals(""))
    	            	ps.setString(i++, rule.getCreatedBy());
    	            else
    	            	ps.setNull(i++, Types.STRING);
    	            return ps;
    	        }
    	    },
    	    keyHolder);
    	
    	int ruleId = keyHolder.getKey().intValue();

    	// clone the rule and set the appropriate ID 
    	Rule clone = rule.clone();
    	clone.setId(ruleId);
    	
    	return clone;
	}

	private static class RuleRowMapper implements RowMapper<Rule> {
        public Rule mapRow(ResultSet rs, int rowNum) throws SQLException {
        	Rule rule = new Rule();
            rule.setId(rs.getInt("id"));
            rule.setType(rs.getString("type"));
            rule.setServiceId(rs.getInt("service_id"));
            rule.setOffering(rs.getString("offering"));
            rule.setObservedProperty(rs.getString("observed_property"));
            rule.setVariable(rs.getString("variable"));
            rule.setValue(new Double(rs.getDouble("value")));
            rule.setActive(rs.getBoolean("active"));
            rule.setCreated(new Date(rs.getTimestamp("created").getTime()));
            rule.setCreatedBy(rs.getString("created_by"));
            return rule;
        }
    }
}