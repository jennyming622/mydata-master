package test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Base64;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class testPayload {
	private final Base64.Encoder encoder = Base64.getEncoder();
	private final static Base64.Decoder decoder = Base64.getDecoder();
	public static void main(String[] args) throws UnsupportedEncodingException {
		String result ="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJ3d3cuZ3NwLmdvdi50dyIsInV1aWQiOiI0Zjc5NTJhZC00NzVkLTRjNzAtYTcwZi0zMTRhMDFhZWRkYTMiLCJyZXN1bHQiOiJBMDAwMDrmqqLmoLjmraPnoroiLCJpYXQiOjE1NTcyOTg3NTB9.o58k-63ac7B_n0J4TVEDXY5sUJHJs2sM09LfsmN1ULIHoYiGfrrktwSIM4adu8rB8Pk8zRlisgEzx-2ohM0fswJRm76fh-rP_W3vcX3XWqJVW6VJipHSOyg_eUn8yaYqT1QwLD67ger5VLNUxR_DZqUb90c6RyRUWIZ8A233dYRTEGGW25-OkmBL2PEdPC4LoJTMdrEoWPlol5AkZMoh7wgNHCWiWXv_W4tuCCsOGUu7VXlGwbjIbsLtNqJ5zMYiHZxGZYK4ig3JwR2VXVea0pDvF9t0DSaBFi9wvihCpV6GlfI6j6jHZoe16Gey78BGoWbFe1DPHqNommGc2LtCOg";
		System.out.println("result:"+result);
		if(result!=null&&result.length()>0) {
			String[] resultList = result.split("\\.");
			System.out.println("resultList length:"+resultList.length);
			if(resultList!=null&&resultList.length==3) {
				for(String s:resultList) {
					System.out.println(s);
					System.out.println(URLDecoder.decode(s, "UTF-8"));
				}
				String payloadStr = resultList[1];
				System.out.println("payloadStr:"+result);
				JSONObject payloadObj = null;
				try {
					String payloadStrDecode = new String(decoder.decode(payloadStr),"UTF-8");
					payloadObj = (JSONObject) JSONValue.parseWithException(payloadStrDecode);
				} catch (ParseException | UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if(payloadObj!=null) {
					String uuid = payloadObj.get("uuid").toString();
					String statusStr = payloadObj.get("result").toString();
					System.out.println("uuid:"+uuid);
					System.out.println("statusStr:"+statusStr);
				}
			}
		}
	}

}
