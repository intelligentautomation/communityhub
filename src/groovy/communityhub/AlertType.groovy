/*
 * Copyright (C) 2013 Intelligent Automation Inc. 
 * 
 * All Rights Reserved.
 */
package communityhub;

/**
 * Enumeration of alert types
 *  
 * @author Jakob Henriksson
 *
 */
public enum AlertType {
	
	SERVICE_DOWN("ALERT_SERVICE_DOWN", 
		"Service down alert"),
	IRREGULAR_DELIVERY("ALERT_IRREGULAR_DATA_DELIVERY", 
		"Irregular data delivery alert"),
	CUSTOM("ALERT_CUSTOM", 
		"Custom alert")

	String type 
	String readable

	/**
	 * 
	 * @param type
	 * @param readable
	 * @return
	 */
	public AlertType(String type, String readable) {
		this.type = type
		this.readable = readable
	}
	
	@Override
	public String toString() {
		return type
	}
	
	public String toReadableString() {
		return readable
	}
	
	/**
	 * Parse string into a enumeration value 
	 * 
	 * @param type
	 * @return
	 */
	public static AlertType parse(String type) {
		for (AlertType e : AlertType.values()) {
			if (type.toString().equalsIgnoreCase(e.toString())) {
				return e;
			}
		}
		throw new IllegalArgumentException("No constant with text " + 
				type + " found");
	}		
}

