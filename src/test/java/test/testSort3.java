package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tw.gov.ndc.emsg.mydata.entity.PortalServiceExt;

public class testSort3 {
	private static Map<String, Integer> chineseCharacterMap = new HashMap<String, Integer>();
	public static void main(String[] args) throws FileNotFoundException, IOException {
		File f = new File("/Users/mac/Desktop/tmp/13000.1.csv");
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	//System.out.println(line);
		    	String[] s1 = line.split("[,]+");
		    	chineseCharacterMap.put(s1[1], Integer.valueOf(s1[2]));
		    }
		}
		List<tobj> list = new ArrayList<tobj>();
		tobj t1 = new tobj();
		t1.setS1("未成年人線上開立數位帳戶補傳資料服務");
		t1.setS2("第一商業銀行");
		list.add(t1);
		tobj t2 = new tobj();
		t2.setS1("未成年人線上開立數位帳戶服務");
		t2.setS2("第一商業銀行");
		list.add(t2);
		
		tobj t3 = new tobj();
		t3.setS1("卡友貸申請補件服務");
		t3.setS2("玉山商業銀行");
		list.add(t3);		
		
		tobj t4 = new tobj();
		t4.setS1("永久額度調整");
		t4.setS2("永豐商業銀行");
		list.add(t4);

		
		tobj t5 = new tobj();
		t5.setS1("車貸線上申請");
		t5.setS2("台新國際商業銀行");
		list.add(t5);
		
		tobj t6 = new tobj();
		t6.setS1("信用卡申請補件服務");
		t6.setS2("永豐商業銀行");
		list.add(t6);
		
		tobj t7 = new tobj();
		t7.setS1("信用卡線上申請");
		t7.setS2("彰化商業銀行");
		list.add(t7);
		
		tobj t8 = new tobj();
		t8.setS1("信用卡線上申請");
		t8.setS2("台中商業銀行");
		list.add(t8);
		for(tobj o:list) {
			System.out.println(o.getS1()+" - "+ o.getS2());
		}
		System.out.println("------------------------");
		//Collections.sort(list,sortPortalServiceExtList(getFinanceServiceCompare()));
		testSort3 sort3 = new testSort3();
		sort3.sortPortalServiceExtList(list);
		
		for(tobj o:list) {
			System.out.println(o.getS1()+" - "+ o.getS2());
			for(int i =0;i< o.getS1().length();i++) {
				System.out.print(chineseCharacterMap.get(o.getS1().substring(i, i+1))+" ");
			}
			System.out.println();
		}
	}

    /**
     * 金融類服務多條件比較
     * @return
     */
    public List<Comparator<tobj>> getFinanceServiceCompare() {
    	List<Comparator<tobj>> compareList = new ArrayList<Comparator<tobj>>();
    	//服務名稱
    	compareList.add(serviceNameStrokeComparator);
    	//單位名稱
    	compareList.add(serviceProviderNameStrokeComparator);
		return compareList;
    }
    
    public void sortPortalServiceExtList(List<tobj> portalServiceExtList) {
    	Collections.sort(portalServiceExtList, new Comparator<tobj>() {
            @Override
            public int compare(tobj o1, tobj o2) {
    			int returnFlag = 0;
    			for(Comparator<tobj> comparator : getFinanceServiceCompare()) {
            		if(comparator.compare(o1, o2) < 0) {
            			returnFlag = -1;
            			break;
                    } else if(comparator.compare(o1, o2) > 0) {
                    	returnFlag = 1;
                    	break;
                    }
            	}
    			return returnFlag;
            }
    	});
    }
	
	
    /**
     * 根據服務名稱中文筆畫
     */
    private static Comparator<tobj> serviceNameStrokeComparator = new Comparator<tobj>() {
        @Override
        public int compare(tobj o1, tobj o2) {
        	int o1namelen = o1.getS1().length();
        	int o2namelen = o2.getS1().length();
        	String o1name = o1.getS1();
        	String o2name = o2.getS1();
        	boolean check = true;
        	int i = 0;
        	int returnFlag = 0;
        	while(check) {
        		if(i > (o1namelen-1) || i > (o2namelen-1)) {
        			//break
        			check = false;
        			if(o1namelen==o2namelen) {
        				returnFlag = 0;
        			}else {
        				if(o1namelen < o2namelen) {
        					returnFlag = -1;
        				}else {
        					returnFlag = 1;
        				}
        			}
        		}
        		if(check) {
        			if(chineseCharacterMap.get(o1name.substring(i, i+1))==null||chineseCharacterMap.get(o2name.substring(i, i+1))==null) {
        				check = false;
        				if(chineseCharacterMap.get(o1name.substring(i, i+1))==null&&chineseCharacterMap.get(o2name.substring(i, i+1))==null) {
        					returnFlag = 0;
        				}else if(chineseCharacterMap.get(o1name.substring(i, i+1))==null) {
        					returnFlag = -1;
        				}else {
        					returnFlag = 1;
        				}
        			}else {
                		if(chineseCharacterMap.get(o1name.substring(i, i+1)).compareTo(chineseCharacterMap.get(o2name.substring(i, i+1)))==0) {
                			//UNDO
                		} else {
                			//break
                			check = false;
                			if(chineseCharacterMap.get(o1name.substring(i, i+1)).compareTo(chineseCharacterMap.get(o2name.substring(i, i+1))) < 0) {
                				returnFlag = -1;
                			}else {
                				returnFlag = 1;
                			}
                		} 
        			}       			
        		}
        		i = i + 1;
        	}
        	return returnFlag;
        }
    };
    
    /**
     * 根據服務名稱中文筆畫
     */
    private static Comparator<tobj> serviceProviderNameStrokeComparator = new Comparator<tobj>() {
        @Override
        public int compare(tobj o1, tobj o2) {
        	int o1namelen = o1.getS2().length();
        	int o2namelen = o2.getS2().length();
        	String o1name = o1.getS2();
        	String o2name = o2.getS2();
        	boolean check = true;
        	int i = 0;
        	int returnFlag = 0;
        	while(check) {
        		if(i > (o1namelen-1) || i > (o2namelen-1)) {
        			//break
        			check = false;
        			if(o1namelen==o2namelen) {
        				returnFlag = 0;
        			}else {
        				if(o1namelen < o2namelen) {
        					returnFlag = -1;
        				}else {
        					returnFlag = 1;
        				}
        			}
        		}
        		if(check) {
        			if(chineseCharacterMap.get(o1name.substring(i, i+1))==null||chineseCharacterMap.get(o2name.substring(i, i+1))==null) {
        				check = false;
        				if(chineseCharacterMap.get(o1name.substring(i, i+1))==null&&chineseCharacterMap.get(o2name.substring(i, i+1))==null) {
        					returnFlag = 0;
        				}else if(chineseCharacterMap.get(o1name.substring(i, i+1))==null) {
        					returnFlag = -1;
        				}else {
        					returnFlag = 1;
        				}
        			}else {
                		if(chineseCharacterMap.get(o1name.substring(i, i+1)).compareTo(chineseCharacterMap.get(o2name.substring(i, i+1)))==0) {
                			//UNDO
                		} else {
                			//break
                			check = false;
                			if(chineseCharacterMap.get(o1name.substring(i, i+1)).compareTo(chineseCharacterMap.get(o2name.substring(i, i+1))) < 0) {
                				returnFlag = -1;
                			}else {
                				returnFlag = 1;
                			}
                		} 
        			}       			
        		}
        		i = i + 1;
        	}
        	return returnFlag;
        }
    };    
}
