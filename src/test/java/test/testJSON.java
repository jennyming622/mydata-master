package test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import tw.gov.ndc.emsg.mydata.entity.Param;

public class testJSON {

	public static void main(String[] args) {
		String jsonStr = "{\"mobile\":\"綁定電話號碼\",\"verifycode\":\"手機驗證碼\",\"invnum\":\"商家統編\",\"invdate\":\"消費日\"}";
		LinkedHashMap json = null;
		JSONParser parser = new JSONParser();
		ContainerFactory orderedKeyFactory = new ContainerFactory()
		{
		    public List creatArrayContainer() {
		      return new LinkedList();
		    }

		    public Map createObjectContainer() {
		      return new LinkedHashMap();
		    }

		};
		try {
			json = (LinkedHashMap) parser.parse(jsonStr, orderedKeyFactory);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Iterator iter = json.keySet().iterator();
		while (iter.hasNext()) {
			Param p = new Param(); 
		    Object key = iter.next();
		    String val = json.get(key).toString();
		    System.out.println(key+" : "+val);
		}
		System.out.println("------------------------------");		
	}

}
