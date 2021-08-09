package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceMaintain;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceMaintainExample;

public interface PortalResourceMaintainMapper {
    int deleteByPrId(@Param("prId") Integer prId);

    int deleteByPrimaryKey(Integer id);

    int insert(PortalResourceMaintain record);

    int insertSelective(PortalResourceMaintain record);

    List<PortalResourceMaintain> selectByPrId(@Param("prId") Integer prId);

    PortalResourceMaintain selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PortalResourceMaintain record);

    int updateByPrimaryKey(PortalResourceMaintain record);
}