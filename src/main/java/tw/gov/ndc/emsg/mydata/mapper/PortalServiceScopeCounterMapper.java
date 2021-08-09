package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceScopeCounter;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceScopeCounterExample;

public interface PortalServiceScopeCounterMapper {

    int deleteByExample(Map<String,Object> param);

    int deleteByPrimaryKey(Integer id);

    int insert(PortalServiceScopeCounter record);

    int insertSelective(PortalServiceScopeCounter record);

    List<PortalServiceScopeCounter> selectByExample(Map<String,Object> param);

    PortalServiceScopeCounter selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PortalServiceScopeCounter record);

    int updateByPrimaryKey(PortalServiceScopeCounter record);
    
	int countByPsId(Integer psId);

	String prListByPsId(Integer psId);

	List<PortalServiceScopeCounter> selectByScope(Map<String,Object> param);
}