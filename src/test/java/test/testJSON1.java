package test;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import tw.gov.ndc.emsg.mydata.entity.Param;

public class testJSON1 {

	public static void main(String[] args) {
		String jsonStr = "{\n"
				+ "    \"verified\": \"true\""
				+ "}";
		ObjectMapper om = new ObjectMapper();
		try {
			JsonNode node = om.readTree(jsonStr);
			System.out.println(node.findValue("verified").asText());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("------------------------------");	

	}

}
