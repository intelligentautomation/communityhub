package com.iai.communityhub.model;

import java.util.Date;

public class CapabilitiesCache {
	
	private int id; 
	private int service;
	private String capabilities; 
	private Date timestamp; 
	
	/**
	 * Constructor 
	 * 
	 */
	public CapabilitiesCache() {
		
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
	 * @return the service
	 */
	public int getService() {
		return service;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(int service) {
		this.service = service;
	}

	/**
	 * @return the capabilities
	 */
	public String getCapabilities() {
		return capabilities;
	}

	/**
	 * @param capabilities the capabilities to set
	 */
	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
