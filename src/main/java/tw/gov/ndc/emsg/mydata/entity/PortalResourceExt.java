package tw.gov.ndc.emsg.mydata.entity;

import java.util.List;

public class PortalResourceExt extends PortalResource { 
	private static final long serialVersionUID = -2585385090377753261L;
	private String providerName;
	private String providerAddress;
	private String redirectUri;
	private String downloadUri;
	private long count;
	private int downloadStatus;
	private float percent;
	private int remainingTime;
	private int waitTime;
	private String filename;
	private String downloadSn;
	private String downloadVerify;
	private List<PortalResourceCategory> cateList;
	private List<PortalResourceField> fieldList;
	private List<UlogApiExt> ulogList;
	private List<Param> paramList;
	private String endTimeNote;
	private String oid;
	private Integer boxid;
	private String code;
	private String cateName;
	private Integer uidType;
	private Integer type;
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
	public String getRedirectUri() {
		return redirectUri;
	}
	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}
	public String getDownloadUri() {
		return downloadUri;
	}
	public void setDownloadUri(String downloadUri) {
		this.downloadUri = downloadUri;
	}
	public int getDownloadStatus() {
		return downloadStatus;
	}
	public void setDownloadStatus(int downloadStatus) {
		this.downloadStatus = downloadStatus;
	}
	public float getPercent() {
		return percent;
	}
	public void setPercent(float percent) {
		this.percent = percent;
	}
	public int getRemainingTime() {
		return remainingTime;
	}
	public void setRemainingTime(int remainingTime) {
		this.remainingTime = remainingTime;
	}
	public int getWaitTime() {
		return waitTime;
	}
	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getDownloadSn() {
		return downloadSn;
	}
	public void setDownloadSn(String downloadSn) {
		this.downloadSn = downloadSn;
	}
	public String getDownloadVerify() {
		return downloadVerify;
	}
	public void setDownloadVerify(String downloadVerify) {
		this.downloadVerify = downloadVerify;
	}
	public List<PortalResourceCategory> getCateList() {
		return cateList;
	}
	public void setCateList(List<PortalResourceCategory> cateList) {
		this.cateList = cateList;
	}
	public List<PortalResourceField> getFieldList() {
		return fieldList;
	}
	public void setFieldList(List<PortalResourceField> fieldList) {
		this.fieldList = fieldList;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public List<UlogApiExt> getUlogList() {
		return ulogList;
	}
	public void setUlogList(List<UlogApiExt> ulogList) {
		this.ulogList = ulogList;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getEndTimeNote() {
		return endTimeNote;
	}
	public void setEndTimeNote(String endTimeNote) {
		this.endTimeNote = endTimeNote;
	}
	public List<Param> getParamList() {
		return paramList;
	}
	public void setParamList(List<Param> paramList) {
		this.paramList = paramList;
	}
	public Integer getBoxid() {
		return boxid;
	}
	public void setBoxid(Integer boxid) {
		this.boxid = boxid;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCateName() {
		return cateName;
	}
	public void setCateName(String cateName) {
		this.cateName = cateName;
	}
	
	public Integer getUidType() {
		return uidType;
	}
	public void setUidType(Integer uidType) {
		this.uidType = uidType;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
}
