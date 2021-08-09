/**
 * 
 */
package com.riease.common.bean;

/**
 * 用來輔助實作分頁機制的物件
 * @author wesleyzhuang
 *
 */
public class PagingInfo {

	private String target;
	private int pageIndex = 0;
	private int pageSize = 20;
//	private long totalSize = 0;
	
	public PagingInfo() {
	}
	
	public PagingInfo(int pageIndex, int pageSize) {
		setPageIndex(pageIndex);
		setPageSize(pageSize);
	}
	
	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}
	/**
	 * @param target the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}
	/**
	 * @return the pageIndex
	 */
	public int getPageIndex() {
		return pageIndex;
	}
	/**
	 * @param pageIndex the pageIndex to set
	 */
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}
	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
//	/**
//	 * @return the totalSize
//	 */
//	public long getTotalSize() {
//		return totalSize;
//	}
//	/**
//	 * @param totalSize the totalSize to set
//	 */
//	public void setTotalSize(long totalSize) {
//		this.totalSize = totalSize;
//	}
	
}
