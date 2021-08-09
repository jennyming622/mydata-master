package tw.gov.ndc.emsg.mydata.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.riease.common.helper.ValidatorHelper;
import com.riease.common.sysinit.SessionRecord;

import tw.gov.ndc.emsg.mydata.entity.PortalBoxLogExt;
import tw.gov.ndc.emsg.mydata.entity.PortalProvider;
import tw.gov.ndc.emsg.mydata.entity.PortalResource;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceExt;
import tw.gov.ndc.emsg.mydata.entity.PortalService;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceExt;
import tw.gov.ndc.emsg.mydata.entity.UlogApi;
import tw.gov.ndc.emsg.mydata.entity.UlogApiExt;
import tw.gov.ndc.emsg.mydata.mapper.PortalProviderMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceScopeMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalServiceMapper;
import tw.gov.ndc.emsg.mydata.mapper.ext.PortalResourceExtMapper;
import tw.gov.ndc.emsg.mydata.mapper.ext.UlogApiMapperExt;

@Controller
@RequestMapping("/authorization")
public class AuController {
	private static final Logger logger = LoggerFactory.getLogger(AuController.class);
	private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
	private static SimpleDateFormat sdf3 = new SimpleDateFormat("年MM月dd日");
	
	@Value("${baidu.tongji.url}") private String baiduUrl;
	@Value("${app.frontend.context.url}") private String frontendContextUrl;
	@Value("${app.backend.context.url}") private String backendContextUrl;
	@Value("${gsp.oidc.client.secret}") private String clientSecret;
	
	@Autowired private PortalServiceMapper portalServiceMapper;
	@Autowired private UlogApiMapperExt ulogApiMapperExt;
	@Autowired private PortalResourceMapper portalResourceMapper;
	@Autowired private PortalResourceExtMapper portalResourceExtMapper;
	@Autowired private PortalResourceScopeMapper portalResourceScopeMapper;
	@Autowired private PortalProviderMapper portalProviderMapper;
	@GetMapping
	public String getAuthorization(HttpServletRequest request, HttpServletResponse response,ModelMap model) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		HttpSession session = request.getSession();
		SessionRecord sr = (SessionRecord)session.getAttribute(SessionRecord.SessionKey);
		
		List<UlogApiExt> ulogExtMiddleList = new ArrayList<UlogApiExt>();
		List<UlogApiExt> ulogExtList = new ArrayList<UlogApiExt>();
		//基本授權項目
		List<String> defaultScopeList = new ArrayList<String>();
		defaultScopeList.add("openid");
		defaultScopeList.add("profile");
		defaultScopeList.add("gsp.profile");
		defaultScopeList.add("email");
		defaultScopeList.add("uid");
		defaultScopeList.add("phone_number");
		defaultScopeList.add("API.amr");
		defaultScopeList.add("API0000001-read");
		List<String> dpScopeList = new ArrayList<String>();
		
		// 查詢授權記錄
		//List<TokenRecord> tokenRecords = null;
		List<PortalServiceExt> psExtList = new ArrayList<PortalServiceExt>();
		/*try {
			tokenRecords = gspClient.requestTokenRecords((String) session.getAttribute("accessToken"));
			if (tokenRecords != null&&tokenRecords.size()>0) {
				tokenRecords.forEach(p -> {
					System.out.println("-----------scope start---------------:"+p.getClientId());
					for(String s:p.getTokenData().getScopes()) {
						if(s!=null&&s.trim().length()>0&&!defaultScopeList.contains(s.trim())) {
							dpScopeList.add(s.trim());
						}
					}
					System.out.println("-----------scope end---------------:"+p.getClientId());
				});
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		System.out.println("--------------------psExtList  size........:" + psExtList.size());
		Map<String, Object> psallparam = new HashMap<String, Object>();
		psallparam.put("providerIdAndPsIdAsc", true);
		List<PortalService> portalServiceList = portalServiceMapper.selectByExample(psallparam);
		if(portalServiceList!=null&&portalServiceList.size()>0) {
			for(PortalService ps:portalServiceList) {
				PortalServiceExt psExt = new PortalServiceExt();
				ulogExtMiddleList = new ArrayList<UlogApiExt>();
				ulogExtList = new ArrayList<UlogApiExt>();
				try {
					BeanUtils.copyProperties(psExt, ps);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(ps.getProviderId()));
				psExt.setProviderName(portalProvider.getName());
				List<UlogApi> ulogList = null;
				if(sr!=null&&sr.getMember().getAccount()!=null) {
					ulogList = ulogApiMapperExt.getLogByAccountAndClientIdAndAuditEventForService(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount(), ValidatorHelper.removeSpecialCharacters(ps.getClientId()));
				}
				if(ulogList!=null&&ulogList.size()>0) {
					for(UlogApi ulog : ulogList) {
						UlogApiExt titleExt = new UlogApiExt();
						UlogApiExt ulogExt = new UlogApiExt();
						try {
							BeanUtils.copyProperties(ulogExt, ulog);
						} catch (IllegalAccessException | InvocationTargetException e) {
							e.printStackTrace();
						}
						
						/**
						 * 服務：
						 * 21 申請：您申請此筆資料/服務 
						 * 22 同意：您同意服務條款
						 * 23 驗證：您完成身分驗證
						 * 24 傳輸：您同意 MyData 傳送資料給服務提供者
						 * 25 儲存：您下載資料
						 * 26 條碼取用：機關取用資料
						 * 27 服務應用：MyData 將資料傳送給服務提供者
						 */
						System.out.println("---------------------------------------------------------------");
						System.out.println("==:"+ulog.getAuditEvent());
						System.out.println("==:"+ulog.getAuditEvent()!=null&&ulog.getAuditEvent()==21);
						System.out.println("==:"+ulogExtMiddleList.size());
						//申請
						if(ulog.getAuditEvent()!=null&&ulog.getAuditEvent()==21) {
							List<String> scopeItemList = new ArrayList<String>();
							//scopeItemList.add("申請"+portalProvider.getName()+"的"+ps.getName()+"。");
							ulogExt.setProviderName(portalProvider.getName());
							ulogExt.setServiceName(ps.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("申請");
						//申請完成
						}else if(ulog.getAuditEvent()!=null&&ulog.getAuditEvent()==22) {
							List<String> scopeItemList = new ArrayList<String>();
							//scopeItemList.add("您申請的"+portalProvider.getName()+"的"+ps.getName()+"已儲存。");
							ulogExt.setProviderName(portalProvider.getName());
							ulogExt.setServiceName(ps.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("同意");
						//收到
						}else if(ulog.getAuditEvent()!=null&&ulog.getAuditEvent()==23) {
							List<String> scopeItemList = new ArrayList<String>();
							//scopeItemList.add("您申請的"+portalProvider.getName()+"的"+ps.getName()+"服務已取用。");
							ulogExt.setProviderName(portalProvider.getName());
							ulogExt.setServiceName(ps.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("驗證");
						}else if(ulog.getAuditEvent()!=null&&ulog.getAuditEvent()==24) {
							List<String> scopeItemList = new ArrayList<String>();
							//scopeItemList.add("您申請的"+portalProvider.getName()+"的"+ps.getName()+"服務已取用。");
							ulogExt.setProviderName(portalProvider.getName());
							ulogExt.setServiceName(ps.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("傳輸");
						}else if(ulog.getAuditEvent()!=null&&ulog.getAuditEvent()==25) {
							List<String> scopeItemList = new ArrayList<String>();
							//scopeItemList.add("您申請的"+portalProvider.getName()+"的"+ps.getName()+"服務已取用。");
							ulogExt.setProviderName(portalProvider.getName());
							ulogExt.setServiceName(ps.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("儲存");
						}else if(ulog.getAuditEvent()!=null&&ulog.getAuditEvent()==26) {
							List<String> scopeItemList = new ArrayList<String>();
							//scopeItemList.add("您申請的"+portalProvider.getName()+"的"+ps.getName()+"服務已取用。");
							ulogExt.setProviderName(portalProvider.getName());
							ulogExt.setServiceName(ps.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("臨櫃核驗");
						}else if(ulog.getAuditEvent()!=null&&ulog.getAuditEvent()==27) {
							List<String> scopeItemList = new ArrayList<String>();
							//scopeItemList.add("您申請的"+portalProvider.getName()+"的"+ps.getName()+"服務已取用。");
							ulogExt.setProviderName(portalProvider.getName());
							ulogExt.setServiceName(ps.getName());
							ulogExt.setScopeItemList(scopeItemList);
							ulogExt.setActionStr("服務應用");
						}
						if(ulog.getCtime()!=null) {
							ulogExt.setCtimeStr(DateFormatUtils.format(ulog.getCtime(), "yyyy年MM月dd日"));
							ulogExt.setCtimeStr1(DateFormatUtils.format(ulog.getCtime(), "HH時mm分ss秒"));
						}
						if(ulogExt.getAuditEvent()==21) {
							try {
								BeanUtils.copyProperties(titleExt, ulogExt);
								titleExt.setActionStr("申請");
								List<String> scopeItemList = new ArrayList<String>();
								//scopeItemList.add("您申請"+titleExt.getProviderName()+"的"+titleExt.getServiceName()+"。");
								//titleExt.setScopeItemList(scopeItemList);
								UlogApiExtComparator comparator1 = new UlogApiExtComparator();
								Collections.sort(ulogExtMiddleList, comparator1);
								titleExt.setUlogApiExtList(ulogExtMiddleList);
								/**
								 * 當遇到ulogExt.getAuditEvent()==11 為一個完整資料歷程
								 */
								ulogExtList.add(titleExt);
								ulogExtMiddleList = new ArrayList<UlogApiExt>();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
						}else {
							ulogExtMiddleList.add(ulogExt);
						}
					}
					psExt.setUlogList(ulogExtList);
					psExtList.add(psExt);
				}
			}
		}
		model.addAttribute("psExtList", psExtList);
		if(psExtList!=null&&psExtList.size()>0) {
			for(PortalServiceExt psext:psExtList) {
				System.out.println("===================psext=======================");
				System.out.println(psext.getName());
				System.out.println("psext.getUlogList().size()="+psext.getUlogList().size());
				if(psext.getUlogList()!=null&&psext.getUlogList().size()>0) {
					for(UlogApiExt u:psext.getUlogList()) {
						System.out.println("---- "+u.getAuditEvent()+" ------");
						System.out.println("---- "+u.getUlogApiExtList().size()+" ------");
					}					
				}
			}
		}
		/**
		 * dp download log
		 */
		List<Integer> dpPrIdList = new ArrayList<Integer>(); 
		/*if(dpScopeList!=null&&dpScopeList.size()>0) {
			for(String s:dpScopeList) {
				PortalResourceScopeExample portalResourceScopeExample = new PortalResourceScopeExample();
				portalResourceScopeExample.createCriteria().andScopeEqualTo(s);
				Map<String,Object> sparam = new HashMap<String,Object>();
				sparam.put("scope", s);
				List<PortalResourceScope> portalResourceScopeList = portalResourceScopeMapper.selectByExample(sparam);
				if(portalResourceScopeList!=null&&portalResourceScopeList.size()>0) {
					if(!dpPrIdList.contains(portalResourceScopeList.get(0).getPrId())) {
						dpPrIdList.add(portalResourceScopeList.get(0).getPrId());
					}
				}
			}
		}*/
		List<PortalResourceExt> tmpPrExtList = portalResourceExtMapper.selectPortalResourceWithCateName();
		if(tmpPrExtList!=null&&tmpPrExtList.size()>0) {
			for(PortalResourceExt tpr:tmpPrExtList) {
				dpPrIdList.add(tpr.getPrId());
			}
		}
		List<PortalResourceExt> prExtList = new ArrayList<>();
		if(dpPrIdList!=null&&dpPrIdList.size()>0) {
			for(Integer prId:dpPrIdList){
				PortalResource portalResource = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prId));
				ulogExtList = new ArrayList<UlogApiExt>();
				if(portalResource!=null) {
					PortalResourceExt prExt = new PortalResourceExt();
					try {
							BeanUtils.copyProperties(prExt, portalResource);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					//providerName
					PortalProvider portalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalResource.getProviderId()));
					prExt.setProviderName(portalProvider.getName());
					prExt.setOid(portalProvider.getOid());
					List<UlogApi> ulogList = null;
					if(sr!=null&&sr.getMember().getAccount()!=null) {
						ulogList = ulogApiMapperExt.getLogByAccountAndResourceIdAndAuditEventForResource(SessionMember.getSessionMemberToMember(sr.getMember()).getAccount(), ValidatorHelper.removeSpecialCharacters(portalResource.getResourceId()));
					}
					ulogExtMiddleList = new ArrayList<UlogApiExt>();
					if(ulogList != null && ulogList.size() >0) {
						for(UlogApi ulog : ulogList) {
							UlogApiExt titleExt = new UlogApiExt();
							UlogApiExt ulogExt = new UlogApiExt();
							try {
								BeanUtils.copyProperties(ulogExt, ulog);
							} catch (IllegalAccessException | InvocationTargetException e) {
								e.printStackTrace();
							}
							/**
							 * 資料集：
							 * 11 申請：您申請此筆資料/服務 
							 * 12 同意：您同意服務條款
							 * 13 驗證：您完成身分驗證
							 * 14 傳輸：您同意 MyData 傳送資料給服務提供者
							 * 15 儲存：您下載資料
							 * 16 條碼取用：機關取用資料
							 * 17 服務應用：MyData 將資料傳送給服務提供者
							 */
							
							if(ulogExt.getClientId()!=null&&ulogExt.getClientId().trim().length()>0) {
								if(ulogExt.getClientId()!=null&&ulogExt.getClientId().equalsIgnoreCase("CLI.mydata.portal")) {
									if(ulog.getAuditEvent()!=null&&ulog.getAuditEvent()==11) {
										List<String> scopeItemList = new ArrayList<String>();
										//scopeItemList.add("您要求"+portalProvider.getName()+"提供您的"+portalResource.getName()+"。");
										ulogExt.setProviderName(portalProvider.getName());
										ulogExt.setResourceName(portalResource.getName());
										ulogExt.setScopeItemList(scopeItemList);
										ulogExt.setActionStr("申請");
									}else if(ulog.getAuditEvent()!=null&&ulog.getAuditEvent()==12) {
										List<String> scopeItemList = new ArrayList<String>();
										//scopeItemList.add(portalProvider.getName()+"傳送"+portalResource.getName()+"給您。");
										ulogExt.setProviderName(portalProvider.getName());
										ulogExt.setResourceName(portalResource.getName());
										ulogExt.setScopeItemList(scopeItemList);
										ulogExt.setActionStr("同意");
									}else if(ulog.getAuditEvent()!=null&&ulog.getAuditEvent()==13) {
										List<String> scopeItemList = new ArrayList<String>();
										//scopeItemList.add("您已儲存"+portalProvider.getName()+"的"+portalResource.getName()+"。");
										ulogExt.setProviderName(portalProvider.getName());
										ulogExt.setResourceName(portalResource.getName());
										ulogExt.setScopeItemList(scopeItemList);
										ulogExt.setActionStr("驗證");
									}else if(ulog.getAuditEvent()!=null&&ulog.getAuditEvent()==14) {
										List<String> scopeItemList = new ArrayList<String>();
										//scopeItemList.add("您已取用"+portalProvider.getName()+"的"+portalResource.getName()+"。");
										ulogExt.setProviderName(portalProvider.getName());
										ulogExt.setResourceName(portalResource.getName());
										ulogExt.setScopeItemList(scopeItemList);
										ulogExt.setActionStr("傳輸");
									}else if(ulog.getAuditEvent()!=null&&ulog.getAuditEvent()==15) {
										List<String> scopeItemList = new ArrayList<String>();
										//scopeItemList.add("您已取用"+portalProvider.getName()+"的"+portalResource.getName()+"。");
										ulogExt.setProviderName(portalProvider.getName());
										ulogExt.setResourceName(portalResource.getName());
										ulogExt.setScopeItemList(scopeItemList);
										ulogExt.setActionStr("儲存");
									}else if(ulog.getAuditEvent()!=null&&ulog.getAuditEvent()==16) {
										List<String> scopeItemList = new ArrayList<String>();
										//scopeItemList.add("您已取用"+portalProvider.getName()+"的"+portalResource.getName()+"。");
										ulogExt.setProviderName(portalProvider.getName());
										ulogExt.setResourceName(portalResource.getName());
										ulogExt.setScopeItemList(scopeItemList);
										ulogExt.setActionStr("臨櫃核驗");
									}else if(ulog.getAuditEvent()!=null&&ulog.getAuditEvent()==17) {
										List<String> scopeItemList = new ArrayList<String>();
										//scopeItemList.add("您已取用"+portalProvider.getName()+"的"+portalResource.getName()+"。");
										ulogExt.setProviderName(portalProvider.getName());
										ulogExt.setResourceName(portalResource.getName());
										ulogExt.setScopeItemList(scopeItemList);
										ulogExt.setActionStr("服務應用");
									}
								}else if(ulogExt.getClientId()!=null) {
									/*PortalServiceExample portalServiceExample = new PortalServiceExample();
									portalServiceExample.createCriteria().andClientIdEqualTo(ulogExt.getClientId());*/
									Map<String,Object> psparam = new HashMap<String,Object>();
									psparam.put("clientId", ulogExt.getClientId());
									List<PortalService> tmpPortalServiceList = portalServiceMapper.selectByExample(psparam);
									if(tmpPortalServiceList!=null&&tmpPortalServiceList.size()>0) {
										PortalService tmpPortalService= tmpPortalServiceList.get(0);
										PortalProvider psPortalProvider = portalProviderMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(tmpPortalService.getProviderId()));	
										/**
										 * 資料集：
										 * 11 申請：您申請此筆資料/服務 
										 * 12 同意：您同意服務條款
										 * 13 驗證：您完成身分驗證
										 * 14 傳輸：您同意 MyData 傳送資料給服務提供者
										 * 15 儲存：您下載資料
										 * 16 條碼取用：機關取用資料
										 * 17 服務應用：MyData 將資料傳送給服務提供者
										 */
										if(ulog.getAuditEvent()!=null&&ulog.getAuditEvent()==11) {
											List<String> scopeItemList = new ArrayList<String>();
											//scopeItemList.add("您要求"+portalProvider.getName()+"提供您的"+portalResource.getName()+"。");
											ulogExt.setProviderName(portalProvider.getName());
											ulogExt.setResourceName(portalResource.getName());
											ulogExt.setScopeItemList(scopeItemList);
											ulogExt.setActionStr("申請");
										}else if(ulog.getAuditEvent()!=null&&ulog.getAuditEvent()==12) {
											List<String> scopeItemList = new ArrayList<String>();
											//scopeItemList.add(portalProvider.getName()+"傳送"+portalResource.getName()+"給您。");
											ulogExt.setProviderName(portalProvider.getName());
											ulogExt.setResourceName(portalResource.getName());
											ulogExt.setScopeItemList(scopeItemList);
											ulogExt.setActionStr("同意");
										}else if(ulog.getAuditEvent()!=null&&ulog.getAuditEvent()==13) {
											List<String> scopeItemList = new ArrayList<String>();
											//scopeItemList.add("您已儲存"+portalProvider.getName()+"的"+portalResource.getName()+"。");
											ulogExt.setProviderName(portalProvider.getName());
											ulogExt.setResourceName(portalResource.getName());
											ulogExt.setScopeItemList(scopeItemList);
											ulogExt.setActionStr("驗證");
										}else if(ulog.getAuditEvent()!=null&&ulog.getAuditEvent()==14) {
											List<String> scopeItemList = new ArrayList<String>();
											//scopeItemList.add("您已取用"+portalProvider.getName()+"的"+portalResource.getName()+"。");
											ulogExt.setProviderName(portalProvider.getName());
											ulogExt.setResourceName(portalResource.getName());
											ulogExt.setScopeItemList(scopeItemList);
											ulogExt.setActionStr("傳輸");
										}else if(ulog.getAuditEvent()!=null&&ulog.getAuditEvent()==15) {
											List<String> scopeItemList = new ArrayList<String>();
											//scopeItemList.add("您已取用"+portalProvider.getName()+"的"+portalResource.getName()+"。");
											ulogExt.setProviderName(portalProvider.getName());
											ulogExt.setResourceName(portalResource.getName());
											ulogExt.setScopeItemList(scopeItemList);
											ulogExt.setActionStr("儲存");
										}else if(ulog.getAuditEvent()!=null&&ulog.getAuditEvent()==16) {
											List<String> scopeItemList = new ArrayList<String>();
											//scopeItemList.add("您已取用"+portalProvider.getName()+"的"+portalResource.getName()+"。");
											ulogExt.setProviderName(portalProvider.getName());
											ulogExt.setResourceName(portalResource.getName());
											ulogExt.setScopeItemList(scopeItemList);
											ulogExt.setActionStr("臨櫃核驗");
										}else if(ulog.getAuditEvent()!=null&&ulog.getAuditEvent()==17) {
											List<String> scopeItemList = new ArrayList<String>();
											//scopeItemList.add("您已取用"+portalProvider.getName()+"的"+portalResource.getName()+"。");
											ulogExt.setProviderName(portalProvider.getName());
											ulogExt.setResourceName(portalResource.getName());
											ulogExt.setScopeItemList(scopeItemList);
											ulogExt.setActionStr("服務應用");
										}
									}
								}
							}
							if(ulog.getCtime()!=null) {
								ulogExt.setCtimeStr(DateFormatUtils.format(ulog.getCtime(), "yyyy年MM月dd日"));
								ulogExt.setCtimeStr1(DateFormatUtils.format(ulog.getCtime(), "HH時mm分ss秒"));
							}
							
							if(ulogExt.getAuditEvent()==11) {
								try {
									BeanUtils.copyProperties(titleExt, ulogExt);
									titleExt.setActionStr("申請");
									List<String> scopeItemList = new ArrayList<String>();
									scopeItemList.add("您申請下載"+titleExt.getProviderName()+"的"+titleExt.getResourceName()+"。");
									titleExt.setScopeItemList(scopeItemList);
									UlogApiExtComparator comparator1 = new UlogApiExtComparator();
									Collections.sort(ulogExtMiddleList, comparator1);
									titleExt.setUlogApiExtList(ulogExtMiddleList);
									/**
									 * 當遇到ulogExt.getAuditEvent()==4 為一個完整資料歷程
									 */
									ulogExtList.add(titleExt);
									ulogExtMiddleList = new ArrayList<UlogApiExt>();
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								} catch (InvocationTargetException e) {
									e.printStackTrace();
								}
							}else {
								ulogExtMiddleList.add(ulogExt);
							}
						}
					}
					if(ulogExtList!=null&&ulogExtList.size()>0&&ulogExtList.get(0).getUlogApiExtList().size()>0) {
						prExt.setUlogList(ulogExtList);
						prExtList.add(prExt);
					}
				}
			}
		}
		if(prExtList!=null&&prExtList.size()>0) {
			PortalResourceExtComparator comparator = new PortalResourceExtComparator();
			Collections.sort(prExtList, comparator);
		}
		System.out.println("--------------------prExtList  size........:" + prExtList.size());
		model.addAttribute("prExtList", prExtList);
		
		//MyBox
		/*List<PortalBoxLogExt> portalBoxLogExtList = new ArrayList<PortalBoxLogExt>();
		List<PortalBoxLogExt> finalPortalBoxLogExtList = new ArrayList<PortalBoxLogExt>();
		if(userInfoEntity != null) {
			portalBoxLogExtList = portalResourceExtMapper.selectMyBoxLogByAccount(userInfoEntity.getAccount());
			//System.out.println("portalBoxLogExtList size="+portalBoxLogExtList.size());
			if(portalBoxLogExtList!=null&&portalBoxLogExtList.size()>0) {
				for(PortalBoxLogExt plog:portalBoxLogExtList) {
					PortalBoxLogExt bean = new PortalBoxLogExt();
					try {
						BeanUtils.copyProperties(bean, plog);
						bean.setCtimeStr(DateFormatUtils.format(plog.getCtime(), "yyyy年MM月dd日"));
						bean.setCtimeStr1(DateFormatUtils.format(plog.getCtime(), "HH時mm分ss秒"));
						finalPortalBoxLogExtList.add(bean);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
		model.addAttribute("finalPortalBoxLogExtList", finalPortalBoxLogExtList);*/
		
		return "authorization";
	}
	
	public class PortalResourceExtComparator implements Comparator<PortalResourceExt> {
	    public int compare(PortalResourceExt obj1, PortalResourceExt obj2) {
	        //return obj1.getOid().compareTo(obj2.getOid());
	        int value1 = obj1.getOid().compareTo(obj2.getOid());
	        if (value1 == 0) {
	            int value2 = obj1.getPrId().compareTo(obj2.getPrId());
	            return value2;
	        }else {
	        		return value1;
	        }
	    }
	}
	
	public class FinalPortalBoxLogExtComparator implements Comparator<PortalBoxLogExt> {
		public int compare(PortalBoxLogExt obj1, PortalBoxLogExt obj2) {
			int value1 = obj1.getId().compareTo(obj2.getId());
			return value1;
		}
	}
	
	public static class UlogApiExtComparator implements Comparator<UlogApiExt> {
		public int compare(UlogApiExt obj1, UlogApiExt obj2) {
			int value1 = obj1.getId().compareTo(obj2.getId());
			return value1;
		}
	}
}
