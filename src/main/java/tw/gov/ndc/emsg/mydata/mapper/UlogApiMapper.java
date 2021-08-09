package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.UlogApi;
import tw.gov.ndc.emsg.mydata.entity.UlogApiExample;

public interface UlogApiMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(UlogApi record);

	int insertSelective(UlogApi record);

	List<UlogApi> selectByExampleWithRowbounds(Map<String,Object> param,RowBounds rowBounds);

	List<UlogApi> selectByExample(Map<String,Object> param);

	UlogApi selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(UlogApi record);

	int updateByPrimaryKey(UlogApi record);
}