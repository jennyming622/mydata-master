package tw.gov.ndc.emsg.mydata.util;

import com.google.common.hash.Hashing;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.PrivateKey;

public class DigestUtil {
    // 產生檔案檔案數位簽章（digest）
    public static String generateFileDigest(File f) throws Exception{
        // http code == 204
        if(f == null || !f.exists()){
            return "";
        }

        byte[] b = Files.readAllBytes(Paths.get(f.getPath()));
        String  sha256FileDigest = Hashing.sha256().hashBytes(b).toString();
        System.out.println("digest -> " + sha256FileDigest);

        return sha256FileDigest;
    }
}
