package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.VerificationLog;
import tw.gov.ndc.emsg.mydata.entity.VerificationLogExample;

public interface VerificationLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(VerificationLog vlog);

    int insertSelective(VerificationLog record);

    List<VerificationLog> selectByExample(Map<String,Object> param);

    VerificationLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(VerificationLog vlog);

    int updateByPrimaryKey(VerificationLog vlog);
}