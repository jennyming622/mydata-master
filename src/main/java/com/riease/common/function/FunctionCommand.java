/**
 * 
 */
package com.riease.common.function;

/**
 * @author Cid
 *
 */
public class FunctionCommand extends FunctionItem {

	//啟用 logger 記錄
	private boolean enableLogger = false;
	
	/**
	 * 
	 */
	public FunctionCommand() {
	}
	
	public FunctionCommand(String id, String name) {
		super(id, name);
	}
	
	/**
	 * @return the enableLogger
	 */
	public boolean isEnableLogger() {
		return enableLogger;
	}

	/**
	 * @param enableLogger the enableLogger to set
	 */
	public void setEnableLogger(boolean enableLogger) {
		this.enableLogger = enableLogger;
	}

	/* (non-Javadoc)
	 * @see com.riease.common.function.FunctionItem#getType()
	 */
	@Override
	public FunctionItemType getType() {
		return FunctionItemType.Command;
	}
	
}
