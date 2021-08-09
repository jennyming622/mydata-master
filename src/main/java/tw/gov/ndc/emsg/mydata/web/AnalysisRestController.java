package tw.gov.ndc.emsg.mydata.web;

import com.riease.common.helper.ValidatorHelper;
import com.riease.common.sysinit.controller.BaseRestController;
import com.riease.common.sysinit.rest.RestResponseBean;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.gov.ndc.emsg.mydata.entity.Analysis;
import tw.gov.ndc.emsg.mydata.mapper.AnalysisMapper;
import tw.gov.ndc.emsg.mydata.type.BrowserType;
import tw.gov.ndc.emsg.mydata.type.DeviceType;
import tw.gov.ndc.emsg.mydata.util.DeviceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URL;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/rest/analysis")
public class AnalysisRestController extends BaseRestController {
    private static Logger logger = LoggerFactory.getLogger(AnalysisRestController.class);

    @Autowired
    private AnalysisMapper analysisMapper;

    @PostMapping("/record")
    public ResponseEntity<RestResponseBean> record(@RequestBody Map<String, Object> param,
                                                   HttpServletRequest request) throws Exception{

        HttpSession httpSession = request.getSession();
        String session = ValidatorHelper.removeSpecialCharacters(httpSession.getId());
        String userAgent = ValidatorHelper.removeSpecialCharacters(request.getHeader("User-Agent"));
        String referer = ValidatorHelper.removeSpecialCharacters(request.getHeader("referer"));
        String referrer = ValidatorHelper.removeSpecialCharacters(MapUtils.getString(param, "referrer"));
        String title = ValidatorHelper.removeSpecialCharacters(MapUtils.getString(param, "title"));

        String page = "";
        String prevPage = "";

        if(StringUtils.isNotBlank(referer)) {
            page = parsePath(referer);
        }

        if(StringUtils.isNotBlank(referrer)) {
            prevPage = parsePath(referrer);
        }

        BrowserType browserType = DeviceUtil.parseBrowser(userAgent);
        DeviceType deviceType = DeviceUtil.parseDevice(userAgent);

        logger.info("session id >> {}", session);
        logger.info("userAgent >> {}", userAgent);
        logger.info("browserType >> {}, osType >> {}", browserType, deviceType);


        Analysis analysis = new Analysis();
        analysis.setSession(session);
        analysis.setBrowser(browserType.name());
        analysis.setDevice(deviceType.name());
        analysis.setPage(page);
        analysis.setPrevPage(prevPage);
        analysis.setRequest(userAgent);
        analysis.setCtime(new Date());
        analysis.setTitle(title);

        analysisMapper.insertSelective(analysis);
        return responseOK();
    }

    private String parsePath(String url) throws Exception{
        URL tmpUrl = new URL(url);
        return tmpUrl.getPath();
    }
}
