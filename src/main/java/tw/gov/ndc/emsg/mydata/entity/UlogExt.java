package tw.gov.ndc.emsg.mydata.entity;

public class UlogExt extends Ulog{
	
	private String scopeName;
	private String clientName;
	
	public String getScopeName() {
		return scopeName;
	}

	public void setScopeName(String scopeName) {
		this.scopeName = scopeName;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
}
