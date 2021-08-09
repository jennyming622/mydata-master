package tw.gov.ndc.emsg.mydata.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tw.gov.ndc.emsg.mydata.util.MailUtil;
import tw.gov.ndc.emsg.mydata.util.SystemOptionUtil;
import tw.gov.ndc.emsg.mydata.util.TWFidoUtil;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/rest/system/option")
public class SystemOptionRestController {

    @Autowired
    private SystemOptionUtil systemOptionUtil;

    @Value("${tfido.syscode}")
    private String tfidoSyscode;
    @Value("${tfido.apikey}")
    private String tfidoApikey;

    /**
     * 讓修改後的 Options 立即生效
     * @return
     */
    @GetMapping("/init")
    public String init() {
        systemOptionUtil.initOption();
        return "init finish";
    }

    @GetMapping("/test/sendEmail")
    public String testSendEmail() {
        List<String> reveicers = Arrays.asList("dennis@riease.com");
        String from = "mydata_system@ndc.gov.tw";
        String title2 = "測試2Mydata";
        String content2 = "測試信件\n1234";
        MailUtil.sendMail(reveicers, from, title2, content2, "true");
        return "OK";
    }

    @GetMapping("/test/TWFido/push")
    public String testExPushAuth(@RequestParam(name = "pid") String pid) {
        return TWFidoUtil.exPushAuth(pid, tfidoSyscode, tfidoApikey);
    }

    @GetMapping("/test/TWFido/confirm")
    public String testAuthConfirm(@RequestParam(name = "qrid") String qrid) {
        return TWFidoUtil.authConfirm(qrid, tfidoSyscode, tfidoApikey);
    }

}
