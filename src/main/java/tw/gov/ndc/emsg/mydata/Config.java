package tw.gov.ndc.emsg.mydata;

import java.util.Locale;

import com.riease.common.sysinit.InitConfig;

/**
 * 繼承自com.riease.sysinit.InitConfig
 * 注意所需的property是否已被宣告於InitConfig。
 * 註：未來有機會被重覆使用的項目，宣告於InitConfig。
 * 
 * @author wesleyzhuang
 * @see com.riease.common.sysinit.InitConfig
 */
public class Config extends InitConfig { 
	/**
	 * 目前版本
	 */
	public static final String Version = "1.0.02-20170613";
	
	/**
	 * 會員預設語系
	 */
	public static final Locale MemberDefaultLocale = new Locale("zh","CN");
	/**
	 * 網址contextUrl
	 */
	public static final String AppContextUrl = getValue("app.frontend.context.url");	
	
	/**
	 * 排程參數
	 */
	public static final boolean CronExp_Enable = getBooleanValue("CronExp_Enable");	
	public static final boolean Organ_Enable = getBooleanValue("Organ_Enable");	
	public static final String file_CronExp = getValue("file_CronExp");
	public static final String mailEnable = getValue("mail.enable");
	/**
	 * 檔案上傳暫存目錄
	 */
	public static String FileUpload_TempDir = getValue("app.download.path.temp");
	/**
	 * datasource.cbc.iv.base64
	 */
	public static final String cbcIvBase64str = getValue("datasource.cbc.iv.base64");
	/**
	 * datasource.secret.base64
	 */
	public static final String secretBase64str = getValue("datasource.secret.base64");
	/**
	 * keystorePublicCrypto
	 */
	public static final String keystorePublicCrypto = getValue("gsp.oidc.nhicard.keystorePublicCrypto");
	public static final String logoPath = getValue("logo.path");
	public static final String mailServerHost = getValue("mail.server.host");
	public static final String mailServerPort = getValue("mail.server.port");
	public static final String mailServerUserName = getValue("mail.server.username");
	public static final String mailServerPassword = getValue("mail.server.password");
	public static final String emailPfxPath = getValue("email.pfx.path");
	/**
	 * ValidatorHelper char
	 */
	public static final String char1 = getValue("ValidatorHelper.char1");
	public static final String char2 = getValue("ValidatorHelper.char2");
	public static final String char3 = getValue("ValidatorHelper.char3");
	public static final String char4 = getValue("ValidatorHelper.char4");
	public static final String char5 = getValue("ValidatorHelper.char5");
	public static final String char6 = getValue("ValidatorHelper.char6");
	public static final String char7 = getValue("ValidatorHelper.char7");
	public static final String char8 = getValue("ValidatorHelper.char8");
	public static final String char9 = getValue("ValidatorHelper.char9");
	public static final String char10 = getValue("ValidatorHelper.char10");
	public static final String char11 = getValue("ValidatorHelper.char11");
	public static final String char12 = getValue("ValidatorHelper.char12");
	public static final String char13 = getValue("ValidatorHelper.char13");
	public static final String char14 = getValue("ValidatorHelper.char14");
	public static final String char15 = getValue("ValidatorHelper.char15");
	public static final String char16 = getValue("ValidatorHelper.char16");
	public static final String char17 = getValue("ValidatorHelper.char17");
	public static final String char18 = getValue("ValidatorHelper.char18");
	public static final String char19 = getValue("ValidatorHelper.char19");
	public static final String char20 = getValue("ValidatorHelper.char20");
	public static final String char21 = getValue("ValidatorHelper.char21");
	public static final String char22 = getValue("ValidatorHelper.char22");
	public static final String char23 = getValue("ValidatorHelper.char23");
	public static final String char24 = getValue("ValidatorHelper.char24");
	public static final String char25 = getValue("ValidatorHelper.char25");
	public static final String char26 = getValue("ValidatorHelper.char26");
	public static final String char27 = getValue("ValidatorHelper.char27");
	public static final String char28 = getValue("ValidatorHelper.char28");
	public static final String char29 = getValue("ValidatorHelper.char29");
	public static final String char30 = getValue("ValidatorHelper.char30");
	public static final String char31 = getValue("ValidatorHelper.char31");
}
