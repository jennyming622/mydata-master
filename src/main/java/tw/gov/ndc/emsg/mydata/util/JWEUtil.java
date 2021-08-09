package tw.gov.ndc.emsg.mydata.util;

import org.apache.commons.lang3.StringUtils;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.keys.AesKey;
import org.jose4j.lang.JoseException;

import java.security.Key;

/**
 * JWE 工具物件
 */
public class JWEUtil {

    /**
     * 回傳encrypt後字串
     * @param payload
     * @param secretKey
     * @param iv
     * @return
     */
    public static String encrypt(String payload, String secretKey, byte[] iv){
        try {
            Key key = new AesKey(secretKey.getBytes());
            JsonWebEncryption jwe = new JsonWebEncryption();
            jwe.setPayload(payload);
            jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A256KW);
            jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_256_CBC_HMAC_SHA_512);
            jwe.setKey(key);
            jwe.setIv(iv);
            String serializedJwe = jwe.getCompactSerialization();
//            System.out.println("payload valid result -> "  + isSame(jwe,key,serializedJwe));
            return serializedJwe;

        } catch (JoseException e) {
            e.printStackTrace();
        }

        return "";
    }


    /**
     * for debug
     * 驗證加密後的jwe
     * @param jwe
     * @return
     */
    private static boolean isSame(JsonWebEncryption jwe, Key key ,String serializedJwe) throws JoseException {

        JsonWebEncryption jwe2 = new JsonWebEncryption();
            jwe.setAlgorithmConstraints(new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST,
                    KeyManagementAlgorithmIdentifiers.A256KW));
        jwe2.setContentEncryptionAlgorithmConstraints(new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST,
                ContentEncryptionAlgorithmIdentifiers.AES_256_CBC_HMAC_SHA_512));
        jwe2.setKey(key);
        jwe2.setCompactSerialization(serializedJwe);

        return StringUtils.equals(jwe.getPayload(),jwe2.getPayload());
    }


}
