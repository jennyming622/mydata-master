package tw.gov.ndc.emsg.mydata.chatbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ChatbotResponseFactory {

    @Autowired
    private ApplicationContext app;

    public ChatBotResponse get(ResponseType type) {
        switch (type) {
            case dp:
                return (ChatBotResponse) app.getBean("dpChatBotResponse");
            case sp:
                return (ChatBotResponse) app.getBean("spChatBotResponse");
            case counter:
                return (ChatBotResponse) app.getBean("counterChatBotResponse");
        }
        return null;
    }
}
