package tw.gov.ndc.emsg.mydata.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.riease.common.helper.ValidatorHelper;

import tw.gov.ndc.emsg.mydata.entity.PortalProvider;
import tw.gov.ndc.emsg.mydata.entity.PortalResource;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceCategory;
import tw.gov.ndc.emsg.mydata.gspclient.bean.NonceEntity;
import tw.gov.ndc.emsg.mydata.mapper.PortalProviderMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceCategoryMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceMapper;
import tw.gov.ndc.emsg.mydata.mapper.ext.PortalResourceExtMapper;

@Controller
@RequestMapping("/find_all_resources")
public class FindAllResourceController {
	@Autowired
	private PortalResourceMapper portalResourceMapper;
	@Autowired
	private PortalResourceExtMapper portalResourceExtMapper;
	@Autowired
	private PortalProviderMapper portalProviderMapper;
	@Autowired
	private PortalResourceCategoryMapper portalResourceCategoryMapper;
	@GetMapping
	public void getFindAllResources(
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
        Map<String,Object> rparam = new HashMap<String,Object>();
        rparam.put("isShow", 1);
        rparam.put("status", 1);
        rparam.put("providerIdAsc", true);
        List<PortalResource> allPortalResourceList = portalResourceMapper.selectByExample(rparam);
		String outStr = "";
		if(allPortalResourceList!=null&&allPortalResourceList.size()>0) {
			outStr = outStr + "[";
			for(int i=0;i<allPortalResourceList.size();i++) {
				PortalResource pr = allPortalResourceList.get(i);
				if(i==(allPortalResourceList.size()-1)) {
					outStr = outStr + "{";
					outStr = outStr + "\"resource_id\":\""+ValidatorHelper.removeSpecialCharacters(pr.getResourceId())+"\",";
					PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(pr.getProviderId()));
					outStr = outStr + "\"dep\":\""+(portalProvider==null?"":ValidatorHelper.removeSpecialCharacters(portalProvider.getName()))+"\",";
					outStr = outStr + "\"name\":\""+ValidatorHelper.removeSpecialCharacters(pr.getName())+"\",";
					outStr = outStr + "\"link\":\"https://mydata.nat.gov.tw/personal/cate/"+pr.getCateId()+"/list?current="+pr.getPrId()+"\",";
					PortalResourceCategory portalResourceCategory = portalResourceCategoryMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(pr.getCateId()));
					outStr = outStr + "\"cate\":\""+(portalResourceCategory==null?"":ValidatorHelper.removeSpecialCharacters(portalResourceCategory.getCateName()))+"\",";
					String statusStr = "01";
					if(pr.getCheckStat()!=null&&pr.getCheckStat()==1) {
						/**
						 * Error check_stat == 1
						 */
						statusStr = "00";
					}else {
						/**
						 * OK
						 */
						statusStr = "01";
					}
					outStr = outStr + "\"status\":\""+statusStr+"\"";
					outStr = outStr + "}";
				}else {
					outStr = outStr + "{";
					outStr = outStr + "\"resource_id\":\""+ValidatorHelper.removeSpecialCharacters(pr.getResourceId())+"\",";
					PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(pr.getProviderId()));
					outStr = outStr + "\"dep\":\""+(portalProvider==null?"":ValidatorHelper.removeSpecialCharacters(portalProvider.getName()))+"\",";
					outStr = outStr + "\"name\":\""+ValidatorHelper.removeSpecialCharacters(pr.getName())+"\",";
					outStr = outStr + "\"link\":\"https://mydata.nat.gov.tw/personal/cate/"+pr.getCateId()+"/list?current="+pr.getPrId()+"\",";
					PortalResourceCategory portalResourceCategory = portalResourceCategoryMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(pr.getCateId()));
					outStr = outStr + "\"cate\":\""+(portalResourceCategory==null?"":ValidatorHelper.removeSpecialCharacters(portalResourceCategory.getCateName()))+"\",";
					String statusStr = "01";
					if(pr.getCheckStat()!=null&&pr.getCheckStat()==1) {
						/**
						 * Error check_stat == 1
						 */
						statusStr = "00";
					}else {
						/**
						 * OK
						 */
						statusStr = "01";
					}
					outStr = outStr + "\"status\":\""+statusStr+"\"";
					outStr = outStr + "},";
				}
			}
			outStr = outStr + "]";
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print(outStr);
		out.close();	
	}
}
