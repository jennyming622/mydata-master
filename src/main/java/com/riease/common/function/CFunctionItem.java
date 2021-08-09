/**
 * 
 */
package com.riease.common.function;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Cid
 *
 */
public class CFunctionItem {

	//private static Logger log = LoggerFactory.getLogger(CFunctionItem.class);
	
	public static CFunctionItem ROOT = new CFunctionItem().id("ROOT").text("ROOT");
	
	private String id;
	private String text;
	private List<String> urls;	//屬於該功能的網頁路徑
	private String toUrl;		//點擊後，前往的網頁路徑，指定此屬性也會加入到 urls 集合中.
	private List<CFunctionAction> actions;
	private CFunctionItem parent;
	private List<CFunctionItem> child;
	
	public CFunctionItem id(String id) {
		this.id = id;
		return this;
	}
	
	public CFunctionItem text(String text) {
		this.text = text;
		return this;
	}
	
	/**
	 * @return the toUrl
	 */
	public String getToUrl() {
		return toUrl;
	}

	/**
	 * @param toUrl the toUrl to set
	 */
	public CFunctionItem toUrl(String toUrl) {
		this.toUrl = toUrl;
		if(this.urls == null) {
			this.urls = new ArrayList<String>();
		}
		this.urls.add(this.toUrl);
		return this;
	}

	public CFunctionItem url(String... urls) {
		
		if(this.urls == null) {
			this.urls = new ArrayList<String>();
		}
		
		for(String url : urls) {
			this.urls.add(url);
		}
		return this;
	}
	
	public CFunctionItem act(CFunctionAction... actions) {
		
		if(this.actions == null) {
			this.actions = new ArrayList<CFunctionAction>();
		}
		
		for(CFunctionAction act : actions) {
			this.actions.add(act);
		}
		return this;
	}
	
	public CFunctionItem parent(CFunctionItem parent) {
		this.parent = parent;
		if(parent != null) {
			parent.addChild(this);
		}
		return this;
	}
	
	public CFunctionItem parent(CFunction func) {
		
		if(func == null) {
			return this;
		}
		
		this.parent(func.item());
		return this;
	}
	
	/**
	 * 是否為第一層的功能
	 * @return
	 */
	public boolean isTop() {
		return this.parent == null || this.parent.equals(ROOT);
	}
	
	/**
	 * 是否為最後一層功能
	 * @return
	 */
	public boolean isEnd() {
		return this.child == null || this.child.size() == 0;
	}
	
	/**
	 * @return the parent
	 */
	public CFunctionItem getParent() {
		return parent;
	}

	/**
	 * @return the child
	 */
	public List<CFunctionItem> getChild() {
		return child;
	}

	/**
	 * @param item
	 */
	public CFunctionItem addChild(CFunctionItem item) {
		
		if(item == null) {
			return this;
		}
		
		if(this.child == null) {
			this.child = new ArrayList<CFunctionItem>();
		}
		
		this.child.add(item);
		return this;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the urls
	 */
	public List<String> getUrls() {
		return urls;
	}

	/**
	 * @return the actions
	 */
	public List<CFunctionAction> getActions() {
		return actions;
	}
	
	public static String jsonStringPrettyPrint(CFunction func) {
		try {
			return jsonString(func, true);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return func.toString();
	}
	
	public static String jsonString(CFunction func) {
		try {
			return jsonString(func, false);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return func.toString();
	}
	
	private static String jsonString(CFunction func, boolean prettyPrint) throws JsonProcessingException {
		
		if(func == null) 
			return "";
		
		ObjectMapper om = new ObjectMapper();
		if(prettyPrint) {			
			om.enable(SerializationFeature.INDENT_OUTPUT);
		}
		
		ObjectNode node = om.createObjectNode();
		node.put("name", func.name());
		
		if(func.item() == null) {
			node.putNull("item");
			return om.writeValueAsString(node);
		}
		
		
		ObjectNode itemNode = node.putObject("item");
		CFunctionItem item = func.item();
		itemNode.put("id", item.getId());
		itemNode.put("text", item.getText());
		if(item.getUrls() != null) {				
			itemNode.putArray("url")
				.addAll((ArrayNode) om.valueToTree(item.getUrls()));
		}else {
			itemNode.putNull("url");
		}
		
		if(item.getActions() != null) {				
			itemNode.putArray("action")
				.addAll((ArrayNode) om.valueToTree(item.getActions()));
		}else {
			itemNode.putNull("action");
		}
		
		if(item.getParent() != null) {
			itemNode.putObject("parent")
				.put("id", item.getParent().getId())
				.put("text", item.getParent().getText());
		}else {
			itemNode.putNull("parent");
		}
		
		if(item.getChild() != null) {
			ArrayNode childNode = itemNode.putArray("child");
			for(CFunctionItem c : item.getChild()) {
				childNode.addObject()
					.put("id", c.getId())
					.put("text", c.getText());
			}
			
		}else {
			itemNode.putNull("child");
		}
		
		return om.writeValueAsString(node);
	}
	
	/**
	 * 比對 url 是否屬於這個功能項目
	 * @param url
	 * @return
	 */
	public boolean belongUrl(String url) {
		
		if(StringUtils.isEmpty(url)) {
			return false;
		}
		
		if(StringUtils.endsWith(url, this.getToUrl())) {
			return true;
		}
		
		if(this.urls != null) {
			for(String u : this.getUrls()) {
				if(StringUtils.endsWith(url, u)) {
					return true;
				}
			}
		}
		
		if(this.getChild() != null) {
			for(CFunctionItem child : this.getChild()) {
				if(child.belongUrl(url)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
}
