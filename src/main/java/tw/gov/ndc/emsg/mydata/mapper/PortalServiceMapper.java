package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.PortalService;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceExample;

public interface PortalServiceMapper {

	int deleteByPrimaryKey(Integer psId);

	int insert(PortalService record);

	int insertSelective(PortalService record);

	List<PortalService> selectByExample(Map<String,Object> param);
	
	List<PortalService> selectForFrontByExample(Map<String,Object> param);

	PortalService selectByPrimaryKey(Integer psId);

	int updateByPrimaryKeySelective(PortalService record);

	int updateByPrimaryKey(PortalService record);
}