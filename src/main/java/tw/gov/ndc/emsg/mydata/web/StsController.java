package tw.gov.ndc.emsg.mydata.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.riease.common.helper.ValidatorHelper;
import com.riease.common.sysinit.SysCode;
import tw.gov.ndc.emsg.mydata.Config;
import tw.gov.ndc.emsg.mydata.auth.SigninForm;
import tw.gov.ndc.emsg.mydata.entity.ClickLogExample;
import tw.gov.ndc.emsg.mydata.entity.PortalProvider;
import tw.gov.ndc.emsg.mydata.entity.PortalResource;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceCategory;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceExample;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceExt;
import tw.gov.ndc.emsg.mydata.entity.PortalService;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceCategory;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceExample;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceExt;
import tw.gov.ndc.emsg.mydata.mapper.ClickLogMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalProviderMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceCategoryMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalServiceCategoryMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalServiceMapper;

@Controller
@RequestMapping("/sts")
public class StsController {
	
	private static Logger logger = LoggerFactory.getLogger(StsController.class);
	
	@Autowired
	private PortalServiceMapper portalServiceMapper;
	@Autowired
	private PortalServiceCategoryMapper portalServiceCategoryMapper;
	@Autowired
	private PortalResourceMapper portalResourceMapper;
	@Autowired
	private PortalResourceCategoryMapper portalResourceCategoryMapper;
	@Autowired
	private PortalProviderMapper portalProviderMapper;	
	
	@Autowired
	private ClickLogMapper clickLogMapper;	
	
	@GetMapping("/cklist")
	public String getStsCkList(
    		ModelMap model) {
		/*PortalServiceExample portalServiceExample = new PortalServiceExample();
		portalServiceExample.setOrderByClause("ctime asc");*/
		Map<String,Object> psparam = new HashMap<String,Object>();
		psparam.put("ctimeAsc", 1);
		List<PortalService> portalServiceList = portalServiceMapper.selectByExample(psparam);
		List<PortalServiceExt> portalServiceExtList = new ArrayList<PortalServiceExt>();
		if(portalServiceList!=null&&portalServiceList.size()>0) {
			for(PortalService ps:portalServiceList) {
				PortalServiceExt form = new PortalServiceExt();
				List<PortalServiceCategory> cateList = new ArrayList<PortalServiceCategory>();
				BeanUtils.copyProperties(ps, form);
				PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(form.getProviderId()));
				form.setProviderName(portalProvider.getName());
				PortalServiceCategory l2Cate = portalServiceCategoryMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(ps.getCateId()));
				PortalServiceCategory l1Cate = portalServiceCategoryMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(l2Cate.getParentId()));
				cateList.add(l1Cate);
				cateList.add(l2Cate);
//				form.setCateList(cateList);

				Map<String,Object> param = new HashMap<String,Object>();
				param.put("cntType", "service");
				param.put("cntId", ValidatorHelper.limitNumber(ps.getPsId()));
				long clickCount = clickLogMapper.countByExample(param);
				form.setCount(clickCount);
				
				portalServiceExtList.add(form);
			}
			model.addAttribute("portalServiceList", portalServiceExtList);		
		}
		return "stsCK_list";
	}
	
	@GetMapping("/ckchart")
	public String getStsCkChart(
    		ModelMap model) {
		
		return "stsCK_chart";
	}
	
	@GetMapping("/dllist")
	public String getStsDlList(
    		ModelMap model) {
		PortalResourceExample portalResourceExample = new PortalResourceExample();
		portalResourceExample.setOrderByClause("ctime asc");
		Map<String,Object> rparam = new HashMap<String,Object>();
		rparam.put("ctimeAsc", true);
		List<PortalResource> portalResourceList = portalResourceMapper.selectByExample(rparam);
		List<PortalResourceExt> portalResourceExtList = new ArrayList<PortalResourceExt>();
		if(portalResourceList!=null&&portalResourceList.size()>0) {
			for(PortalResource ps:portalResourceList) {
				PortalResourceExt form = new PortalResourceExt();
				List<PortalResourceCategory> cateList = new ArrayList<PortalResourceCategory>();
				BeanUtils.copyProperties(ps, form);
				PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(form.getProviderId()));
				form.setProviderName(portalProvider.getName());
				PortalResourceCategory l2Cate = portalResourceCategoryMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(ps.getCateId()));
				PortalResourceCategory l1Cate = portalResourceCategoryMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(l2Cate.getParentId()));
				cateList.add(l1Cate);
				cateList.add(l2Cate);
				form.setCateList(cateList);

				Map<String,Object> param = new HashMap<String,Object>();
				param.put("cntType", "resource");
				param.put("cntId", ValidatorHelper.limitNumber(ps.getPrId()));
				long clickCount = clickLogMapper.countByExample(param);
				form.setCount(clickCount);
				
				portalResourceExtList.add(form);
				
			}
			model.addAttribute("portalResourceList", portalResourceExtList);		
		}
		return "stsDL_list";
	}
	
	@GetMapping("/dlchart")
	public String getStsDlChart(
    			ModelMap model) {
		return "stsDL_chart";
	}		
}
