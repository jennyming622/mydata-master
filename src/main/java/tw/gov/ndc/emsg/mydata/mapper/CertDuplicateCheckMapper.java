package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import tw.gov.ndc.emsg.mydata.entity.CertDuplicateCheck;
import tw.gov.ndc.emsg.mydata.entity.CertDuplicateCheckExample;

public interface CertDuplicateCheckMapper {

    int deleteByPrimaryKey(String sn);

    int insert(CertDuplicateCheck record);

    int insertSelective(CertDuplicateCheck record);

    CertDuplicateCheck selectByPrimaryKey(String sn);

    int updateByPrimaryKeySelective(CertDuplicateCheck record);

    int updateByPrimaryKey(CertDuplicateCheck record);
}