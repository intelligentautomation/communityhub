/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.communityhub.model;

import org.springframework.jdbc.core.JdbcTemplate;

import com.iai.communityhub.dao.ServiceDao;
import com.iai.proteus.common.Labeling;

public class Offering {
	
	private int id;
	private int service;
	private String offering;
	private String observedProperty;
	private int spiderNode;
	private boolean active;
	
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
	 * @return the spiderNode
	 */
	public int getSpiderNode() {
		return spiderNode;
	}
	
	/**
	 * @param spiderNode the spiderNode to set
	 */
	public void setSpiderNode(int spiderNode) {
		this.spiderNode = spiderNode;
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
	public Service getService(JdbcTemplate jdbcTemplate) {
		return new ServiceDao(jdbcTemplate).findUniqueObjectById("" + service);
	}
	
}
