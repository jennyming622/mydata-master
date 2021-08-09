/**
 * 
 */
package com.riease.common.sysinit.rest;

import java.util.List;

/**
 * 配合jQuery DataTables plugins 使用的資料回傳物件
 * @author wesleyzhuang
 *
 */
public class DataTableResponseBean<T> {

	private int draw;
	private long recordsTotal;
	private long recordsFiltered;
	private List<T> data;
	
	
	public int getDraw() {
		return draw;
	}
	public void setDraw(int draw) {
		this.draw = draw;
	}
	public long getRecordsTotal() {
		return recordsTotal;
	}
	public void setRecordsTotal(long recordsTotal) {
		this.recordsTotal = recordsTotal;
	}
	public long getRecordsFiltered() {
		return recordsFiltered;
	}
	public void setRecordsFiltered(long recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}
	public List<T> getData() {
		return data;
	}
	public void setData(List<T> data) {
		this.data = data;
	}
	
}
