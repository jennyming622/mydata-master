package tw.gov.ndc.emsg.mydata.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riease.common.helper.ValidatorHelper;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.mchange.v2.codegen.bean.BeangenUtils;
//import com.riease.common.helper.HttpClientHelper;
import com.riease.common.sysinit.SessionRecord;

import tw.gov.ndc.emsg.mydata.entity.*;
import tw.gov.ndc.emsg.mydata.entity.ext.PortalCounterSubExt;
import tw.gov.ndc.emsg.mydata.entity.ext.PortalCounterSubScopeExt;
import tw.gov.ndc.emsg.mydata.mapper.*;
import tw.gov.ndc.emsg.mydata.mapper.ext.PortalResourceExtMapper;
import tw.gov.ndc.emsg.mydata.mapper.ext.UlogApiMapperExt;
import tw.gov.ndc.emsg.mydata.util.CDateUtil;
import tw.gov.ndc.emsg.mydata.util.ComparatorUtil;
import tw.gov.ndc.emsg.mydata.util.MaintainUtils;
import tw.gov.ndc.emsg.mydata.util.MaskUtil;
import tw.gov.ndc.emsg.mydata.web.AuController.UlogApiExtComparator;

@Controller
@RequestMapping("/sp")
public class SpController {
	private static final Logger logger = LoggerFactory.getLogger(SpController.class);
	private static DecimalFormat formatter = new DecimalFormat("#.#");
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
	private static SimpleDateFormat sdf3 = new SimpleDateFormat("年MM月dd日");
	private static SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy");
	private static SimpleDateFormat sdf5 = new SimpleDateFormat("年MM月dd日HH時mm分ss秒");

	private static final String ATTR_CER = "javax.servlet.request.X509Certificate";
	private static final String SCHEME_HTTPS = "https";

	/**
	 * 檔案保存期限(小時)
	 */
	private static int fileStoreTime = 8;
	/*
	 * @Autowired private ApplicationMapper applicationMapper;
	 */
	@Autowired
	private PortalServiceMapper portalServiceMapper;
	@Autowired
	private PortalServiceCategoryMapper portalServiceCategoryMapper;
	@Autowired
	private PortalServiceScopeMapper portalServiceScopeMapper;
	@Autowired
	private PortalServiceScopeCounterMapper portalServiceScopeCounterMapper;
	@Autowired
	private PortalServiceDownloadMapper portalServiceDownloadMapper;
	@Autowired
	private PortalServiceDownloadSubMapper portalServiceDownloadSubMapper;
	@Autowired
	private PortalCounterSubMapperExt portalCounterSubMapperExt;
	
	@Autowired
	private PortalResourceMapper portalResourceMapper;
	@Autowired
	private PortalResourceExtMapper portalResourceExtMapper;
	@Autowired
	private PortalResourceCategoryMapper portalResourceCategoryMapper;

	@Autowired
	private PortalProviderMapper portalProviderMapper;
	@Autowired
	private CategoryMapper categoryMapper;

	@Autowired
	private UlogMapper ulogMapper;
	@Autowired
	private UlogApiMapperExt ulogApiMapperExt;
	@Autowired
	private PortalResourceScopeMapper portalResourceScopeMapper;

	@Autowired
	private PortalBoxMapper portalBoxMapper;
	@Autowired
	private PortalResourceDownloadMapper portalResourceDownloadMapper;
	@Autowired
	private PortalBatchDownloadMapper portalBatchDownloadMapper;
	@Autowired
	private MemberMapper memberMapper;
	@Autowired
	private MemberPrivacyMapper memberPrivacyMapper;
	@Autowired
	private MaintainUtils maintainUtils;
	@Autowired
	private ComparatorUtil comparatorUtil;
	
	@Value("${app.frontend.context.url}")
	private String frontendContextUrl;
	/**
	 * https://login.cp.gov.tw/v1/connect/authorize
	 */
	@Value("${app.oidc.authorize.uri}")
	private String authorizeUri;

	// Mydata
	@Value("${gsp.oidc.client.id}")
	private String clientId;
	@Value("${app.oidc.redirect.uri}")
	private String redirectUri;

	@Value("${app.oidc.response.type}")
	private String responseType;
	@Value("${app.oidc.response.mode}")
	private String responseMode;

	@Value("${app.download.path.temp}")
	private String downloadPath;
	
	/**
	 * init for /client
	 */
	private List<PortalResource> allPortalResourceList = null;
	private List<PortalProvider> allPortalProviderList = null;
	private List<PortalResourceExt> allPortalResourceExtList = null;
	private List<PortalService> allPortalServiceList1 = null;
	private List<PortalService> allPortalServiceList2 = null;
	private List<PortalResourceScope> allPortalResourceScopeList = null;
	private List<PortalServiceScope> allPortalServiceScopeList = null;
	private List<PortalServiceScopeCounter> allPortalServiceScopeCounterList = null;
	private List<PortalServiceCategory> allPortalServiceCategoryList = null;
	
    @PostConstruct
    public void initSpController() {
    	Map<String,Object> nullparam = new HashMap<String,Object>();
    	allPortalResourceList = portalResourceMapper.selectByExample(nullparam);
    	allPortalProviderList = portalProviderMapper.selectByExample(nullparam);
    	allPortalResourceExtList = portalResourceExtMapper.selectPortalResourceWithCateName();
		Map<String, Object> psparam1 = new HashMap<String, Object>();
		psparam1.put("isShow", 1);
		psparam1.put("ProviderNameAndNameAsc", true);
		allPortalServiceList1 = portalServiceMapper.selectForFrontByExample(psparam1);
		Map<String, Object> psparam2 = new HashMap<String, Object>();
		psparam2.put("isCounter", 1);
		psparam2.put("ProviderNameAndNameAsc", true);
		allPortalServiceList2 = portalServiceMapper.selectForFrontByExample(psparam2);
		Map<String, Object> sparam = new HashMap<String, Object>();
		allPortalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
		Map<String, Object> pssparam = new HashMap<String, Object>();
		allPortalServiceScopeList = portalServiceScopeMapper.selectByExample(pssparam);
		Map<String, Object> pssparam1 = new HashMap<String, Object>();
		allPortalServiceScopeCounterList = portalServiceScopeCounterMapper.selectByExample(pssparam1);
		allPortalServiceCategoryList = portalServiceCategoryMapper.selectByExample(nullparam);
    }
	
    @Scheduled(fixedRate = 60000)
    public void execute() {
    	Map<String,Object> nullparam = new HashMap<String,Object>();
    	allPortalResourceList = portalResourceMapper.selectByExample(nullparam);
    	allPortalProviderList = portalProviderMapper.selectByExample(nullparam);
    	allPortalResourceExtList = portalResourceExtMapper.selectPortalResourceWithCateName();
		Map<String, Object> psparam1 = new HashMap<String, Object>();
		psparam1.put("isShow", 1);
		psparam1.put("ProviderNameAndNameAsc", true);
		allPortalServiceList1 = portalServiceMapper.selectForFrontByExample(psparam1);
		Map<String, Object> psparam2 = new HashMap<String, Object>();
		psparam2.put("isCounter", 1);
		psparam2.put("ProviderNameAndNameAsc", true);
		allPortalServiceList2 = portalServiceMapper.selectForFrontByExample(psparam2);
		Map<String, Object> sparam = new HashMap<String, Object>();
		allPortalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
		Map<String, Object> pssparam = new HashMap<String, Object>();
		allPortalServiceScopeList = portalServiceScopeMapper.selectByExample(pssparam);
		Map<String, Object> pssparam1 = new HashMap<String, Object>();
		allPortalServiceScopeCounterList = portalServiceScopeCounterMapper.selectByExample(pssparam1);
		allPortalServiceCategoryList = portalServiceCategoryMapper.selectByExample(nullparam);
    }
	/**
	 * 網站地圖 /sp/sitemap
	 *
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@GetMapping("/sitemap")
	public String getSpSiteMap(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return "sitemap";
	}

	@GetMapping("/contactus")
	public String getSpContactUs(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return "contact_us";
	}

	@GetMapping("/servicepolicy")
	public String getSpServicePolicy(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return "service-policy";
	}

	@GetMapping("/downloadpolicy")
	public String getSpDownloadPolicy(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return "download-policy";
	}

	@GetMapping("/privacypolicy")
	public String getSpPrivatePolicy(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return "privacy-policy";
	}

	@GetMapping("/faq")
	public String getSpFaq(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return "faq";
	}

	@GetMapping("/about")
	public String getSpAbout(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
//		Map<String, Object> sparam = new HashMap<String, Object>();
//		List<PortalResourceScope> tmpPortalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
		if(allPortalResourceScopeList==null) {
			Map<String, Object> sparam = new HashMap<String, Object>();
			allPortalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
		}
		
//		Map<String, Object> pssparam = new HashMap<String, Object>();
//		List<PortalServiceScope> tmpPortalServiceScopeList = portalServiceScopeMapper.selectByExample(pssparam);
		if(allPortalServiceScopeList==null) {
			Map<String, Object> pssparam = new HashMap<String, Object>();
			allPortalServiceScopeList = portalServiceScopeMapper.selectByExample(pssparam);
		}
		
//		Map<String, Object> pssparam1 = new HashMap<String, Object>();
//		List<PortalServiceScopeCounter> tmpPortalServiceScopeCounterList = portalServiceScopeCounterMapper.selectByExample(pssparam1);
		if(allPortalServiceScopeCounterList==null) {
			Map<String, Object> pssparam1 = new HashMap<String, Object>();
			allPortalServiceScopeCounterList = portalServiceScopeCounterMapper.selectByExample(pssparam1);
		}
		
		/**
		 * 資料集
		 */
		//List<PortalResourceExt> prExtList = portalResourceExtMapper.selectPortalResourceWithCateName();
		if(allPortalResourceExtList==null) {
			allPortalResourceExtList = portalResourceExtMapper.selectPortalResourceWithCateName();
		}
		List<PortalResourceExt> finalPrExtList = new ArrayList<PortalResourceExt>();
		if (allPortalResourceExtList != null && allPortalResourceExtList.size() > 0) {
			for (PortalResourceExt ext : allPortalResourceExtList) {
				PortalResourceExt tmpExt = new PortalResourceExt();
				BeanUtils.copyProperties(ext, tmpExt);
				if (maintainUtils.checkInMaintain(ext.getPrId())) {
					tmpExt.setCheckStat(9);
				}
				finalPrExtList.add(tmpExt);
			}
		}
		model.addAttribute("prExtList", finalPrExtList);

		/**
		 * 線上服務
		 * ProviderNameAndNameAsc
		 * order by psc.seq asc,pp.seq asc,pp.name asc,ps.name asc
		 * 
		 * 服務分類排序
		 * 3,2,4,1,5
		 */
		if(allPortalServiceCategoryList==null) {
			Map<String,Object> nullparam = new HashMap<String,Object>();
			allPortalServiceCategoryList = portalServiceCategoryMapper.selectByExample(nullparam);
		}
		
		List<PortalServiceExt> portalServiceExtList = new ArrayList<PortalServiceExt>();
//		Map<String, Object> psparam1 = new HashMap<String, Object>();
//		psparam1.put("isShow", 1);
//		psparam1.put("ProviderNameAndNameAsc", true);
//		List<PortalService> portalServiceList1 = portalServiceMapper.selectForFrontByExample(psparam1);
		if(allPortalServiceList1==null) {
			Map<String, Object> psparam1 = new HashMap<String, Object>();
			psparam1.put("isShow", 1);
			psparam1.put("ProviderNameAndNameAsc", true);
			allPortalServiceList1 = portalServiceMapper.selectForFrontByExample(psparam1);
		}
		if (allPortalServiceList1 != null && allPortalServiceList1.size() > 0) {
			for (PortalService p : allPortalServiceList1) {
				List<Integer> allowPrIdList = new ArrayList<Integer>();
				PortalServiceExt ext = new PortalServiceExt();
				BeanUtils.copyProperties(p, ext);
				PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(ext.getProviderId()));
				if (portalProvider != null) {
					ext.setProviderName(portalProvider.getName());
					if(portalProvider.getType()!=null) {
						ext.setType(portalProvider.getType());
					}else {
						ext.setType(0);
					}
				}
				PortalServiceCategory cate = null;
				if(allPortalServiceCategoryList!=null&&allPortalServiceCategoryList.size()>0) {
					for(PortalServiceCategory tcate:allPortalServiceCategoryList) {
						if(ext.getCateId().compareTo(tcate.getCateId())==0) {
							cate = tcate;
						}
					}
				}
				if (cate != null) {
					ext.setCate(cate);
				}


				checkDpMaintainAndLevel(ext, allPortalResourceList, allPortalResourceScopeList, allPortalServiceScopeList);
				portalServiceExtList.add(ext);
			}
		}
		
		comparatorUtil.sortPortalServiceExtList(portalServiceExtList);
		model.addAttribute("psExtList", portalServiceExtList);

		/**
		 * 臨櫃服務
		 */
		List<PortalServiceExt> portalServiceExtList1 = new ArrayList<PortalServiceExt>();
		Map<String, Object> psparam2 = new HashMap<String, Object>();
		psparam2.put("isCounter", 1);
		psparam2.put("ProviderNameAndNameAsc", true);

		Map<String, Object> pcsParam = new HashMap<>();
		List<PortalCounterSubExt> tmpPortalCounterSubExtList = portalCounterSubMapperExt.selectExt(pcsParam);

		List<PortalService> portalServiceList2 = portalServiceMapper.selectForFrontByExample(psparam2);
		if (portalServiceList2 != null && portalServiceList2.size() > 0) {
			for (PortalService p : portalServiceList2) {
				PortalServiceExt ext = new PortalServiceExt();
				BeanUtils.copyProperties(p, ext);
				PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(ext.getProviderId()));
				if (portalProvider != null) {
					ext.setProviderName(portalProvider.getName());
					if(portalProvider.getType()!=null) {
						ext.setType(portalProvider.getType());
					}else {
						ext.setType(0);
					}
				}
				PortalServiceCategory cate = null;
				if(allPortalServiceCategoryList!=null&&allPortalServiceCategoryList.size()>0) {
					for(PortalServiceCategory tcate:allPortalServiceCategoryList) {
						if(ext.getCateId().compareTo(tcate.getCateId())==0) {
							cate = tcate;
						}
					}
				}
				if (cate != null) {
					ext.setCate(cate);
				}
				Boolean hasEnable = checkDpMaintainAndLevelCounter(ext, tmpPortalCounterSubExtList);
				if(hasEnable == true) {
					portalServiceExtList1.add(ext);
				}
			}
		}

		comparatorUtil.sortPortalServiceExtList(portalServiceExtList1);
		model.addAttribute("psExtList1", portalServiceExtList1);

		return "about";
	}

	private void checkDpMaintainAndLevel(PortalServiceExt ext, List<PortalResource> prExtList,
			List<PortalResourceScope> tmpPortalResourceScopeList, List<PortalServiceScope> tmpPortalServiceScopeList) {
		List<Integer> allowPrIdList = new ArrayList<Integer>();
		List<PortalServiceScope> portalServiceScopeList = new ArrayList<PortalServiceScope>();
		if (tmpPortalServiceScopeList != null && tmpPortalServiceScopeList.size() > 0) {
			for (PortalServiceScope tmppsc : tmpPortalServiceScopeList) {
				if (tmppsc.getPsId().compareTo(ext.getPsId()) == 0) {
					portalServiceScopeList.add(tmppsc);
				}
			}
		}
		if (portalServiceScopeList != null && portalServiceScopeList.size() > 0) {
			for (PortalServiceScope psc : portalServiceScopeList) {
				List<PortalResourceScope> tmpScopePortalResourceScopeList = new ArrayList<PortalResourceScope>();
				if (tmpPortalResourceScopeList != null && tmpPortalResourceScopeList.size() > 0) {
					for (PortalResourceScope tmpPrc : tmpPortalResourceScopeList) {
						if (tmpPrc.getScope().equalsIgnoreCase(psc.getScope())) {
							tmpScopePortalResourceScopeList.add(tmpPrc);
						}
					}
				}
				if (tmpScopePortalResourceScopeList != null && tmpScopePortalResourceScopeList.size() > 0) {
					if (!allowPrIdList.contains(tmpScopePortalResourceScopeList.get(0).getPrId())) {
						allowPrIdList.add(tmpScopePortalResourceScopeList.get(0).getPrId());
					}
				}
			}
		}

		int level = 3;
//		int type = 0;
		Boolean isDPMaintain = false;
		Boolean moecaCheck = true;
		if (allowPrIdList != null && allowPrIdList.size() > 0) {
			for (Integer tmpPrId : allowPrIdList) {
				PortalResource tmpPortalResource = null;
				if (prExtList != null && prExtList.size() > 0) {
					for (PortalResource tmppr : prExtList) {
						if (tmpPrId.compareTo(tmppr.getPrId()) == 0) {
							tmpPortalResource = tmppr;
						}
					}
				}
				if (tmpPortalResource != null) {
					if (tmpPortalResource.getLevel().compareTo(level) < 0) {
						level = tmpPortalResource.getLevel();
					}
					if((tmpPortalResource.getCheckStat() != null && tmpPortalResource.getCheckStat() != 0) || maintainUtils.checkInMaintain(tmpPortalResource.getPrId())) {
						isDPMaintain = true;
					}
					if( !(tmpPortalResource.getMoecaCheck()!=null && tmpPortalResource.getMoecaCheck() == 1)) {
						moecaCheck = false;
					}
				}
			}
		}

		Boolean isSPMaintain = ext.getCheckStat() != null && ext.getCheckStat() != 0;
		ext.setMaintain(isSPMaintain || isDPMaintain);
//		ext.setType(type);
		ext.setLevel(level);
		ext.setMoecaCheck(moecaCheck);
	}

	private Boolean checkDpMaintainAndLevelCounter(PortalServiceExt ext, List<PortalCounterSubExt> tmpPortalCounterSubExtList) {
		int level = 0;
		Boolean isDPMaintain = false;
		Boolean moecaCheck = true;
		Integer isOpenAgent = new Integer(0);
		Boolean hasEnable = false;

		List<PortalCounterSubExt> counterSubExtList = tmpPortalCounterSubExtList.stream()
				.filter( sub -> sub.getPsId().equals(ext.getPsId())).collect(Collectors.toList());
		ext.setCounterSubExtList(counterSubExtList);

		for(PortalCounterSubExt counterSubExt : counterSubExtList) {
			for(PortalCounterSubScopeExt subScopeExt : counterSubExt.getSubScopeExtList()) {
				for(PortalResourceExt prExt : subScopeExt.getDpList()) {
					if (prExt.getLevel().compareTo(level) > 0) {
						level = prExt.getLevel();
					}
					if((prExt.getCheckStat() != null && prExt.getCheckStat() != 0) || maintainUtils.checkInMaintain(prExt.getPrId())) {
						isDPMaintain = true;
					}
					if( !(prExt.getMoecaCheck()!=null && prExt.getMoecaCheck() == 1)) {
						moecaCheck = false;
					}
				}
			}
			if(counterSubExt.getIsOpenAgent() != 0) {
				isOpenAgent = counterSubExt.getIsOpenAgent();
			}
			
			if(counterSubExt.getEnable().equals(new Integer(1))) {
				hasEnable = true;
			}
		}

		//如果SP排程輪詢，判斷SP heartbeat error = isSPMaintain !=null && ext.getCheckStat() != 0
		//Boolean isSPMaintain = ext.getCheckStat() != null && ext.getCheckStat() != 0;
		ext.setMaintain(isDPMaintain);
		ext.setLevel(level);
		ext.setMoecaCheck(moecaCheck);
		ext.setIsOpenAgent(isOpenAgent);

		return hasEnable;
	}

	@GetMapping("/verification")
	public String getSpVerification(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return "verification";
	}

	@GetMapping("/verification/error1")
	public String getSpVerificationError1(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return "verification";
	}

	@GetMapping("/verification/error2")
	public String getSpVerificationError2(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return "verification";
	}

	@GetMapping("/verification/error3")
	public String getSpVerificationError3(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return "verification";
	}

	@GetMapping("/verification/list")
	public String getSpVerificationList(HttpServletRequest request, HttpServletResponse response, ModelMap model)
			throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
		if (sr != null) {
			System.out.println("account=" + SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
			List<PortalBoxExt> finalPortalBoxExtList = new ArrayList<PortalBoxExt>();
			List<PortalBoxExt> portalBoxExtList = portalResourceExtMapper
					.selectMyBoxByAccount(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
			List<Integer> forFinalPrIdCheck = new ArrayList<Integer>();
			if (portalBoxExtList != null && portalBoxExtList.size() > 0) {
				for (PortalBoxExt p : portalBoxExtList) {
					PortalBoxExt box = new PortalBoxExt();
					BeanUtils.copyProperties(p, box);
					PortalResourceDownload portalResourceDownload = portalResourceDownloadMapper
							.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(box.getDownloadSn()));
					PortalResource portalResource = null;
					if (portalResourceDownload != null) {
						box.setCode(portalResourceDownload.getCode());
						portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResourceDownload.getPrId()));
						if (portalResource != null) {
							PortalProvider portalProvider = portalProviderMapper
									.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResource.getProviderId()));
							if (portalProvider != null) {
								box.setPrName(portalResource.getName());
								box.setPrId(portalResource.getPrId());
								box.setCateId(portalResource.getCateId());
								box.setProviderName(portalProvider.getName());
							}
						}
					}
					/**
					 * 計算剩餘時間
					 */
					Calendar cal1 = GregorianCalendar.getInstance();
					cal1.setTime(box.getCtime());
					cal1.add(Calendar.MINUTE, 20);
					Date endDate = cal1.getTime();
					String yearStr = sdf4.format(endDate);
					int year = Integer.valueOf(yearStr) - 1911;
					String monthDayHousrMinSec = sdf5.format(endDate);
					// endTimeNote
					box.setEndTimeNote("此序號" + year + monthDayHousrMinSec + "前有效");
					Date now = new Date();
					if (now.before(endDate)) {
						box.setStat(0);
					} else {
						box.setStat(1);
					}
					System.out.println("=========================");
					System.out.println(box.getFiles());
					if (box.getFiles() != null && portalResource != null && portalResource.getPrId() != null
							&& portalResource.getPrId() != 27 && portalResource.getPrId() != 42) {
						box.setCanPreView(1);
					} else {
						box.setCanPreView(0);
					}

					/**
					 * currentTime >= (ctime+waitTime)-----> 已申請等待下載 currentTime <
					 * (ctime+waitTime)-------> 申請處理中
					 */
					long ctime = box.getCtime().getTime();
					int waitTime = box.getWaitTime() * 1000;
					long nowTime = (new Date()).getTime();
					System.out.println("ctime=" + ctime);
					System.out.println("waitTime=" + waitTime);
					System.out.println("nowTime=" + nowTime);
					/**
					 * 算多少秒過期
					 */
					long endtimelong = cal1.getTimeInMillis();
					Calendar cal2 = GregorianCalendar.getInstance();
					cal2.setTime(new Date());
					long nowtimelong = cal2.getTimeInMillis();
					if (nowtimelong >= endtimelong) {
						box.setDataTime(0);
						box.setMin(0);
					} else {
						int waittimesec = (int) ((endtimelong - nowtimelong) / 1000);
						int waitmin = waittimesec / 60;
						box.setDataTime(waittimesec);
						box.setMin(waitmin);
					}

					/**
					 * 算多少秒開始
					 */
					if (nowTime >= ctime) {
						box.setStartTime(-1);
					} else {
						int startTime = (int) ((ctime - nowTime) / 1000);
						box.setStartTime(startTime);
					}

					if ((nowTime + waitTime) >= ctime) {
						if (!forFinalPrIdCheck.contains(box.getPrId())) {
							PortalResourceDownload download = portalResourceDownloadMapper
									.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(box.getDownloadSn()));
							if (download.getStat() != 9) {
								System.out.println("download.getBatchId()===:" + download.getBatchId());
								if (box.getPrName() != null && box.getPrName().trim().length() > 0) {
									finalPortalBoxExtList.add(box);
									forFinalPrIdCheck.add(box.getPrId());
								}
							}
						}
					}
				}
			}
			model.addAttribute("portalBoxExtList", finalPortalBoxExtList);
		}
		return "verification_list";
	}

	@GetMapping("/verification/list/{boxid}")
	public String getSpVerificationListAndBoxId(@PathVariable("boxid") Integer boxid, HttpServletRequest request,
			HttpServletResponse response, ModelMap model)
			throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
		if (sr != null) {
			System.out.println("account=" + SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
			List<PortalBoxExt> finalPortalBoxExtList = new ArrayList<PortalBoxExt>();
			List<PortalBoxExt> portalBoxExtList = portalResourceExtMapper
					.selectMyBoxByAccount(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
			List<Integer> forFinalPrIdCheck = new ArrayList<Integer>();
			if (portalBoxExtList != null && portalBoxExtList.size() > 0) {
				for (PortalBoxExt p : portalBoxExtList) {
					PortalBoxExt box = new PortalBoxExt();
					BeanUtils.copyProperties(p, box);
					PortalResourceDownload portalResourceDownload = portalResourceDownloadMapper
							.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(box.getDownloadSn()));
					if (portalResourceDownload != null) {
						box.setCode(portalResourceDownload.getCode());
						PortalResource portalResource = portalResourceMapper
								.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResourceDownload.getPrId()));
						if (portalResource != null) {
							PortalProvider portalProvider = portalProviderMapper
									.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResource.getProviderId()));
							if (portalProvider != null) {
								box.setPrName(portalResource.getName());
								box.setPrId(portalResource.getPrId());
								box.setCateId(portalResource.getCateId());
								box.setProviderName(portalProvider.getName());
							}
						}
					}
					/**
					 * 計算剩餘時間
					 */
					Calendar cal1 = GregorianCalendar.getInstance();
					cal1.setTime(box.getCtime());
					cal1.add(Calendar.MINUTE, 20);
					Date endDate = cal1.getTime();
					String yearStr = sdf4.format(endDate);
					int year = Integer.valueOf(yearStr) - 1911;
					String monthDayHousrMinSec = sdf5.format(endDate);
					// endTimeNote
					box.setEndTimeNote("此序號" + year + monthDayHousrMinSec + "前有效");
					Date now = new Date();
					if (now.before(endDate)) {
						box.setStat(0);
					} else {
						box.setStat(1);
					}
					System.out.println("=========================");
					System.out.println(box.getFiles());
					if (box.getFiles() != null && box.getFiles().endsWith(".pdf")) {
						box.setCanPreView(1);
					} else {
						box.setCanPreView(0);
					}

					/**
					 * currentTime >= (ctime+waitTime)-----> 已申請等待下載 currentTime <
					 * (ctime+waitTime)-------> 申請處理中
					 */
					long ctime = box.getCtime().getTime();
					int waitTime = box.getWaitTime() * 1000;
					long nowTime = (new Date()).getTime();
					System.out.println("ctime=" + ctime);
					System.out.println("waitTime=" + waitTime);
					System.out.println("nowTime=" + nowTime);
					/**
					 * 算多少秒過期
					 */
					long endtimelong = cal1.getTimeInMillis();
					Calendar cal2 = GregorianCalendar.getInstance();
					cal2.setTime(new Date());
					long nowtimelong = cal2.getTimeInMillis();
					if (nowtimelong >= endtimelong) {
						box.setDataTime(0);
						box.setMin(0);
					} else {
						int waittimesec = (int) ((endtimelong - nowtimelong) / 1000);
						int waitmin = waittimesec / 60;
						box.setDataTime(waittimesec);
						box.setMin(waitmin);
					}

					/**
					 * 算多少秒開始
					 */
					if (nowTime >= ctime) {
						box.setStartTime(-1);
					} else {
						int startTime = (int) ((ctime - nowTime) / 1000);
						box.setStartTime(startTime);
					}

					if ((nowTime + waitTime) >= ctime) {
						if (!forFinalPrIdCheck.contains(box.getPrId())) {
							PortalResourceDownload download = portalResourceDownloadMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(box.getDownloadSn()));
							if (download.getStat() != 9) {
								System.out.println("download.getBatchId()===:" + download.getBatchId());
								if (box.getPrName() != null && box.getPrName().trim().length() > 0) {
									finalPortalBoxExtList.add(box);
									forFinalPrIdCheck.add(box.getPrId());
								}
							}
						}
					}
				}
			}
			model.addAttribute("portalBoxExtList", finalPortalBoxExtList);
			model.addAttribute("boxid", boxid);
		}
		return "verification_list";
	}

	/**
	 * MyData服務 /sp/service
	 *
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	@GetMapping("/service")
	public String getSpService(@RequestParam(name = "current", required = false) Integer psId,
			HttpServletRequest request, HttpServletResponse response, ModelMap model)
			throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
		if (session != null && sr != null) {
			List<PortalBoxExt> portalBoxExtList = portalResourceExtMapper.selectMyBoxByAccount(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
			if (portalBoxExtList != null && portalBoxExtList.size() > 0) {
				sr.setBoxcheck(true);
			} else {
				sr.setBoxcheck(false);
			}
			session.setAttribute(SessionRecord.SessionKey, sr);
		}

		Map<String, Object> prcparam1 = new HashMap<String, Object>();
		List<PortalResourceCategory> portalResourceCategoryList = portalResourceCategoryMapper
				.selectByExample(prcparam1);
		// model.addAttribute("portalResourceCategoryList", portalResourceCategoryList);
		Map<Integer, Integer> cateStatList = new HashMap<Integer, Integer>();
		for (PortalResourceCategory p : portalResourceCategoryList) {
			cateStatList.put(p.getCateId(), p.getStatus());
		}
		model.addAttribute("resourceCateStatList", cateStatList);
		Map<String,Object> nullparam = new HashMap<String,Object>();
		List<PortalResource> prExtList = portalResourceMapper.selectByExample(nullparam);

		Map<String, Object> sparam = new HashMap<String, Object>();
		List<PortalResourceScope> tmpPortalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);

		Map<String, Object> pssparam = new HashMap<String, Object>();
		List<PortalServiceScope> tmpPortalServiceScopeList = portalServiceScopeMapper.selectByExample(pssparam);

		/**
		 * 服務分類1,2,3,4,5
		 */
		List<Integer> cateIdList = new ArrayList<Integer>();
		cateIdList.add(1);
		cateIdList.add(2);
		cateIdList.add(3);
		cateIdList.add(4);
		cateIdList.add(5);
		model.addAttribute("psExtList", getServiceExtByCateList(cateIdList, prExtList, tmpPortalResourceScopeList, tmpPortalServiceScopeList));

		model.addAttribute("location", "package-service");
		return "service";
	}

	/**
	 * MyData服務 /sp/service
	 *
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	@GetMapping("/service/counter")
	public String getSpServiceCounter(@RequestParam(name = "current", required = false) Integer psId,
			HttpServletRequest request, HttpServletResponse response, ModelMap model)
			throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
		if (session != null && sr != null) {
			List<PortalBoxExt> portalBoxExtList = portalResourceExtMapper
					.selectMyBoxByAccount(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
			if (portalBoxExtList != null && portalBoxExtList.size() > 0) {
				sr.setBoxcheck(true);
			} else {
				sr.setBoxcheck(false);
			}
			session.setAttribute(SessionRecord.SessionKey, sr);
		}

		Map<String, Object> psc = new HashedMap();
		List<PortalServiceCategory> serviceCategories = portalServiceCategoryMapper.selectByExample(psc);

		Map<String, Object> pcsParam = new HashMap<>();
		List<PortalCounterSubExt> tmpPortalCounterSubExtList = portalCounterSubMapperExt.selectExt(pcsParam);
		/**
		 * 服務分類1,2,3,4,5
		 */
		List<Integer> cateIdList = new ArrayList<Integer>();
		cateIdList.add(1);
		cateIdList.add(2);
		cateIdList.add(3);
		cateIdList.add(4);
		cateIdList.add(5);

		List<PortalServiceExt> psExtList = getServiceExtByCateListForCounter(cateIdList,
				serviceCategories,
				tmpPortalCounterSubExtList);

		model.addAttribute("psExtList", psExtList);

		List<PortalServiceCategoryExt> categoryExtList = new ArrayList<>();

		for(PortalServiceCategory category : serviceCategories) {
			PortalServiceCategoryExt categoryExt = new PortalServiceCategoryExt();
			BeanUtils.copyProperties(category, categoryExt);

			Integer count = psExtList.stream()
					.filter( ps -> ps.getCate().getCateId().equals(category.getCateId()) )
					.collect(Collectors.toList()).size();

			categoryExt.setCount(count);
			categoryExtList.add(categoryExt);
		}

		model.addAttribute("categoryList", categoryExtList);

		return "v2/counter-index";
	}

	@GetMapping("/service/counter/{cate}/list")
	public String getSpServiceCounterByCate(@PathVariable(name = "cate") Integer cate,
									  HttpServletRequest request, HttpServletResponse response, ModelMap model)
			throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
		if (session != null && sr != null) {
			List<PortalBoxExt> portalBoxExtList = portalResourceExtMapper
					.selectMyBoxByAccount(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
			if (portalBoxExtList != null && portalBoxExtList.size() > 0) {
				sr.setBoxcheck(true);
			} else {
				sr.setBoxcheck(false);
			}
			session.setAttribute(SessionRecord.SessionKey, sr);
		}

		Map<String, Object> pcsParam = new HashMap<>();
		List<PortalCounterSubExt> tmpPortalCounterSubExtList = portalCounterSubMapperExt.selectExt(pcsParam);
		/**
		 * 服務分類1,2,3,4,5
		 */
		List<Integer> cateIdList = new ArrayList<Integer>();
		cateIdList.add(cate);
		model.addAttribute("psExtList", getServiceExtByCateListForCounter(cateIdList,
				new ArrayList<>(),
				tmpPortalCounterSubExtList));

		model.addAttribute("category", cate);

		return "v2/counter_cate";
	}

	/**
	 * MyData服務 /sp/service
	 *
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	@GetMapping("/service/counter/agent")
	public String getSpServiceCounterAgent(HttpServletRequest request, HttpServletResponse response, ModelMap model)
			throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
		if(sr == null) {
			return "redirect:/signin?toPage=counter-agent";
		}
		return "v2/counter-agent";
	}

	private List<PortalServiceExt> getServiceExtByCateList(List<Integer> cateIdList, List<PortalResource> prExtList,
			List<PortalResourceScope> tmpPortalResourceScopeList, List<PortalServiceScope> tmpPortalServiceScopeList) {
		
		Map<String, Object> psparam = new HashMap<String, Object>();
		if (cateIdList != null) {
			psparam.put("cateIdList", cateIdList);
		}
		psparam.put("isShow", 1);
		psparam.put("ProviderNameAndNameAsc", true);
		List<PortalService> portalServiceList = portalServiceMapper.selectForFrontByExample(psparam);
		List<PortalServiceExt> portalServiceExtList = new ArrayList<PortalServiceExt>();
		if (portalServiceList != null && portalServiceList.size() > 0) {
			for (PortalService p : portalServiceList) {
				PortalServiceExt ext = new PortalServiceExt();
				BeanUtils.copyProperties(p, ext);
				PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(ext.getProviderId()));
				if (portalProvider != null) {
					ext.setProviderName(portalProvider.getName());
				}
				checkDpMaintainAndLevel(ext, prExtList, tmpPortalResourceScopeList, tmpPortalServiceScopeList);
				List<PortalResourceExt> prList = portalResourceExtMapper.selectPrByPs(ext.getPsId());
				ext.setPrList(prList);
				portalServiceExtList.add(ext);
			}
		}
		comparatorUtil.sortPortalServiceExtList(portalServiceExtList);
		return portalServiceExtList;
	}
	
	private List<PortalServiceExt> getServiceExtByCateListForCounter(List<Integer> cateIdList,
																	 List<PortalServiceCategory> portalServiceCategories,
																	 List<PortalCounterSubExt> tmpPortalCounterSubExtList) {
		
		Map<String, Object> psparam = new HashMap<String, Object>();
		if (cateIdList != null) {
			psparam.put("cateIdList", cateIdList);
		}
		psparam.put("isCounter", 1);
		psparam.put("ProviderNameAndNameAsc", true);
		List<PortalService> portalServiceList = portalServiceMapper.selectForFrontByExample(psparam);
		List<PortalServiceExt> portalServiceExtList = new ArrayList<PortalServiceExt>();
		if (portalServiceList != null && portalServiceList.size() > 0) {
			for (PortalService p : portalServiceList) {
				PortalServiceExt ext = new PortalServiceExt();
				BeanUtils.copyProperties(p, ext);
				PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(ext.getProviderId()));
				if (portalProvider != null) {
					ext.setProviderName(portalProvider.getName());
				}
				Boolean hasEnable = checkDpMaintainAndLevelCounter(ext, tmpPortalCounterSubExtList);
				List<PortalResourceExt> prList = portalResourceExtMapper.selectPrByPsCounter(ext.getPsId());
				ext.setPrList(prList);
				PortalServiceCategory portalServiceCategory = portalServiceCategories.stream()
						.filter( cate -> cate.getCateId() == p.getCateId())
						.findFirst().orElse(null);
				ext.setCate(portalServiceCategory);
				if(hasEnable == true) {
					portalServiceExtList.add(ext);
				}
			}
		}
		comparatorUtil.sortPortalServiceExtList(portalServiceExtList);
		return portalServiceExtList;
	}

	/**
	 * MyData服務端驗證
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@GetMapping("/verifyCertificate")
	public void getSpVerifyCertificate(HttpServletRequest request, HttpServletResponse response, ModelMap model)
			throws IOException {
		System.out.println("---@GetMapping---");
		response.setContentType("text/plain;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		X509Certificate[] certs = (X509Certificate[]) request.getAttribute(ATTR_CER);
		if (certs != null) {
			int count = certs.length;
			out.println("共檢驗到[" + count + "]個客戶端證書");
			for (int i = 0; i < count; i++) {
				out.println("客戶端證書 [" + (++i) + "]： ");
				out.println("校驗結果：" + verifyCertificate(certs[--i]));
				out.println("證書詳細：\r" + certs[i].toString());
			}
		} else {
			if (SCHEME_HTTPS.equalsIgnoreCase(request.getScheme())) {
				out.println("這是一個HTTPS請求，但是沒有可用的客戶端證書");
			} else {
				out.println("這不是一個HTTPS請求，因此無法獲得客戶端證書列表");
			}
		}
	}

	/**
	 * MyData服務 /sp/service
	 *
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/verifyCertificate")
	public void postSpVerifyCertificate(HttpServletRequest request, HttpServletResponse response, ModelMap model)
			throws IOException {
		System.out.println("---@PostMapping---");
		response.setContentType("text/plain;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		X509Certificate[] certs = (X509Certificate[]) request.getAttribute(ATTR_CER);
		if (certs != null) {
			int count = certs.length;
			out.println("共檢驗到[" + count + "]個客戶端證書");
			for (int i = 0; i < count; i++) {
				out.println("客戶端證書 [" + (++i) + "]： ");
				out.println("校驗結果：" + verifyCertificate(certs[--i]));
				out.println("證書詳細：\r" + certs[i].toString());
			}
		} else {
			if (SCHEME_HTTPS.equalsIgnoreCase(request.getScheme())) {
				out.println("這是一個HTTPS請求，但是沒有可用的客戶端證書");
			} else {
				out.println("這不是一個HTTPS請求，因此無法獲得客戶端證書列表");
			}
		}
	}

	/**
	 * 會員專區 /sp/member
	 *
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws UnsupportedEncodingException
	 */
	@GetMapping("/member")
	public String getMember(HttpServletRequest request, HttpServletResponse response, ModelMap model)
			throws InvocationTargetException, IllegalAccessException, BadPaddingException, IllegalBlockSizeException,
			UnsupportedEncodingException, JsonProcessingException {
		request.getSession().setMaxInactiveInterval(120);
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
		List<PortalBoxExt> portalBoxExtList = null;
		if (sr.getMember() != null) {
			Member member = memberMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(sr.getMember().getId()));
			if(member == null) {
				return "redirect:/signin";
			}
			Member maskMember = new Member();
			BeanUtils.copyProperties(member, maskMember);
			sr.setMaskMember(MaskUtil.maskSensitiveInformation(maskMember));
			sr.setMember(new SessionMember(member));
			portalBoxExtList = portalResourceExtMapper.selectMyBoxByAccount(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
			if (portalBoxExtList != null && portalBoxExtList.size() > 0) {
				sr.setBoxcheck(true);
			} else {
				sr.setBoxcheck(false);
			}
			session.setAttribute(SessionRecord.SessionKey, sr);
			model.addAttribute("member", SessionMember.getSessionMemberToMember(sr.getMember()));
            Map<String, Object> mpParam = new HashMap<>();
            mpParam.put("memberId", ValidatorHelper.limitNumber(member.getId()));
            List<MemberPrivacy> mpList = memberPrivacyMapper.selectByExample(mpParam);
            if(StringUtils.isEmpty(member.getName())||StringUtils.isEmpty(member.getInformMethod())||((member.getIsOld()==null||member.getIsOld()==false)&&(member.getIsDoubleVerify()==null||member.getIsDoubleVerify()== false))||mpList.size()==0) {
				return "login-v2";
			}
		}
		
		List<PortalResourceExt> tmpPrExtList = portalResourceExtMapper.selectPortalResourceWithCateName();

		List<PortalResourceExt> prExtList = new ArrayList<PortalResourceExt>();
		if(tmpPrExtList!=null&&tmpPrExtList.size()>0) {
			for (PortalResourceExt tpr : tmpPrExtList) {
				if (tpr != null) {
					Integer ulogCount = 0;
					if (sr != null && sr.getMember().getAccount() != null) {
						ulogCount = ulogApiMapperExt.countLogByAccountAndResourceIdAndAuditEventForResource(
							SessionMember.getSessionMemberToMember(sr.getMember()).getAccount(),
							ValidatorHelper.removeSpecialCharacters(tpr.getResourceId()));
					}
					if (ulogCount != null && ulogCount > 0) {
						prExtList.add(tpr);
					}
				}
			}
		}

		model.addAttribute("prExtList", prExtList);

		if (sr != null) {
			System.out.println("account=" + SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
			List<PortalBoxExt> finalPortalBoxExtList = new ArrayList<PortalBoxExt>();
			List<Integer> forFinalPrIdCheck = new ArrayList<Integer>();
			if (portalBoxExtList != null && portalBoxExtList.size() > 0) {
				for (PortalBoxExt p : portalBoxExtList) {
					PortalBoxExt box = new PortalBoxExt();
					BeanUtils.copyProperties(p, box);
					PortalResourceDownload portalResourceDownload = portalResourceDownloadMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(box.getDownloadSn()));
					PortalResource portalResource = null;
					if (portalResourceDownload != null) {
						box.setCode(portalResourceDownload.getCode());
						if(allPortalResourceList!=null&&allPortalResourceList.size()>0) {
							for(PortalResource tpr:allPortalResourceList) {
								if(tpr.getPrId().compareTo(portalResourceDownload.getPrId())==0) {
									portalResource = tpr;
								}
							}
						}
						if (portalResource != null) {
							PortalProvider portalProvider = null;
							if(allPortalProviderList!=null&&allPortalProviderList.size()>0) {
								for(PortalProvider tpp:allPortalProviderList) {
									if(tpp.getProviderId().compareTo(portalResource.getProviderId())==0) {
										portalProvider = tpp;
									}
								}
							}
							if (portalProvider != null) {
								box.setPrName(portalResource.getName());
								box.setPrId(portalResource.getPrId());
								box.setCateId(portalResource.getCateId());
								box.setProviderName(portalProvider.getName());
							}
						}
					}
					/**
					 * 計算剩餘時間
					 */
					Calendar cal1 = GregorianCalendar.getInstance();
					cal1.setTime(box.getCtime());
					cal1.add(Calendar.MINUTE, 20);
					Date endDate = cal1.getTime();
					String yearStr = sdf4.format(endDate);
					int year = Integer.valueOf(yearStr) - 1911;
					String monthDayHousrMinSec = sdf5.format(endDate);
					// endTimeNote
					box.setEndTimeNote("此序號" + year + monthDayHousrMinSec + "前有效");
					Date now = new Date();
					if (now.before(endDate)) {
						box.setStat(0);
					} else {
						box.setStat(1);
					}
					System.out.println("=========================");
					System.out.println(box.getFiles());
					if (box.getFiles() != null && portalResource != null && portalResource.getPrId() != null
							&& portalResource.getPrId() != 27 && portalResource.getPrId() != 42) {
						box.setCanPreView(1);
					} else {
						box.setCanPreView(0);
					}

					/**
					 * currentTime >= (ctime+waitTime)-----> 已申請等待下載 currentTime <
					 * (ctime+waitTime)-------> 申請處理中
					 */
					long ctime = box.getCtime().getTime();
					int waitTime = box.getWaitTime() * 1000;
					long nowTime = (new Date()).getTime();
					System.out.println("ctime=" + ctime);
					System.out.println("waitTime=" + waitTime);
					System.out.println("nowTime=" + nowTime);
					/**
					 * 算多少秒過期
					 */
					long endtimelong = cal1.getTimeInMillis();
					Calendar cal2 = GregorianCalendar.getInstance();
					cal2.setTime(new Date());
					long nowtimelong = cal2.getTimeInMillis();
					if (nowtimelong >= endtimelong) {
						box.setDataTime(0);
						box.setMin(0);
					} else {
						int waittimesec = (int) ((endtimelong - nowtimelong) / 1000);
						int waitmin = waittimesec / 60;
						box.setDataTime(waittimesec);
						box.setMin(waitmin);
					}

					/**
					 * 算多少秒開始
					 */
					if (nowTime >= ctime) {
						box.setStartTime(-1);
					} else {
						int startTime = (int) ((ctime - nowTime) / 1000);
						box.setStartTime(startTime);
					}

					if ((nowTime + waitTime) >= ctime) {
						if (!forFinalPrIdCheck.contains(box.getPrId())) {
							PortalResourceDownload download = portalResourceDownloadMapper
									.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(box.getDownloadSn()));
							if (download.getStat() != 9) {
								System.out.println("download.getBatchId()===:" + download.getBatchId());
								if (box.getPrName() != null && box.getPrName().trim().length() > 0) {
									finalPortalBoxExtList.add(box);
									forFinalPrIdCheck.add(box.getPrId());
								}
							}
						}
					}
				}
			}
			model.addAttribute("portalBoxExtList", finalPortalBoxExtList);
			
			List<PortalBoxExt> portalBoxExtList1 = null;
			List<PortalBoxExt> finalPortalBoxExtList1 = new ArrayList<PortalBoxExt>();
			List<Integer> forFinalPsIdCheck = new ArrayList<Integer>();

			Map<String, Object> prExtParam = new HashMap<>();
			Member member = SessionMember.getSessionMemberToMember(sr.getMember());
			prExtParam.put("account", ValidatorHelper.removeSpecialCharacters(member.getAccount()));
			prExtParam.put("uid", ValidatorHelper.removeSpecialCharacters(sr.getMember().getUid()));
			prExtParam.put("birthdate", ValidatorHelper.limitDate(member.getBirthdate()));

			logger.info("{}", new ObjectMapper().writeValueAsString(prExtParam));

			portalBoxExtList1 = portalResourceExtMapper.selectMyBoxForCounter(prExtParam);

			logger.info("portalBoxExtList1 >> {}", portalBoxExtList1.size());
			if (portalBoxExtList1 != null && portalBoxExtList1.size() > 0) {
				for (PortalBoxExt p : portalBoxExtList1) {
					PortalBoxExt box = new PortalBoxExt();
					BeanUtils.copyProperties(p, box);
					PortalServiceDownload portalServiceDownload = portalServiceDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(box.getPsdId()));
					PortalServiceDownloadSub downloadSub = portalServiceDownloadSubMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalServiceDownload.getId()));
					PortalService portalService = null;
					if (portalServiceDownload != null) {
						box.setCode(portalServiceDownload.getCode());
						box.setApplyTime(portalServiceDownload.getCtime());
						portalService = portalServiceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalServiceDownload.getPsId()));
						if (portalService != null) {

							PortalCounterSub counterSub = portalCounterSubMapperExt.selectByPrimaryKey(ValidatorHelper.limitNumber(downloadSub.getPcsId()));
							box.setIsOpenAgent(counterSub.getIsOpenAgent());
							PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalService.getProviderId()));

							if (portalProvider != null) {
								box.setPrName(portalService.getName());
								box.setPsId(portalService.getPsId());
								box.setCateId(portalService.getCateId());
								box.setProviderName(portalProvider.getName());
							}
						}
					}
					/**
					 * 計算剩餘時間
					 */
					Calendar cal1 = GregorianCalendar.getInstance();
					cal1.setTime(box.getCtime());
					cal1.add(Calendar.MINUTE, 20);
					Date endDate = cal1.getTime();
					String yearStr = sdf4.format(endDate);
					int year = Integer.valueOf(yearStr) - 1911;
					String monthDayHousrMinSec = sdf5.format(endDate);
					// endTimeNote
					box.setEndTimeNote("此序號" + year + monthDayHousrMinSec + "前有效");
					Date now = new Date();
					/**
					 * box.stat 0 正常 1 逾期
					 */
					if (now.before(endDate)) {
						if(box.getStat()>0) {
							box.setStat(1);
						}else {
							box.setStat(0);
						}
					} else {
						box.setStat(1);
					}
					box.setCanPreView(0);

					/**
					 * currentTime >= (ctime+waitTime)-----> 已申請等待下載 currentTime <
					 * (ctime+waitTime)-------> 申請處理中
					 */
					long ctime = box.getCtime().getTime();
					int waitTime = box.getWaitTime() * 1000;
					long nowTime = (new Date()).getTime();
					
					/**
					 * 算多少秒過期
					 */
					long endtimelong = cal1.getTimeInMillis();
					Calendar cal2 = GregorianCalendar.getInstance();
					cal2.setTime(new Date());
					long nowtimelong = cal2.getTimeInMillis();
					if (nowtimelong >= endtimelong) {
						box.setDataTime(0);
						box.setMin(0);
					} else {
						int waittimesec = (int) ((endtimelong - nowtimelong) / 1000);
						int waitmin = waittimesec / 60;
						box.setDataTime(waittimesec);
						box.setMin(waitmin);
					}

					/**
					 * 算多少秒開始
					 */
					if (nowTime >= ctime) {
						box.setStartTime(-1);
					} else {
						int startTime = (int) ((ctime - nowTime) / 1000);
						box.setStartTime(startTime);
					}

					/**
					 * 確認是否有代辦人
					 */
					if(box.getAgentUid()!=null&&box.getAgentBirthdate()!=null) {
			            Map<String,Object> mParam = new HashMap<>();
			            mParam.put("uid", box.getAgentUid().toUpperCase());
			            mParam.put("birthdate", box.getAgentBirthdate());
			            mParam.put("statIsNullorZero", true);
			            List<Member> mList = memberMapper.selectByExample(mParam);
			            if(mList!=null&&mList.size()>0) {
			            		box.setAgentName(MaskUtil.maskNameByO(mList.get(0).getName()));
			            }
					}
					
					/**
					 * 相關box
					 */
					List<PortalBoxExt> tmpPortalBoxExtList = portalResourceExtMapper.selectMyBoxForCounterByPsdId(box.getPsdId());
					List<PortalBoxExt> tmpfinalPortalBoxExtList = new ArrayList<PortalBoxExt>();
					List<Integer> tmpforFinalPrIdCheck = new ArrayList<Integer>();
					if(tmpPortalBoxExtList!=null&&tmpPortalBoxExtList.size()>0) {
						for(PortalBoxExt tp:tmpPortalBoxExtList) {
							PortalBoxExt tmpbox = new PortalBoxExt();
							BeanUtils.copyProperties(tp, tmpbox);
							PortalResourceDownload portalResourceDownload = portalResourceDownloadMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(tmpbox.getDownloadSn()));
							PortalResource portalResource = null;
							if (portalResourceDownload != null) {
								tmpbox.setCode(portalResourceDownload.getCode());
								if(allPortalResourceList!=null&&allPortalResourceList.size()>0) {
									for(PortalResource tpr:allPortalResourceList) {
										if(tpr.getPrId().compareTo(portalResourceDownload.getPrId())==0) {
											portalResource = tpr;
										}
									}
								}
								if (portalResource != null) {
									PortalProvider portalProvider = null;
									if(allPortalProviderList!=null&&allPortalProviderList.size()>0) {
										for(PortalProvider tpp:allPortalProviderList) {
											if(tpp.getProviderId().compareTo(portalResource.getProviderId())==0) {
												portalProvider = tpp;
											}
										}
									}
									if (portalProvider != null) {
										tmpbox.setPrName(portalResource.getName());
										tmpbox.setPrId(portalResource.getPrId());
										tmpbox.setCateId(portalResource.getCateId());
										tmpbox.setProviderName(portalProvider.getName());
									}
								}
							}
							/**
							 * 計算剩餘時間
							 */
							Calendar cal3 = GregorianCalendar.getInstance();
							cal3.setTime(tmpbox.getCtime());
							cal3.add(Calendar.MINUTE, 20);
							Date tmpendDate = cal3.getTime();
							String tmpyearStr = sdf4.format(tmpendDate);
							int tmpyear = Integer.valueOf(tmpyearStr) - 1911;
							String tmpmonthDayHousrMinSec = sdf5.format(tmpendDate);
							// endTimeNote
							tmpbox.setEndTimeNote("此序號" + tmpyear + tmpmonthDayHousrMinSec + "前有效");
							Date tmpnow = new Date();
							if (tmpnow.before(tmpendDate)) {
								tmpbox.setStat(0);
							} else {
								tmpbox.setStat(1);
							}

							if (tmpbox.getFiles() != null && portalResource != null && portalResource.getPrId() != null
									&& portalResource.getPrId() != 27 && portalResource.getPrId() != 42) {
								tmpbox.setCanPreView(1);
							} else {
								tmpbox.setCanPreView(0);
							}

							/**
							 * currentTime >= (ctime+waitTime)-----> 已申請等待下載 currentTime <
							 * (ctime+waitTime)-------> 申請處理中
							 */
							long tmpctime = tmpbox.getCtime().getTime();
							int tmpwaitTime = tmpbox.getWaitTime() * 1000;
							long tmpnowTime = (new Date()).getTime();

							/**
							 * 算多少秒過期
							 */
							long tmpendtimelong = cal3.getTimeInMillis();
							Calendar cal4 = GregorianCalendar.getInstance();
							cal4.setTime(new Date());
							long tmpnowtimelong = cal4.getTimeInMillis();
							if (tmpnowtimelong >= tmpendtimelong) {
								tmpbox.setDataTime(0);
								tmpbox.setMin(0);
							} else {
								int waittimesec = (int) ((tmpendtimelong - tmpnowtimelong) / 1000);
								int waitmin = waittimesec / 60;
								tmpbox.setDataTime(waittimesec);
								tmpbox.setMin(waitmin);
							}

							/**
							 * 算多少秒開始
							 */
							if (tmpnowTime >= tmpctime) {
								tmpbox.setStartTime(-1);
							} else {
								int startTime = (int) ((tmpctime - tmpnowTime) / 1000);
								tmpbox.setStartTime(startTime);
							}

							if ((tmpnowTime + tmpwaitTime) >= tmpctime) {
								if (!tmpforFinalPrIdCheck.contains(tmpbox.getPrId())) {
									PortalResourceDownload download = portalResourceDownloadMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(tmpbox.getDownloadSn()));
									if (download.getStat() != 9) {
										System.out.println("download.getBatchId()===:" + download.getBatchId());
										if (tmpbox.getPrName() != null && tmpbox.getPrName().trim().length() > 0) {
											tmpfinalPortalBoxExtList.add(tmpbox);
											tmpforFinalPrIdCheck.add(tmpbox.getPrId());
										}
									}
								}
							}
						}
						
						box.setBoxList(tmpfinalPortalBoxExtList);
					}
					
					/**
					 * service box 是否顯示 
					 */
					if ((nowTime + waitTime) >= ctime) {
						if (!forFinalPsIdCheck.contains(box.getPsId())) {
							finalPortalBoxExtList1.add(box);
						}
					}
				}
			}
			logger.info("finalPortalBoxExtList1 >> {}", new ObjectMapper().writeValueAsString(finalPortalBoxExtList1));
			model.addAttribute("portalBoxExtList1", finalPortalBoxExtList1);
		}
		
		// return "member";
		return "member1";

	}

	/**
	 * 會員專區 /sp/member/{boxId}
	 *
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws UnsupportedEncodingException
	 */
	@GetMapping("/member/{boxid}")
	public String getMemberByBoxId(@PathVariable("boxid") Integer boxid, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) throws InvocationTargetException, IllegalAccessException,
			BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		request.getSession().setMaxInactiveInterval(120);
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord) session.getAttribute(SessionRecord.SessionKey);
		List<PortalBoxExt> portalBoxExtList = null;
		if (sr.getMember() != null) {
			Member member = memberMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(sr.getMember().getId()));
			Member maskMember = new Member();
			BeanUtils.copyProperties(member, maskMember);
			sr.setMaskMember(MaskUtil.maskSensitiveInformation(maskMember));
			sr.setMember(new SessionMember(member));
			portalBoxExtList = portalResourceExtMapper.selectMyBoxByAccount(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
			if (portalBoxExtList != null && portalBoxExtList.size() > 0) {
				sr.setBoxcheck(true);
			} else {
				sr.setBoxcheck(false);
			}
			session.setAttribute(SessionRecord.SessionKey, sr);
			model.addAttribute("member", SessionMember.getSessionMemberToMember(sr.getMember()));
			if(StringUtils.isEmpty(member.getName())||StringUtils.isEmpty(member.getInformMethod())) {
				return "login-v2";
			}
		}

		List<PortalResourceExt> tmpPrExtList = portalResourceExtMapper.selectPortalResourceWithCateName();

		List<PortalResourceExt> prExtList = new ArrayList<PortalResourceExt>();
		if(tmpPrExtList!=null&&tmpPrExtList.size()>0) {
			for (PortalResourceExt tpr : tmpPrExtList) {
				if (tpr != null) {
					Integer ulogCount = 0;
					if (sr != null && sr.getMember().getAccount() != null) {
						ulogCount = ulogApiMapperExt.countLogByAccountAndResourceIdAndAuditEventForResource(
							SessionMember.getSessionMemberToMember(sr.getMember()).getAccount(),
							ValidatorHelper.removeSpecialCharacters(tpr.getResourceId()));
					}
					if (ulogCount != null && ulogCount > 0) {
						prExtList.add(tpr);
					}
				}
			}
		}

		model.addAttribute("prExtList", prExtList);

		if (sr != null) {
			System.out.println("account=" + SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
			List<PortalBoxExt> finalPortalBoxExtList = new ArrayList<PortalBoxExt>();
			List<Integer> forFinalPrIdCheck = new ArrayList<Integer>();
			if (portalBoxExtList != null && portalBoxExtList.size() > 0) {
				for (PortalBoxExt p : portalBoxExtList) {
					PortalBoxExt box = new PortalBoxExt();
					BeanUtils.copyProperties(p, box);
					PortalResourceDownload portalResourceDownload = portalResourceDownloadMapper
							.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(box.getDownloadSn()));
					PortalResource portalResource = null;
					if (portalResourceDownload != null) {
						box.setCode(portalResourceDownload.getCode());
						if(allPortalResourceList!=null&&allPortalResourceList.size()>0) {
							for(PortalResource tpr:allPortalResourceList) {
								if(tpr.getPrId().compareTo(portalResourceDownload.getPrId())==0) {
									portalResource = tpr;
								}
							}
						}
						if (portalResource != null) {
							PortalProvider portalProvider = null;
							if(allPortalProviderList!=null&&allPortalProviderList.size()>0) {
								for(PortalProvider tpp:allPortalProviderList) {
									if(tpp.getProviderId().compareTo(portalResource.getProviderId())==0) {
										portalProvider = tpp;
									}
								}
							}
							if (portalProvider != null) {
								box.setPrName(portalResource.getName());
								box.setPrId(portalResource.getPrId());
								box.setCateId(portalResource.getCateId());
								box.setProviderName(portalProvider.getName());
							}
						}
					}
					/**
					 * 計算剩餘時間
					 */
					Calendar cal1 = GregorianCalendar.getInstance();
					cal1.setTime(box.getCtime());
					cal1.add(Calendar.MINUTE, 20);
					Date endDate = cal1.getTime();
					String yearStr = sdf4.format(endDate);
					int year = Integer.valueOf(yearStr) - 1911;
					String monthDayHousrMinSec = sdf5.format(endDate);
					// endTimeNote
					box.setEndTimeNote("此序號" + year + monthDayHousrMinSec + "前有效");
					Date now = new Date();
					if (now.before(endDate)) {
						box.setStat(0);
					} else {
						box.setStat(1);
					}
					System.out.println("=========================");
					System.out.println(box.getFiles());
					if (box.getFiles() != null && portalResource != null && portalResource.getPrId() != null
							&& portalResource.getPrId() != 27 && portalResource.getPrId() != 42) {
						box.setCanPreView(1);
					} else {
						box.setCanPreView(0);
					}

					/**
					 * currentTime >= (ctime+waitTime)-----> 已申請等待下載 currentTime <
					 * (ctime+waitTime)-------> 申請處理中
					 */
					long ctime = box.getCtime().getTime();
					int waitTime = box.getWaitTime() * 1000;
					long nowTime = (new Date()).getTime();
					System.out.println("ctime=" + ctime);
					System.out.println("waitTime=" + waitTime);
					System.out.println("nowTime=" + nowTime);
					/**
					 * 算多少秒過期
					 */
					long endtimelong = cal1.getTimeInMillis();
					Calendar cal2 = GregorianCalendar.getInstance();
					cal2.setTime(new Date());
					long nowtimelong = cal2.getTimeInMillis();
					if (nowtimelong >= endtimelong) {
						box.setDataTime(0);
						box.setMin(0);
					} else {
						int waittimesec = (int) ((endtimelong - nowtimelong) / 1000);
						int waitmin = waittimesec / 60;
						box.setDataTime(waittimesec);
						box.setMin(waitmin);
					}

					/**
					 * 算多少秒開始
					 */
					if (nowTime >= ctime) {
						box.setStartTime(-1);
					} else {
						int startTime = (int) ((ctime - nowTime) / 1000);
						box.setStartTime(startTime);
					}

					if ((nowTime + waitTime) >= ctime) {
						if (!forFinalPrIdCheck.contains(box.getPrId())) {
							PortalResourceDownload download = portalResourceDownloadMapper
									.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(box.getDownloadSn()));
							if (download.getStat() != 9) {
								System.out.println("download.getBatchId()===:" + download.getBatchId());
								if (box.getPrName() != null && box.getPrName().trim().length() > 0) {
									finalPortalBoxExtList.add(box);
									forFinalPrIdCheck.add(box.getPrId());
								}
							}
						}
					}
				}
			}
			model.addAttribute("portalBoxExtList", finalPortalBoxExtList);
			
			List<PortalBoxExt> portalBoxExtList1 = null;
			List<PortalBoxExt> finalPortalBoxExtList1 = new ArrayList<PortalBoxExt>();
			List<Integer> forFinalPsIdCheck = new ArrayList<Integer>();
			portalBoxExtList1 = portalResourceExtMapper.selectMyBoxForCounterByAccount(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount());
			if (portalBoxExtList1 != null && portalBoxExtList1.size() > 0) {
				for (PortalBoxExt p : portalBoxExtList1) {
					PortalBoxExt box = new PortalBoxExt();
					BeanUtils.copyProperties(p, box);
					PortalServiceDownload portalServiceDownload = portalServiceDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(box.getPsdId()));
					PortalService portalService = null;
					if (portalServiceDownload != null) {
						box.setCode(portalServiceDownload.getCode());
						box.setApplyTime(portalServiceDownload.getCtime());
						portalService = portalServiceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalServiceDownload.getPsId()));
						if (portalService != null) {
							box.setIsOpenAgent(portalService.getIsOpenAgent());
							PortalProvider portalProvider = null;
							if(allPortalProviderList!=null&&allPortalProviderList.size()>0) {
								for(PortalProvider tpp:allPortalProviderList) {
									if(tpp.getProviderId().compareTo(portalService.getProviderId())==0) {
										portalProvider = tpp;
									}
								}
							}
							if (portalProvider != null) {
								box.setPrName(portalService.getName());
								box.setPsId(portalService.getPsId());
								box.setCateId(portalService.getCateId());
								box.setProviderName(portalProvider.getName());
							}
						}
					}
					/**
					 * 計算剩餘時間
					 */
					Calendar cal1 = GregorianCalendar.getInstance();
					cal1.setTime(box.getCtime());
					cal1.add(Calendar.MINUTE, 20);
					Date endDate = cal1.getTime();
					String yearStr = sdf4.format(endDate);
					int year = Integer.valueOf(yearStr) - 1911;
					String monthDayHousrMinSec = sdf5.format(endDate);
					// endTimeNote
					box.setEndTimeNote("此序號" + year + monthDayHousrMinSec + "前有效");
					Date now = new Date();
					if (now.before(endDate)) {
						if(box.getStat()>0) {
							box.setStat(1);
						}else {
							box.setStat(0);
						}
					} else {
						box.setStat(1);
					}
					box.setCanPreView(0);

					/**
					 * currentTime >= (ctime+waitTime)-----> 已申請等待下載 currentTime <
					 * (ctime+waitTime)-------> 申請處理中
					 */
					long ctime = box.getCtime().getTime();
					int waitTime = box.getWaitTime() * 1000;
					long nowTime = (new Date()).getTime();
					
					/**
					 * 算多少秒過期
					 */
					long endtimelong = cal1.getTimeInMillis();
					Calendar cal2 = GregorianCalendar.getInstance();
					cal2.setTime(new Date());
					long nowtimelong = cal2.getTimeInMillis();
					if (nowtimelong >= endtimelong) {
						box.setDataTime(0);
						box.setMin(0);
					} else {
						int waittimesec = (int) ((endtimelong - nowtimelong) / 1000);
						int waitmin = waittimesec / 60;
						box.setDataTime(waittimesec);
						box.setMin(waitmin);
					}

					/**
					 * 算多少秒開始
					 */
					if (nowTime >= ctime) {
						box.setStartTime(-1);
					} else {
						int startTime = (int) ((ctime - nowTime) / 1000);
						box.setStartTime(startTime);
					}

					/**
					 * 確認是否有代辦人
					 */
					if(box.getAgentUid()!=null&&box.getAgentBirthdate()!=null) {
			            Map<String,Object> mParam = new HashMap<>();
			            mParam.put("uid", box.getAgentUid().toUpperCase());
			            mParam.put("birthdate", box.getAgentBirthdate());
			            mParam.put("statIsNullorZero", true);
			            List<Member> mList = memberMapper.selectByExample(mParam);
			            if(mList!=null&&mList.size()>0) {
			            		box.setAgentName(MaskUtil.maskNameByO(mList.get(0).getName()));
			            }
					}
					
					/**
					 * 相關box
					 */
					List<PortalBoxExt> tmpPortalBoxExtList = portalResourceExtMapper.selectMyBoxForCounterByPsdId(box.getPsdId());
					List<PortalBoxExt> tmpfinalPortalBoxExtList = new ArrayList<PortalBoxExt>();
					List<Integer> tmpforFinalPrIdCheck = new ArrayList<Integer>();
					if(tmpPortalBoxExtList!=null&&tmpPortalBoxExtList.size()>0) {
						for(PortalBoxExt tp:tmpPortalBoxExtList) {
							PortalBoxExt tmpbox = new PortalBoxExt();
							BeanUtils.copyProperties(tp, tmpbox);
							PortalResourceDownload portalResourceDownload = portalResourceDownloadMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(tmpbox.getDownloadSn()));
							PortalResource portalResource = null;
							if (portalResourceDownload != null) {
								tmpbox.setCode(portalResourceDownload.getCode());
								if(allPortalResourceList!=null&&allPortalResourceList.size()>0) {
									for(PortalResource tpr:allPortalResourceList) {
										if(tpr.getPrId().compareTo(portalResourceDownload.getPrId())==0) {
											portalResource = tpr;
										}
									}
								}
								if (portalResource != null) {
									PortalProvider portalProvider = null;
									if(allPortalProviderList!=null&&allPortalProviderList.size()>0) {
										for(PortalProvider tpp:allPortalProviderList) {
											if(tpp.getProviderId().compareTo(portalResource.getProviderId())==0) {
												portalProvider = tpp;
											}
										}
									}
									if (portalProvider != null) {
										tmpbox.setPrName(portalResource.getName());
										tmpbox.setPrId(portalResource.getPrId());
										tmpbox.setCateId(portalResource.getCateId());
										tmpbox.setProviderName(portalProvider.getName());
									}
								}
							}
							/**
							 * 計算剩餘時間
							 */
							Calendar cal3 = GregorianCalendar.getInstance();
							cal3.setTime(tmpbox.getCtime());
							cal3.add(Calendar.MINUTE, 20);
							Date tmpendDate = cal3.getTime();
							String tmpyearStr = sdf4.format(tmpendDate);
							int tmpyear = Integer.valueOf(tmpyearStr) - 1911;
							String tmpmonthDayHousrMinSec = sdf5.format(tmpendDate);
							// endTimeNote
							tmpbox.setEndTimeNote("此序號" + tmpyear + tmpmonthDayHousrMinSec + "前有效");
							Date tmpnow = new Date();
							if (tmpnow.before(tmpendDate)) {
								tmpbox.setStat(0);
							} else {
								tmpbox.setStat(1);
							}

							if (tmpbox.getFiles() != null && portalResource != null && portalResource.getPrId() != null
									&& portalResource.getPrId() != 27 && portalResource.getPrId() != 42) {
								tmpbox.setCanPreView(1);
							} else {
								tmpbox.setCanPreView(0);
							}

							/**
							 * currentTime >= (ctime+waitTime)-----> 已申請等待下載 currentTime <
							 * (ctime+waitTime)-------> 申請處理中
							 */
							long tmpctime = tmpbox.getCtime().getTime();
							int tmpwaitTime = tmpbox.getWaitTime() * 1000;
							long tmpnowTime = (new Date()).getTime();

							/**
							 * 算多少秒過期
							 */
							long tmpendtimelong = cal3.getTimeInMillis();
							Calendar cal4 = GregorianCalendar.getInstance();
							cal4.setTime(new Date());
							long tmpnowtimelong = cal4.getTimeInMillis();
							if (tmpnowtimelong >= tmpendtimelong) {
								tmpbox.setDataTime(0);
								tmpbox.setMin(0);
							} else {
								int waittimesec = (int) ((tmpendtimelong - tmpnowtimelong) / 1000);
								int waitmin = waittimesec / 60;
								tmpbox.setDataTime(waittimesec);
								tmpbox.setMin(waitmin);
							}

							/**
							 * 算多少秒開始
							 */
							if (tmpnowTime >= tmpctime) {
								tmpbox.setStartTime(-1);
							} else {
								int startTime = (int) ((tmpctime - tmpnowTime) / 1000);
								tmpbox.setStartTime(startTime);
							}

							if ((tmpnowTime + tmpwaitTime) >= tmpctime) {
								if (!tmpforFinalPrIdCheck.contains(tmpbox.getPrId())) {
									PortalResourceDownload download = portalResourceDownloadMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(tmpbox.getDownloadSn()));
									if (download.getStat() != 9) {
										System.out.println("download.getBatchId()===:" + download.getBatchId());
										if (tmpbox.getPrName() != null && tmpbox.getPrName().trim().length() > 0) {
											tmpfinalPortalBoxExtList.add(tmpbox);
											tmpforFinalPrIdCheck.add(tmpbox.getPrId());
										}
									}
								}
							}
						}
						
						box.setBoxList(tmpfinalPortalBoxExtList);
					}
					
					/**
					 * service box 是否顯示 
					 */
					if ((nowTime + waitTime) >= ctime) {
						if (!forFinalPsIdCheck.contains(box.getPsId())) {
							finalPortalBoxExtList1.add(box);
						}
					}
				}
			}
			model.addAttribute("portalBoxExtList1", finalPortalBoxExtList1);
		}
		model.addAttribute("boxid", boxid);
		return "member";
	}

	/**
	 * 校驗證書是否過期
	 *
	 * @param certificate
	 * @return
	 */
	private boolean verifyCertificate(X509Certificate certificate) {
		boolean valid = true;
		try {
			certificate.checkValidity();
		} catch (Exception e) {
			e.printStackTrace();
			valid = false;
		}
		return valid;
	}

	public class TableHeader extends PdfPageEventHelper {
		String header;
		PdfTemplate total;

		public void setHeader(String header) {
			this.header = header;
		}

		public void onOpenDocument(PdfWriter writer, Document document) {
			total = writer.getDirectContent().createTemplate(30, 16);
		}

		public void onEndPage(PdfWriter writer, Document document) {
			PdfPTable table = new PdfPTable(3);
			try {
				BaseFont bfChinese = BaseFont.createFont("kaiu.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
				Font chineseFont = new Font(bfChinese, 12, Font.NORMAL);
				table.setWidths(new int[] { 24, 24, 2 });
				table.setTotalWidth(527);
				table.setLockedWidth(true);
				table.getDefaultCell().setFixedHeight(20);
				table.getDefaultCell().setBorder(Rectangle.BOTTOM);
				table.addCell(header);
				table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
				// table.addCell(String.format("Page %d of", writer.getPageNumber()));
				table.addCell(new Phrase("內政部戶政司 下載時間:" + sdf1.format(new Date()), chineseFont));
				PdfPCell cell = new PdfPCell(Image.getInstance(total));
				cell.setBorder(Rectangle.BOTTOM);
				table.addCell(cell);
				table.writeSelectedRows(0, -1, 34, 803, writer.getDirectContent());
			} catch (DocumentException | IOException de) {
				throw new ExceptionConverter(de);
			}
			/*
			 * try { PdfContentByte cb = writer.getDirectContent(); Image imgSoc =
			 * Image.getInstance(
			 * "D:/webapps/tygh-children-care/resources/dist/img/unnamed.png");
			 * imgSoc.scaleToFit(30, 30); imgSoc.setAbsolutePosition(33, 780);
			 * 
			 * cb.addImage(imgSoc); } catch (Exception e) { e.printStackTrace(); }
			 */
		}

		public void onCloseDocument(PdfWriter writer, Document document) {
			ColumnText.showTextAligned(total, Element.ALIGN_LEFT,
					new Phrase(String.valueOf(writer.getPageNumber() - 1)), 2, 2, 0);
		}
	}

	@GetMapping("/news")
	public String getNews(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return "newsList";
	}

	@GetMapping("/news/slist1")
	public String getNewsSlist1(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return "news-slist1";
	}

	@GetMapping("/news/slist2")
	public String getNewsSlist2(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return "news-slist2";
	}

	@GetMapping("/news/slist3")
	public String getNewsSlist3(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return "news-slist3";
	}

	@GetMapping("/news/slist4")
	public String getNewsSlist4(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return "news-slist4";
	}

	@GetMapping("/news/notify")
	public String getNewsNotify(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return "news-notify";
	}

	@GetMapping("/news/op")
	public String getNewsOp(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return "redirect:/news/detail/623308a1-2ae2-42d8-80ca-167adc7f5fa0";
	}

	@GetMapping("/news/manual")
	public String getNewsOpManual(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return "redirect:/news/detail/939300e9-7c57-403c-ae58-d3639adebaec";
	}

	@GetMapping("/news/download")
	public String getNewsDownload(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return "redirect:/news/detail/3920c1e7-09bc-46a8-8841-3a7c7ce9231a";
	}

	@GetMapping("/news/ready")
	public String getNewsReady(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return "redirect:/news/detail/27e36d99-df38-4314-996b-99d8bb3ce2b3";
	}

	@GetMapping("/news/lazy")
	public String getNewsLazy(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return "news-lazy";
	}

	@GetMapping("/news/apply")
	public String getNewsApply(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return "redirect:/news/detail/dcd47325-2c73-407d-9de9-1764d95434cd";
	}
	
}
