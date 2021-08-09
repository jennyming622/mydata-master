package tw.gov.ndc.emsg.mydata.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.riease.common.helper.ValidatorHelper;
import com.riease.common.sysinit.controller.BaseRestController;
import com.riease.common.sysinit.rest.RestResponseBean;

@Controller
@RequestMapping("/rest/search")
public class SearchRestController extends BaseRestController {
	private static Logger logger = LoggerFactory.getLogger(SearchRestController.class);
	
	@PostMapping("/check")
	public ResponseEntity<RestResponseBean> getPersonalDownloadApply(
			HttpServletRequest request, 
			HttpServletResponse response, 
			@RequestBody Map<String, Object> params) {
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("word", ValidatorHelper.removeSpecialCharactersForXss(params.get("word").toString()));
		return responseOK(data);
	}
}
