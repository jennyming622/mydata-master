package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.OrganWhiteIpList;
import tw.gov.ndc.emsg.mydata.entity.OrganWhiteIpListExample;

public interface OrganWhiteIpListMapper {
    List<OrganWhiteIpList> selectByExample(Map<String,Object> param);
}