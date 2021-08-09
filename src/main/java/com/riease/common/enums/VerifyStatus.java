/**
 * 
 */
package com.riease.common.enums;

/**
 * 驗證狀態
 * @author wesleyzhuang
 *
 */
public enum VerifyStatus {
	/**
	 * 未驗證
	 */
	NotVerified(0),
	/**
	 * 已驗證，成功
	 */
	Verified(1),
	/**
	 * 驗證失敗
	 */
	VerifyFailed(2)
	;
	
	private int value = 0;
	
	VerifyStatus(int value) {
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
	public static VerifyStatus ofIntValue(int value) {
		VerifyStatus[] vs = VerifyStatus.values();
		for(VerifyStatus v : vs) {
			if(v.getValue() == value) {
				return v;
			}
		}
		return null;
	}
}
