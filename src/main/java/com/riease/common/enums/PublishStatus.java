/**
 * 
 */
package com.riease.common.enums;

import java.util.Locale;

/**
 * 內容發佈狀態
 * @author wesleyzhuang
 *
 */
public enum PublishStatus {
	/**
	 * 已下架
	 */
	Offline(0),
	/**
	 * 已上架
	 */
	Online(1),
	/**
	 * 存為草稿
	 */
	Draft(2),
	/**
	 * 已排程（準備上架）
	 */
	Schedule(3)
	;
	
	private int value = 0;
	
	PublishStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	/**
	 * 以值找列舉物件
	 * @param value
	 * @return
	 */
	public static PublishStatus ofIntValue(int value) {
		PublishStatus[] vs = PublishStatus.values();
		for(PublishStatus v : vs) {
			if(v.getValue() == value) {
				return v;
			}
		}
		return null;
	}
	
	/**
	 * 以名稱找列舉物件
	 * @param name
	 * @return
	 */
	public static PublishStatus ofNameIgnoreCase(String name) {
		PublishStatus[] vs = PublishStatus.values();
		for(PublishStatus v : vs) {
			if(v.toString().toLowerCase(Locale.ENGLISH).contentEquals(name.toLowerCase(Locale.ENGLISH))) {
				return v;
			}
		}
		return null;
	}
}
