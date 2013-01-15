/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.communityhub.model;

import java.util.Date;

import com.iai.communityhub.AlertType;

public class Rule implements Cloneable {

	private int id;
	private String type;
	// the service ID this rule is related to
	private int serviceId; 
	// the offering ID this rule is specified on (if present)
	private String offering;
	// the observed property this rule is specified on (if present)
	private String observedProperty;
	// the column of data this rule is specified on (if present)
	private String variable; 
	private Double value; 
	private boolean active; 
	private Date created;
	private String createdBy;
	
	/**
	 * Constructor
	 * 
	 */
	public Rule() {
		// defaults
		variable = null;
		value = null;
		active = true;
		created = new Date();
		createdBy = "admin";
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @return the type
	 */
	public String getTypePretty() {
		return AlertType.parse(getType()).getSane();
//		String str = type.replaceAll("\\_", " ").toLowerCase(); 
//		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return the service
	 */
	public int getServiceId() {
		return serviceId;
	}

	/**
	 * @param serviceId the service to set
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

	/**
	 * @param observedProperty the observedProperty to set
	 */
	public void setObservedProperty(String observedProperty) {
		this.observedProperty = observedProperty;
	}

	/**
	 * @return the variable
	 */
	public String getVariable() {
		return variable;
	}

	/**
	 * @param variable the variable to set
	 */
	public void setVariable(String variable) {
		this.variable = variable;
	}

	/**
	 * @return the value
	 */
	public Double getValue() {
		return value;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(Double value) {
		this.value = value;
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
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}
	
	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	} 
	
	/**
	 * @return the user who created the rule 
	 */
//	public User getCreatedByUser(JdbcTemplate jdbcTemplate) {
//		return new UserDao(jdbcTemplate).findUniqueObjectById("" + createdBy);
//	}
	
	@Override
	public Rule clone() {
		Rule rule = new Rule();
		rule.setId(getId());
		rule.setType(getType());
		rule.setServiceId(getServiceId());
		rule.setOffering(getOffering());
		rule.setObservedProperty(getObservedProperty());
		rule.setVariable(getVariable());
		rule.setValue(getValue());
		rule.setActive(isActive());
		rule.setCreated(getCreated());
		rule.setCreatedBy(getCreatedBy());
		return rule;
	}

}
