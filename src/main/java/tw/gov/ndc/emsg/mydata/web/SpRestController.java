package tw.gov.ndc.emsg.mydata.web;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.riease.common.helper.ValidatorHelper;
import com.riease.common.sysinit.SessionRecord;
import com.riease.common.sysinit.controller.BaseRestController;
import com.riease.common.sysinit.rest.RestResponseBean;

import tw.gov.ndc.emsg.mydata.entity.*;
import tw.gov.ndc.emsg.mydata.mapper.*;
import tw.gov.ndc.emsg.mydata.mapper.ext.PortalResourceExtMapper;
@Controller
@RequestMapping("/rest/sp")
public class SpRestController extends BaseRestController {
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
	private PortalProviderMapper portalProviderMapper;
	@Autowired
	private PortalResourceMapper portalResourceMapper;
	@Autowired
	private PortalResourceExtMapper portalResourceExtMapper;
	@Autowired
	private PortalResourceCategoryMapper portalResourceCategoryMapper;
	@Autowired
	private PortalResourceScopeMapper portalResourceScopeMapper;
	@Autowired
	private PortalCounterSubMapperExt portalCounterSubMapperExt;
	
	/**
	 * 線上服務查詢
	 * @param request
	 * @param response
	 * @param params
	 * 1. choose-service-area  -> cateIdList
	 * 2. service-type         -> type
	 * 3. service-city         -> provider name or ps name like service-city
	 * 4. service-keyword      -> provider name or ps name like service-keyword
	 * @return
	 */
	@PostMapping("/service")
	public ResponseEntity<RestResponseBean> postSpService(HttpServletRequest request, 
			HttpServletResponse response, 
			@RequestBody Map<String, Object> params) {
		List<PortalResourceExt> prExtList = portalResourceExtMapper.selectPortalResourceWithCateName();

		Map<String, Object> sparam = new HashMap<String, Object>();
		List<PortalResourceScope> tmpPortalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);

		Map<String, Object> pssparam = new HashMap<String, Object>();
		List<PortalServiceScope> tmpPortalServiceScopeList = portalServiceScopeMapper.selectByExample(pssparam);

		/**
		 * 服務分類1,2,3,4,5
		 */
		List<Integer> cateIdList = new ArrayList<Integer>();
		if(params.get("cateIdList")!=null&&params.get("cateIdList") instanceof ArrayList&& ((ArrayList)params.get("cateIdList")).size()>0) {
			cateIdList = ((ArrayList)params.get("cateIdList"));
			if(cateIdList!=null&&cateIdList.size()>0) {
				if(cateIdList.get(0)==0) {
					cateIdList = new ArrayList<Integer>();
					cateIdList.add(1);
					cateIdList.add(2);
					cateIdList.add(3);
					cateIdList.add(4);
					cateIdList.add(5);
				}
			}
		}else {
			cateIdList.add(1);
			cateIdList.add(2);
			cateIdList.add(3);
			cateIdList.add(4);
			cateIdList.add(5);
		}

		//getServiceExtByCateList(cateIdList, prExtList, tmpPortalResourceScopeList, tmpPortalServiceScopeList);
		
		Map<String, Object> psparam = new HashMap<String, Object>();
		if (cateIdList != null) {
			psparam.put("cateIdList", cateIdList);
		}
		if(params.get("type")!=null&&params.get("type").toString().length()>0) {
			psparam.put("type", params.get("type"));
		}
		if(params.get("serviceCity")!=null&&params.get("serviceCity").toString().length()>0) {
			psparam.put("serviceCity", params.get("serviceCity").toString());
		}
		if(params.get("serviceKeyword")!=null&&params.get("serviceKeyword").toString().length()>0) {
			psparam.put("serviceKeyword", params.get("serviceKeyword").toString());
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
				portalServiceExtList.add(ext);
			}
		}
		return responseOK(portalServiceExtList);
	}
	
	/**
	 * 臨櫃服務查詢
	 * @param request
	 * @param response
	 * @param params
	 * 1. choose-service-area  -> cateIdList
	 * 2. service-type         -> type
	 * 3. service-city         -> provider name or ps name like service-city
	 * 4. service-keyword      -> provider name or ps name like service-keyword
	 * @return
	 */	
	@PostMapping("/service/counter")
	public ResponseEntity<RestResponseBean> postSpServiceCounter(HttpServletRequest request, 
			HttpServletResponse response, 
			@RequestBody Map<String, Object> params) {
		List<PortalResourceExt> prExtList = portalResourceExtMapper.selectPortalResourceWithCateName();

		Map<String, Object> sparam = new HashMap<String, Object>();
		List<PortalResourceScope> tmpPortalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);

		Map<String, Object> pssparam1 = new HashMap<String, Object>();
		List<PortalServiceScopeCounter> tmpPortalServiceScopeCounterList = portalServiceScopeCounterMapper.selectByExample(pssparam1);
		/**
		 * 服務分類1,2,3,4,5
		 */
		List<Integer> cateIdList = new ArrayList<Integer>();
		if(params.get("cateIdList")!=null&&params.get("cateIdList") instanceof ArrayList&& ((ArrayList)params.get("cateIdList")).size()>0) {
			cateIdList = ((ArrayList)params.get("cateIdList"));
			if(cateIdList.get(0)==0) {
				cateIdList = new ArrayList<Integer>();
				cateIdList.add(1);
				cateIdList.add(2);
				cateIdList.add(3);
				cateIdList.add(4);
				cateIdList.add(5);
			}
		}else {
			cateIdList.add(1);
			cateIdList.add(2);
			cateIdList.add(3);
			cateIdList.add(4);
			cateIdList.add(5);
		}
		
		Map<String, Object> psparam = new HashMap<String, Object>();
		if (cateIdList != null) {
			psparam.put("cateIdList", cateIdList);
		}
		if(params.get("type")!=null&&params.get("type").toString().length()>0) {
			psparam.put("type", params.get("type"));
		}
		if(params.get("serviceCity")!=null&&params.get("serviceCity").toString().length()>0) {
			psparam.put("serviceCity", params.get("serviceCity").toString());
		}
		if(params.get("serviceKeyword")!=null&&params.get("serviceKeyword").toString().length()>0) {
			psparam.put("serviceKeyword", params.get("serviceKeyword").toString());
		}
		Integer isOpenAgent = MapUtils.getInteger(params, "isOpenAgent");

		psparam.put("isCounter", 1);
		psparam.put("ProviderNameAndNameAsc", true);
		List<PortalService> portalServiceList = portalServiceMapper.selectForFrontByExample(psparam);
		List<PortalServiceExt> portalServiceExtList = new ArrayList<PortalServiceExt>();

		List<Integer> psIdList = portalServiceList.stream().map(p -> ValidatorHelper.limitNumber(p.getPsId())).collect(Collectors.toList());
		Map<String, Object> pcsParam = new HashMap<>();
		pcsParam.put("psIdList", psIdList);
		List<PortalCounterSub> portalCounterSubList = portalCounterSubMapperExt.selectByExample(pcsParam);

		if (portalServiceList != null && portalServiceList.size() > 0) {
			for (PortalService p : portalServiceList) {
				PortalServiceExt ext = new PortalServiceExt();
				BeanUtils.copyProperties(p, ext);
				PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(ext.getProviderId()));
				if (portalProvider != null) {
					ext.setProviderName(portalProvider.getName());
				}
				checkDpMaintainAndLevelCounter(ext, prExtList, tmpPortalResourceScopeList, tmpPortalServiceScopeCounterList);

				if(isOpenAgent != null && isOpenAgent.equals(1)) {
					List<PortalCounterSub> portalCounterSubs = portalCounterSubList.stream()
							.filter(cs -> cs.getPsId().equals(p.getPsId()))
							.collect(Collectors.toList());

					Integer size = portalCounterSubs.stream().filter(tmp -> tmp.getIsOpenAgent().equals(1))
							.collect(Collectors.toList()).size();

					if(size > 0) {
						portalServiceExtList.add(ext);
					}
				} else {
					portalServiceExtList.add(ext);
				}

			}
		}

		return responseOK(portalServiceExtList);
	}
		
	private void checkDpMaintainAndLevel(PortalServiceExt ext, List<PortalResourceExt> prExtList,
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
		Boolean isDPMaintain = false;
		if (allowPrIdList != null && allowPrIdList.size() > 0) {
			for (Integer tmpPrId : allowPrIdList) {
				PortalResourceExt tmpPortalResource = null;
				if (prExtList != null && prExtList.size() > 0) {
					for (PortalResourceExt tmppr : prExtList) {
						if (tmpPrId.compareTo(tmppr.getPrId()) == 0) {
							tmpPortalResource = tmppr;
						}
					}
				}
				if (tmpPortalResource != null) {
					if (tmpPortalResource.getLevel().compareTo(level) < 0) {
						level = tmpPortalResource.getLevel();
					}
					if (tmpPortalResource.getCheckStat() != null && tmpPortalResource.getCheckStat() != 0) {
						isDPMaintain = true;
					}
				}
			}
		}

		Boolean isSPMaintain = ext.getCheckStat() != null && ext.getCheckStat() != 0;
		ext.setMaintain(isSPMaintain || isDPMaintain);
		ext.setLevel(level);
	}

	private void checkDpMaintainAndLevelCounter(PortalServiceExt ext, List<PortalResourceExt> prExtList,
			List<PortalResourceScope> tmpPortalResourceScopeList,
			List<PortalServiceScopeCounter> tmpPortalServiceScopeList) {
		List<Integer> allowPrIdList = new ArrayList<Integer>();
		List<PortalServiceScopeCounter> portalServiceScopeList = new ArrayList<PortalServiceScopeCounter>();
		if (tmpPortalServiceScopeList != null && tmpPortalServiceScopeList.size() > 0) {
			for (PortalServiceScopeCounter tmppsc : tmpPortalServiceScopeList) {
				if (tmppsc.getPsId().compareTo(ext.getPsId()) == 0) {
					portalServiceScopeList.add(tmppsc);
				}
			}
		}
		if (portalServiceScopeList != null && portalServiceScopeList.size() > 0) {
			for (PortalServiceScopeCounter psc : portalServiceScopeList) {
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
		Boolean isDPMaintain = false;
		if (allowPrIdList != null && allowPrIdList.size() > 0) {
			for (Integer tmpPrId : allowPrIdList) {
				PortalResourceExt tmpPortalResource = null;
				if (prExtList != null && prExtList.size() > 0) {
					for (PortalResourceExt tmppr : prExtList) {
						if (tmpPrId.compareTo(tmppr.getPrId()) == 0) {
							tmpPortalResource = tmppr;
						}
					}
				}
				if (tmpPortalResource != null) {
					if (tmpPortalResource.getLevel().compareTo(level) < 0) {
						level = tmpPortalResource.getLevel();
					}
					if (tmpPortalResource.getCheckStat() != null && tmpPortalResource.getCheckStat() != 0) {
						isDPMaintain = true;
					}
				}
			}
		}

		Boolean isSPMaintain = ext.getCheckStat() != null && ext.getCheckStat() != 0;
		ext.setMaintain(isSPMaintain || isDPMaintain);
		ext.setLevel(level);
	}
}
