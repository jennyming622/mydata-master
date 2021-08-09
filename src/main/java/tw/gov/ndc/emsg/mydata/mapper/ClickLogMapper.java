package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.ClickLog;
import tw.gov.ndc.emsg.mydata.entity.ClickLogExample;

public interface ClickLogMapper {

	long countByExample(Map<String,Object> param);

	//int deleteByExample(ClickLogExample example);

	int deleteByPrimaryKey(Integer sn);

	int insert(ClickLog record);

	int insertSelective(ClickLog record);

	//List<ClickLog> selectByExampleWithRowbounds(ClickLogExample example, RowBounds rowBounds);

	//List<ClickLog> selectByExample(ClickLogExample example);

	ClickLog selectByPrimaryKey(Integer sn);

	//int updateByExampleSelective(@Param("record") ClickLog record, @Param("example") ClickLogExample example);

	//int updateByExample(@Param("record") ClickLog record, @Param("example") ClickLogExample example);

	int updateByPrimaryKeySelective(ClickLog record);

	int updateByPrimaryKey(ClickLog record);
}