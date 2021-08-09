package tw.gov.ndc.emsg.mydata.model;

import java.util.Arrays;

public enum  NhiVerifyResponse {


    Success("A0000"), // 成功
    Fail_Not_Exist("A9999"), //輸入不合規則身份證號、輸入不合規則健保卡卡號
    Fail_Incompatible("A8899"); //健保卡卡號或身分證字號錯誤

    String value = "";

    NhiVerifyResponse(String value) {
        this.value = value;
    }

    public String getValue(){return value;}

    static NhiVerifyResponse valuesOf(String v){
        return Arrays.stream(NhiVerifyResponse.values())
                .filter(res -> res.equals(v))
                .findFirst()
                .orElse(null);
    }

}
