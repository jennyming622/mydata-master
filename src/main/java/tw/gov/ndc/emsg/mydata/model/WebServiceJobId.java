package tw.gov.ndc.emsg.mydata.model;

import java.util.Arrays;

public enum  WebServiceJobId {

    // 驗證生日
    BirthDate("V1C103"),
    // 驗證身分證換補發
    IdMarkAndHouseHold("V1C104");

    private String value;

    WebServiceJobId(String value) {
        this.value = value;
    }

    public String getStringValue(){
        return value;
    }

    static WebServiceJobId jobOf(String v ){
        return Arrays.stream(WebServiceJobId.values())
                .filter(v::equals)
                .findFirst()
                .orElse(null);
    }
}
