package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;
import tw.gov.ndc.emsg.mydata.entity.EtdTownInfo;

public interface EtdTownInfoMapper {

    List<EtdTownInfo> selectByExample(Map<String,Object> param);
}