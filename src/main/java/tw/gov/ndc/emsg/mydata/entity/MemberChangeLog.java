package tw.gov.ndc.emsg.mydata.entity;

import java.io.Serializable;
import java.util.Date;

public class MemberChangeLog implements Serializable {

	private Integer id;
	private Integer memberId;
	private String type;
	private String beforeData;
	private String afterData;
	private String verificationType;
	private Date ctime;

	private static final long serialVersionUID = 1L;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBeforeData() {
		return beforeData;
	}

	public void setBeforeData(String beforeData) {
		this.beforeData = beforeData;
	}

	public String getAfterData() {
		return afterData;
	}

	public void setAfterData(String afterData) {
		this.afterData = afterData;
	}

	public String getVerificationType() {
		return verificationType;
	}

	public void setVerificationType(String verificationType) {
		this.verificationType = verificationType;
	}

	public Date getCtime() {
		return ctime;
	}

	public void setCtime(Date ctime) {
		this.ctime = ctime;
	}

	@Override
	public String toString() {
		return "MemberChangeLog{" +
				"id=" + id +
				", memberId=" + memberId +
				", type='" + type + '\'' +
				", beforeData='" + beforeData + '\'' +
				", afterData='" + afterData + '\'' +
				", verificationType='" + verificationType + '\'' +
				", ctime=" + ctime +
				'}';
	}
}