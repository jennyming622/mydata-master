package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class testNumber {

	public static void main(String[] args) {
		String str = ""
				+ " 02 03 06	 08 13 21 07 "
				+ "10	18	26	29	30	34	05 02	03	14	17	20	38	03 "
				+ "04	06	16	18	19	28	02 05	 06	18	24	25	35	02 "
				+ "20	23	24	29	35	38	01 07	09	14	16	19	21	05 "
				+ "07	14	29	32	36	38	05 11	21	26	33	35	37	04 "
				+ "11	13	19	33	34	36	01 03	14	16	19	21	30	02 "
				+ "14	23	27	34	35	36	03 02	05	11	17	29	34	05 "
				+ "02	14	16	17	27	28	01 22	23	24	33	34	38	04 "
				+ "04	07	16	21	27	34	04 01	15	18	24	36	38	06 "
				+ "07	08	10	15	17	23	03 05	06	13	17	23	33	02 "
				+ "05	07	08	09	12	25	06 03	15	18	19	22	26	02 "
				+ "03	09	23	27	29	30	01 04	10	13	15	31	35	01 "
				+ "04	06	10	13	36	37	08";
		String[] sa = str.split("[\\D]+");
		Map<String,Integer> smap = new HashMap<String,Integer>();
		for(String s:sa) {
			//System.out.println(s);
			if(smap.get(s)==null) {
				smap.put(s, 1);
			}else {
				smap.put(s, smap.get(s)+1);
			}
		}
		System.out.println("--------------------------------");
        List<Entry<String,Integer>> list = new ArrayList<Entry<String,Integer>>(smap.entrySet());
        list.sort(Entry.comparingByValue());
        //list.sort(Entry.comparingByKey());
        Map<String,Integer> result = new LinkedHashMap<>();
        for (Entry<String,Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
            System.out.println(entry.getKey()+"    "+ entry.getValue());
        }
	}

}
