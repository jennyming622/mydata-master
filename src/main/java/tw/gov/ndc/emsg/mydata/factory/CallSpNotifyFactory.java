package tw.gov.ndc.emsg.mydata.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import tw.gov.ndc.emsg.mydata.chatbot.ChatBotResponse;
import tw.gov.ndc.emsg.mydata.service.AbstractCallSpNotify;

@Component
public class CallSpNotifyFactory {

    @Autowired
    private ApplicationContext app;

    public AbstractCallSpNotify get(Boolean isTwoWay) {

        if(isTwoWay) {
            return (AbstractCallSpNotify) app.getBean("clientTwoWayCallSpNotify");
        }

        return (AbstractCallSpNotify) app.getBean("defaultCallSpNotify");
    }
}
