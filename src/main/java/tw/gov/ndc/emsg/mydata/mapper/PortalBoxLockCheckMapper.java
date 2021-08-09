package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.PortalBoxLockCheck;
import tw.gov.ndc.emsg.mydata.entity.PortalBoxLockCheckExample;

public interface PortalBoxLockCheckMapper {

    int deleteByPrimaryKey(String ip);

    int insert(PortalBoxLockCheck record);

    int insertSelective(PortalBoxLockCheck record);

    PortalBoxLockCheck selectByPrimaryKey(String ip);

    int updateByPrimaryKeySelective(PortalBoxLockCheck record);

    int updateByPrimaryKey(PortalBoxLockCheck record);
}