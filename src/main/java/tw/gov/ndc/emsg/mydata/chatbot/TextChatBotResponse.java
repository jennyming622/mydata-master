package tw.gov.ndc.emsg.mydata.chatbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceCategory;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceCategoryMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextChatBotResponse extends ChatBotResponse{

    public TextChatBotResponse() {
    }

    public TextChatBotResponse(String text, ChatBotType type) {
        super(text, type);
    }

    public TextChatBotResponse(String text, ChatBotType type, List<ChatBotOption> options) {
        super(text, type, options);
    }

    public TextChatBotResponse(String text, ChatBotType type, String value) {
        super(text, type, value);
    }

    @Override
    public void data() {}
}
