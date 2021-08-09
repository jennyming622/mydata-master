package tw.gov.ndc.emsg.mydata.entity;

import java.io.Serializable;

public class PortalCounterSubScope implements Serializable {

    private Integer id;
    private Integer psId;
    private Integer pcsId;
    private String type;
    private Integer selectCount;

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

    public Integer getPcsId() {
        return pcsId;
    }

    public void setPcsId(Integer pcsId) {
        this.pcsId = pcsId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSelectCount() {
        return selectCount;
    }

    public void setSelectCount(Integer selectCount) {
        this.selectCount = selectCount;
    }
}
