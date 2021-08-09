package com.riease.common.enums;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class ActionStatus {
	private static Map<String,String> map = getClearStringCompareMap();
	
	private static Map<String,String> getClearStringCompareMap() {
		Map<String,String> map = new HashMap<String,String>();
		map.put("110", "民眾在SP做自然人憑證驗證");
		map.put("120", "SP 請求一次性驗證參數");
		map.put("130", "將壓密過的民眾的個人資料與簽章憑證傳給 MyData");
		map.put("140", "SP 跳轉至 MyData 同意頁");
		map.put("150", "MyData向內政部 API 驗民眾憑證與數位簽章");
		map.put("160", "MyData 呼叫 ICS API");
		map.put("170", "MyData 呼叫生日 API");
		map.put("180", "民眾於 MyData 頁面完成身分驗證");
		map.put("190", "自動註冊帳號");
		map.put("200", "發送手機認證簡訊");
		map.put("210", "完成手機認證");
		map.put("220", "發送 email 認證信");
		map.put("230", "完成 email 認證");
		map.put("240", "民眾同意傳輸資料給 SP");
		//DP
		map.put("250", "MyData 請求 DP 資料集");
		// /connect/introspect
		map.put("260", "DP 呼叫 Introspection API");
		// /connect/userinfo
		map.put("270", "DP 呼叫 UserInfo API");
		
		map.put("280", "MyData 取得DP資料集");
		map.put("290", "MyData呼叫SP-API通知取資料");
		map.put("300", "MyData 跳轉回 SP");
		map.put("310", "SP 呼叫 MyData-API 取個人資料");
		map.put("320", "民眾臨櫃核驗，MyData 發送資料條碼驗證密碼給民眾");
		map.put("330", "臨櫃人員輸入資料條碼驗證密碼");
		map.put("340", "MyData 發送資料取用通知簡訊/信（轉存、服務應用、條碼取用）");
		map.put("350", "MyData 刪除個人資料檔案");
		map.put("360", "SP 刪除個人資料檔案");
		return map;
	}
	
	public static String getByCode(String code) {
		return map.get(code);
	}
}
