package tw.gov.ndc.emsg.mydata.mapper;

import org.apache.ibatis.annotations.Param;
import tw.gov.ndc.emsg.mydata.entity.LogMemberSession;

import java.util.List;

public interface LogMemberSessionMapper {

    List<LogMemberSession> findWithMemberId(@Param("memberId") Integer memberId,
                                            @Param("state") String state,
                                            @Param("t1") Long t1);

    List<LogMemberSession> findWithSessionId(@Param("sessionId") String sessionId);

    int insertSelective(LogMemberSession record);

    int updateByPrimaryKeySelective(LogMemberSession record);

    void deleteExpiredSession();

    void deleteOtherMember(@Param("memberId") Integer memberId, @Param("sessionId") String sessionId);
}