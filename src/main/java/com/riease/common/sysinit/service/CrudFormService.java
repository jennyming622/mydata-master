/**
 * 
 */
package com.riease.common.sysinit.service;

import java.util.List;
import java.util.Map;

import com.riease.common.bean.PagingInfo;

/**
 * 表單資料CRUD管理的服務介面
 * @author wesleyzhuang
 *
 */
public interface CrudFormService<T> {

	/**
	 * 新增/更新表單資料。
	 * 有鍵值就是更新，沒有鍵值就是新增。
	 * @param form
	 */
	public void saveForm(T form);

	/**
	 * 查詢單筆表單資料
	 * @param id
	 * @return
	 */
	public T oneForm(int id);
	
	/**
	 * 移除表單資料
	 * @param roleId
	 * @return
	 */
	public int removeForm(List<Integer> idList);
	
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
