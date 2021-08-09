package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceCategory;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceCategory;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceCategoryExample;

public interface PortalServiceCategoryMapper {

	List<PortalServiceCategory> selectByExample(Map<String,Object> param);

	int deleteByPrimaryKey(Integer cateId);

	int insert(PortalServiceCategory record);

	int insertSelective(PortalServiceCategory record);

	PortalServiceCategory selectByPrimaryKey(Integer cateId);

	int updateByPrimaryKeySelective(PortalServiceCategory record);

	int updateByPrimaryKey(PortalServiceCategory record);
}