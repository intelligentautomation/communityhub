package com.iai.communityhub.dao;

import java.util.Collection;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public abstract class AbstractDao<T> {

	protected final RowMapper<T> rowMapper;

	protected final String sqlFindById;
	protected String sqlFindAll;
	protected String sqlList; 
	protected String sqlCount; 
	protected String sqlDeleteById; 
	
	protected final JdbcTemplate jdbcTemplate;

	/**
	 * Constructor 
	 * 
	 * @param rowMapper
	 * @param tableName
	 * @param jdbcTemplate
	 */
	protected AbstractDao(RowMapper<T> rowMapper, String tableName,
			JdbcTemplate jdbcTemplate) {
		this.rowMapper = rowMapper;
		
		// common SQL statements  
		this.sqlFindById = "SELECT * FROM " + tableName + " WHERE id=?";
		this.sqlFindAll = "SELECT * FROM " + tableName;
		this.sqlList = "SELECT * FROM " + tableName + 
				" ORDER BY id LIMIT ? OFFSET ?";
		this.sqlCount = "SELECT COUNT(*) FROM " + tableName;
		this.sqlDeleteById = "DELETE FROM " + tableName + " WHERE id=?";
		
		this.jdbcTemplate = jdbcTemplate;
	}
	
	/**
	 * Create the table 
	 */
	public abstract void create();
	
	/**
	 * Returns all records 
	 * 
	 * @return
	 */
	public Collection<T> findAll() {
		return jdbcTemplate.query(sqlFindAll, rowMapper);
	}
	
	/**
	 * Lists the next 10 records from the given 'offset'
	 * 
	 * @param offset
	 * @return
	 */
	public Collection<T> list(int offset) {
		return list(10, offset);
	}

	/**
	 * Lists the next 'max' records from the given 'offset'
	 * 
	 * @param max
	 * @param offset
	 * @return
	 */
	public Collection<T> list(int max, int offset) {
		Object[] params = { max, offset };
		return jdbcTemplate.query(sqlList, params, rowMapper);
	}

	/**
	 * Find by ID
	 * 
	 * @param id
	 * @return
	 */
	public  Collection<T> findById(final String id) {
		Object[] params = {id};
		return jdbcTemplate.query(sqlFindById, params, rowMapper);
	}
	
	/**
	 * Find unique by ID
	 * 
	 * @param id
	 * @return
	 */
	public T findUniqueObjectById(final String id) {
	    Object[] params = {id};
	    return jdbcTemplate.queryForObject(sqlFindById, params, rowMapper);
	}	
	
	/**
	 * Delete record by ID
	 * 
	 * @param id
	 * @return
	 */
	public int deleteById(final String id) {
	    Object[] params = {id};
	    return jdbcTemplate.update(sqlDeleteById, params);
	}		
	
	/**
	 * Count number of rows in table
	 * 
	 * @return
	 */
	public int count() {
		return jdbcTemplate.queryForInt(sqlCount);
	}
	
}
