/**
 * 
 */
package com.riease.common.sysinit;

/**
 * 系統代碼，含錯誤代碼。<br>
 * 用戶操作面的錯誤代碼由 -1 起頭：<br>
 *  - 身分驗證及權限相關的錯誤代碼由 -11 起頭<br>
 *  - 動作及參數值相關的錯誤代碼由 -12 起頭<br>
 * 系統面的錯誤代碼由 -2 起頭<br>
 *  - 系統功能面的錯誤代碼由 -21 起頭<br>
 * 
 * @author Wesley
 *
 */
public enum SysCode {
	
	/**
	 * 0, 正常 
	 */
	OK(0),
	
	// -- 用戶操作面的錯誤代碼由 -1 起頭 --
	/**
	 * -1, 操作錯誤
	 */
	OperationFailed(-1),
	
	// -- 身分驗證及權限相關的錯誤代碼由 -11 起頭 --
	/**
	 * -1101, 拒絕存取
	 */
	RequestRefused(-1101),
	/**
	 * -1102, 無存取權限
	 */
	NoPermission(-1102),
	/**
	 * -1103, 無效的動作
	 */
	InvalidAction(-1103),
	/**
	 * -1104, 尚未完成身分驗證
	 */
	NotAuthenticated(-1104),
	/**
	 * -1105, 身分驗證錯誤
	 */
	AuthenticateFail(-1105),
	/**
	 * -1106, 帳號不存在
	 */
	AccountNotExist(-1106),
	/**
	 * -1107, 帳號已停用
	 */
	AccountDisabled(-1107),
	/**
	 * -1108, 帳號已經啟用，無需再啟用
	 */
	AccountActivated(-1108),
	/**
	 * -1109, 帳號未啟用
	 */
	AccountNotActivated(-1109),
	/**
	 * -1110, 圖型驗證碼不正確
	 */
	InvalidCaptcha(-1110),
	/**
	 * -1111, 存取被拒絕
	 */
	AccessDenied(-1111),
	/**
	 * -1112, 不是被允許的IP
	 */
	NotAllowedIp(-1112),
	/**
	 * -1113, 不是被允許的app代碼
	 */
	NotAllowedAppId(-1113),
	/**
	 * -1114, 帳號已被鎖定
	 */
	AccountLocked(-1114),
	/**
	 * -1115, 帳號已存在
	 */
	AccountExist(-1115),
	/**
	 * -1116, 代辦人不可為同一使用者
	 */
	AgentIsNotSameUser(-1116),
	/**
	 * -1117, 代辦人不存在
	 */
	AgentIsExit(-1117),
	/**
	 * -1118, 代辦人聯絡資料不全
	 */
	AgentMemberNotFill(-1118),
	
	// -- 動作及參數值相關的錯誤代碼由 -12 起頭 -- 
	/**
	 * -1201, 缺少必要參數
	 */
	MissingRequiredParameter(-1201),
	/**
	 * -1202, 無效的參數
	 */
	InvalidParameter(-1202),
	/**
	 * -1203, 格式不正確
	 */
	IncorrectFormat(-1203),
	/**
	 * -1204, 資料不存在
	 */
	DataNotExist(-1204),
	/**
	 * -1205, 資料已存在
	 */
	DataExist(-1205),
	/**
	 * -1206, 資料已被參考使用 
	 */
	DataReferenced(-1206),
	/**
	 * -1207, 資料已被處理或執行
	 */
	DataProcessed(-1207),
	/**
	 * -1208, 驗證碼發送失敗
	 */
	VerifyCodeSendFailed(-1208),
	/**
	 * -1209, 無效的驗證碼
	 */
	InvalidVerifyCode(-1209),
	/**
	 * -1210, 資料庫存取失敗
	 */
	DatabaseAccessFailed(-1210),
	/**
	 * -1211, 身分驗證失敗，請重新輸入。
	 */
	AccountNotSynBirthDateDisabled(-1211),
		
	// -- 系統面的錯誤由 -2 起頭 -- 
	/**
	 * -2, 系統錯誤
	 */
	SystemError(-2),
	/**
	 * 資料庫錯誤
	 */
	DatabaseError(-2101),
	

	// -- 商業邏輯面的錯誤由 -3 起頭 --
	BusinessLogicError(-3),
	/**
	 * 會員類別不符合
	 */
	IncorrectMemberType(-3101),

	//自定義
	CustomError(-4),

	/**
	 * Session Timeout
	 */
	SessionTimeout(-5),
	;
	
	
	int value = 0;
	
	/**
	 * 
	 */
	private SysCode(int value) {
		this.value = value;
	}
	
	/**
	 * 回傳數值型態的系統代碼
	 * @return
	 */
	public int value() {
		return this.value;
	}

	/**
	 * 回傳字串型態的系統代碼
	 * @return
	 */
	public String stringValue() {
		return String.valueOf(value);
	}
	
	/**
	 * 以字串型態的值轉換為物件。
	 * @param value
	 * @return
	 */
	public static SysCode ofStringValue(String value) {
		SysCode[] ss = SysCode.values();
		for(SysCode s : ss) {
			if(String.valueOf(s.value()).contentEquals(value)) {
				return s;
			}
		}
		return null;
	}
	
	/**
	 * 以數值型態的值轉換為物件
	 * @param value
	 * @return
	 */
	public static SysCode ofIntValue(int value) {
		SysCode[] ss = SysCode.values();
		for(SysCode s : ss) {
			if(s.value() == value) {
				return s;
			}
		}
		return null;
	}
		
}
