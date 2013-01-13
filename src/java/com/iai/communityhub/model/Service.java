package com.iai.communityhub.model;

import java.util.Date;

public class Service implements Cloneable {

	private int id;
	private String title;
	private String type;
	private String endpoint; 
	private String databaseName;
	private Date added;
	private boolean active;
	private boolean alive; 
	
	/**
	 * Constructor
	 */
	public Service() {
		// defaults 
		type = "SOS";
		added = new Date();
		active = true;
		alive = false;
	}
	
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
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return the endpoint
	 */
	public String getEndpoint() {
		return endpoint;
	}
	
	/**
	 * @param endpoint the endpoint to set
	 */
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	/**
	 * @return the databaseName
	 */
	public String getDatabaseName() {
		return databaseName;
	}
	
	/**
	 * @param databaseName the databaseName to set
	 */
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	
	/**
	 * @return the added
	 */
	public Date getAdded() {
		return added;
	}
	
	/**
	 * @param added the added to set
	 */
	public void setAdded(Date added) {
		this.added = added;
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
	 * @return the alive
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * @param alive the alive to set
	 */
	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	@Override
	public Service clone() {
		Service service = new Service();
		service.setId(getId());
		service.setTitle(getTitle());
		service.setType(getType());
		service.setEndpoint(getEndpoint());
		service.setDatabaseName(getDatabaseName());
		service.setAdded(getAdded());
		service.setActive(isActive());
		return service;
	}
}
