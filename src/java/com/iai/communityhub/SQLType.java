/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package com.iai.communityhub;

import com.iai.proteus.common.TimeUtils;

public enum SQLType {
	
	TIMESTAMP, 
	DOUBLE, 
	INTEGER, 
	STRING;

	/**
	 * Returns the data type we understand the given string as  
	 * 
	 * @param str
	 * @return
	 */
	public static SQLType parse(String str) {
		
		if (TimeUtils.parseDefault(str, false) != null) 
			return TIMESTAMP;

		try {
			if (str.contains(".")) {
				Double.parseDouble(str);
				return DOUBLE;
			}
		} catch (NumberFormatException e) {
			
		}
		
		try {
			Integer.parseInt(str);
			return INTEGER;
		} catch (NumberFormatException e) {
			
		}		
		
		// default 
		return STRING;
	}

}
