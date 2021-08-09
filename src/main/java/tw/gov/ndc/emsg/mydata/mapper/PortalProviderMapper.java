package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.PortalProvider;
import tw.gov.ndc.emsg.mydata.entity.PortalProviderExample;

public interface PortalProviderMapper {

	//long countByExample(PortalProviderExample example);

	//int deleteByExample(PortalProviderExample example);

	int deleteByPrimaryKey(Integer providerId);

	int insert(PortalProvider record);

	int insertSelective(PortalProvider record);

	//List<PortalProvider> selectByExampleWithRowbounds(PortalProviderExample example,RowBounds rowBounds);

	List<PortalProvider> selectByExample(Map<String,Object> param);

	PortalProvider selectByPrimaryKey(Integer providerId);

	//int updateByExampleSelective(@Param("record") PortalProvider record,@Param("example") PortalProviderExample example);

	//int updateByExample(@Param("record") PortalProvider record,@Param("example") PortalProviderExample example);

	int updateByPrimaryKeySelective(PortalProvider record);

	int updateByPrimaryKey(PortalProvider record);
}