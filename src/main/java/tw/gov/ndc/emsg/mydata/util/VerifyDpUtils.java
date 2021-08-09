package tw.gov.ndc.emsg.mydata.util;

import com.google.common.hash.Hashing;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerifyDpUtils {

    private static Logger logger = LoggerFactory.getLogger(VerifyDpUtils.class);
    private static Element root;

    private static Map<String, String> pdfMap = new HashMap<>();
    private static Map<String, String> jsonMap = new HashMap<>();

    private static File manifestFile = null;

    private static StringBuilder sb = new StringBuilder();

    public static String testDpZip(File packDir){

        unzipFile(packDir);
        testVerify();
        testDpData();

        String result = sb.toString();
        sb.setLength(0);
        logger.debug("result -> " + result);
        return result;
    }


    /**
     * 解壓縮檔案
     */
    public static void unzipFile(File packDir) {
        SpUtils utils = new SpUtils();
//        File packDir = new File(dirPath + dirName + ".zip");
        // 產生解壓縮目的地的File物件
        String resName = FilenameUtils.removeExtension(packDir.getName());
        File targetFile = new File(packDir.getParent() + File.separator + resName + File.separator);
        try {
            if (packDir.exists()) {
                utils.unzip(packDir, targetFile);
            }
            // 去除掉mac系統的隱藏檔
            File[] fs = targetFile.listFiles((dir, name) -> !name.equals(".DS_Store"));
            if (fs.length < 1) {
                utils.unzip(packDir, targetFile);
            }
            logger.debug("before deconstruction");
            targetFileDeconstruction(fs);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解構targetFile資料夾
     */
    private static void targetFileDeconstruction(File[] targetFiles) {

        for (File f : targetFiles) {
            if (!f.isDirectory()) {
                // 需要解析的pdf, json(p7b)
                String extension = FilenameUtils.getExtension(f.getName());
                switch (extension) {
                    case "pdf":
                        String pdfPath = f.getPath();
                        String pdfName = f.getName();
                        pdfMap.put(pdfName, pdfPath);
                        break;
                    case "csv":
                    case "p7b":
                    case "json":
                        String jsonName = f.getName();
                        String jsonPath = f.getPath();
                        jsonMap.put(jsonName, jsonPath);
                        break;
//                        jsonName = f.getName();
                }
            } else {
                targetFileDeconstruction(f.listFiles());
            }
        }

    }

    public static void testVerify() {
        logger.debug("do test verify");
        SpUtils utils = new SpUtils();
        File jsonFile = null, pdfFile = null;
        File packDir = null;
        for(String jsonName : jsonMap.keySet()) {
            jsonFile = new File(jsonMap.get(jsonName)) ;
            if(packDir == null && jsonFile!=null && jsonFile.exists()){
                packDir = jsonFile.getParentFile();
                break;
            }
        }
        for(String pdfName : pdfMap.keySet()) {
            pdfFile = new File(pdfMap.get(pdfName)) ;
            if(packDir == null && pdfFile!=null && pdfFile.exists()){
                packDir = pdfFile.getParentFile();
            }
        }

        if(packDir == null){
            // 無json和pdf，數位簽章必定失敗
            System.out.println("DP打包檔數位簽章驗證失敗");
            sb.append("DP打包檔數位簽章驗證失敗\n");
            return;
        }

        manifestFile = new File(packDir.getPath() + File.separator + "META-INFO"+File.separator+"manifest.xml");

        try {
            boolean verifyDpSignatur = utils.verifySignature(packDir);
            if (verifyDpSignatur) {
                System.out.println("DP打包檔數位簽章驗證成功");
                sb.append("DP打包檔數位簽章驗證成功\n");
            } else {
                System.out.println("DP打包檔數位簽章驗證失敗");
                sb.append("DP打包檔數位簽章驗證失敗\n");
            }
        } catch (IOException | InvalidKeyException | CertificateException | SignatureException e) {
            e.printStackTrace();
        }
    }


    public static void testDpData() {
        /**
         * XML處理 dom4j
         */

        // 組成manifest存放路徑
//        String dirMeta = File.separator +"META-INFO" + File.separator + "manifest.xml";
//        String manifestPathStr = dirPath + dirName + dirMeta;
//        File manifestfile = new File(manifestPathStr);
        File manifestfile = manifestFile;
        Map<String, String> hashmap = new HashMap<String, String>();
        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read(manifestfile);
            root = doc.getRootElement();
            List<Element> childElements = root.elements();
            Integer idx = new Integer(0);
            for (Element child : childElements) {
                idx++;
                hashmap.put(child.elementText("filename"), child.elementText("digest"));
                System.out.println("[" + idx + "] filename= " + child.elementText("filename"));
                System.out.println("[" + idx + "] digest= " + child.elementText("digest"));
                sb.append("[" + idx + "] filename= " + child.elementText("filename")).append("\n");
                sb.append("[" + idx + "] digest= " + child.elementText("digest")).append("\n");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        /**
         * SHA-256驗證
         */

        System.out.println(StringUtils.rightPad("", 20, "=="));

        try {

            Integer idx = new Integer(0);
            for(String pdfName : pdfMap.keySet()) {
                String pdfPathStr = pdfMap.get(pdfName) ;
                if (StringUtils.isNotEmpty(pdfName)) {
                    idx++;
                    byte[] b = Files.readAllBytes(Paths.get(pdfPathStr));
                    String sha256file = Hashing.sha256().hashBytes(b).toString();
                    System.out.println("[" + idx + "] SHA256-PDF file：" + Hashing.sha256().hashBytes(b).toString());
                    sb.append("[" + idx + "] SHA256-PDF file：" + Hashing.sha256().hashBytes(b).toString()).append("\n");
                    String sha256manifest = hashmap.get(pdfName);
                    if (sha256file.equalsIgnoreCase(sha256manifest)) {
                        System.out.println("[" + idx + "] ==SHA-256 PDF==：" + true);
                        sb.append("[" + idx + "] ==SHA-256 PDF==：" + true).append("\n");
                    } else {
                        System.out.println("[" + idx + "] ==SHA-256 PDF==：" + false);
                        sb.append("[" + idx + "] ==SHA-256 PDF==：" + false).append("\n");
                    }
                }
            }
            // 有些資料集沒有json
            for(String jsonName : jsonMap.keySet()) {
                String jsonPathStr = jsonMap.get(jsonName) ;
                if (StringUtils.isNotEmpty(jsonName)) {
                    idx++;
                    byte[] bs = Files.readAllBytes(Paths.get(jsonPathStr));
                    String sha256filebs = Hashing.sha256().hashBytes(bs).toString();
                    System.out.println("[" + idx + "] SHA256-JSON file：" + Hashing.sha256().hashBytes(bs).toString());
                    sb.append("[" + idx + "] SHA256-JSON file：" + Hashing.sha256().hashBytes(bs).toString()).append("\n");
                    String sha256manifestbs = hashmap.get(jsonName);
                    if (sha256filebs.equalsIgnoreCase(sha256manifestbs)) {
                        System.out.println("[" + idx + "] ==SHA-256 JSON==：" + true);
                        sb.append("[" + idx + "] ==SHA-256 JSON==：" + true).append("\n");
                    } else {
                        System.out.println("[" + idx + "] ==SHA-256 JSON==：" + false);
                        sb.append("[" + idx + "] ==SHA-256 JSON==：" + true).append("\n");
                    }
                }
            }


            logger.debug("print sb -> {}" , sb.toString() );


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
