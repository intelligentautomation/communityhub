package com.iai.communityhub.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.iai.communityhub.model.OfferingProperties;

public class OfferingPropertiesDao extends AbstractDao<OfferingProperties> {

    private final static String TABLE_NAME = "communityhub.offerings_properties";

    /**
     * Constructor 
     * 
     * @param jdbcTemplate
     */
    public OfferingPropertiesDao(JdbcTemplate jdbcTemplate) {
        super(new OfferingPropertiesRowMapper(), TABLE_NAME, jdbcTemplate);
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
    			"offering VARCHAR(128) NOT NULL, " +
    			"observed_property VARCHAR(128) NOT NULL, " +
    			"active TINYINT NOT NULL DEFAULT 1" + 
    			")";
    	jdbcTemplate.execute(sql);
    }
    
    /**
     * Finds all records that are active and with the given service ID
     * 
     * @param serviceId
     * @return
     */
	public Collection<OfferingProperties> findActiveForService(int serviceId) {
		String sql = "SELECT * FROM " + TABLE_NAME + 
				" WHERE service_id=? AND active=1 ORDER BY offering";
		Object[] params = new Object[] { serviceId };
		return jdbcTemplate.query(sql, params, rowMapper);
	}

	/**
	 * 
	 * @param serviceId
	 * @param offering
	 * @return
	 */
	public List<String> findActiveObservedProperties(int serviceId, String offering) {
		String sql = "SELECT observed_property FROM " + TABLE_NAME + 
				" WHERE service_id=? AND offering=? AND active=1";
		Object[] params = new Object[] { serviceId, offering };
		return jdbcTemplate.queryForList(sql, params, String.class);
	}

	/**
	 * 
	 * @param serviceId
	 * @return
	 */
	public List<String> findDistinctActiveOfferings(int serviceId) {
		String sql = "SELECT DISTINCT(offering) FROM " + TABLE_NAME + 
				" WHERE service_id=? AND active=1";
		Object[] params = new Object[] { serviceId };
		return jdbcTemplate.queryForList(sql, params, String.class);
	}
	
    /**
     * Counts the offerings with the given observed properties for the given service
     * 
     * @param serviceId
     * @return
     */
	public int countOfferingsWithObservedProperty(int serviceId, 
			String observedProperty) {
		String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + 
				" WHERE service_id=? AND observed_property=?";
		Object[] params = new Object[] { serviceId, observedProperty };
		return jdbcTemplate.queryForInt(sql, params);
	}	
	
	
	/**
	 * Insert 
	 * 
	 * @param offering
	 * @param rule
	 */
    public void insert(OfferingProperties offering) {
    	final String sql = "INSERT INTO " + TABLE_NAME + 
    			"  (service_id, offering, observed_property) VALUES " + 
    			" (?, ?, ?)" ;
    	jdbcTemplate.update(sql, new Object[] { offering.getServiceId(), 
    			offering.getOffering(), offering.getObservedProperty() });
    }
	
    private static class OfferingPropertiesRowMapper implements RowMapper<OfferingProperties> {
        public OfferingProperties mapRow(ResultSet rs, int rowNum) throws SQLException {
        	OfferingProperties offering = new OfferingProperties();
            offering.setId(rs.getInt("id"));
            offering.setServiceId(rs.getInt("service_id"));
            offering.setOffering(rs.getString("offering"));
            offering.setObservedProperty(rs.getString("observed_property"));
            offering.setActive(rs.getBoolean("active"));
            return offering;
        }
    }
}