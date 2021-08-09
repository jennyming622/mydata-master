package tw.gov.ndc.emsg.dbencrypt;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 用於資料庫機敏資料欄位加解密之工具程式。
 * 採用 AES/ECB/PKCS5PADDING 加密演算法。
 */
public class DbEncryptUtils {

    /*
     * 重要！ secretKey 值不可異動，將導致已加密資料資料無法順利解密。
     */

    //加解密金鑰
    private static final String secretKey = "ws9egEAk2QxlQFQ8TYA4N6MJMgNZMlkS";
    //存入資料庫前加密值的前置碼
    private static final String PREFIX = "{aes/ecb}";

    private static Base64.Encoder base64Encoder = Base64.getEncoder();
    private static Base64.Decoder base64Decoder = Base64.getDecoder();

    private static Cipher encryptCipher;
    private static Cipher decryptCipher;

    static {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(seckey(),"AES");
            //encrypt
            encryptCipher = Cipher.getInstance(new String(base64Decoder.decode("QUVTL0VDQi9QS0NTNVBhZGRpbmc=")));
            encryptCipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            //decrypt
            decryptCipher = Cipher.getInstance(new String(base64Decoder.decode("QUVTL0VDQi9QS0NTNVBhZGRpbmc=")));
            decryptCipher.init(Cipher.DECRYPT_MODE, skeySpec);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    //值不可異動，將導致已加密資料資料無法順利解密。
    private static byte[] seckey() {
        return base64Decoder.decode("d3M5ZWdFQWsyUXhsUUZROFRZQTRONk1KTWdOWk1sa1M=");
    }

    /**
     * 以AES加密
     * @param value
     * @return
     */
    public static String encryptAES(String value) throws BadPaddingException, IllegalBlockSizeException {
        if (value == null) {
            return null;
        }
        else if(value.startsWith(PREFIX)) {
            return value;
        }
        else {
            return PREFIX + base64Encoder.encodeToString(encryptCipher.doFinal(value.getBytes()));
        }
    }

    /**
     * 以AES解密
     * @param encrypted
     * @return
     */
    public static String decryptAES(String encrypted) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        if (encrypted == null || encrypted.isEmpty()) {
            return encrypted;
        } else {
            //有前置碼才處理解密，代表它是已加密的字串。
            if(encrypted.startsWith(PREFIX)) {
                String substr = encrypted.substring(PREFIX.length());

                byte[] original = decryptCipher.doFinal(base64Decoder.decode(substr));
                String result = new String(original, "UTF-8");

                return result;
            }else {
                return encrypted;
            }
        }
    }

}
