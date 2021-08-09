package tw.gov.ndc.emsg.mydata.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.riease.common.sysinit.SysCode;
import com.riease.common.sysinit.controller.BaseRestController;
import com.riease.common.sysinit.rest.RestResponseBean;

import tw.gov.ndc.emsg.mydata.entity.PortalResource;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceExample;
import tw.gov.ndc.emsg.mydata.entity.PortalService;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceExample;
import tw.gov.ndc.emsg.mydata.entity.UlogApi;
import tw.gov.ndc.emsg.mydata.entity.UlogApiExample;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalServiceMapper;
import tw.gov.ndc.emsg.mydata.mapper.UlogApiMapper;

/**
 * 目前暫時Mark掉，當正式開放時再全部開啟，並移除mydatalog project
 * 因為有ip控制會造成有些舊的資料集連線進不來，會異常
 * 所以拆成兩個專案，而mydatalog project允許所有連線進入
 * @author Weder
 */
@Controller
@RequestMapping("/mydatalog/v01")
public class MyDataLogRestController extends BaseRestController {
	private static Logger logger = LoggerFactory.getLogger(MyDataLogRestController.class);
	private final Base64.Encoder encoder = Base64.getEncoder();
	private final Base64.Decoder decoder = Base64.getDecoder();
	
	@Value("${gsp.oidc.client.id}")
	private String gspClientId;		
	@Value("${gsp.oidc.client.secret}")
	private String gspClientSecret;	
	@Autowired
	private UlogApiMapper ulogApiMapper;
	@Autowired
	private PortalResourceMapper portalResourceMapper;
	@Autowired
	private PortalServiceMapper portalServiceMapper;	
	
	@GetMapping("/log")
	public ResponseEntity<RestResponseBean> getLogByPortalProvider(
			@RequestHeader("Authorization") String authorization,
			HttpServletRequest request) {
			if(authorization!=null&&authorization.trim().length()>0) {
				String[] authorizations = authorization.trim().split("[,|\\s]");
				if(authorizations!=null&&authorizations.length==2) {
					String authorization1 = authorizations[0];
					String authorization2 = authorizations[1];
					System.out.println("basicAuthenticationSchema="+basicAuthenticationSchema(gspClientId,gspClientSecret));
					if(authorization1.equals("Basic")) {
						//gsp gspClientId,gspClientSecret
						if(authorization2.equals(basicAuthenticationSchema(gspClientId,gspClientSecret))) {
							//login.cp.gv.tw
							//UlogApiExample ulogApiExample = new UlogApiExample();
							List<Integer> auditEventList = new ArrayList<Integer>();
							auditEventList.add(1);
							auditEventList.add(2);
							auditEventList.add(3);
							auditEventList.add(7);
							/*ulogApiExample.createCriteria().andAuditEventIn(auditEventList);
							ulogApiExample.setOrderByClause("ctime desc");*/
							Map<String,Object> ulogparam = new HashMap<String,Object>();
							ulogparam.put("auditEventList", auditEventList);
							ulogparam.put("ctimeDesc", true);
							List<UlogApi> UlogApiLst = ulogApiMapper.selectByExampleWithRowbounds(ulogparam, new RowBounds(0,100));
							return responseOK(UlogApiLst);
						}else {
							//other
							//dp check
							/*PortalResourceExample portalResourceExample = new PortalResourceExample();*/
							Map<String,Object> rparam = new HashMap<String,Object>();
							List<PortalResource> portalResourceList = portalResourceMapper.selectByExample(rparam);
							if(portalResourceList!=null&&portalResourceList.size()>0) {
								for(PortalResource pr:portalResourceList) {
									String basicAuthenticationSchemaStr = basicAuthenticationSchema(pr.getResourceId(),pr.getResourceSecret());
									if(authorization2.equals(basicAuthenticationSchemaStr)) {
										//UlogApiExample ulogApiExample = new UlogApiExample();
										List<Integer> auditEventList = new ArrayList<Integer>();
										auditEventList.add(5);
										/*ulogApiExample.createCriteria().andResourceIdEqualTo(pr.getResourceId()).andAuditEventIn(auditEventList);
										ulogApiExample.setOrderByClause("ctime desc");*/
										Map<String,Object> ulogparam = new HashMap<>();
										ulogparam.put("auditEventList", auditEventList);
										ulogparam.put("ctimeDesc", true);
										List<UlogApi> UlogApiLst = ulogApiMapper.selectByExampleWithRowbounds(ulogparam, new RowBounds(0,100));
										return responseOK(UlogApiLst);
									}
								}
							}
							//sp check
							/*PortalServiceExample portalServiceExample = new PortalServiceExample();*/
							Map<String,Object> psparam = new HashMap<String,Object>();
							List<PortalService>  portalServiceList = portalServiceMapper.selectByExample(psparam);
							if(portalServiceList!=null&&portalServiceList.size()>0) {
								for(PortalService ps:portalServiceList) {
									String basicAuthenticationSchemaStr = basicAuthenticationSchema(ps.getClientId(),ps.getClientSecret());
									if(authorization2.equals(basicAuthenticationSchemaStr)) {
										//UlogApiExample ulogApiExample = new UlogApiExample();
										List<Integer> auditEventList = new ArrayList<Integer>();
										auditEventList.add(4);
										auditEventList.add(6);
										/*ulogApiExample.createCriteria().andClientIdEqualTo(ps.getClientId()).andAuditEventIn(auditEventList);
										ulogApiExample.setOrderByClause("ctime desc");*/
										Map<String,Object> ulogparam = new HashMap<>();
										ulogparam.put("auditEventList", auditEventList);
										ulogparam.put("ctimeDesc", true);
										List<UlogApi> UlogApiLst = ulogApiMapper.selectByExampleWithRowbounds(ulogparam, new RowBounds(0,100));
										return responseOK(UlogApiLst);
									}
								}
							}
						}
				}else {
					return responseError(SysCode.AuthenticateFail,"Authorization","驗證失敗");
				}
			}else {
				return responseError(SysCode.AuthenticateFail,"Authorization","驗證失敗");
			}
		}else {
			return responseError(SysCode.AuthenticateFail,"Authorization","驗證失敗");
		}
		return responseOK();
	}
	
	@PostMapping("/log")
	public ResponseEntity<RestResponseBean> newOrUpdatePortalProvider(
			@RequestHeader("Authorization") String authorization,
			@RequestBody Map<String,Object> params,
			HttpServletRequest request) {
		System.out.println("Authorization=\n"+authorization);
		if(authorization!=null&&authorization.trim().length()>0) {
			String[] authorizations = authorization.trim().split("[,|\\s]");
			if(authorizations!=null&&authorizations.length==2) {
				boolean recordog = false;
				String authorization1 = authorizations[0];
				String authorization2 = authorizations[1];
				System.out.println("authorization1="+authorization1);
				System.out.println("authorization2="+authorization2);
				System.out.println("basicAuthenticationSchema="+basicAuthenticationSchema(gspClientId,gspClientSecret));
				if(authorization1.equals("Basic")) {
					//gsp gspClientId,gspClientSecret
					if(authorization2.equals(basicAuthenticationSchema(gspClientId,gspClientSecret))) {
						//由GSP回寫log
						UlogApi record = new UlogApi();
						//egov account
						if(params.get("providerKey")!=null&&params.get("providerKey").toString().trim().length()>0
							&& params.get("auditEvent")!=null&&params.get("auditEvent").toString().trim().length()>0) {
							if(params.get("providerKey")!=null&&params.get("providerKey").toString().trim().length()>0) {
								record.setProviderKey(params.get("providerKey").toString().trim());
							}
							if(params.get("userName")!=null&&params.get("userName").toString().trim().length()>0) {
								record.setUserName(params.get("userName").toString().trim());
							}
							if(params.get("uid")!=null&&params.get("uid").toString().trim().length()>0) {
								record.setUid(params.get("uid").toString().trim());
							}
							if(params.get("clientId")!=null&&params.get("clientId").toString().trim().length()>0) {
								record.setClientId(params.get("clientId").toString().trim());
							}
							if(params.get("auditEvent")!=null&&params.get("auditEvent").toString().trim().length()>0) {
								record.setAuditEvent(Integer.valueOf(params.get("auditEvent").toString().trim()));
							}
							if(params.get("scope")!=null&&params.get("scope").toString().trim().length()>0) {
								record.setScope(params.get("scope").toString().trim());
							}
							if(params.get("ip")!=null&&params.get("ip").toString().trim().length()>0) {
								record.setIp(params.get("ip").toString().trim());
							}
							record.setCtime(new Date());
							ulogApiMapper.insertSelective(record);
							recordog = true;
						}
					}else {
						//dp check
						/*PortalResourceExample portalResourceExample = new PortalResourceExample();*/
						Map<String,Object> rparam = new HashMap<String,Object>();
						List<PortalResource> portalResourceList = portalResourceMapper.selectByExample(rparam);
						if(portalResourceList!=null&&portalResourceList.size()>0) {
							for(PortalResource pr:portalResourceList) {
								String basicAuthenticationSchemaStr = basicAuthenticationSchema(pr.getResourceId(),pr.getResourceSecret());
								if(authorization2.equals(basicAuthenticationSchemaStr)) {
									//由dp回寫log
									UlogApi record = new UlogApi();
									//egov account
									if(params.get("providerKey")!=null&&params.get("providerKey").toString().trim().length()>0
											&& params.get("auditEvent")!=null&&params.get("auditEvent").toString().trim().length()>0) {
										if(params.get("providerKey")!=null&&params.get("providerKey").toString().trim().length()>0) {
											record.setProviderKey(params.get("providerKey").toString().trim());
										}
										if(params.get("userName")!=null&&params.get("userName").toString().trim().length()>0) {
											record.setUserName(params.get("userName").toString().trim());
										}
										if(params.get("uid")!=null&&params.get("uid").toString().trim().length()>0) {
											record.setUid(params.get("uid").toString().trim());
										}
										if(params.get("clientId")!=null&&params.get("clientId").toString().trim().length()>0) {
											record.setClientId(params.get("clientId").toString().trim());
										}
										if(params.get("resourceId")!=null&&params.get("resourceId").toString().trim().length()>0) {
											record.setResourceId(params.get("resourceId").toString().trim());
										}
										if(params.get("auditEvent")!=null&&params.get("auditEvent").toString().trim().length()>0) {
											record.setAuditEvent(Integer.valueOf(params.get("auditEvent").toString().trim()));
										}
										if(params.get("scope")!=null&&params.get("scope").toString().trim().length()>0) {
											record.setScope(params.get("scope").toString().trim());
										}
										if(params.get("ip")!=null&&params.get("ip").toString().trim().length()>0) {
											record.setIp(params.get("ip").toString().trim());
										}
										record.setCtime(new Date());
										//ulogApiMapper.insertSelective(record);
										recordog = true;
									}
								}
							}
						}
						//sp check
						/*PortalServiceExample portalServiceExample = new PortalServiceExample();*/
						Map<String,Object> psparam = new HashMap<String,Object>();
						List<PortalService>  portalServiceList = portalServiceMapper.selectByExample(psparam);
						if(portalServiceList!=null&&portalServiceList.size()>0) {
							for(PortalService ps:portalServiceList) {
								String basicAuthenticationSchemaStr = basicAuthenticationSchema(ps.getClientId(),ps.getClientSecret());
								if(authorization2.equals(basicAuthenticationSchemaStr)) {
									//由sp回寫log
									UlogApi record = new UlogApi();
									//egov account
									if(params.get("providerKey")!=null&&params.get("providerKey").toString().trim().length()>0
										&& params.get("auditEvent")!=null&&params.get("auditEvent").toString().trim().length()>0) {
										if(params.get("providerKey")!=null&&params.get("providerKey").toString().trim().length()>0) {
											record.setProviderKey(params.get("providerKey").toString());
										}
										if(params.get("userName")!=null&&params.get("userName").toString().trim().length()>0) {
											record.setUserName(params.get("userName").toString().trim());
										}
										if(params.get("uid")!=null&&params.get("uid").toString().trim().length()>0) {
											record.setUid(params.get("uid").toString().trim());
										}
										if(params.get("clientId")!=null&&params.get("clientId").toString().trim().length()>0) {
											record.setClientId(params.get("clientId").toString().trim());
										}
										if(params.get("resourceId")!=null&&params.get("resourceId").toString().trim().length()>0) {
											record.setResourceId(params.get("resourceId").toString().trim());
										}
										if(params.get("auditEvent")!=null&&params.get("auditEvent").toString().trim().length()>0) {
											record.setAuditEvent(Integer.valueOf(params.get("auditEvent").toString().trim()));
										}
										if(params.get("scope")!=null&&params.get("scope").toString().trim().length()>0) {
											record.setScope(params.get("scope").toString().trim());
										}
										if(params.get("ip")!=null&&params.get("ip").toString().trim().length()>0) {
											record.setIp(params.get("ip").toString().trim());
										}
										//record.setAction(action);
										//record.setActionText(actionText);
										record.setCtime(new Date());
										ulogApiMapper.insertSelective(record);
										recordog = true;
									}
								}
							}
						}
					}
					if(!recordog) {
						return responseError(SysCode.AuthenticateFail,"Authorization","驗證失敗");
					}
				}else {
					return responseError(SysCode.AuthenticateFail,"Authorization","驗證失敗");
				}
			}else {
				return responseError(SysCode.AuthenticateFail,"Authorization","驗證失敗");
			}
		}else {
			return responseError(SysCode.AuthenticateFail,"Authorization","驗證失敗");
		}
		
		return responseOK();
	}
	
	private String basicAuthenticationSchema(String clientId, String clientSecret) {
		StringBuilder sb = new StringBuilder();
		sb.append(clientId).append(":").append(clientSecret);
		try {
			//return encoder.encodeToString(sb.toString().getBytes("UTF-8"));
			return encoder.encodeToString(sb.toString().getBytes());
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}	
}
