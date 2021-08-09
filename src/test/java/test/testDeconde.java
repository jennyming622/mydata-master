package test;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

public class testDeconde {
	public static void main(String[] args) throws UnsupportedEncodingException {
		Base64.Decoder decoder = Base64.getDecoder();
		String s ="W3siUkVHX1VOSVRfTkFNRSI6Iuahg+WckuW4guaUv+W6nCIsIkVTVEFCX0FQUF9OTyI6IjA5ODMyMDU0MDEiLCJOQU1FIjoi6I6K56eJ5rWkIiwiQ09SUF9SRVAiOiJOIiwiRFRSX0VORF9EQVRFIjp7ImRhdGUiOjksImRheSI6MiwiaG91cnMiOjAsIm1pbnV0ZXMiOjAsIm1vbnRoIjozLCJuYW5vcyI6MCwic2Vjb25kcyI6MCwidGltZSI6MTU1NDczOTIwMDAwMCwidGltZXpvbmVPZmZzZXQiOi00ODAsInllYXIiOjExOX0sIk9SR19UWVBFIjoiMDEiLCJDSEdfQVBQX05PIjoiMTA3OTA4OTAyMSIsIkRVVFlfVFlQRV9OQU1FIjoi5pyJ6ZmQIiwiQ01QWV9SRVAiOiJZIiwiQ0hHX0FQUF9EQVRFIjp7ImRhdGUiOjIxLCJkYXkiOjQsImhvdXJzIjo5LCJtaW51dGVzIjoyNiwibW9udGgiOjUsIm5hbm9zIjowLCJzZWNvbmRzIjoxOCwidGltZSI6MTUyOTU0NDM3ODAwMCwidGltZXpvbmVPZmZzZXQiOi00ODAsInllYXIiOjExOH0sIlBPU19OQU1FIjoi6JGj5LqL6ZW3IiwiRFRSX0JFR19EQVRFIjp7ImRhdGUiOjEwLCJkYXkiOjAsImhvdXJzIjowLCJtaW51dGVzIjowLCJtb250aCI6MywibmFub3MiOjAsInNlY29uZHMiOjAsInRpbWUiOjE0NjAyMTc2MDAwMDAsInRpbWV6b25lT2Zmc2V0IjotNDgwLCJ5ZWFyIjoxMTZ9LCJBUl9EQVRFIjpudWxsLCJTRVFfTk8iOjIyNTMxMzYyNSwiSU5WRVNUX0FNVCI6MzM1MDAwLCJFU1RBQl9BUFBfREFURSI6eyJkYXRlIjo4LCJkYXkiOjMsImhvdXJzIjowLCJtaW51dGVzIjowLCJtb250aCI6MywibmFub3MiOjAsInNlY29uZHMiOjAsInRpbWUiOjEyMzkxMjAwMDAwMDAsInRpbWV6b25lT2Zmc2V0IjotNDgwLCJ5ZWFyIjoxMDl9LCJDTVBZX1NUQVRVUyI6IjAxIiwiQ01QWV9OQU1FIjoi552/6Y2256eR5oqA6IKh5Lu95pyJ6ZmQ5YWs5Y+4IiwiRU5BTUUiOm51bGwsIkNOIjoi5Lit6I+v5rCR5ZyLIiwiQkFOX05PIjoiMjQzODQyMjQifSx7IlJFR19VTklUX05BTUUiOiLmoYPlnJLluILmlL/lupwiLCJFU1RBQl9BUFBfTk8iOiIxMDc5MDkxOTE3IiwiTkFNRSI6IuiOiueniea1pCIsIkNPUlBfUkVQIjoiTiIsIkRUUl9FTkRfREFURSI6eyJkYXRlIjo1LCJkYXkiOjEsImhvdXJzIjowLCJtaW51dGVzIjowLCJtb250aCI6NiwibmFub3MiOjAsInNlY29uZHMiOjAsInRpbWUiOjE2MjU0MTQ0MDAwMDAsInRpbWV6b25lT2Zmc2V0IjotNDgwLCJ5ZWFyIjoxMjF9LCJPUkdfVFlQRSI6IjAxIiwiQ0hHX0FQUF9OTyI6bnVsbCwiRFVUWV9UWVBFX05BTUUiOiLmnInpmZAiLCJDTVBZX1JFUCI6Ik4iLCJDSEdfQVBQX0RBVEUiOm51bGwsIlBPU19OQU1FIjoi6JGj5LqLIiwiRFRSX0JFR19EQVRFIjp7ImRhdGUiOjYsImRheSI6NSwiaG91cnMiOjAsIm1pbnV0ZXMiOjAsIm1vbnRoIjo2LCJuYW5vcyI6MCwic2Vjb25kcyI6MCwidGltZSI6MTUzMDgwNjQwMDAwMCwidGltZXpvbmVPZmZzZXQiOi00ODAsInllYXIiOjExOH0sIkFSX0RBVEUiOm51bGwsIlNFUV9OTyI6MjI2NTQ3NDUzLCJJTlZFU1RfQU1UIjo5MDAwLCJFU1RBQl9BUFBfREFURSI6eyJkYXRlIjoxNiwiZGF5IjoxLCJob3VycyI6MCwibWludXRlcyI6MCwibW9udGgiOjYsIm5hbm9zIjowLCJzZWNvbmRzIjowLCJ0aW1lIjoxNTMxNjcwNDAwMDAwLCJ0aW1lem9uZU9mZnNldCI6LTQ4MCwieWVhciI6MTE4fSwiQ01QWV9TVEFUVVMiOiIwMSIsIkNNUFlfTkFNRSI6IuWMr+WHseenkeaKgOiCoeS7veaciemZkOWFrOWPuCIsIkVOQU1FIjpudWxsLCJDTiI6IuS4reiPr+awkeWciyIsIkJBTl9OTyI6IjU0OTQwNTg0In1d";
		String s1 = new String(decoder.decode(s), "UTF-8");
		System.out.println(s1);
		//JSONObject obj = new JSONObject(s1);
		JSONArray objArray = new JSONArray(s1);
		System.out.println(objArray.length());
		for(int i=0;i<objArray.length();i++) {
			JSONObject obj = (JSONObject) objArray.get(i);
			JSONObject drtObj = (JSONObject)obj.get("DTR_END_DATE");
			System.out.println(obj.get("REG_UNIT_NAME"));
			System.out.println(obj.get("ESTAB_APP_NO"));
			System.out.println(obj.get("CMPY_NAME"));
			System.out.println(obj.get("NAME"));
			System.out.println(obj.get("POS_NAME"));
			System.out.println(drtObj.get("time"));
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(Long.valueOf(drtObj.get("time").toString()));
			System.out.println(cal.getTime());
			System.out.println("---------------------");
		}
		
	}

}
