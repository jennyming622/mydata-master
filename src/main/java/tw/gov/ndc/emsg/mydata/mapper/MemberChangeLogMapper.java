package tw.gov.ndc.emsg.mydata.mapper;

import tw.gov.ndc.emsg.mydata.entity.Member;
import tw.gov.ndc.emsg.mydata.entity.MemberChangeLog;

import java.util.List;
import java.util.Map;

public interface MemberChangeLogMapper {

    int insertSelective(MemberChangeLog record);

}