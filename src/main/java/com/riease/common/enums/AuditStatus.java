package com.riease.common.enums;


public enum AuditStatus {
	/**
	 * GSP登入
	 */
	登入(1),
	授權(2),
	登出(3),
	/**
	 * MyData平台
	 */
	要求(4),
	傳送(5),
	儲存(6),
	/**
	 * 於MyData平台執行，取消授權特定client_id
	 */
	取消授權(7),
	/**
	 * 於MyData平台執行下載
	 */
	取用(8),
	/**
	 * SP要求取用資料
	 */
	申請(11),
	/**
	 * SP要求取用資料,MyData收到
	 */
	申請完成(12),
	/**
	 * SP要求取用資料,SP收到
	 */
	收到(13)
	;
	
	private Integer value;
	
	AuditStatus(Integer value){
		this.value = value;
	}
	
	public Integer getValue() {
		return this.value;
	}
	
	public static AuditStatus getStatus(Integer value) {
		AuditStatus[] as = AuditStatus.values();
		for(AuditStatus a : as) {
			if(a.getValue().equals(value)) {
				return a;
			}
		}
		return null;
	}
}
