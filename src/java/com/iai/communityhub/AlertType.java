package com.iai.communityhub;

public enum AlertType {
	
	ALERT_SERVICE_DOWN("ALERT_SERVICE_DOWN", "Service down alert"), 
	ALERT_IRREGULAR_DATA_DELIVERY("ALERT_IRREGULAR_DATA_DELIVERY", 
			"Irregular data delivery alert"), 
	ALERT_USER_TEMPLATE("ALERT_USER_TEMPLATE", "User-specified alert"); 
	
	private String type;
	private String sane; 
	
	/**
	 * Constructor
	 * 
	 * @param type
	 */
	private AlertType(String type, String sane) {
		this.type = type; 
		this.sane = sane;
	}
	
	/**
	 * Parse string into a enumeration value 
	 * 
	 * @param type
	 * @return
	 */
	public static AlertType parse(String type) {
		for (AlertType e : AlertType.values()) {
			if (type.equalsIgnoreCase(e.toString())) {
				return e;
			}
		}
		throw new IllegalArgumentException("No constant with text " + 
				type + " found");
	}	
	
	@Override
	public String toString() {
		return type;
	}
	
	public String getSane() {
		return sane; 
	}
	
}
