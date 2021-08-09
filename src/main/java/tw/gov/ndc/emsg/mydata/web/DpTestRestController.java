package tw.gov.ndc.emsg.mydata.web;

import com.riease.common.bean.Base64File;
import com.riease.common.bean.PagingInfo;
import com.riease.common.sysinit.SysCode;
import com.riease.common.sysinit.controller.BaseRestController;
import com.riease.common.sysinit.rest.RestResponseBean;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import tw.gov.ndc.emsg.mydata.util.VerifyDpUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rest/dp")
public class DpTestRestController extends BaseRestController {


    // 暫存上傳的資料及檔案位置
    @Value("${app.download.path.temp}") String urlResPath;
    Logger logger = LoggerFactory.getLogger(getClass());

    // 解壓縮資料集
    @PostMapping("/unzip")
    public ResponseEntity<RestResponseBean> unzip(
            @RequestBody Map<String, Object> params,
            BindingResult result,
            HttpServletRequest request,
            PagingInfo paging) {

        logger.debug("into unzip...");
        String path = urlResPath +File.separator ;
        File resourcePath = new File(path);
        Map<String,Object> fileObj =  (Map<String, Object>) params.get("file");
        String fileName = fileObj.get("fname").toString();
        String fileData = fileObj.get("data").toString();
        System.out.println("---------------------------------------");
        System.out.println(fileName);
//        System.out.println(fileData);
        Base64File f = null;
        File packDir = null;
        try {
            System.out.println("!resourcePath.exists()="+!resourcePath.exists());
            if(!resourcePath.exists()) {
                resourcePath.mkdir();
            }
            f = new Base64File(fileObj);
            if(f != null) {
                packDir = f.writeFile(resourcePath, fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.debug("fout is dir -> {}" , packDir==null?null:packDir.isDirectory());

        Map<String,String> data = new HashMap<>();
        if(packDir != null && packDir.exists() && !packDir.isDirectory()){
            // 產生解壓縮結果
            String verifyResult = VerifyDpUtils.testDpZip(packDir);
            logger.debug("verifyResult -> {}" ,verifyResult);
            data.put("result", verifyResult);
            // 開發階段不刪除
//            try {
//                // 刪除暫存zip
//                FileUtils.deleteQuietly(packDir);
//                // 刪除暫存解壓縮後dir
//                File targetFile = new File(packDir.getParent() + File.separator + FilenameUtils.removeExtension(packDir.getName()));
//                // 刪除解壓後子目錄
//                FileUtils.cleanDirectory(targetFile);
//                // 刪除解壓檔根目錄
//                FileUtils.deleteQuietly(targetFile);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        }

        return responseOK(data);
    }

    // 解壓縮資料集
    @PostMapping("/unzip2")
    public ResponseEntity<RestResponseBean> unzip2(
            @RequestBody Map<String, Object> params,
            BindingResult result,
            HttpServletRequest request,
            PagingInfo paging) {

        logger.debug("into unzip2...");


//        MultipartFile file = (MultipartFile) params.get("file");
//        logger.debug("file name -> {}" , file.getName());

        return responseOK();
    }

}
