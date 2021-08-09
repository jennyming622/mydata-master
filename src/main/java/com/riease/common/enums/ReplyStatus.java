package com.riease.common.enums;

/**
 * 內容回覆狀態
 * @author wesleyzhuang
 *
 */
public enum ReplyStatus {
	
	NotReply(0),
	Replied(1)
	;
	
	private int value = 0;
	
	ReplyStatus(int value) {
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
	public static ReplyStatus ofIntValue(int value) {
		ReplyStatus[] vs = ReplyStatus.values();
		for(ReplyStatus s : vs) {
			if(s.getValue() == value) {
				return s;
			}
		}
		return null;
	}
	
	
}
