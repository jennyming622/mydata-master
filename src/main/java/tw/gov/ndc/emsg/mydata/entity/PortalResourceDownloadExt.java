package tw.gov.ndc.emsg.mydata.entity;

public class PortalResourceDownloadExt extends PortalResourceDownload {
	private int seq;
	private int process;
	private String processText;
	private String ctimeStr;
	private String serviceName;
	
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public int getProcess() {
		return process;
	}
	public void setProcess(int process) {
		this.process = process;
	}
	public String getProcessText() {
		return processText;
	}
	public void setProcessText(String processText) {
		this.processText = processText;
	}
	public String getCtimeStr() {
		return ctimeStr;
	}
	public void setCtimeStr(String ctimeStr) {
		this.ctimeStr = ctimeStr;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
}
