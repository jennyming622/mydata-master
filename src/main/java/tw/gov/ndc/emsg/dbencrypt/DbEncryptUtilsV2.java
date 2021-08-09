package tw.gov.ndc.emsg.dbencrypt;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import tw.gov.ndc.emsg.mydata.Config;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 用於資料庫機敏資料欄位加解密之工具程式。
 * 採用 AES/CBC/PKCS5PADDING 加密演算法。
 * 如果原本加密資料採用 AES/ECB/PKCS5PADDING 會將之轉換為 AES/CBC/PKCS5PADDING 後儲存。
 */
public class DbEncryptUtilsV2 implements InitializingBean{

    //存入資料庫前加密值的前置碼
    private static final String PREFIX_CBC = "{aes/cbc}";

    private static Base64.Encoder base64Encoder = Base64.getEncoder();
    private static Base64.Decoder base64Decoder = Base64.getDecoder();

    private static Cipher encryptCipherCbc;
    private static Cipher decryptCipherCbc;
    
    private static String cbcIvBase64str = Config.cbcIvBase64str;
    private static String secretBase64str = Config.secretBase64str;

    private static Lock lockE = new ReentrantLock();
    private static Lock lockD = new ReentrantLock();
    static {
        try {
            IvParameterSpec iv = new IvParameterSpec(iv());
            SecretKeySpec skeySpec = new SecretKeySpec(seckey(),"AES");
            //encrypt
            encryptCipherCbc = Cipher.getInstance(new String(base64Decoder.decode("QUVTL0NCQy9QS0NTNVBBRERJTkc=")));
            encryptCipherCbc.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            //decrypt
            decryptCipherCbc = Cipher.getInstance(new String(base64Decoder.decode("QUVTL0NCQy9QS0NTNVBBRERJTkc=")));
            decryptCipherCbc.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    //值不可異動，將導致已加密資料資料無法順利解密。
    private static byte[] iv() {
		return base64Decoder.decode(cbcIvBase64str);
    }
    //值不可異動，將導致已加密資料資料無法順利解密。
    private static byte[] seckey() {
    		return base64Decoder.decode(secretBase64str);
    }


    /**
     * 以AES加密
     * @param value
     * @return
     */
    public static String encryptAES(String value) throws BadPaddingException, IllegalBlockSizeException {
        lockE.lock();
        try {
            if (value == null) {
                return null;
            } else if (value.startsWith(PREFIX_CBC)) {
                return value;
            } else {
                return PREFIX_CBC + base64Encoder.encodeToString(encryptCipherCbc.doFinal(value.getBytes()));
            }
        } finally {
            lockE.unlock();
        }
    }

    /**
     * 以AES解密
     * @param encrypted
     * @return
     */
    public static String decryptAES(String encrypted) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        lockD.lock();
        try {
            if (encrypted == null || encrypted.isEmpty()) {
                return encrypted;
            } else {
                //有前置碼才處理解密，代表它是已加密的字串。
                if (encrypted.startsWith(PREFIX_CBC)) {
                    String substr = encrypted.substring(PREFIX_CBC.length());
                    byte[] original = decryptCipherCbc.doFinal(base64Decoder.decode(substr));
                    String result = new String(original, "UTF-8");
                    return result;
                } else {
                    return encrypted;
                }
            }
        } finally {
            lockD.unlock();
        }
    }
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
