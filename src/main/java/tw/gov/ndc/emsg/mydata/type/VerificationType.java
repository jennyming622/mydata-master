package tw.gov.ndc.emsg.mydata.type;

import org.thymeleaf.util.StringUtils;

public enum VerificationType {

    cer, //自然人憑證
    fic, //晶片金融卡
    fch, //硬體金融憑證
    fcs, //軟體金融憑證
    moeaca, //工商憑證
    tfido,  //Tfido
    nhi,    //健保卡
    pii_1,  //雙證件(身分證發證日期+健保卡卡號)
    pii_2,  //雙證件(設籍戶口名簿戶號+健保卡卡號)
    emailOrMobile,  //動態密碼
    mobileId  // TWCA MSIDN門號所屬人員確認
    ;

    public static VerificationType convertToType(String level, String multifactorType) {
        if(StringUtils.equals("0", level)) {
            return VerificationType.cer;
        } else if(StringUtils.equals("0.1", level)) {
            return VerificationType.fic;
        } else if(StringUtils.equals("0.2", level)) {
            return VerificationType.fch;
        } else if(StringUtils.equals("0.5", level)) {
            return VerificationType.moeaca;
        } else if(StringUtils.equals("1", level)) {
            return VerificationType.tfido;
        } else if(StringUtils.equals("2", level)) {
            return VerificationType.nhi;
        } else if(StringUtils.equals("2.1", level)) {
            return VerificationType.fcs;
        } else if(StringUtils.equals("3", level) && StringUtils.equals(multifactorType, "12")) {
            return VerificationType.pii_1;
        } else if(StringUtils.equals("3", level) && StringUtils.equals(multifactorType, "10")) {
            return VerificationType.pii_2;
        } else if(StringUtils.equals("4", level)) {
        	return VerificationType.emailOrMobile;
        } else if(StringUtils.equals("5", level)) {
        	return VerificationType.mobileId;
        }
        return null;
    }
}
