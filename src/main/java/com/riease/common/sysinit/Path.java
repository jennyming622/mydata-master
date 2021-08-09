package com.riease.common.sysinit;


public class Path {
	private String realPath = "";
	private String webContextPath = "";
	private String hostName = "";
		
	private static Path ref = null;
	
	private Path() {
	}
	
	public static Path getInstance() {
		if(ref == null)
			ref = new Path();
		return ref;
	}

	public String getRealPath() {
		return realPath;
	}

	public void setRealPath(String realPath) {
		this.realPath = realPath;
	}

	public String getWebContextPath() {
		return webContextPath;
	}

	public void setWebContextPath(String webContextPath) {
		this.webContextPath = webContextPath;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

}
