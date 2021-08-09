package com.riease.common.enums;

public enum EnableStatus {

	Disabled(0),
	Enabled(1)
	;
	
	private int value = 0;
	
	EnableStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	/**
	 * 以值取列舉物件
	 * @param value
	 * @return
	 */
	public static EnableStatus ofIntValue(int value) {
		EnableStatus[] vs = EnableStatus.values();
		for(EnableStatus s : vs) {
			if(s.getValue() == value) {
				return s;
			}
		}
		return null;
	}
	
	
}
