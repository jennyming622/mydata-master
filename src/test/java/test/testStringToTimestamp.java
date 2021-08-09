package test;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class testStringToTimestamp {

	public static void main(String[] args) throws ParseException {
		String birthdateStr = "1970/9/9";
		DateFormat f1 = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");
		Date date1 = f1.parse(birthdateStr);
		System.out.println(date1);
		System.out.println(Timestamp.valueOf(sdf1.format(date1)));
		System.out.println(Timestamp.valueOf(sdf1.format(date1)).toGMTString());
		System.out.println(Timestamp.valueOf(sdf1.format(date1)).toLocaleString());
	}

}
