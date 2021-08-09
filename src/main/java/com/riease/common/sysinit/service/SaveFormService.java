/**
 * 
 */
package com.riease.common.sysinit.service;

/**
 * 表單資料更新服務介面
 * @author wesleyzhuang
 *
 */
public interface SaveFormService<T> {

	/**
	 * 新增/更新表單資料。
	 * @param form
	 */
	public void saveForm(T form);
		
}
