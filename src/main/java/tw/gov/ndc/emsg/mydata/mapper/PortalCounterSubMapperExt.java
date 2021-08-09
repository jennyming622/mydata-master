package tw.gov.ndc.emsg.mydata.mapper;

import tw.gov.ndc.emsg.mydata.entity.PortalCounterSub;
import tw.gov.ndc.emsg.mydata.entity.ext.PortalCounterSubExt;

import java.util.List;
import java.util.Map;

public interface PortalCounterSubMapperExt {

    PortalCounterSub selectByPrimaryKey(Integer id);

    List<PortalCounterSub> selectByExample(Map<String, Object> param);

    List<PortalCounterSubExt> selectExt(Map<String, Object> param);
}