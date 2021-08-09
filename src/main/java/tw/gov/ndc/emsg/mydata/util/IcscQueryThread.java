package tw.gov.ndc.emsg.mydata.util;

import tw.com.chttl.ics.ICSCJApi;

public class IcscQueryThread extends Thread {
	private ICSCJApi icscapi;
	private String szIP;
	private int iPort;
	private String szCGIURL;
	private byte[] bSignature;
	private int iRet = 0;
	private Integer code;
	public IcscQueryThread(ICSCJApi icscapi,String szIP,int iPort,String szCGIURL,byte[] bSignature) {
		this.icscapi = icscapi;
		this.szIP = szIP;
		this.iPort = iPort;
		this.szCGIURL = szCGIURL;
		this.bSignature = bSignature;
	}
	public void run() {
		iRet = icscapi.iQuery(szIP, iPort, szCGIURL, bSignature);
		code = 0;
	}
	public int getiRet() {
		return iRet;
	}
	public void setiRet(int iRet) {
		this.iRet = iRet;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	
}
