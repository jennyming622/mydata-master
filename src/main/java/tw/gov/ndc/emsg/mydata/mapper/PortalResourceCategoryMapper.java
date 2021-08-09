package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceCategory;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceCategoryExample;

public interface PortalResourceCategoryMapper {
	
	List<PortalResourceCategory> selectByExample(Map<String,Object> param);
	
	int deleteByPrimaryKey(Integer cateId);

	int insert(PortalResourceCategory record);

	int insertSelective(PortalResourceCategory record);

	PortalResourceCategory selectByPrimaryKey(Integer cateId);

	int updateByPrimaryKeySelective(PortalResourceCategory record);

	int updateByPrimaryKey(PortalResourceCategory record);
}