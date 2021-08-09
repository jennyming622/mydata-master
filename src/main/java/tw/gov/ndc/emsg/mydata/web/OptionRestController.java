package tw.gov.ndc.emsg.mydata.web;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.riease.common.sysinit.SysCode;
import com.riease.common.sysinit.controller.BaseRestController;
import com.riease.common.sysinit.rest.RestResponseBean;

import tw.gov.ndc.emsg.mydata.entity.EtdTownInfo;
import tw.gov.ndc.emsg.mydata.entity.EtdVillInfo;
import tw.gov.ndc.emsg.mydata.mapper.EtdTownInfoMapper;
import tw.gov.ndc.emsg.mydata.mapper.EtdVillInfoMapper;
import tw.gov.ndc.emsg.mydata.mapper.UlogApiMapper;

@Controller
@RequestMapping("/rest/option")
public class OptionRestController extends BaseRestController{
	private static Logger logger = LoggerFactory.getLogger(OptionRestController.class);
	@Autowired
	private EtdTownInfoMapper etdTownInfoMapper;
	@Autowired
	private EtdVillInfoMapper etdVillInfoMapper;
	@PostMapping("/town")
	@ResponseBody
	public ResponseEntity<RestResponseBean> postTownOptionData(HttpServletRequest request,
			@RequestBody Map<String, Object> params)
			throws NoSuchAlgorithmException, KeyManagementException, IOException {
		Map<String, Object> queryparam = new HashMap<String, Object>();
		if(params.get("taxType")!=null&&params.get("taxType").toString().trim().length()>0) {
			queryparam.put("taxType", params.get("taxType").toString().trim());
		}else {
			queryparam.put("taxType", "L");
		}
		if(params.get("hsnCd")!=null&&params.get("hsnCd").toString().trim().length()>0) {
			queryparam.put("hsnCd", params.get("hsnCd").toString().trim());
			List<EtdTownInfo> etdTownInfoList = etdTownInfoMapper.selectByExample(queryparam);
			if(etdTownInfoList!=null&&etdTownInfoList.size()>0) {
				return responseOK(etdTownInfoList);
			}else {
				return responseOK(Collections.emptyList());
			}
		}else {
			return responseError(SysCode.DataNotExist,"hsnCd","資料不存在");
		}
	}
	
	@PostMapping("/vill")
	@ResponseBody
	public ResponseEntity<RestResponseBean> postVillOptionData(HttpServletRequest request,
			@RequestBody Map<String, Object> params)
			throws NoSuchAlgorithmException, KeyManagementException, IOException {
		Map<String, Object> queryparam = new HashMap<String, Object>();
		if(params.get("taxType")!=null&&params.get("taxType").toString().trim().length()>0) {
			queryparam.put("taxType", params.get("taxType").toString().trim());
		}else {
			queryparam.put("taxType", "L");
		}
		if(params.get("townCd")!=null&&params.get("townCd").toString().trim().length()>0) {
			queryparam.put("townCd", params.get("townCd").toString().trim());
		}
		if(params.get("hsnCd")!=null&&params.get("hsnCd").toString().trim().length()>0) {
			queryparam.put("hsnCd", params.get("hsnCd").toString().trim());
			List<EtdVillInfo> etdVillInfoList = etdVillInfoMapper.selectByExample(queryparam);
			if(etdVillInfoList!=null&&etdVillInfoList.size()>0) {
				return responseOK(etdVillInfoList);
			}else {
				return responseOK(Collections.emptyList());
			}
		}else {
			return responseError(SysCode.DataNotExist,"hsnCd","資料不存在");
		}
	}
}
