package tw.gov.ndc.emsg.mydata.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tw.gov.ndc.emsg.mydata.entity.ChineseCharacters;
import tw.gov.ndc.emsg.mydata.entity.PortalProvider;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceCategory;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceExt;
import tw.gov.ndc.emsg.mydata.entity.SystemOption;
import tw.gov.ndc.emsg.mydata.mapper.ChineseCharactersMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalProviderMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalServiceCategoryMapper;


@Component
public class ComparatorUtil {
	@Autowired
	private ChineseCharactersMapper chineseCharactersMapper;
	@Autowired
	private PortalProviderMapper portalProviderMapper;
	@Autowired
	private PortalServiceCategoryMapper portalServiceCategoryMapper;
	
	private static Map<String, Integer> chineseCharacterMap = new HashMap<String, Integer>();
	private static Map<Integer, Integer> providerIdTypeMap = new HashMap<Integer, Integer>();
	private static Map<Integer, Integer> portalServiceCategoryMap = new HashMap<Integer, Integer>();
    /**
     * 載入 ChineseCharacters
     */
    @PostConstruct
    public void initChineseCharacters() {
    	chineseCharacterMap = chineseCharactersMapper.selectAll().stream().collect(Collectors.toMap(ChineseCharacters::getWord, ChineseCharacters::getStroke));
    	Map<String,Object> nullparam = new HashMap<String,Object>();
    	providerIdTypeMap = portalProviderMapper.selectByExample(nullparam).stream().collect(Collectors.toMap(PortalProvider::getProviderId, PortalProvider::getType));
    	portalServiceCategoryMap = portalServiceCategoryMapper.selectByExample(nullparam).stream().collect(Collectors.toMap(PortalServiceCategory::getCateId, PortalServiceCategory::getSeq));
    }
    
    /**
     * 金融類服務多條件比較
     * @return
     */
    public List<Comparator<PortalServiceExt>> getFinanceServiceCompare() {
    	List<Comparator<PortalServiceExt>> compareList = new ArrayList<Comparator<PortalServiceExt>>();
    	//服務名稱
    	compareList.add(serviceNameStrokeComparator);
    	//單位名稱
    	compareList.add(serviceProviderNameStrokeComparator);
		return compareList;
    }
    
    /**
     * 非金融類服務（其他）多條件比較
     * @return
     */
    public List<Comparator<PortalServiceExt>> getNonFinanceServiceCompare() {
    	List<Comparator<PortalServiceExt>> compareList = new ArrayList<Comparator<PortalServiceExt>>();
    	//服務類別順序比較
    	//compareList.add(serviceCategorySeqComparator);
    	//單位類型（部會>地方>國營>民營）
    	compareList.add(serviceProviderTypeComparator);
    	//單位名稱
    	compareList.add(serviceProviderNameStrokeComparator);
    	//服務名稱
    	compareList.add(serviceNameStrokeComparator);
		return compareList;
    }
    
    public void sortPortalServiceExtList(List<PortalServiceExt> portalServiceExtList) {
    	Collections.sort(portalServiceExtList, new Comparator<PortalServiceExt>() {
            @Override
            public int compare(PortalServiceExt o1, PortalServiceExt o2) {
            	/**
            	 * 服務類別排序
            	 */
            	if(serviceCategorySeqComparator.compare(o1, o2)<0) {
            		return -1;
            	}else if(serviceCategorySeqComparator.compare(o1, o2) > 0) {
            		return 1;
            	} else {
            		/**
            		 *  服務類別相等時，消費金融
            		 */
            		if(o1.getCateId().compareTo(1)==0) {
            			int returnFlag = 0;
            			int i = 0;
            			for(Comparator<PortalServiceExt> comparator : getFinanceServiceCompare()) {
            				i++;
                    		if(comparator.compare(o1, o2) < 0) {
                    			returnFlag = -1;
                    			break;
                            } else if(comparator.compare(o1, o2) > 0) {
                            	returnFlag = 1;
                            	break;
                            }
                    	}
            			return returnFlag;
            		}else {
            			int returnFlag = 0;
            			for(Comparator<PortalServiceExt> comparator : getNonFinanceServiceCompare()) {
                    		if(comparator.compare(o1, o2) < 0) {
                    			returnFlag =  -1;
                    			break;
                            } else if(comparator.compare(o1, o2) > 0) {
                            	returnFlag = 1;
                            	break;
                            }
                    	}
            			return returnFlag;
            		}
            	}
            }
    	});
    }
    
    /**
     * 根據服務類別順序比較（由小到大）
     */
    private Comparator<PortalServiceExt> serviceCategorySeqComparator = new Comparator<PortalServiceExt>() {
        @Override
        public int compare(PortalServiceExt o1, PortalServiceExt o2) {
            if(portalServiceCategoryMap.get(o1.getCateId()).compareTo(portalServiceCategoryMap.get(o2.getCateId()))== 0) {
                return 0;
            } else {
                return (portalServiceCategoryMap.get(o1.getCateId()).compareTo(portalServiceCategoryMap.get(o2.getCateId())) < 0) ? -1 : 1;
            }
        }
    };
    
    /**
     * 根據服務名稱中文筆畫
     */
    private Comparator<PortalServiceExt> serviceNameStrokeComparator = new Comparator<PortalServiceExt>() {
        @Override
        public int compare(PortalServiceExt o1, PortalServiceExt o2) {
        	int o1namelen = o1.getName().length();
        	int o2namelen = o2.getName().length();
        	String o1name = o1.getName();
        	String o2name = o2.getName();
        	boolean check = true;
        	int i = 0;
        	int returnFlag = 0;
        	boolean flag = false;
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
     * 根據單位名稱中文筆畫
     */
    private Comparator<PortalServiceExt> serviceProviderNameStrokeComparator = new Comparator<PortalServiceExt>() {
        @Override
        public int compare(PortalServiceExt o1, PortalServiceExt o2) {
        	int o1namelen = o1.getProviderName().length();
        	int o2namelen = o2.getProviderName().length();
        	String o1name = o1.getProviderName();
        	String o2name = o2.getProviderName();
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
     * 根據單位類型（部會>地方>國營>民營）（機關(構)類型）
     */
    private Comparator<PortalServiceExt> serviceProviderTypeComparator = new Comparator<PortalServiceExt>() {
        @Override
        public int compare(PortalServiceExt o1, PortalServiceExt o2) {
        	if(providerIdTypeMap.get(o1.getProviderId())==null||providerIdTypeMap.get(o2.getProviderId())==null) {
        		Map<String,Object> nullparam = new HashMap<String,Object>();
        		providerIdTypeMap = portalProviderMapper.selectByExample(nullparam).stream().collect(Collectors.toMap(PortalProvider::getProviderId, PortalProvider::getType));
        	}
            if(providerIdTypeMap.get(o1.getProviderId()).compareTo(providerIdTypeMap.get(o2.getProviderId()))== 0) {
                return 0;
            } else {
                return (providerIdTypeMap.get(o1.getProviderId()).compareTo(providerIdTypeMap.get(o2.getProviderId())) < 0) ? -1 : 1;
            }
        }
    };
}
