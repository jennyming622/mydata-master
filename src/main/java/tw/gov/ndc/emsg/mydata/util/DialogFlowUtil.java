package tw.gov.ndc.emsg.mydata.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.dialogflow.v2.*;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.google.protobuf.util.JsonFormat;
import com.riease.common.helper.HttpClientHelper;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.gov.ndc.emsg.mydata.chatbot.ChatBotResponse;
import tw.gov.ndc.emsg.mydata.chatbot.ChatBotType;
import tw.gov.ndc.emsg.mydata.chatbot.TextChatBotResponse;
import tw.gov.ndc.emsg.mydata.entity.ChatbotMessage;
import tw.gov.ndc.emsg.mydata.mapper.ChatbotMessageMapper;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DialogFlowUtil {

    private static final Logger logger = LoggerFactory.getLogger(DialogFlowUtil.class);

    private static final String PROJECT_ID = "mydata-uudv";
    private static final String LANGUAGE_CODE = "zh-TW";
    private static final String CONTEXTS_PATH = "/contexts";

    private static final String CLOUD_PLATFORM_SCOPE = "https://www.googleapis.com/auth/cloud-platform";

    private static final List<String> clearContextsList = Arrays.asList(new String[]{"平臺介紹", "服務介紹", "操作說明", "聯絡客服"});

    private static GoogleCredentials credentials = null;

    private ObjectMapper om = new ObjectMapper();

    @Autowired
    private ChatbotMessageMapper chatbotMessageMapper;

    static {
    	FileInputStream inputStream = null;
        try {
            logger.info("GoogleCredentials init start");
            File file = new File(DialogFlowUtil.class.getResource("/mydata-uudv-64c4993592ee.json").getPath());
            inputStream = new FileInputStream(file);
            credentials = GoogleCredentials.fromStream(inputStream).createScoped(CLOUD_PLATFORM_SCOPE);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        } finally {
			if(inputStream!=null) {
				HttpClientHelper.safeClose(inputStream);
			}
            logger.info("GoogleCredentials init finish");
        }
    }

    public List<String> detectIntentTexts(Map<String, Object> params, String token, String account) {

        SessionsClient sessionsClient = null;
        try {
            // 獲取交易令牌

            credentials.refresh();

            // 連線設定
            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            SessionName session = SessionName.of(PROJECT_ID, token);
            System.out.println("Session Path: " + session.toString());

            String text = MapUtils.getString(params, "text");
            String value = MapUtils.getString(params, "value");
            String type = MapUtils.getString(params, "type", "user");
            String feel = MapUtils.getString(params, "feel", "D");


            addChatBotMessageByUser(account, Arrays.asList(new TextChatBotResponse(text, ChatBotType.text, value)), token, type, feel);

            // Set the text (hello) and language code (en-US) for the query
            TextInput.Builder textInput =
                    TextInput.newBuilder().setText(text).setLanguageCode(LANGUAGE_CODE);

            // Build the query with the TextInput
            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

            QueryParameters parameters = QueryParameters.newBuilder().setResetContexts(true).build();

            DetectIntentRequest.Builder requestBuilder =
                    DetectIntentRequest.newBuilder()
                            .setSession(session == null ? null : session.toString())
                            .setQueryInput(queryInput);

            if(clearContextsList.contains(text)) {
                logger.info("clear contexts");
                requestBuilder.setQueryParams(parameters);
            }

            // Performs the detect intent request
            DetectIntentResponse response = sessionsClient.detectIntent(requestBuilder.build());

            // Display the query result
            QueryResult queryResult = response.getQueryResult();

            List<String> resultList =  new ArrayList<>();
            List<ChatBotResponse> responseList = new ArrayList<>();



            List<Intent.Message> messages = queryResult.getFulfillmentMessagesList();
            for(Intent.Message message : messages) {
                for(String tmp : message.getText().getTextList()) {
                    resultList.add(tmp);
                    ChatBotResponse chatBotResponse = om.readValue(tmp, ChatBotResponse.class);
                    responseList.add(chatBotResponse);
                }
            }

            addChatBotMessageByBot(account, responseList, queryResult, token);

            return resultList;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            System.out.println(e.getLocalizedMessage());
            System.out.println(e);
        } finally {
            if(sessionsClient != null) {
                sessionsClient.close();
            }
        }
        return Collections.emptyList();
    }

    private void addChatBotMessageByUser(String account, List<ChatBotResponse> result, String token, String type, String feel) throws Exception{
        ChatbotMessage chatbotMessage = new ChatbotMessage();
        chatbotMessage.setAccount(account);
        chatbotMessage.setType(type);
        chatbotMessage.setMessage(om.writeValueAsString(result));
        chatbotMessage.setCtime(new Date());
        chatbotMessage.setSession(token);

        String value = result.stream().map(item -> item.getValue()).collect(Collectors.joining(","));
        if(!StringUtils.equals(value, "null")) {
            chatbotMessage.setValue(value);
        }
        chatbotMessage.setFeel(feel);

        chatbotMessageMapper.insertSelective(chatbotMessage);
    }

    private void addChatBotMessageByBot(String account, List<ChatBotResponse> result, QueryResult queryResult, String token) throws Exception{
        ChatbotMessage chatbotMessage = new ChatbotMessage();
        chatbotMessage.setAccount(account);
        chatbotMessage.setType("bot");
        chatbotMessage.setMessage(om.writeValueAsString(result));

        Intent intent = queryResult.getIntent();
        String intentName = intent.getDisplayName();
        chatbotMessage.setIntentName(intentName);
        chatbotMessage.setBotResponse(JsonFormat.printer().print(queryResult));
        chatbotMessage.setCtime(new Date());
        chatbotMessage.setSession(token);

        String value = result.stream().map(item -> item.getValue()).collect(Collectors.joining(","));
        if(!StringUtils.equals(value, "null")) {
            chatbotMessage.setValue(value);
        }
        chatbotMessage.setFeel("D");

        chatbotMessageMapper.insertSelective(chatbotMessage);
    }

    public Map<String, String> parseWebHookRequest(String body) throws Exception{
        Map<String, String> params = new LinkedHashMap<>();

        logger.info("body >> {}", body);
        // body to WebHookRequest
        WebhookRequest.Builder builder = WebhookRequest.newBuilder();
        JsonFormat.parser().merge(body, builder);
        WebhookRequest webhookRequest = builder.build();

        // 解析請求參數
        QueryResult queryResult = webhookRequest.getQueryResult();
        String queryText = queryResult.getQueryText();
        Struct struct = queryResult.getParameters();
        Map<String, Value> parameters = struct.getFieldsMap();

        logger.info("queryText >> {}", queryText);

        for (String key : parameters.keySet()) {
            Value value = parameters.get(key);
            params.put(key, value.getStringValue());
        }

        return params;
    }

    public static void main(String[] args) throws Exception {
        requestTest();
        //responseTest();
    }

    private static void responseTest() throws Exception{
        Intent.Message message = Intent.Message.newBuilder()
                .setText(Intent.Message.Text.newBuilder().addText("123").build()).build();

        System.out.println(message.toString());

        List<Intent.Message> messages = new ArrayList<>();
        messages.add(message);



        WebhookResponse webhookResponse = WebhookResponse.newBuilder().addAllFulfillmentMessages(messages).build();
        webhookResponse.getFulfillmentMessagesOrBuilder(0);

        String result = JsonFormat.printer().print(webhookResponse);

        System.out.println(result);
    }

    private static void requestTest() {
        DialogFlowUtil util = new DialogFlowUtil();
        Map<String, Object> params = new HashMap<>();
        params.put("text", "資料下載");
        List<String> json = util.detectIntentTexts(params, UUID.randomUUID().toString(), null);
        json.stream().forEach(item ->  System.out.println(item));
    }
}
