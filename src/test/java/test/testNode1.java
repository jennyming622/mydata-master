package test;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class testNode1 {

	public static void main(String[] args) {
		ObjectMapper om = new ObjectMapper();
		String data = "{\n" + 
				"	\"Authorization\":\"1234\",\n" + 
				"	\"sris-consumerAdminId\":\"00000000\",\n" + 
				"	\"X-Consumer-Custom-ID\":\"A41000000G\"\n" + 
				"}";
		try {
			/*JsonNode node = om.readTree(data);
			Consumer<JsonNode> dodata = (JsonNode node1) -> {System.out.println(node1.fieldNames());System.out.println(node1.asText());};
			node.forEach(dodata);*/
			JsonNode jsonNode = om.readTree(data);
		    Iterator<Entry<String, JsonNode>> jsonNodes = jsonNode.fields();  
		    while (jsonNodes.hasNext()) {  
		        Entry<String, JsonNode> node = jsonNodes.next();  
		        System.out.println(node.getKey());  
		        //System.out.println(node.getValue().toString());  
		        System.out.println(node.getValue().asText());
		    }  
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
