package tw.gov.ndc.emsg.mydata.web;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.riease.common.helper.ValidatorHelper;

import tw.gov.ndc.emsg.mydata.entity.PortalProvider;
import tw.gov.ndc.emsg.mydata.entity.PortalProviderExample;
import tw.gov.ndc.emsg.mydata.entity.PortalResource;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceExt;
import tw.gov.ndc.emsg.mydata.entity.PortalService;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceExample;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceExt;
import tw.gov.ndc.emsg.mydata.mapper.PortalProviderMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalServiceMapper;
import tw.gov.ndc.emsg.mydata.mapper.ext.PortalResourceExtMapper;
import tw.gov.ndc.emsg.mydata.util.ComparatorUtil;

@Controller
@RequestMapping("/search")
public class SearchController {
	private static Logger logger = LoggerFactory.getLogger(SearchController.class);
	
	@Autowired
	private PortalResourceMapper portalResourceMapper;
	@Autowired
	private PortalResourceExtMapper portalResourceExtMapper;
	@Autowired
	private PortalServiceMapper portalServiceMapper;	
	@Autowired
	private PortalProviderMapper portalProviderMapper;
	@Autowired
	private ComparatorUtil comparatorUtil;
	
	@GetMapping("/{keyword}")
	public String getSearchByKeyword(@PathVariable("keyword") String keyword,
			HttpServletRequest request, HttpServletResponse response, ModelMap model)
			throws IllegalAccessException, InvocationTargetException {
		HttpSession session = request.getSession();	
		List<String> scopeList = new ArrayList<String>();

		Map<String,Object> param = new HashMap<String,Object>();
		param.put("name", ValidatorHelper.removeSpecialCharacters(keyword));
		List<PortalProvider> portalProviderList = portalProviderMapper.selectByExample(param);
		List<Integer> inProviderIdList = new ArrayList<Integer>();
		if(portalProviderList!=null&&portalProviderList.size()>0) {
			for(PortalProvider p:portalProviderList) {
				if(!inProviderIdList.contains(p.getProviderId())) {
					inProviderIdList.add(p.getProviderId());
				}
			}
		}
		List<PortalResourceExt> finalprList = new ArrayList<PortalResourceExt>();
		List<PortalResource> prList = portalResourceExtMapper.selectByExampleOrderByOid();
		if(prList!=null&&prList.size()>0) {
			for(PortalResource pr:prList) {
				boolean check = false;
				if(pr.getName().contains(ValidatorHelper.removeSpecialCharacters(keyword))) {
					check = true;
				}
				if(inProviderIdList.contains(pr.getProviderId())) {
					check = true;
				}
				if(check) {
					PortalResourceExt ext = new PortalResourceExt();
					BeanUtils.copyProperties(pr, ext);
					PortalProvider tmpProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(ext.getProviderId()));
					ext.setProviderName(tmpProvider.getName());
					finalprList.add(ext);
				}
			}
		}
		List<PortalServiceExt> finalpsList = new ArrayList<PortalServiceExt>();
		Map<String,Object> psparam = new HashMap<String,Object>();
		psparam.put("isShow", 1);
		List<PortalService> psList = portalServiceMapper.selectByExample(psparam);
		if(psList!=null&&psList.size()>0) {
			for(PortalService ps:psList) {
				boolean check = false;
				if(ps.getName().contains(ValidatorHelper.removeSpecialCharacters(keyword))) {
					check = true;
				}
				if(inProviderIdList.contains(ps.getProviderId())) {
					check = true;
				}
				if(check) {
					PortalServiceExt ext = new PortalServiceExt();
					BeanUtils.copyProperties(ps, ext);
					PortalProvider tmpProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(ext.getProviderId()));
					ext.setProviderName(tmpProvider.getName());
					finalpsList.add(ext);
				}
			}
		}
		List<PortalServiceExt> finalpsList1 = new ArrayList<PortalServiceExt>();
		Map<String,Object> psparam1 = new HashMap<String,Object>();
		psparam1.put("isCounter", 1);
		List<PortalService> psList1 = portalServiceMapper.selectByExample(psparam1);
		if(psList1!=null&&psList1.size()>0) {
			for(PortalService ps:psList1) {
				boolean check = false;
				if(ps.getName().contains(ValidatorHelper.removeSpecialCharacters(keyword))) {
					check = true;
				}
				if(inProviderIdList.contains(ps.getProviderId())) {
					check = true;
				}
				if(check) {
					PortalServiceExt ext = new PortalServiceExt();
					BeanUtils.copyProperties(ps, ext);
					PortalProvider tmpProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(ext.getProviderId()));
					ext.setProviderName(tmpProvider.getName());
					finalpsList1.add(ext);
				}
			}
		}
		model.addAttribute("prExtList", finalprList);
		comparatorUtil.sortPortalServiceExtList(finalpsList);
		model.addAttribute("psExtList", finalpsList);
		comparatorUtil.sortPortalServiceExtList(finalpsList1);
		model.addAttribute("psExtList1", finalpsList1);
		/*model.addAttribute("keyword", ValidatorHelper.removeSpecialCharacters(keyword));*/
		return "search";
	}
}
