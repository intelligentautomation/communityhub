package com.iai.communityhub.model;

import java.util.Date;

public class Group {
	
	private int id; 
	private String name;
	private String description; 
	private Date created;
	private boolean active;
	private boolean communal; 
	private String createdBy;
	private String admin; 
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}
	
	/**
	 * @param created the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}
	
	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the communal
	 */
	public boolean isCommunal() {
		return communal;
	}

	/**
	 * @param communal the communal to set
	 */
	public void setCommunal(boolean communal) {
		this.communal = communal;
	}

	/**
	 * @return the createBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createBy the createBy to set
	 */
	public void setCreatedBy(String createBy) {
		this.createdBy = createBy;
	} 
	
	/**
	 * @return the admin
	 */
	public String getAdmin() {
		return admin;
	}

	/**
	 * @param admin the admin to set
	 */
	public void setAdmin(String admin) {
		this.admin = admin;
	}

	/**
	 * @return the user
	 */
//	public SecUser getGroupAdminUser(JdbcTemplate jdbcTemplate) {
//		
//	}
//	public User getCreatedByUser(JdbcTemplate jdbcTemplate) {
//		return new UserDao(jdbcTemplate).findUniqueObjectById("" + createdBy);
//	}
		
}
