package tw.gov.ndc.emsg.mydata.entity;

import java.io.Serializable;
import java.util.Date;

public class PortalCounterSub implements Serializable {

    private Integer id;
    private Integer psId;
    private String name;
    private Integer isOpenAgent;
    private Integer type;
    private Integer enable;
    private String preparedDocument;
    private Date ctime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPsId() {
        return psId;
    }

    public void setPsId(Integer psId) {
        this.psId = psId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsOpenAgent() {
        return isOpenAgent;
    }

    public void setIsOpenAgent(Integer isOpenAgent) {
        this.isOpenAgent = isOpenAgent;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public String getPreparedDocument() {
        return preparedDocument;
    }

    public void setPreparedDocument(String preparedDocument) {
        this.preparedDocument = preparedDocument;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }
}
