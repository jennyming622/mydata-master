package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import tw.gov.ndc.emsg.mydata.entity.MemberTemp;

public interface MemberTempMapper {

	int deleteByPrimaryKey(Integer id);

    int insert(MemberTemp record);

    int insertSelective(MemberTemp record);

    List<MemberTemp> selectByExample(Map<String,Object> param);

    MemberTemp selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MemberTemp record);

    int updateByPrimaryKey(MemberTemp record);
}