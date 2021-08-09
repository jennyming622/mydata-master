package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.MemberPrivacy;
import tw.gov.ndc.emsg.mydata.entity.MemberPrivacyExample;

public interface MemberPrivacyMapper {

    int insertSelective(MemberPrivacy record);

    List<MemberPrivacy> selectByExample(Map<String, Object> param);

    int updateByMemberIdSelective(MemberPrivacy record);
    
    int deleteByPrimaryKey(@Param("memberId")Integer memberId);
}