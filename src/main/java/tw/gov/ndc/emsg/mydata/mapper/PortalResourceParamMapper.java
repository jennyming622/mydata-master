package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import tw.gov.ndc.emsg.mydata.entity.PortalResourceParam;

public interface PortalResourceParamMapper {
	int deleteByPrimaryKey(Integer prId);

	int insert(PortalResourceParam record);

	int insertSelective(PortalResourceParam record);

	List<PortalResourceParam> selectByExample(Map<String,Object> param);

	PortalResourceParam selectByPrimaryKey(Integer prId);

	int updateByPrimaryKeySelective(PortalResourceParam record);

	int updateByPrimaryKey(PortalResourceParam record);
}