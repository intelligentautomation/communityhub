/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.communityhub;

import org.joda.time.DateTime;

import com.iai.proteus.common.sos.data.SensorData;

/**
 * Contains a response from the service
 * 
 * @author jhenriksson
 *
 */
public class ServiceResponse {
	
	private Result result;
	private SensorData sensorData;
	
	// optional 
	private DateTime from;
	private DateTime to; 
	
	public ServiceResponse(Result result, SensorData sensorData) {
		this.result = result;
		this.sensorData = sensorData;
		
		// default
		from = null;
		to = null;
	}

	/**
	 * @return the result
	 */
	public Result getResult() {
		return result;
	}

	/**
	 * @return the sensorData
	 */
	public SensorData getSensorData() {
		return sensorData;
	}

	/**
	 * @return the from
	 */
	public DateTime getFrom() {
		return from;
	}

	/**
	 * @param from the from to set
	 */
	public void setFrom(DateTime from) {
		this.from = from;
	}

	/**
	 * @return the to
	 */
	public DateTime getTo() {
		return to;
	}

	/**
	 * @param to the to to set
	 */
	public void setTo(DateTime to) {
		this.to = to;
	}
	
}
