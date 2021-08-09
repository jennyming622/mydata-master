package test;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class testNode {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ObjectMapper om = new ObjectMapper();
		String data = "{\"SubjectId\":\"eccd0689-4da1-431c-99f9-d20c56fd7534\",\"ClientId\":\"CLI.mydata.portal\",\"Scopes\":[\"openid\",\"gsp.profile\",\"email\",\"API0000001-read\",\"profile\"],\"CreationTime\":\"2018-06-04T09:50:46Z\",\"Expiration\":null}";
		try {
			JsonNode node = om.readTree(data);
			
			System.out.println(node.findValue("SubjectId").asText());
			System.out.println(node.findValue("ClientId").asText());
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
