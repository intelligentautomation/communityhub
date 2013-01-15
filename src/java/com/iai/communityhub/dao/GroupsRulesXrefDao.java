/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.communityhub.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.iai.communityhub.model.Group;
import com.iai.communityhub.model.GroupsRulesXref;
import com.iai.communityhub.model.Rule;

public class GroupsRulesXrefDao extends AbstractDao<GroupsRulesXref> {

    private final static String TABLE_NAME = "communityhub.groups_rules_xref";

    /**
     * Constructor 
     * 
     * @param jdbcTemplate
     */
    public GroupsRulesXrefDao(JdbcTemplate jdbcTemplate) {
        super(new GroupsRulesXrefRowMapper(), TABLE_NAME, jdbcTemplate);
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
    			"rule_id INT NOT NULL " +
    			")";
    	jdbcTemplate.execute(sql);
    }    
    
    /**
     * Insert model object  
     * 
     * @param xref
     */
    public void insert(GroupsRulesXref xref) {
    	String sql = "INSERT INTO " + TABLE_NAME + 
    			" (group_id, rule_id) VALUES (?, ?)"; 
    	jdbcTemplate.update(sql, 
    			new Object[] { xref.getGroup(), xref.getRule() });
    }
    
    /**
     * Insert reference between group and rule 
     * 
     * @param group
     * @param rule
     */
    public void insert(Group group, Rule rule) {
    	
    	// do not enter double records 
    	if (exists(group, rule))
    		return;
    	
    	String sql = "INSERT INTO " + TABLE_NAME + 
    			" (group_id, rule_id) VALUES (?, ?)"; 
    	jdbcTemplate.update(sql, 
    			new Object[] { group.getId(), rule.getId() });
    }
    
    /**
     * Returns true if the service end-point already exists, false otherwise
     * 
     * @param service
     * @return
     */
    public boolean exists(Group group, Rule rule) {
		String count = "SELECT COUNT(*) FROM " + TABLE_NAME + 
				" WHERE group_id=? AND rule_id=?";
		try {
			Object[] params = { group.getId(), rule.getId() };
			if (jdbcTemplate.queryForInt(count, params) > 0) {
				return true; 
			} 
		} catch (DataAccessException e) { 
			System.err.println("Data access exception: " + e.getMessage());
		}
		// default 
		return false;
    }    
        
    /**
     * Insert model object  
     * 
     * @param xref
     */
    public void remove(GroupsRulesXref xref) {
    	String sql = "DELETE FROM " + TABLE_NAME + 
    			" WHERE group_id = ? AND rule_id = ?"; 
    	jdbcTemplate.update(sql, 
    			new Object[] { xref.getGroup(), xref.getRule() });
    }
        

	private static class GroupsRulesXrefRowMapper implements RowMapper<GroupsRulesXref> {
        public GroupsRulesXref mapRow(ResultSet rs, int rowNum) throws SQLException {
        	GroupsRulesXref xref = new GroupsRulesXref();
            xref.setId(rs.getInt("id"));
            xref.setGroup(rs.getInt("group_id"));
            xref.setRule(rs.getInt("rule_id"));
            return xref;
        }
    }
}
