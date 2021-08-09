package tw.gov.ndc.emsg.mydata.web;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.riease.common.helper.HttpHelper;
import com.riease.common.helper.ValidatorHelper;

import tw.gov.ndc.emsg.mydata.entity.OrganWhiteIpList;
import tw.gov.ndc.emsg.mydata.entity.OrganWhiteIpListExample;
import tw.gov.ndc.emsg.mydata.entity.PortalBoxLockCheck;
import tw.gov.ndc.emsg.mydata.mapper.OrganWhiteIpListMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalBoxLockCheckMapper;

@Controller
@RequestMapping("/organ")
public class OrganController {
	private static final Logger logger = LoggerFactory.getLogger(OrganController.class);

	@Autowired
	private PortalBoxLockCheckMapper portalBoxLockCheckMapper;
	@Autowired
	private OrganWhiteIpListMapper organWhiteIpListMapper;
	@Value("${Organ_Enable}")
	private boolean OrganEnable;
	
	@GetMapping
	public String getSpVerification(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		HttpSession session = request.getSession();
		model.addAttribute("error", 0);
		String ip = HttpHelper.getRemoteIp(request);
		/**
		 * 機關ip檢查白名單，Organ_Enable = true，進行白名單檢查
		 */
		if(OrganEnable) {
			Map<String,Object> param = new HashMap<String,Object>();
			List<OrganWhiteIpList> organWhiteIpLists = organWhiteIpListMapper.selectByExample(param);
			logger.debug("ip >> {}", ip);
			logger.debug("organ white ip list >> {}", organWhiteIpLists.size());
			if(organWhiteIpLists==null||organWhiteIpLists.size()==0) {
				return "ip-error";
			}else {
				boolean authorized = false;
				for (OrganWhiteIpList organIp : organWhiteIpLists) {
					String wIp = organIp.getIp();
					String[] ipAddressSplitList = wIp.split("[.]");
					logger.debug("wIp >> {}", wIp);
					logger.debug("ipAddressSplitList length >> {}", ipAddressSplitList.length);
					if (ipAddressSplitList != null && ipAddressSplitList.length == 3) {
						if (ip.startsWith(wIp)) {
	                        authorized = true;
	                        break;
						}
					}else {
	                    if (ip.equals(wIp)) {
		                    authorized = true;
		                    break;
		                }
					}
				}
				if (!authorized) {
					return "ip-error";
				 }
			}			
		}
		
		if(ip!=null&&ip.trim().length()>0) {
			PortalBoxLockCheck portalBoxLockCheck= portalBoxLockCheckMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(ip));
			if(portalBoxLockCheck!=null) {
				Date now = new Date();
				Date ctime = portalBoxLockCheck.getCtime();
				long nowTime = now.getTime();
				long ctimeTime = ctime.getTime();
				//如果超過15分鐘，原紀錄不理會，重新記數
				if(nowTime>(ctimeTime+(15*60*1000))) {
					PortalBoxLockCheck portalBoxLockCheck1 = new PortalBoxLockCheck();
					portalBoxLockCheck1.setIp(ValidatorHelper.removeSpecialCharacters(ip));
					portalBoxLockCheck1.setCtime(new Date());
					portalBoxLockCheck1.setStat(0);
					portalBoxLockCheck1.setCount(0);
					portalBoxLockCheckMapper.updateByPrimaryKeySelective(portalBoxLockCheck1);
				}else {
					//沒超過10分鐘且超過五次轉跳頁面
					if(portalBoxLockCheck.getCount() >= 5) {
						return "redirect:/organ/error6";
					}
				}
			}
		}
		
		return "verification";
	}
	
	/**
	 * 未登入，請先登入
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@GetMapping("/error1")
	public String getSpVerificationError1(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		HttpSession session = request.getSession();
		model.addAttribute("error", 1);
		return "verification_error";
	}
	
	/**
	 * 查無驗證碼
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@GetMapping("/error2")
	public String getSpVerificationError2(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		HttpSession session = request.getSession();
		model.addAttribute("error", 2);
		String ip = HttpHelper.getRemoteIp(request);
		checkPortalBoxLockCheck(ip);
		return "verification_error";
	}
	
	/**
	 * 查無下載檔案
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@GetMapping("/error3")
	public String getSpVerificationError3(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		HttpSession session = request.getSession();
		model.addAttribute("error", 3);
		return "verification_error";
	}
	
	/**
	 * 非pdf檔，不可預覽
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@GetMapping("/error4")
	public String getSpVerificationError4(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		HttpSession session = request.getSession();
		model.addAttribute("error", 4);
		return "verification_error";
	}	
	
	/**
	 * 我不是機器人驗證碼錯誤
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@GetMapping("/error5")
	public String getSpVerificationError5(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		HttpSession session = request.getSession();
		model.addAttribute("error", 5);
		return "verification_error";
	}
	
	/**
	 * 資料條碼連續錯誤五次，該IP鎖住10分鐘
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@GetMapping("/error6")
	public String getSpVerificationError6(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		HttpSession session = request.getSession();
		model.addAttribute("error", 6);
		String ip = HttpHelper.getRemoteIp(request);
		if(ip!=null&&ip.trim().length()>0) {
			PortalBoxLockCheck portalBoxLockCheck= portalBoxLockCheckMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(ip));
			if(portalBoxLockCheck!=null && portalBoxLockCheck.getStat() == 1) {
				model.addAttribute("lock", portalBoxLockCheck);
			}
		}
		return "verification_error";
	}	
	
	private void checkPortalBoxLockCheck(String ip) {
		logger.debug("==checkPortalBoxLockCheck ip== {}", ip);
		if(ip!=null&&ip.trim().length()>0) {
			PortalBoxLockCheck portalBoxLockCheck= portalBoxLockCheckMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(ip));
			if(portalBoxLockCheck!=null) {
				Date now = new Date();
				Date ctime = portalBoxLockCheck.getCtime();
				long nowTime = now.getTime();
				long ctimeTime = ctime.getTime();
				//如果超過10分鐘，原紀錄不理會，重新記數
				if(nowTime>(ctimeTime+(15*60*1000))) {
					PortalBoxLockCheck portalBoxLockCheck1 = new PortalBoxLockCheck();
					portalBoxLockCheck1.setIp(ValidatorHelper.removeSpecialCharacters(ip));
					portalBoxLockCheck1.setCtime(new Date());
					portalBoxLockCheck1.setStat(0);
					portalBoxLockCheck1.setCount(1);
					portalBoxLockCheckMapper.updateByPrimaryKeySelective(portalBoxLockCheck1);
				}else {
					//沒超過10分鐘，次數少4次(沒加本次)
					if(portalBoxLockCheck.getCount()<4) {
						PortalBoxLockCheck portalBoxLockCheck1 = new PortalBoxLockCheck();
						portalBoxLockCheck1.setCtime(new Date());
						portalBoxLockCheck1.setCount(ValidatorHelper.limitNumber(portalBoxLockCheck.getCount())+1);
						portalBoxLockCheck1.setIp(ValidatorHelper.removeSpecialCharacters(ip));
						portalBoxLockCheckMapper.updateByPrimaryKeySelective(portalBoxLockCheck1);
					}else {
						//超過5次(含本次)
						PortalBoxLockCheck portalBoxLockCheck1 = new PortalBoxLockCheck();
						portalBoxLockCheck1.setStat(1);
						portalBoxLockCheck1.setCount(ValidatorHelper.limitNumber(portalBoxLockCheck.getCount())+1);
						portalBoxLockCheck1.setIp(ValidatorHelper.removeSpecialCharacters(ip));
						portalBoxLockCheckMapper.updateByPrimaryKeySelective(portalBoxLockCheck1);
					}
				}
			}else {
				PortalBoxLockCheck record = new PortalBoxLockCheck();
				record.setIp(ValidatorHelper.removeSpecialCharacters(ip));
				record.setCount(1);
				record.setCtime(new Date());
				record.setStat(0);
				portalBoxLockCheckMapper.insertSelective(record);
			}
		}
	}
	
}
