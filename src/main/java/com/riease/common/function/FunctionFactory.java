/**
 * 
 */
package com.riease.common.function;

/**
 * @author Cid
 *
 */
public class FunctionFactory {

	public static FunctionFactory get() {
		return new FunctionFactory();
	}
	
	public FunctionItem menu(String id, String text) {
		return new FunctionMenu(id, text);
	}
	
	public FunctionItem cmd(String id, String text) {
		return new FunctionCommand(id, text);
	}
	
	public FunctionItem cmdAdd(String id) {
		return cmd(id, "新增");
	}
	
	public FunctionItem cmdEdit(String id) {
		return cmd(id, "編輯");
	}
	
	public FunctionItem cmdDel(String id) {
		return cmd(id, "刪除");
	}
	
	public FunctionItem cmdChPwd(String id) {
		return cmd(id, "變更密碼");
	}
	
	public FunctionItem cmdQuery(String id) {
		return cmd(id, "查詢");
	}
	
	public FunctionItem cmdSend(String id) {
		return cmd(id, "發送");
	}
	
	public FunctionItem cmdOn(String id) {
		return cmd(id, "上架");
	}
	
	public FunctionItem cmdOff(String id) {
		return cmd(id, "下架");
	}
	
	
	
}
