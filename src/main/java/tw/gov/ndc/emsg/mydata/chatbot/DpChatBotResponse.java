package tw.gov.ndc.emsg.mydata.chatbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceCategory;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceCategoryMapper;

import java.util.*;

@Component("dpChatBotResponse")
public class DpChatBotResponse extends ChatBotResponse{

    @Autowired
    private PortalResourceCategoryMapper portalResourceCategoryMapper;

    @Override
    public void data() {
        StringBuilder builder = new StringBuilder();
        builder.append("MyData可以下載到存放各機關的資料。").append(LINE_BREAK).append(LINE_BREAK);
        builder.append("透過平臺經身分驗證後自行下載個人資料，包括下載個人戶籍、戶政國民身分證影像、地籍及實價、勞保投保、財產、個人所得、車駕籍等資料。").append(LINE_BREAK).append(LINE_BREAK);
        builder.append("想下載哪一個類別的資料集呢？點擊下列選項，小 D 會幫您開啟網頁哦").append(LINE_BREAK);

        Map<String, Object> prcparam1 = new HashMap<String, Object>();
        List<PortalResourceCategory> portalResourceCategoryList = portalResourceCategoryMapper
                .selectByExample(prcparam1);

        List<ChatBotOption> list = new ArrayList<>();
        for(PortalResourceCategory category : portalResourceCategoryList) {
            if(category.getStatus() != 1) continue;

            ChatBotOption option = new ChatBotOption();
            option.setText(category.getCateName());
            option.setValue(category.getCateId().toString());
            option.setValue(String.format("B1%02d", category.getCateId()));
            option.setType(OptionType.link);
            option.setUrl(String.format("personal/cate/%d/list", category.getCateId()));
            list.add(option);
        }

        setText(builder.toString());
        setType(ChatBotType.grid_btn);
        setOptions(list);
        setValue("B1");
    }
}
