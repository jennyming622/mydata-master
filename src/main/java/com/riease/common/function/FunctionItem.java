/**
 * 
 */
package com.riease.common.function;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 功能項目父類別
 * @author Cid
 *
 */
@JsonSerialize(using=FunctionItemJsonSerializer.class)
public class FunctionItem {

	public enum FunctionItemType {
		Menu,
		Command,
	}
	
	private String id;
	private String text;
	private FunctionItemType type;
	private List<FunctionMenu> menus;
	private List<FunctionCommand> commands;
	
	
	
	/**
	 * 
	 */
	public FunctionItem() {
	}
	
	public FunctionItem(String id, String text) {
		this.setId(id);
		this.setText(text);
	}
	
	public FunctionItem sub(FunctionItem... menus) {
		for(FunctionItem menu : menus) {
			if(menu instanceof FunctionMenu) {				
				add((FunctionMenu) menu);
			}
		}
		return this;
	}
	
	public FunctionItem add(FunctionMenu menu) {
		if(this.menus == null) {
			this.menus = new ArrayList<FunctionMenu>();
		}
		this.menus.add(menu);
		return this;
	}

	public FunctionItem cmd(FunctionItem... commands) {
		for(FunctionItem cmd : commands) {
			if(cmd instanceof FunctionCommand) {				
				add((FunctionCommand) cmd);
			}
		}
		return this;
	}
	
	public FunctionItem add(FunctionCommand command) {
		if(this.commands == null) {
			this.commands = new ArrayList<FunctionCommand>();
		}
		this.commands.add(command);
		return this;
	}
	
	/**
	 * @return the menus
	 */
	public List<FunctionMenu> getMenus() {
		return menus;
	}

	/**
	 * @param menus the menus to set
	 */
	public void setMenus(List<FunctionMenu> menus) {
		this.menus = menus;
	}

	/**
	 * @return the commands
	 */
	public List<FunctionCommand> getCommands() {
		return commands;
	}

	/**
	 * @param commands the commands to set
	 */
	public void setCommands(List<FunctionCommand> commands) {
		this.commands = commands;
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
	
	public FunctionItem text(String text) {
		setText(text);
		return this;
	}
	
	/**
	 * @return the type
	 */
	public FunctionItemType getType() {
		return type;
	}	
	
}

class FunctionItemJsonSerializer extends JsonSerializer<FunctionItem> {

	@Override
	public void serialize(FunctionItem item, JsonGenerator gen, SerializerProvider arg2)
			throws IOException, JsonProcessingException {
		
		gen.writeStartObject();
		gen.writeStringField("function_id", item.getId());
		gen.writeStringField("function_text", item.getText());
		gen.writeEndObject();
		
	}
	
}

