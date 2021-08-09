package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;

import tw.gov.ndc.emsg.mydata.entity.SystemOption;

public interface SystemOptionMapper {

    List<SystemOption> selectByExample();
}