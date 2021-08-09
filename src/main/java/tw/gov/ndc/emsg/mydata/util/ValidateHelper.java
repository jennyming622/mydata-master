package tw.gov.ndc.emsg.mydata.util;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 */
public class ValidateHelper {

	private static Pattern TWPID_PATTERN = Pattern.compile("[ABCDEFGHJKLMNPQRSTUVXYWZIO][12]\\d{8}");

	private static Pattern NEW_RPID_PATTERN = Pattern.compile("[ABCDEFGHJKLMNPQRSTUVXYWZIO][89]\\d{8}");
	
	private static Pattern PHONE_PATTERN = Pattern.compile("^(09[0-9]{8})|([0-9]{3}-[0-9]{6,8})$");
	
	private static Pattern RPID_PATTERN = Pattern.compile("[ABCDEFGHJKLMNPQRSTUVXYWZIO]{2}\\d{8}");
	
	/**
	 * 身分證字號檢查程式，身分證字號規則： 字母(ABCDEFGHJKLMNPQRSTUVXYWZIO)對應一組數(10~35)，
	 * 令其十位數為X1，個位數為X2；( 如Ａ：X1=1 , X2=0 )；
	 * D表示2~9數字 
	 * Y = X1 + 9*X2 + 8*D1 + 7*D2 + 6*D3 + 5*D4 + 4*D5 + 3*D6 + 2*D7+ 1*D8 + D9 
	 * 如Y能被10整除，則表示該身分證號碼為正確，否則為錯誤。
	 * 臺北市(A)、臺中市(B)、基隆市(C)、臺南市(D)、高雄市(E)、臺北縣(F)、
	 * 宜蘭縣(G)、桃園縣(H)、嘉義市(I)、新竹縣(J)、苗栗縣(K)、臺中縣(L)、
	 * 南投縣(M)、彰化縣(N)、新竹市(O)、雲林縣(P)、嘉義縣(Q)、臺南縣(R)、
	 * 高雄縣(S)、屏東縣(T)、花蓮縣(U)、臺東縣(V)、金門縣(W)、澎湖縣(X)、 陽明山(Y)、連江縣(Z)
	 * 
	 * @since 2006/07/19
	 * @author https://www.javaworld.com.tw/jute/post/view?bid=35&id=168222&sty=3
	 */
	public static Boolean isValidTWPID(String twpid) {
		if(StringUtils.isBlank(twpid)) {
			return false;
		}
		boolean result = false;
		String pattern = "ABCDEFGHJKLMNPQRSTUVXYWZIO";
		if (TWPID_PATTERN.matcher(twpid.toUpperCase()).matches()) {
			int code = pattern.indexOf(twpid.toUpperCase().charAt(0)) + 10;
			int sum = 0;
			sum = (int) (code / 10) + 9 * (code % 10) + 8 * (twpid.charAt(1) - '0') + 7 * (twpid.charAt(2) - '0')
					+ 6 * (twpid.charAt(3) - '0') + 5 * (twpid.charAt(4) - '0') + 4 * (twpid.charAt(5) - '0')
					+ 3 * (twpid.charAt(6) - '0') + 2 * (twpid.charAt(7) - '0') + 1 * (twpid.charAt(8) - '0')
					+ (twpid.charAt(9) - '0');
			if ((sum % 10) == 0) {
				result = true;
			}
		}
		return result;
	}
	
	/**
	 * 手機/電話號碼驗證
	 * ex 0911223344 | 02-xxxxxxxx | 068-xxxxxxx
	 * 
	 * @param phone
	 * @return
	 */
	public static Boolean isValidPhone(String phone) {
		return PHONE_PATTERN.matcher(phone).matches();
	}
	
	/**
	 * 居留證號碼驗證
	 * ex FA12345689
	 * 居留證號碼檢查程式，居留證號碼規則： 字母(ABCDEFGHJKLMNPQRSTUVXYWZIO)對應一組數(10~35)，
	 * 第一字母十位數為X1，個位數為X2；( 如Ａ(10)：X1=1 , X2=0 )；
	 * 第二字母個位數為W2；( 如Ａ(10)：W2=0 )；
	 * D表示3~9數字 
	 * Y = X1 + 9*X2 + 8*W2 + 7*D1 + 6*D2 + 5*D3 + 4*D4 + 3*D5 + 2*D6+ 1*D7 + D8 
	 * 
	 * @param rpid
	 * @return
	 */
	public static Boolean isValidResidentPermit(String rpid) {
		if(StringUtils.isBlank(rpid)) {
			return isValidNewResidentPermit(rpid);
		}
		
		boolean result = false;
		String pattern = "ABCDEFGHJKLMNPQRSTUVXYWZIO";
		if (RPID_PATTERN.matcher(rpid.toUpperCase()).matches()) {
			int c1 = pattern.indexOf(rpid.toUpperCase().charAt(0)) + 10;
			int c2 = (pattern.indexOf(rpid.toUpperCase().charAt(1)) + 10) % 10 ;
			int sum = 0;
			sum = 1 * (c1 / 10) + 9 * (c1 % 10) + 8 * c2 + 7 * (rpid.charAt(2) - '0')
					+ 6 * (rpid.charAt(3) - '0') + 5 * (rpid.charAt(4) - '0') + 4 * (rpid.charAt(5) - '0')
					+ 3 * (rpid.charAt(6) - '0') + 2 * (rpid.charAt(7) - '0') + 1 * (rpid.charAt(8) - '0')
					+ (rpid.charAt(9) - '0');
			
			if ((sum % 10) == 0) {
				result = true;
			}
		}

		if(result == false) {
			return isValidNewResidentPermit(rpid);
		}

		return result;
	}

	public static Boolean isValidNewResidentPermit(String rpid) {
		if(StringUtils.isBlank(rpid)) {
			return false;
		}
		boolean result = false;
		String pattern = "ABCDEFGHJKLMNPQRSTUVXYWZIO";
		if (NEW_RPID_PATTERN.matcher(rpid.toUpperCase()).matches()) {
			int code = pattern.indexOf(rpid.toUpperCase().charAt(0)) + 10;
			int sum = 0;
			sum = (int) (code / 10) + 9 * (code % 10) + 8 * (rpid.charAt(1) - '0') + 7 * (rpid.charAt(2) - '0')
					+ 6 * (rpid.charAt(3) - '0') + 5 * (rpid.charAt(4) - '0') + 4 * (rpid.charAt(5) - '0')
					+ 3 * (rpid.charAt(6) - '0') + 2 * (rpid.charAt(7) - '0') + 1 * (rpid.charAt(8) - '0')
					+ (rpid.charAt(9) - '0');
			if ((sum % 10) == 0) {
				result = true;
			}
		}
		return result;
	}
}
