package tw.gov.ndc.emsg.mydata.chatbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

@JsonDeserialize(as=TextChatBotResponse.class)
public abstract class ChatBotResponse {

    protected static final Logger logger = LoggerFactory.getLogger(ChatBotResponse.class);

    public static final String LINE_BREAK = "<br>";

    private String text;
    private ChatBotType type;
    private List<ChatBotOption> options = Collections.emptyList();
    private String value;

    public ChatBotResponse() {}

    public ChatBotResponse(String text, ChatBotType type) {
        this.text = text;
        this.type = type;
    }

    public ChatBotResponse(String text, ChatBotType type, List<ChatBotOption> options) {
        this.text = text;
        this.type = type;
        this.options = options;
    }

    public ChatBotResponse(String text, ChatBotType type, String value) {
        this.text = text;
        this.type = type;
        this.value = value;
    }

    public abstract void data();

    public String generatorJsonStr() throws Exception{
        return new ObjectMapper().writeValueAsString(this);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ChatBotType getType() {
        return type;
    }

    public void setType(ChatBotType type) {
        this.type = type;
    }

    public List<ChatBotOption> getOptions() {
        return options;
    }

    public void setOptions(List<ChatBotOption> options) {
        this.options = options;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
