package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.WhiteIpList;
import tw.gov.ndc.emsg.mydata.entity.WhiteIpListExample;

public interface WhiteIpListMapper {
    List<WhiteIpList> selectByExample(Map<String,Object> param);
}