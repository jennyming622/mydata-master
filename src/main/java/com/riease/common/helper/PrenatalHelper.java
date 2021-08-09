package com.riease.common.helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PrenatalHelper {
	public int getPrenatalWeek(Date expiredDate,Date date){
		//預產日
		Date date1 = expiredDate;
		//懷孕日
		Date date2 = new Date(date1.getTime()-(long) 280 * (long) 24 * (long) 60 * (long) 60 * (long)1000);
		System.out.println("懷孕日="+date2);
		//差距天數
		System.out.println(date.getTime() - date2.getTime());
		int distDays = (int)((date.getTime() - date2.getTime())/((long) 24 * (long) 60 * (long) 60 * (long)1000));
		System.out.println("差距天數="+distDays);
		int weeks_mod = (distDays % 7);
		int weeks = (distDays / 7);
		if(weeks_mod!=0){
			weeks = weeks + 1;
		}
		System.out.println("懷孕週數="+weeks);
		return weeks;
	}
	
	public float countBMI(float weight,float height) {
		float bmi = 0f;
		bmi = weight / (height*height); 
		return bmi;
	}
	
	public int bmiLevel(float bmi) {
		int level = 0;
		//體重過輕
		if(bmi<18.5f) {
			 level = 1;
		}
		//正常體重
		if(bmi>=18.5f&&bmi<25.0f) {
			level = 2;
		}
		//體重過重
		if(bmi>=25.0f&&bmi<30.0f) {
			level = 3;
		}
		//肥 胖
		if(bmi>=30.0f) {
			level = 4;
		}
		return level;
	}
	
	public ArrayList<ArrayList<Float>> getBmiHighAndLowStandard(int firstWeek,float firstWeight,float firstHeight){
		ArrayList<ArrayList<Float>> retArrayList = new ArrayList<ArrayList<Float>>();
		int level = bmiLevel(countBMI(firstWeight,firstHeight));
		float increaseLower = 0.45f;
		float increaseUpper = 0.59f;
		//體重過輕
		if(level==1) {
			increaseLower = 0.45f;
			increaseUpper = 0.59f;
		}
		//正常體重
		if(level==2) {
			increaseLower = 0.36f;
			increaseUpper = 0.45f;
		}
		//體重過重
		if(level==3) {
			increaseLower = 0.23f;
			increaseUpper = 0.32f;
		}
		//肥 胖
		if(level==4) {
			increaseLower = 0.18f;
			increaseUpper = 0.27f;
		}
		
		return retArrayList;
	}
	
	public static void main(String[] args) {
		PrenatalHelper h = new PrenatalHelper();
		System.out.println(h.countBMI(50f,1.6f));
		System.out.println(h.bmiLevel(h.countBMI(50f,1.6f)));
	}

}
