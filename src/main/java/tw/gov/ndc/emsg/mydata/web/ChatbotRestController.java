package tw.gov.ndc.emsg.mydata.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.dialogflow.v2.EventInput;
import com.google.cloud.dialogflow.v2.Intent;
import com.google.cloud.dialogflow.v2.WebhookResponse;
import com.google.protobuf.util.JsonFormat;
import com.riease.common.helper.ValidatorHelper;
import com.riease.common.sysinit.SessionRecord;
import com.riease.common.sysinit.controller.BaseRestController;
import com.riease.common.sysinit.rest.RestResponseBean;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.gov.ndc.emsg.mydata.chatbot.ChatBotResponse;
import tw.gov.ndc.emsg.mydata.chatbot.ChatbotResponseFactory;
import tw.gov.ndc.emsg.mydata.chatbot.DpChatBotResponse;
import tw.gov.ndc.emsg.mydata.chatbot.ResponseType;
import tw.gov.ndc.emsg.mydata.entity.ChatbotMessage;
import tw.gov.ndc.emsg.mydata.entity.Member;
import tw.gov.ndc.emsg.mydata.entity.PageParam;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceCategory;
import tw.gov.ndc.emsg.mydata.mapper.ChatbotMessageMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceCategoryMapper;
import tw.gov.ndc.emsg.mydata.util.DialogFlowUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

@RestController
@RequestMapping("/rest/chatbot")
public class ChatbotRestController extends BaseRestController {
    private static final Logger logger = LoggerFactory.getLogger(ChatbotRestController.class);

    private static final String CHATBOT_SESSION = "chatbot";

    @Autowired
    private DialogFlowUtil dialogFlowUtil;

    @Autowired
    private ChatbotResponseFactory chatbotResponseFactory;

    @Autowired
    private ChatbotMessageMapper chatbotMessageMapper;


    @PostMapping("/send")
    public ResponseEntity<RestResponseBean> sendMsg(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession httpSession = request.getSession();
        String chatbotToken = (String) httpSession.getAttribute(CHATBOT_SESSION);
        if(StringUtils.isBlank(chatbotToken)) {
            chatbotToken = UUID.randomUUID().toString();
            httpSession.setAttribute(CHATBOT_SESSION, chatbotToken);
        }

        String account = null;
        SessionRecord sr = currentSessionRecord(httpSession);
        if(sr != null) {
            Member member = SessionMember.getSessionMemberToMember(sr.getMember());
            account = StringUtils.defaultString(member.getAccount());
        }

        List<String> result = dialogFlowUtil.detectIntentTexts(params, chatbotToken, account);
        return responseOK(result);
    }

    @PostMapping("/record")
    public ResponseEntity<RestResponseBean> record(@RequestBody PageParam pageParam, HttpServletRequest request, HttpServletResponse response) {
        HttpSession httpSession = request.getSession();
        String account = currentUserAccount(httpSession);

        if(StringUtils.isBlank(account)) {
            return responseOK();
        }

        Map<String, Object> param = new HashMap<>();
        param.put("account", account);
        param.put("pageParam", pageParam);

        List<ChatbotMessage> chatbotMessages = chatbotMessageMapper.selectByExample(param);

        Integer total = chatbotMessageMapper.countByAccount(param);
        pageParam.setTotal(total);

        Map<String, Object> result = new HashMap<>();
        result.put("record", chatbotMessages);
        result.put("pageParam", pageParam);

        return responseOK(result);
    }

    @PostMapping("/webhook")
    public void webhook(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) {

        try {
            Map<String, String> params = dialogFlowUtil.parseWebHookRequest(body);

            // TODO 實作回應內容
            String type = MapUtils.getString(params, "type");
            String msg = "";

            ResponseType responseType = ResponseType.valueOf(type);
            ChatBotResponse chatBotResponse = chatbotResponseFactory.get(responseType);
            chatBotResponse.data();
            msg = chatBotResponse.generatorJsonStr();
            logger.info("msg >> {}", msg);


            // 講回應內容寫入 WebhookResponse
            Intent.Message message = Intent.Message.newBuilder()
                    .setText(Intent.Message.Text.newBuilder().addText(msg).build())
                    .build();

            EventInput eventInput = EventInput.newBuilder().setLanguageCode("zh-TW").build();

            WebhookResponse webhookResponse = WebhookResponse.newBuilder()
                    .addFulfillmentMessages(message)
                    .build();

            // 將 webhookResponse to json
            String result = JsonFormat.printer().print(webhookResponse);

            PrintWriter out = response.getWriter();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(result);
            out.flush();
        }catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }
}
