package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceAllowIp;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceAllowIpExample;

public interface PortalServiceAllowIpMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(PortalServiceAllowIp record);

    int insertSelective(PortalServiceAllowIp record);

    List<PortalServiceAllowIp> selectByExample(Map<String,Object> param);

    PortalServiceAllowIp selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PortalServiceAllowIp record);

    int updateByPrimaryKey(PortalServiceAllowIp record);
}