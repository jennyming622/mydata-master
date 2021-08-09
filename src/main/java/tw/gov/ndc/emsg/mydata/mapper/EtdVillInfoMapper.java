package tw.gov.ndc.emsg.mydata.mapper;

import java.util.List;
import java.util.Map;
import tw.gov.ndc.emsg.mydata.entity.EtdVillInfo;

public interface EtdVillInfoMapper {
    List<EtdVillInfo> selectByExample(Map<String,Object> param);
}