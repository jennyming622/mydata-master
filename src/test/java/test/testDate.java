package test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class testDate {

	public static void main(String[] args) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.SECOND, 120);
		Date estime = cal.getTime();
		System.out.println(estime.before(new Date()));
		System.out.println(estime.after(new Date()));
	}

}
