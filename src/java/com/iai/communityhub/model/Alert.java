package com.iai.communityhub.model;

import java.util.Date;

public class Alert {
	
//	private DateTimeFormatter fmt = DateTimeFormat.forPattern("MMMM, yyyy");
	
	private int id;
	private int serviceId; 
	private Date timestamp;
	private Date validFrom;
	private Date validTo; 
	private String type; 
	private Number latLower;
	private Number lonLower;
	private Number latUpper;
	private Number lonUpper;
	private String offering;
	private String observedProperty;
	private String detail; 
	
	/**
	 * Constructor 
	 */
	public Alert() {
		// defaults
		timestamp = new Date();
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
	public void setServiceId(int service) {
		this.serviceId = service;
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

	/**
	 * @return the validFrom
	 */
	public Date getValidFrom() {
		return validFrom;
	}

	/**
	 * @param validFrom the validFrom to set
	 */
	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	/**
	 * @return the validTo
	 */
	public Date getValidTo() {
		return validTo;
	}

	/**
	 * @param validTo the validTo to set
	 */
	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	public String getTypePretty() {
		String str = type.replaceAll("\\_", " ").toLowerCase(); 
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return the latLower
	 */
	public Number getLatLower() {
		return latLower;
	}

	/**
	 * @param latLower the latLower to set
	 */
	public void setLatLower(Number latLower) {
		this.latLower = latLower;
	}

	/**
	 * @return the lonLower
	 */
	public Number getLonLower() {
		return lonLower;
	}

	/**
	 * @param lonLower the lonLower to set
	 */
	public void setLonLower(Number lonLower) {
		this.lonLower = lonLower;
	}

	/**
	 * @return the latUpper
	 */
	public Number getLatUpper() {
		return latUpper;
	}

	/**
	 * @param latUpper the latUpper to set
	 */
	public void setLatUpper(Number latUpper) {
		this.latUpper = latUpper;
	}

	/**
	 * @return the lonUpper
	 */
	public Number getLonUpper() {
		return lonUpper;
	}

	/**
	 * @param lonUpper the lonUpper to set
	 */
	public void setLonUpper(Number lonUpper) {
		this.lonUpper = lonUpper;
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

	/**
	 * @param observedProperty the observedProperty to set
	 */
	public void setObservedProperty(String observedProperty) {
		this.observedProperty = observedProperty;
	}

	/**
	 * @return the detail
	 */
	public String getDetail() {
		return detail;
	}

	/**
	 * @param detail the detail to set
	 */
	public void setDetail(String detail) {
		this.detail = detail;
	}

}
