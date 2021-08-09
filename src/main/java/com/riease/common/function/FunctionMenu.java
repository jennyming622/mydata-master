/**
 * 
 */
package com.riease.common.function;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能項目 - 為選單種類
 * 	- 有 URL 屬性
 * @author Cid
 *
 */
public class FunctionMenu extends FunctionItem {

	private List<String> urls;
	private String toUrl;
	
	
	/**
	 * @return the toUrl
	 */
	public String getToUrl() {
		return toUrl;
	}

	/**
	 * @param toUrl the toUrl to set
	 */
	public void setToUrl(String toUrl) {
		this.toUrl = toUrl;
	}

	/**
	 * 
	 */
	public FunctionMenu() {
	}
	
	public FunctionMenu(String id, String name) {
		super(id, name);
	}
	
	/**
	 * @return the urls
	 */
	public List<String> getUrls() {
		return urls;
	}

	/**
	 * @param urls the urls to set
	 */
	public void setUrls(List<String> urls) {
		this.urls = urls;
	}
	
	public void url(String... urls) {
		for(String url : urls) {
			addUrl(url);
		}
	}
	
	public FunctionMenu addUrl(String url) {
		if(this.urls == null) {
			this.urls = new ArrayList<String>();
		}
		this.urls.add(url);
		return this;
	}

	/* (non-Javadoc)
	 * @see com.riease.common.function.FunctionItem#getType()
	 */
	@Override
	public FunctionItemType getType() {
		return FunctionItemType.Menu;
	}
	
}
