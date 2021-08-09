package tw.gov.ndc.emsg.mydata.entity;

import java.util.List;

public class UlogApiExt extends UlogApi {
	private String actionStr;
	private String serviceName;
	private String serviceItem;
	private List<String> scopeItemList;
	private String ctimeStr;
	private String ctimeStr1;
	private List<UlogApiExt> ulogApiExtList;
	private String providerName;
	private String resourceName;
	public String getActionStr() {
		return actionStr;
	}
	public void setActionStr(String actionStr) {
		this.actionStr = actionStr;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getServiceItem() {
		return serviceItem;
	}
	public void setServiceItem(String serviceItem) {
		this.serviceItem = serviceItem;
	}
	public List<String> getScopeItemList() {
		return scopeItemList;
	}
	public void setScopeItemList(List<String> scopeItemList) {
		this.scopeItemList = scopeItemList;
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
	public List<UlogApiExt> getUlogApiExtList() {
		return ulogApiExtList;
	}
	public void setUlogApiExtList(List<UlogApiExt> ulogApiExtList) {
		this.ulogApiExtList = ulogApiExtList;
	}
	public String getProviderName() {
		return providerName;
	}
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	
}
