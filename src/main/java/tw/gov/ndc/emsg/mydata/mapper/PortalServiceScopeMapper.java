package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceScope;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceScopeExample;

public interface PortalServiceScopeMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(PortalServiceScope record);

    int insertSelective(PortalServiceScope record);

    List<PortalServiceScope> selectByExample(Map<String,Object> param);

    PortalServiceScope selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PortalServiceScope record);

    int updateByPrimaryKey(PortalServiceScope record);

    int updateByPrimaryKey(Integer psId);
}