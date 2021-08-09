package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.Member;
import tw.gov.ndc.emsg.mydata.entity.SendLog;
import tw.gov.ndc.emsg.mydata.entity.SendLogExample;

public interface SendLogMapper {
    int insertSelective(SendLog sLog);
}