package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.PortalResource;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceExample;

public interface PortalResourceMapper {

	int deleteByPrimaryKey(Integer prId);

	int insert(PortalResource record);

	int insertSelective(PortalResource record);

	List<PortalResource> selectByExample(Map<String,Object> param);

	PortalResource selectByPrimaryKey(Integer prId);

	int updateByPrimaryKeySelective(PortalResource record);

	int updateByPrimaryKey(PortalResource record);
}