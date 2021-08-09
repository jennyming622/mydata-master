package tw.gov.ndc.emsg.mydata.entity;

import java.util.Date;
import java.util.List;

public class PortalBoxExt extends PortalBox{
	private String prName;
	private String psName;
	private String providerName;
	private String providerKey;
	private String ctimeStr;
	private String ctimeStr1;
	private Integer waitTime;
	private Integer dataTime;
	private Integer startTime;
	private Integer prId;
	private Integer psId;
	private Integer cateId;
	private String files;
	private String endTimeNote;
	private Integer canPreView;
	private Integer batchTime;
	private Integer min;
	private List<PortalBoxExt> boxList;
	private String code;
	private Date applyTime;
	private String agentName;
	private String randomVar;
	private Integer isOpenAgent;
	public String getPrName() {
		return prName;
	}
	public void setPrName(String prName) {
		this.prName = prName;
	}
	public String getProviderName() {
		return providerName;
	}
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
	
	public String getProviderKey() {
		return providerKey;
	}
	public void setProviderKey(String providerKey) {
		this.providerKey = providerKey;
	}
	public String getCtimeStr() {
		return ctimeStr;
	}
	public void setCtimeStr(String ctimeStr) {
		this.ctimeStr = ctimeStr;
	}
	public String getCtimeStr1() {
		return ctimeStr1;
	}
	public void setCtimeStr1(String ctimeStr1) {
		this.ctimeStr1 = ctimeStr1;
	}
	public Integer getWaitTime() {
		return waitTime;
	}
	public void setWaitTime(Integer waitTime) {
		this.waitTime = waitTime;
	}
	public Integer getDataTime() {
		return dataTime;
	}
	public void setDataTime(Integer dataTime) {
		this.dataTime = dataTime;
	}
	public Integer getStartTime() {
		return startTime;
	}
	public void setStartTime(Integer startTime) {
		this.startTime = startTime;
	}
	public Integer getPrId() {
		return prId;
	}
	public void setPrId(Integer prId) {
		this.prId = prId;
	}
	public Integer getCateId() {
		return cateId;
	}
	public void setCateId(Integer cateId) {
		this.cateId = cateId;
	}
	public String getFiles() {
		return files;
	}
	public void setFiles(String files) {
		this.files = files;
	}
	public String getEndTimeNote() {
		return endTimeNote;
	}
	public void setEndTimeNote(String endTimeNote) {
		this.endTimeNote = endTimeNote;
	}
	public Integer getCanPreView() {
		return canPreView;
	}
	public void setCanPreView(Integer canPreView) {
		this.canPreView = canPreView;
	}
	public Integer getBatchTime() {
		return batchTime;
	}
	public void setBatchTime(Integer batchTime) {
		this.batchTime = batchTime;
	}
	public Integer getMin() {
		return min;
	}
	public void setMin(Integer min) {
		this.min = min;
	}
	public List<PortalBoxExt> getBoxList() {
		return boxList;
	}
	public void setBoxList(List<PortalBoxExt> boxList) {
		this.boxList = boxList;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getPsName() {
		return psName;
	}
	public void setPsName(String psName) {
		this.psName = psName;
	}
	public Integer getPsId() {
		return psId;
	}
	public void setPsId(Integer psId) {
		this.psId = psId;
	}
	public Date getApplyTime() {
		return applyTime;
	}
	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public String getRandomVar() {
		return randomVar;
	}
	public void setRandomVar(String randomVar) {
		this.randomVar = randomVar;
	}
	public Integer getIsOpenAgent() {
		return isOpenAgent;
	}
	public void setIsOpenAgent(Integer isOpenAgent) {
		this.isOpenAgent = isOpenAgent;
	}
}
