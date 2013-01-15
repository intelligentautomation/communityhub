/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.communityhub.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.iai.communityhub.model.GroupsAlertsXref;

public class GroupsAlertsXrefDao extends AbstractDao<GroupsAlertsXref> {

    private final static String TABLE_NAME = "communityhub.groups_alerts_xref";

    /**
     * Constructor 
     * 
     * @param jdbcTemplate
     */
    public GroupsAlertsXrefDao(JdbcTemplate jdbcTemplate) {
        super(new GroupsAlertsXrefRowMapper(), TABLE_NAME, jdbcTemplate);
    }
    
    /**
     * Create table 
     */
    @Override
    public void create() {
    	String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + 
    			"(" +  
    			"id INT PRIMARY KEY NOT NULL AUTO_INCREMENT, " +
    			"group_id INT NOT NULL, " + 
    			"alert_id INT NOT NULL " +
    			")";
    	jdbcTemplate.execute(sql);
    }
    
    /**
     * Insert model object  
     * 
     * @param xref
     */
    public void insert(GroupsAlertsXref xref) {
    	String sql = "INSERT INTO " + TABLE_NAME + 
    			" (group_id, alert_id) VALUES (?, ?)"; 
    	jdbcTemplate.update(sql, 
    			new Object[] { xref.getGroup(), xref.getAlert() });
    }

	private static class GroupsAlertsXrefRowMapper implements RowMapper<GroupsAlertsXref> {
        public GroupsAlertsXref mapRow(ResultSet rs, int rowNum) throws SQLException {
        	GroupsAlertsXref xref = new GroupsAlertsXref();
            xref.setId(rs.getInt("id"));
            xref.setGroup(rs.getInt("group_id"));
            xref.setAlert(rs.getInt("alert_id"));
            return xref;
        }
    }
}
