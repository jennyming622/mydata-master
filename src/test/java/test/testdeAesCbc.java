package test;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import javax.servlet.http.HttpServletResponse;

import tw.gov.ndc.emsg.mydata.util.CBCUtil;

public class testdeAesCbc {

	public static void main(String[] args) throws UnsupportedEncodingException {
		Base64.Decoder base64Decoder = Base64.getDecoder();
		// 簽章對象（加密本文）
		byte[] encryptedData = "LT9UYis3qzly25Ui3PGLgoSKKpeTmRj/3bZHys3DnubFHeLW6t4lEsDRN4VIu8ky/yISIRGriPPG4oZkLi1UitpeWsxWb3n33rR+iIRekibJhiIws5d/pXAh6PLuTrdv".getBytes("UTF-8");
        
        // decode & decrypt data
        String secretKey = "uupaDirioV321Di5uupaDirioV321Di5";
        byte[] dataJson;
        try {
        		dataJson = CBCUtil.decrypt_cbc(base64Decoder.decode(encryptedData),secretKey,"838KK2XuSt4DIs2N");
        		System.out.println(new String(dataJson));
	    } catch (Exception ex) {
	    		System.out.println(ex);
	    }
	}

}
