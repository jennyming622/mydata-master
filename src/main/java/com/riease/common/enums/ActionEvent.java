package com.riease.common.enums;

import org.thymeleaf.util.StringUtils;

import java.util.Arrays;

public enum ActionEvent {
    DEFAULT("-1", "無"),
    EVENT_110("110", "民眾在SP驗證身分"),
    EVENT_120("120", "SP 請求一次性驗證參數"),
    EVENT_121("121", "MyData 回覆一次性認證參數"),
    EVENT_130("130", "將壓密過的民眾的個人資料與簽章憑證傳給 MyData"),
    EVENT_131("131", "MyData 回覆身分驗證成功"),
    EVENT_140("140", "SP 跳轉至 MyData 同意頁"),
    EVENT_150("150", "MyData向第三方驗證中心驗證身分"),    // MyData向第三方驗證中心驗證身分
    EVENT_151("151", "MyData向第三方驗證中心驗證身分(成功)"), // MyData向第三方驗證中心驗證身分結束
    EVENT_160("160", "MyData 呼叫 ICSAPI"),
    EVENT_161("161", "MyData 呼叫 ICSAPI(成功)"),
    EVENT_170("170", "MyData 呼叫生日 API"),
    EVENT_171("171", "MyData 呼叫生日 API(成功)"),
    EVENT_180("180", "民眾於 MyData 頁面進行身分驗證(成功)"),
    EVENT_190("190", "自動註冊帳號"),
    EVENT_200("200", "發送手機認證簡訊"),
    EVENT_210("210", "手機認證成功"),
    EVENT_220("220", "發送 email 認證信"),
    EVENT_230("230", "email 認證成功"),
    EVENT_240("240", "民眾同意傳輸資料給 SP"),
    EVENT_241("241", "民眾點擊不同意傳送"),
    EVENT_250("250", "MyData 請求 DP 資料集"),
    EVENT_260("260", "DP呼叫 IntrospectionAPI"),
    EVENT_270("270", "DP呼叫 UserInfoAPI"),
    EVENT_275("275", "驗證：您完成身分驗證(dp)"),   //取資料前紀錄
    EVENT_280("280", "MyData 取得 DP 資料集"),
    EVENT_290("290", "MyData 呼叫 SP-API 通知取資料"),
    EVENT_291("291", "SP 回應 SP-API 完成"),
    EVENT_292("292", "SP 回應 SP-API 成功"),
    EVENT_300("300", "MyData 跳轉回 SP"),
    EVENT_310("310", "SP 呼叫 MyData-API 取個人資料"),
    EVENT_320("320", "民眾臨櫃核驗，MyData 發送驗證密碼給民眾"),
    EVENT_330("330", "臨櫃人員輸入資料條碼驗證碼"),
    EVENT_340("340", "MyData 發送資料取用通知簡 訊/信(自行儲存、服務應用、臨櫃核驗)"),
    EVENT_350("350", "MyData 刪除個人資料檔案"),
    EVENT_360("360", "SP 刪除個人資料檔案"),
    ;


    private String code;
    private String text;

    ActionEvent(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    public static ActionEvent ofCode(String code) {
        return Arrays.stream(values()).filter(actionEvent -> StringUtils.equals(actionEvent.getCode(), code))
                .findFirst().orElse(DEFAULT);
    }
}
