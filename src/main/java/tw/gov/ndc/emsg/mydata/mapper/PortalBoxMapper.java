package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.PortalBox;
import tw.gov.ndc.emsg.mydata.entity.PortalBoxExample;

public interface PortalBoxMapper {

	int deleteByPrimaryKey(Integer id);

	int insert(PortalBox record);

	int insertSelective(PortalBox record);

	List<PortalBox> selectByExample(Map<String,Object> param);

	PortalBox selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(PortalBox record);

	int updateByPrimaryKey(PortalBox record);
}