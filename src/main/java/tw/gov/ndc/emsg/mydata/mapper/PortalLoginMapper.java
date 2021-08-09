package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.PortalLogin;
import tw.gov.ndc.emsg.mydata.entity.PortalLoginExample;

public interface PortalLoginMapper {

	int deleteByPrimaryKey(Integer id);

	int insert(PortalLogin record);

	int insertSelective(PortalLogin record);

	List<PortalLogin> selectByExample(Map<String,Object> param);

	PortalLogin selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(PortalLogin record);

	int updateByPrimaryKey(PortalLogin record);
}