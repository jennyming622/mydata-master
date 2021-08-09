package tw.gov.ndc.emsg.mydata.mapper;

import tw.gov.ndc.emsg.mydata.entity.PortalCounterSubScope;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceExt;

import java.util.List;
import java.util.Map;

public interface PortalCounterSubScopeMapperExt {

    List<PortalCounterSubScope> selectByExample(Map<String, Object> param);

    List<PortalCounterSubScope> selectByPcsId(Integer pcsId);

    List<PortalResourceExt> selectResource(Integer pcssId);
}