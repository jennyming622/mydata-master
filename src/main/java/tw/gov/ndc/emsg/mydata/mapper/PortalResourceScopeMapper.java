package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceScope;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceScopeExample;

public interface PortalResourceScopeMapper {

	int insert(PortalResourceScope record);

	int insertSelective(PortalResourceScope record);

	List<PortalResourceScope> selectByExample(Map<String,Object> param);

	List<PortalResourceScope> selectByExampleLike(Map<String,Object> param);

	List<PortalResourceScope> selectByProviderId(Map<String,Object> param);

}
