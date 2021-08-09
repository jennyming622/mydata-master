package tw.gov.ndc.emsg.mydata.chatbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceCategory;
import tw.gov.ndc.emsg.mydata.mapper.PortalServiceCategoryMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("counterChatBotResponse")
public class CounterChatBotResponse extends ChatBotResponse{

    @Autowired
    private PortalServiceCategoryMapper portalServiceCategoryMapper;

    @Override
    public void data() {
        StringBuilder builder = new StringBuilder();
        builder.append("關於臨櫃服務，").append(LINE_BREAK);
        builder.append("除了現有攜帶相關文件資料至機關臨櫃辦理業務外，亦可透過本平臺下載個人資料後產生對應條碼，將條碼交付臨櫃人員，臨櫃人員即可取出對應之個人資料並辦理後續服務，")
                .append("俾利您可以MyData下載之個人資料數位檔案取代原須檢附之紙本查驗資料，以達便民及無紙化服務目標。").append(LINE_BREAK).append(LINE_BREAK);
        builder.append("想申請哪一種領域的臨櫃服務呢？點擊下列選項，小 D 會幫您開啟網頁").append(LINE_BREAK);

        Map<String, Object> pscparam1 = new HashMap<String, Object>();
        List<PortalServiceCategory> portalServiceCategoryList = portalServiceCategoryMapper
                .selectByExample(pscparam1);

        List<ChatBotOption> list = new ArrayList<>();
        for(PortalServiceCategory category : portalServiceCategoryList) {
            if(category.getStatus() != 1) continue;

            ChatBotOption option = new ChatBotOption();
            option.setText(category.getCateName());
            option.setValue(String.format("B3%02d", category.getCateId()));
            option.setType(OptionType.link);
            option.setUrl(String.format("sp/service/counter?category=%d", category.getCateId()));
            category.getCateId();
            list.add(option);
        }

        setText(builder.toString());
        setType(ChatBotType.grid_btn);
        setOptions(list);
        setValue("B3");
    }
}
