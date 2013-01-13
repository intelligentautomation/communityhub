package com.iai.communityhub.model;

import com.iai.proteus.common.Labeling;

public class OfferingProperties {
	
	private int id;
	private int serviceId;
	private String offering;
	private String observedProperty;
	private boolean active;
	
	/**
	 * Constructor
	 * 
	 */
	public OfferingProperties() {
		// defaults 
		active = true;
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
	public int getServiceId() {
		return serviceId;
	}
	
	/**
	 * @param service the service to set
	 */
	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}
	
	/**
	 * @return the offering
	 */
	public String getOffering() {
		return offering;
	}
	
	/**
	 * @param offering the offering to set
	 */
	public void setOffering(String offering) {
		this.offering = offering;
	}
	
	/**
	 * @return the observedProperty
	 */
	public String getObservedProperty() {
		return observedProperty;
	}
	
	public String getObservedPropertyPretty() {
		return Labeling.labelProperty(getObservedProperty());
	}
	
	/**
	 * @param observedProperty the observedProperty to set
	 */
	public void setObservedProperty(String observedProperty) {
		this.observedProperty = observedProperty;
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
	 * @return the user
	 */
//	public Service getService(JdbcTemplate jdbcTemplate) {
//		return new ServiceDao(jdbcTemplate).findUniqueObjectById("" + service);
//	}
	
}
