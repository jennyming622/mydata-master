/**
 * 
 */
package com.riease.common.sysinit.service;

/**
 * 表單資料更新服務介面
 * @author wesleyzhuang
 *
 */
public interface CreateFormService<T> {

	/**
	 * 新增表單資料。
	 * @param form
	 */
	public void createForm(T form);
		
}
