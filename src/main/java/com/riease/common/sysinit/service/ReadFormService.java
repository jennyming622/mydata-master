/**
 * 
 */
package com.riease.common.sysinit.service;

import java.util.List;
import java.util.Map;

import com.riease.common.bean.PagingInfo;

/**
 * 表單資料讀取服務介面
 * @author wesleyzhuang
 *
 */
public interface ReadFormService<T> {

	/**
	 * 查詢單筆表單資料
	 * @param id
	 * @return
	 */
	public T oneForm(int id);
	
	/**
	 * 查詢表單資料列表
	 * @param params
	 * @param paging
	 * @return
	 */
	public List<T> listForm(Map<String,Object> params, PagingInfo paging);
	
	/**
	 * 查詢表單資料列表
	 * @param params
	 * @return
	 */
	public List<T> listForm(Map<String,Object> params);
	
	/**
	 * 查詢數量
	 * @param params
	 * @return
	 */
	public long countForm(Map<String,Object> params);
}
