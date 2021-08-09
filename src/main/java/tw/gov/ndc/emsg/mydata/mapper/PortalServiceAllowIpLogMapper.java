package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceAllowIpLog;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceAllowIpLogExample;

public interface PortalServiceAllowIpLogMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(PortalServiceAllowIpLog record);

    int insertSelective(PortalServiceAllowIpLog record);

    PortalServiceAllowIpLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PortalServiceAllowIpLog record);

    int updateByPrimaryKey(PortalServiceAllowIpLog record);
}