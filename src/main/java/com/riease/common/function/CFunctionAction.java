/**
 * 
 */
package com.riease.common.function;

/**
 * @author Cid
 *
 */
public enum CFunctionAction {

	/**
	 * 新增
	 */
	Create,
	
	/**
	 * 更新，編輯
	 */
	Update,
	
	/**
	 * 查詢，觀看
	 */
	Read,
	
	/**
	 * 刪除
	 */
	Delete,
	
	/**
	 * 審核
	 */
	Audit,
	;
	
	/**
	 * 是否存在
	 * @param action
	 * @return
	 */
	public static boolean exist(String action) {
		CFunctionAction[] as = CFunctionAction.values();
		for(CFunctionAction a : as) {
			if(a.toString().equals(action)) {
				return true;
			}
		}
		return false;
	}
}
