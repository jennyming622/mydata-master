package test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

public class testIdentify {
	private static String Identify(List<String> params) throws UnsupportedEncodingException {
		String identityStr ="";
		if(params!=null&&params.size()>0) {
			for(String s:params) {
				identityStr = identityStr + s;
			}
		}
		System.out.println(identityStr);
		byte[] _inputByte = identityStr.getBytes("UTF-16LE");
		HashCode _hashByte = Hashing.sha256().hashBytes(_inputByte);
		String IdentifyNo = _hashByte.toString().toLowerCase(Locale.ENGLISH);
		System.out.println("IdentifyNo=\n"+IdentifyNo);
		return IdentifyNo;
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		//pin,businessNo,apiVersion,hashKeyNo,verifyNo,token
		List<String> list = new ArrayList<String>();
//		//BusinessNo +
//		list.add("03374707");
//		//ApiVersion +
//		list.add("1.0");
//		//HashKeyNo +
//		list.add("10");
//		//VerifyNo +
//		list.add("8627286c-ebc7-4909-9374-8c8985cff7b8");
//		
//		//MemberNo +
//		list.add("H120983053");
//		//Token +
//		list.add("006d6961f46040f8a0ae06bdd3df8f27fe5a63a0");
//		
//		//HashKey
//		list.add("trgfdyh6fuk6");
		
		//pin,businessNo,apiVersion,hashKeyNo,verifyNo,token
//		list.add("599999");
//		list.add("03374707");
//		list.add("1.0");
//		list.add("10");
//		list.add("089072159");
//		list.add("74bd4b8729ed432b9aa63fdeaff9d5b2292c5a93");
		
//		SHA256(
//				businessNo +
//				apiVersion +
//				hashKeyNo +
//				verifyNo +
//				token +
//				hashKey
//				)
		//BusinessNo +
		list.add("03374707");
		//ApiVersion +
		list.add("1.0");
		//HashKeyNo +
		list.add("10");
		//VerifyNo +
		list.add("31e5f44acfe44f04898921372707f105");
		// token
		list.add("623bc838ecbd4220853f0ad567547307ac892800");
		//HashKey
		list.add("trgfdyh6fuk6");
		
		String IdentifyNo = Identify(list);
		System.out.println(IdentifyNo);

	}

}
