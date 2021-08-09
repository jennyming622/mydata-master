package tw.gov.ndc.emsg.mydata.entity;

import java.io.Serializable;
import java.util.Date;

public class PortalServiceStageIp implements Serializable {

    private Integer id;

    private Integer psId;

    private String ip;

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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", psId=").append(psId);
        sb.append(", ip=").append(ip);
        sb.append(", ctime=").append(ctime);
        sb.append("]");
        return sb.toString();
    }
}