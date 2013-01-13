package com.iai.communityhub.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.iai.communityhub.model.Group;

public class GroupDao extends AbstractDao<Group> {

    private final static String TABLE_NAME = "communityhub.groups";

    /**
     * Constructor
     * 
     * @param jdbcTemplate
     */
    public GroupDao(JdbcTemplate jdbcTemplate) {
        super(new GroupRowMapper(), TABLE_NAME, jdbcTemplate);
        
        // override findAll() query results 
        sqlFindAll = "SELECT * FROM " + TABLE_NAME + 
        		" WHERE active=1 AND communal=1";
    }
    
    /**
     * Create table 
     */
    @Override
    public void create() {
    	String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + 
    			"(" +  
    			"id INT PRIMARY KEY NOT NULL AUTO_INCREMENT, " +
    			"name VARCHAR(128) DEFAULT NULL, " + 
    			"description TEXT DEFAULT NULL, " +
    			"created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " + 
    			"active TINYINT NOT NULL DEFAULT 1, " + 
    			"communal TINYINT NOT NULL DEFAULT 1, " +
    			"created_by VARCHAR(45) NOT NULL, " + 
    			"admin VARCHAR(45) NOT NULL" + 
    			")";
    	jdbcTemplate.execute(sql);
    }    

    /**
     * Fetches groups for a given rule 
     * 
     * @param ruleId
     * @return
     */
	public Collection<Group> findGroupsForRule(int ruleId) {
		String sql = "SELECT * FROM " + TABLE_NAME + " groups" + 
				" INNER JOIN communityhub.groups_rules_xref xref" +
				" ON groups.id=xref.group_id" + 
				" INNER JOIN communityhub.rules rules" +
				" ON xref.rule_id=rules.id" +
				" WHERE rules.id = " + ruleId; 
		return jdbcTemplate.query(sql, rowMapper); 
	}	

	/**
	 * Insert a new record 
	 * 
	 * @param group
	 */
    public void insert(Group group) {
    	String sql = "INSERT INTO " + TABLE_NAME + 
    			"  (name, description, created_by, admin) VALUES " + 
    			" (?, ?, ?, ?)" ;
    	jdbcTemplate.update(sql, 
    			new Object[] { group.getName(), group.getDescription(), 
    				group.getCreatedBy(), group.getAdmin() }); 
    }
    
    /**
     * Update a record 
     * 
     * @param group
     */
    public void update(Group group) {
    	String sql = "UPDATE " + TABLE_NAME + 
    			" SET name=?, description=? WHERE id=?";
    	jdbcTemplate.update(sql, 
    			new Object[] { group.getName(), 
    				group.getDescription(), group.getId() });
    }
    
    /**
     * "Deletes" a group (makes it inactive)
     * 
     * @param group
     */
    public void delete(Group group) {
    	String sql = "UPDATE " + TABLE_NAME + 
    			" SET active=0 WHERE id=?";
    	jdbcTemplate.update(sql, new Object[] { group.getId() });
    }
    
    private static class GroupRowMapper implements RowMapper<Group> {
        public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
            Group group = new Group();
            group.setId(rs.getInt("id"));
            group.setName(rs.getString("name"));
            group.setDescription(rs.getString("description"));
            group.setCreated(new Date(rs.getTimestamp("created").getTime()));
            group.setActive(rs.getBoolean("active"));
            group.setCommunal(rs.getBoolean("communal"));
            group.setCreatedBy(rs.getString("created_by"));
            group.setAdmin(rs.getString("admin"));
            return group;
        }
    }
}