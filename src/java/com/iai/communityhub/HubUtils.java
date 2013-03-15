/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.communityhub;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;

import org.xml.sax.SAXException;

import com.iai.proteus.common.sos.GetCapabilities;

/**
 * Utility methods 
 * 
 * @author Jakob Henriksson
 *
 */
public class HubUtils {

	/**
	 * Constructor
	 *
	 */
	private HubUtils() {

	}
	
	/**
	 * Creates a NULL check for use in SQL statements 
	 * 
	 * @param column
	 * @param value
	 * @return
	 */
	public static String createNullCheckedClause(String column, Object value) {
	   String operator = (value == null ? "IS" : "=");
	   return String.format("%s %s ?", column, operator);
	}
	
	/**
	 * Returns the SQL column name for a given sensor data column header 
	 * 
	 * @param field
	 * @return
	 */
	public static String getColumnName(String field) {
		return normalizeName(field); 
	}
	

	/**
	 * Returns the Capabilities document from the given service, null
	 * if there is an error 
	 * 
	 * @param endpoint
	 * @return
	 */
	public static String getCapabilitiesDocument(String endpoint) {
		
		// try and fetch the capabilities document
		// TODO: add user-specifiable timeout values
		try {
			 
			String capabilities = GetCapabilities.getDocument(endpoint);

			// TODO: we should do this instead 
//			String capabilities =
//				GetCapabilities.getDocument(service.getEndpoint(), 
//					timeoutConnection, timeoutRead);
			
			return capabilities;

		} catch (SocketTimeoutException e) {
			System.err.println("Socket timeout exception: " + e.getMessage());
		} catch (UnknownHostException e) {
			System.err.println("Unknown host exception: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
		} catch (SAXException e) {
			System.err.println("SAXException: " + e.getMessage());
		}
		
		// default 
		return null;
	}

	/**
	 * Returns the local part of a URL
	 *
	 * @param name
	 * @return
	 */
	public static String localUrl(String name) {
		if (name.startsWith("http")) {
			String local = name.substring(name.lastIndexOf('/') + 1);
			if (local.contains("#")) {
				local = local.substring(local.indexOf("#") + 1);
			}
			return local.replaceAll("_", " ");
		}
		return name.replaceAll("_", " ");
	}	
	
	/**
	 * "Normalizes" a name as described in the method body
	 *
	 * @param name
	 * @return
	 */
	public static String normalizeName(String name) {
		return name.trim().toLowerCase().replaceAll(" ", "_").replaceAll("-", "_").
				replaceAll("\\)", "_").replaceAll("\\(", "_").replaceAll("/", "_");
	}	
	
	/**
	 * Adds the given number of time units to the date 
	 * 
	 * E.g. Date inFiveMinutes = addTimeToDate(new Date(), Calendar.MINUTE, 5)
	 * 
	 * @param date
	 * @param unit
	 * @param number
	 * @return
	 */
	public static Date addTimeToDate(Date date, int unit, int number) {
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(date); 
		cal.add(unit, number); 
		return cal.getTime(); 
	}
	
	private static class SingletonHolder { 
		public static final HubUtils INSTANCE = new HubUtils();
	}

	public static HubUtils getInstance() {
		return SingletonHolder.INSTANCE;
	}	
}
