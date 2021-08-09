package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.Verify;
import tw.gov.ndc.emsg.mydata.entity.VerifyExample;

public interface VerifyMapper {
	int deleteByPrimaryKey(Integer id);

    int insert(Verify record);

    int insertSelective(Verify record);

    List<Verify> selectByExample(Map<String,Object> param);

    Verify selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Verify record);

    int updateByPrimaryKey(Verify record);
}