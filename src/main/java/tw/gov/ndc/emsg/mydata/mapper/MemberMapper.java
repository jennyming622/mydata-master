package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import tw.gov.ndc.emsg.mydata.entity.Member;

public interface MemberMapper {

	int deleteByPrimaryKey(Integer id);

    int insert(Member record);

    int insertSelective(Member record);

    List<Member> selectByExample(Map<String,Object> param);
    
    List<Member> selectNotFinishMember();

    Member selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Member record);

    int updateByPrimaryKey(Member record);
}