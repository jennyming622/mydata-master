package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.Category;
import tw.gov.ndc.emsg.mydata.entity.CategoryExample;

public interface CategoryMapper {
	int deleteByPrimaryKey(Integer cateId);

	int insert(Category record);

	int insertSelective(Category record);

	Category selectByPrimaryKey(Integer cateId);

	int updateByPrimaryKeySelective(Category record);

	int updateByPrimaryKey(Category record);
}