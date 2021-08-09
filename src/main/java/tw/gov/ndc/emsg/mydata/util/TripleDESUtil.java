package tw.gov.ndc.emsg.mydata.util;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Base64;

public class TripleDESUtil {

    //public static final String CIPHER_ALGORITHM_CBC = "DESede/CBC/NoPadding";
    private static Base64.Decoder base64Decoder = Base64.getDecoder();
    public static byte[] encrypt(byte[] hexData, String key, byte[] ivData)
            throws Exception
    {
        byte[] sKey = secretKey(key);

        SecretKeySpec skeySpec = new SecretKeySpec(sKey,new String(base64Decoder.decode("REVTZWRl")));

        // 向量鰎值
        IvParameterSpec iv = new IvParameterSpec(ivData);

        // 實例化
        Cipher cipher = Cipher.getInstance(new String(base64Decoder.decode("REVTZWRlL0NCQy9Ob1BhZGRpbmc=")));
        // 初始化，設置為加密模式
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

        return cipher.doFinal(hexData);
    }

    public static byte[] decrypt(byte[] hexData, String key, byte[] ivData)
            throws Exception
    {
        byte[] sKey = secretKey(key);

        SecretKeySpec skeySpec = new SecretKeySpec(sKey,new String(base64Decoder.decode("REVTZWRl")));

        // 向量鰎值
        IvParameterSpec iv = new IvParameterSpec(ivData);

        // 實例化
        Cipher cipher = Cipher.getInstance(new String(base64Decoder.decode("REVTZWRlL0NCQy9Ob1BhZGRpbmc=")));
        // 初始化，設置為加密模式
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

        return cipher.doFinal(hexData);
    }

    private static byte[] secretKey(String key) {
        byte[] tmp = h2b(key);
        if(tmp.length == 24) {
            return tmp;
        }

        byte[] sKey = new byte[24];
        System.arraycopy(tmp, 0, sKey, 0, 16);
        System.arraycopy(tmp, 0, sKey, 16, 8);
        return sKey;
    }

    public static byte[] h2b(String hex)
    {
        if ((hex.length() & 0x01) == 0x01)
            throw new IllegalArgumentException();
        byte[] bytes = new byte[hex.length() / 2];
        for (int idx = 0; idx < bytes.length; ++idx) {
            int hi = Character.digit((int) hex.charAt(idx * 2), 16);
            int lo = Character.digit((int) hex.charAt(idx * 2 + 1), 16);
            if ((hi < 0) || (lo < 0))
                throw new IllegalArgumentException();
            bytes[idx] = (byte) ((hi << 4) | lo);
        }
        return bytes;
    }


    public static String b2h(byte[] bytes)
    {
        char[] hex = new char[bytes.length * 2];
        for (int idx = 0; idx < bytes.length; ++idx) {
            int hi = (bytes[idx] & 0xF0) >>> 4;
            int lo = (bytes[idx] & 0x0F);
            hex[idx * 2] = (char) (hi < 10 ? '0' + hi : 'A' - 10 + hi);
            hex[idx * 2 + 1] = (char) (lo < 10 ? '0' + lo : 'A' - 10 + lo);
        }
        return new String(hex);
    }

    public static String toHex(String arg) throws UnsupportedEncodingException {
        String hexStr = String.format("%040x", new BigInteger(1, arg.getBytes("UTF-8")));
        int remainder = 8 - ((hexStr.length() / 2) % 8);
        if(remainder == 8) remainder = 0;
        String paddingStr = StringUtils.repeat("00", remainder);
        return hexStr + paddingStr;
    }

    public static byte[] string2b(String data) throws UnsupportedEncodingException {
        return h2b(toHex(data));
    }
}
