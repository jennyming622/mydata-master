package tw.gov.ndc.emsg.mydata.entity;

import java.io.Serializable;

public class PortalServiceDownloadSub implements Serializable {

    private Integer psdId;
    private Integer pcsId;

    private static final long serialVersionUID = 1L;

    public Integer getPsdId() {
        return psdId;
    }

    public void setPsdId(Integer psdId) {
        this.psdId = psdId;
    }

    public Integer getPcsId() {
        return pcsId;
    }

    public void setPcsId(Integer pcsId) {
        this.pcsId = pcsId;
    }
}
