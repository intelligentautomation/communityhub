/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.communityhub.model;

public class GroupsRulesXref {
	
	private int id;
	private int group;
	private int rule; 
	
	/**
	 * Constructor
	 */
	public GroupsRulesXref() {
		
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
	 * @return the group
	 */
	public int getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(int group) {
		this.group = group;
	}

	/**
	 * @return the rule
	 */
	public int getRule() {
		return rule;
	}

	/**
	 * @param rule the rule to set
	 */
	public void setRule(int rule) {
		this.rule = rule;
	}
	
}
