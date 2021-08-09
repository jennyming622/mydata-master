package tw.gov.ndc.emsg.mydata.entity.ext;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tw.gov.ndc.emsg.mydata.entity.PortalCounterSub;

import java.util.List;

@JsonIgnoreProperties(value = "handler")
public class PortalCounterSubExt extends PortalCounterSub {

    private List<PortalCounterSubScopeExt> subScopeExtList;

    public List<PortalCounterSubScopeExt> getSubScopeExtList() {
        return subScopeExtList;
    }

    public void setSubScopeExtList(List<PortalCounterSubScopeExt> subScopeExtList) {
        this.subScopeExtList = subScopeExtList;
    }
}
