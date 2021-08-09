package tw.gov.ndc.emsg.mydata.chatbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceCategory;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceCategory;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceCategoryMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalServiceCategoryMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("spChatBotResponse")
public class SpChatBotResponse extends ChatBotResponse{

    @Autowired
    private PortalServiceCategoryMapper portalServiceCategoryMapper;

    @Override
    public void data() {
        StringBuilder builder = new StringBuilder();
        builder.append("透過MyData也可以引導您申辦各縣市或是銀行的線上申請服務，").append(LINE_BREAK);
        builder.append("可於平臺將資料提供機關介接之個人資料下載後，當次即時同意將其資料線上傳送給其他機關(構)辦理個人化服務。").append(LINE_BREAK).append(LINE_BREAK);
        builder.append("想申請哪一種領域的線上服務呢？點擊下列選項，小 D 會幫您開啟網頁").append(LINE_BREAK);

        Map<String, Object> pscparam1 = new HashMap<String, Object>();
        List<PortalServiceCategory> portalServiceCategoryList = portalServiceCategoryMapper
                .selectByExample(pscparam1);

        List<ChatBotOption> list = new ArrayList<>();
        for(PortalServiceCategory category : portalServiceCategoryList) {
            if(category.getStatus() != 1) continue;

            ChatBotOption option = new ChatBotOption();
            option.setText(category.getCateName());
            option.setValue(category.getCateId().toString());
            option.setValue(String.format("B2%02d", category.getCateId()));
            option.setType(OptionType.link);
            option.setUrl(String.format("sp/service?category=%d#package-service", category.getCateId()));
            list.add(option);
        }

        setText(builder.toString());
        setType(ChatBotType.grid_btn);
        setOptions(list);
        setValue("B2");
    }
}
