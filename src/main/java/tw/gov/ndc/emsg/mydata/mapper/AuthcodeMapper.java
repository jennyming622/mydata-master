package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.Authcode;
import tw.gov.ndc.emsg.mydata.entity.AuthcodeExample;

public interface AuthcodeMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(Authcode record);

	int insertSelective(Authcode record);

	List<Authcode> selectByExample(Map<String,Object> param);

	Authcode selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(Authcode record);

	int updateByPrimaryKey(Authcode record);
}