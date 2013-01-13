package com.iai.communityhub.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.iai.communityhub.model.Service;

public class ServiceDao extends AbstractDao<Service> {

    private final static String TABLE_NAME = "communityhub.services"; 

    /**
     * Constructor 
     * 
     * @param jdbcTemplate
     */
    public ServiceDao(JdbcTemplate jdbcTemplate) {
        super(new ServiceRowMapper(), TABLE_NAME, jdbcTemplate);
        
        // override find all SQL
        this.sqlFindAll = "SELECT * FROM " + TABLE_NAME + " WHERE active=1";       
        
        // override list SQL 
        this.sqlList = "SELECT * FROM " + TABLE_NAME + " WHERE active=1" + 
        		" ORDER BY id LIMIT ? OFFSET ?";        
    }
    
    /**
     * Create table 
     */
    @Override
    public void create() {
    	String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + 
    			"(" +  
    			"id INT PRIMARY KEY NOT NULL AUTO_INCREMENT, " +
    			"title VARCHAR(128) DEFAULT NULL, " + 
    			"type VARCHAR(45) NOT NULL, " +
    			"endpoint VARCHAR(256) NOT NULL, " +
    			"database_name VARCHAR(128), " +
    			"added TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " + 
    			"active TINYINT NOT NULL DEFAULT 1, " + 
    			"alive TINYINT NOT NULL DEFAULT 0" +
    			")";
    	jdbcTemplate.execute(sql);
    }
    
    /**
     * Insert service 
     * 
     * @param service
     * @return
     */
    public Service insert(final Service service) {
    	
    	final String sql = "INSERT INTO " + TABLE_NAME + 
    			"  (title, type, endpoint, database_name, added, active, alive) VALUES " + 
    			" (?, ?, ?, ?, ?, ?, ?)" ;
    	
    	KeyHolder keyHolder = new GeneratedKeyHolder();
    	jdbcTemplate.update(
    	    new PreparedStatementCreator() {
    	        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
    	            PreparedStatement ps =
    	                connection.prepareStatement(sql, new String[] {"id"});
    	            int i = 1;
    	            // title
    	            String title = service.getTitle();
    	            if (title != null)
    	            	ps.setString(i++, title);
    	            else
    	            	ps.setNull(i++, Types.VARCHAR);
    	            ps.setString(i++, service.getType());
    	            ps.setString(i++, service.getEndpoint());
    	            // database name
    	            String dbName = service.getDatabaseName();
    	            if (dbName != null)
    	            	ps.setString(i++, dbName);
    	            else
    	            	ps.setNull(i++, Types.VARCHAR);
	            	ps.setTimestamp(i++, new Timestamp(service.getAdded().getTime()));
	            	ps.setBoolean(i++, service.isActive());
	            	ps.setBoolean(i++, service.isAlive());
    	            return ps;
    	        }
    	    },
    	    keyHolder);
    	
    	int alertId = keyHolder.getKey().intValue();   
    	
    	Service resService = service.clone();
    	resService.setId(alertId);
    	
    	return resService; 
    }
    
    /**
     * Updates the active status 
     * 
     * @param service
     * @param status
     */
    public int setActiveStatus(Service service, boolean status) {
    	String sql = "UPDATE " + TABLE_NAME + 
    			" SET active=? WHERE id=?";
    	return jdbcTemplate.update(sql, new Object[] { status, service.getId() }); 
    }
    
    /**
     * Updates the alive status 
     * 
     * @param service
     * @param status
     */
    public int setAliveStatus(Service service, boolean status) {
    	String sql = "UPDATE " + TABLE_NAME + 
    			" SET alive=? WHERE id=?";
    	return jdbcTemplate.update(sql, new Object[] { status, service.getId() }); 
    }
    
    /**
     * Returns true if the service end-point already exists, false otherwise
     * 
     * @param service
     * @return
     */
    public boolean serviceExists(Service service) {
		String count = "SELECT COUNT(*) FROM " + TABLE_NAME + 
				" WHERE endpoint='" + service.getEndpoint() + "'";
		try {
			if (jdbcTemplate.queryForInt(count) > 0) {
				return true; 
			} 
		} catch (DataAccessException e) { 
			System.err.println("Data access exception: " + e.getMessage());
		}
		// default 
		return false;
    }
	
    private static class ServiceRowMapper implements RowMapper<Service> {
        public Service mapRow(ResultSet rs, int rowNum) throws SQLException {
            Service service = new Service();
			service.setId(rs.getInt("id"));
			service.setTitle(rs.getString("title"));
			service.setType(rs.getString("type"));
			service.setEndpoint(rs.getString("endpoint"));
			service.setDatabaseName(rs.getString("database_name"));
			service.setAdded(new Date(rs.getTimestamp("added").getTime()));
			service.setActive(rs.getBoolean("active"));
			service.setAlive(rs.getBoolean("alive"));
            return service;
        }
    }	

}
