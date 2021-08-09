package test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestDateFormat {

	public static void main(String[] args) throws ParseException {
		String dateStr = "1970/9/27";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM");
		SimpleDateFormat sdf3 = new SimpleDateFormat("dd");
		Date date1 = sdf.parse(dateStr);
		System.out.println(sdf1.format(date1));
		String yearStr = String.valueOf(Integer.valueOf(sdf1.format(date1)) -1911);
		System.out.println(yearStr);
		System.out.println(sdf2.format(date1));
		System.out.println(sdf3.format(date1));
		String datefinalStr = sdf.format(date1);
		System.out.println(datefinalStr);
	}

}
