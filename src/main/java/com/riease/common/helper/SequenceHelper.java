package com.riease.common.helper;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;


public class SequenceHelper {

	//2017/01/01 00:00:00
	public static long initTime = 1483372800000L;
    private static ReentrantLock lock = new ReentrantLock();
    private static long lastNum = 0;
    private static int serial = 1;
    private static long preCtime = System.currentTimeMillis();
    private static int preIntSn = 0;
    private static StringBuilder buf = new StringBuilder();
        
    /**
     * 產生唯一鍵值
     * 格式：yyyyMMddHHmmssSSSnnn，共20碼字串
     * @return
     */
    public static String produceSN() {
        String sn = "";
        try{
            lock.lock();
            if(lastNum == System.currentTimeMillis()) {
                if(serial == 999) {
                    Thread.sleep(1300);
                    serial = 1;
                }else {
                    serial++;
                }
            }else {
                serial = 1;
            }
            
            sn = DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmssSSS")
                    + StringUtils.leftPad(String.valueOf(serial), 3, '0');
            
            lastNum = System.currentTimeMillis();
            
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            lock.unlock();
        }
        return sn;
    }
        
    public static String produceSN(int incremental) {
        String sn = "";
        try{
            lock.lock();
            if(lastNum == System.currentTimeMillis()) {
                if(serial >= 999) {
                    Thread.sleep(1300);
                    serial = 1;
                }else {
                    serial += incremental;
                }
            }else {
                serial = 1;
            }
                        
            sn = DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmssSSS")
                    + StringUtils.leftPad(String.valueOf(serial), 3, '0');
            
            lastNum = System.currentTimeMillis();
            
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            lock.unlock();
        }
        return sn;
    }
    
    /**
     * 產出唯一鍵值
     * 格式：代表時間的long值（至milliseconds）
     * @return
     */
    public static long produceSN2() {
        try {
            lock.lock();
            if(preCtime >= System.currentTimeMillis()){
                if(serial == 999) {
                    Thread.sleep(1300);
                    serial = 1;
                }else {
                    serial++;
                }
            }else {
                serial = 1;
            }
            
            preCtime = System.currentTimeMillis()+serial;
            
        }catch(Exception ex) {
            ex.printStackTrace();
        }finally {
            lock.unlock();
        }
        return preCtime;
    }
    
    /**
     * 產出唯一鍵值
     * 格式：把產生出的數值轉為字串後附加identifier於最後，再轉為long值
     * @param identifier
     * @return
     */
    public static long produceSN2(String identifier) {
    	long result = 0;
    	try {
            lock.lock();
            if(preCtime >= System.currentTimeMillis()){
                if(serial == 999) {
                    Thread.sleep(1300);
                    serial = 1;
                }else {
                    serial++;
                }
            }else {
                serial = 1;
            }
            
            preCtime = System.currentTimeMillis()+serial;
            
            if(buf.length()>0) buf.delete(0, buf.length());
            buf.append(preCtime).append(identifier);
            result = Long.valueOf(buf.toString());
            
        }catch(Exception ex) {
            ex.printStackTrace();
        }finally {
            lock.unlock();
        }
        return result;
    }
    
    /**
     * 產出唯一鍵值。
     * 格式：代表時間的long值（至milliseconds）
     * @return
     */
    public static long produceSN3() {
        try {
            lock.lock();
            if(preCtime >= (System.currentTimeMillis()-initTime)){
                if(serial == 999) {
                    Thread.sleep(1300);
                    serial = 1;
                }else {
                    serial++;
                }
            }else {
                serial = 1;
            }
            
            preCtime = (System.currentTimeMillis()-initTime)+serial;
            
        }catch(Exception ex) {
            ex.printStackTrace();
        }finally {
            lock.unlock();
        }
        return preCtime;
    }
    
    /**
     * 
     * @return
     */
    public static int intSN() {
    	try {
            lock.lock();
            if(preIntSn == 0) {
            	preIntSn = (int)((System.currentTimeMillis()-initTime)/1000);
            }
            
            int diff = (int)((System.currentTimeMillis()-initTime)/1000);
            if(preIntSn >= diff) {
                preIntSn += 1;
            }else {
                preIntSn = diff;
            }
            
        }catch(Exception ex) {
            ex.printStackTrace();
        }finally {
            lock.unlock();
        }
        return preIntSn;
    }
    
    /**
	 * 產生UUID
	 * @return
	 */
	public static String createUUID() {
		String uuid = UUID.randomUUID().toString();
		uuid = uuid.replaceAll("-", "");
		return uuid;
	}
    
    public static void main(String[] args) throws ParseException {
        Date dt = DateUtils.parseDate("20170101000000", "yyyyMMddHHmmss");
        System.out.println(dt.getTime());
        System.out.println((System.currentTimeMillis() - dt.getTime())/1000);
    }
    
    
}
