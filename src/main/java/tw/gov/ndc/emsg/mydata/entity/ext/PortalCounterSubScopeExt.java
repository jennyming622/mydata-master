package tw.gov.ndc.emsg.mydata.entity.ext;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tw.gov.ndc.emsg.mydata.entity.PortalCounterSubScope;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceExt;

import java.util.List;

@JsonIgnoreProperties(value = "handler")
public class PortalCounterSubScopeExt extends PortalCounterSubScope {

    private List<PortalResourceExt> dpList;

    public List<PortalResourceExt> getDpList() {
        return dpList;
    }

    public void setDpList(List<PortalResourceExt> dpList) {
        this.dpList = dpList;
    }
}
