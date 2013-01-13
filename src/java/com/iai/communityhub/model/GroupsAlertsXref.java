package com.iai.communityhub.model;

public class GroupsAlertsXref {
	
	private int id;
	private int group;
	private int alert; 
	
	/**
	 * Constructor
	 */
	public GroupsAlertsXref() {
		
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
	 * @return the alert
	 */
	public int getAlert() {
		return alert;
	}

	/**
	 * @param alert the alert to set
	 */
	public void setAlert(int alert) {
		this.alert = alert;
	}
	
}
