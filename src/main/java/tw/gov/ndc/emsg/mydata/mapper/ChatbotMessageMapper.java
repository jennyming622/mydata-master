package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.ChatbotMessage;
import tw.gov.ndc.emsg.mydata.entity.ChatbotMessageExample;
import tw.gov.ndc.emsg.mydata.entity.PageParam;

public interface ChatbotMessageMapper {


    int insertSelective(ChatbotMessage record);

    List<ChatbotMessage> selectByExample(Map<String, Object> param);

    Integer countByAccount(Map<String, Object> param);
}