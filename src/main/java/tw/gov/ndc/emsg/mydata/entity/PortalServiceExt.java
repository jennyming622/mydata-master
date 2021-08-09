package tw.gov.ndc.emsg.mydata.entity;

import tw.gov.ndc.emsg.mydata.entity.ext.PortalCounterSubExt;

import java.util.List;

public class PortalServiceExt extends PortalService{
	private static final long serialVersionUID = 9041329957001247553L;

	private String providerName;
	private String providerAddress;
	private PortalServiceCategory cate;
	private long count;
	private String key;
	private List<UlogApiExt> ulogList;
	private String creationTime;
	private String expiration;
	private String authStatus;
	private String note;
	private Boolean isMaintain;
	private Integer type;
	private Boolean moecaCheck;
	
	private List<PortalResourceExt> prList;
	private List<PortalCounterSubExt> counterSubExtList;
	
	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getProviderAddress() {
		return providerAddress;
	}

	public void setProviderAddress(String providerAddress) {
		this.providerAddress = providerAddress;
	}

	public PortalServiceCategory getCate() {
		return cate;
	}

	public void setCate(PortalServiceCategory cate) {
		this.cate = cate;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<UlogApiExt> getUlogList() {
		return ulogList;
	}

	public void setUlogList(List<UlogApiExt> ulogList) {
		this.ulogList = ulogList;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	public String getExpiration() {
		return expiration;
	}

	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}

	public String getAuthStatus() {
		return authStatus;
	}

	public void setAuthStatus(String authStatus) {
		this.authStatus = authStatus;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Boolean getMaintain() {
		return isMaintain;
	}

	public void setMaintain(Boolean maintain) {
		isMaintain = maintain;
	}

	public List<PortalResourceExt> getPrList() {
		return prList;
	}

	public void setPrList(List<PortalResourceExt> prList) {
		this.prList = prList;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Boolean getMoecaCheck() {
		return moecaCheck;
	}

	public void setMoecaCheck(Boolean moecaCheck) {
		this.moecaCheck = moecaCheck;
	}

	public List<PortalCounterSubExt> getCounterSubExtList() {
		return counterSubExtList;
	}

	public void setCounterSubExtList(List<PortalCounterSubExt> counterSubExtList) {
		this.counterSubExtList = counterSubExtList;
	}
}
