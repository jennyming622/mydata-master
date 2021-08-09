package tw.gov.ndc.emsg.mydata.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import tw.gov.ndc.emsg.mydata.gspclient.bean.NonceEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller()
@RequestMapping("/dp")
public class DpTestController {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @GetMapping("/testdp")
    public String getTestDP(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        return "testdp";
    }

}
