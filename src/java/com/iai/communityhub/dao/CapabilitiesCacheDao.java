package com.iai.communityhub.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.iai.communityhub.model.CapabilitiesCache;

public class CapabilitiesCacheDao extends AbstractDao<CapabilitiesCache> {

    private final static String TABLE_NAME = "communityhub.capabilities_cache";

    /**
     * Constructor 
     * 
     * @param jdbcTemplate
     */
    public CapabilitiesCacheDao(JdbcTemplate jdbcTemplate) {
        super(new CapabilitiesCacheRowMapper(), TABLE_NAME, jdbcTemplate);
    }
    
    /**
     * Create table 
     */
    @Override
    public void create() {
    	String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + 
    			"(" +  
    			"id INT PRIMARY KEY NOT NULL AUTO_INCREMENT, " +
    			"service INT NOT NULL, " + 
    			"capabilities LONGBLOB DEFAULT NULL, " +
    			"timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP " +
    			")";
    	jdbcTemplate.execute(sql);
    }    
    
    /**
     * Find cache for a service Id
     * 
     * @param serviceId
     */
	public CapabilitiesCache findForServiceId(int id) throws DataAccessException {
    	String sql = "SELECT * FROM " + TABLE_NAME + " WHERE service=?";
	    Object[] params = {id};
    	return jdbcTemplate.queryForObject(sql, params, rowMapper);
	}	

	/**
	 * Inserts a record, or updates an existing one if it already
	 * exists
	 * 
	 * @param cache
	 */
	public void insertOrUpdate(CapabilitiesCache cache) {
		String count = "SELECT COUNT(*) FROM " + TABLE_NAME + 
				" WHERE service=" + cache.getService();
		try {
			if (jdbcTemplate.queryForInt(count) > 0) {
				// update
				update(cache);
			} else {
				// insert
				insert(cache);
			}
		} catch (DataAccessException e) { 
			System.err.println("Data access exception: " + e.getMessage());
		}
	}
	
    /**
     * Insert a new record 
     * 
     * @param cache
     */
    public void insert(CapabilitiesCache cache) {
    	String sql = "INSERT INTO " + TABLE_NAME + 
    			" (service, capabilities) VALUES " + 
    			" (?, ?)" ;
    	jdbcTemplate.update(sql, 
    			new Object[] { cache.getService(), cache.getCapabilities() }); 
    }
    
    /**
     * Update a record 
     * 
     * @param cache
     */
    public void update(CapabilitiesCache cache) {
    	String sql = "UPDATE " + TABLE_NAME + 
    			" SET capabilities=?, timestamp=?" + 
    			" WHERE service=?" ;
    	jdbcTemplate.update(sql, 
    			new Object[] { cache.getCapabilities(), new Date(), cache.getService() }); 
    }
      
    private static class CapabilitiesCacheRowMapper implements RowMapper<CapabilitiesCache> {
        public CapabilitiesCache mapRow(ResultSet rs, int rowNum) throws SQLException {
        	CapabilitiesCache capabilities = new CapabilitiesCache();
            capabilities.setId(rs.getInt("id"));
            capabilities.setService(rs.getInt("service"));
            capabilities.setCapabilities(rs.getString("capabilities"));
            capabilities.setTimestamp(new Date(rs.getTimestamp("timestamp").getTime()));
            return capabilities;
        }
    }
}