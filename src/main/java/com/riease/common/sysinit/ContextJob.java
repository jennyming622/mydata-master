package com.riease.common.sysinit;


public interface ContextJob {

	/**
	 * 初始/執行job
	 * @return	0,	正常
	 * 			-1,	異常
	 */
	public int initialize();
	
	/**
	 * 結束job，回收資源
	 * @return
	 */
	public int destroy();
	
	/**
	 * Job Name
	 * @return
	 */
	public String getName();
	
}
