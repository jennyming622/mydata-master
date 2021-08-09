package tw.gov.ndc.emsg.mydata.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.riease.common.helper.HttpHelper;
import com.riease.common.helper.ValidatorHelper;

import tw.gov.ndc.emsg.mydata.entity.PortalServiceAllowIp;
import tw.gov.ndc.emsg.mydata.entity.UlogApi;
import tw.gov.ndc.emsg.mydata.mapper.PortalServiceAllowIpMapper;
import tw.gov.ndc.emsg.mydata.mapper.ext.UlogApiMapperExt;

@Controller
@RequestMapping("/log")
public class LogController {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat sdf6 = new SimpleDateFormat("yyyyMMdd");
	@Autowired private UlogApiMapperExt ulogApiMapperExt;
	@Autowired 
	private PortalServiceAllowIpMapper portalServiceAllowIpMapper;
	
	@PostMapping("/sp")
	public void postSpLog(
			@RequestBody Map<String, Object> params,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException, ParseException {
	    /**
	     * 允許連線IP
	     */
	    /*boolean checkIpIn = false;
	    String ip = HttpHelper.getRemoteIp(request);
	    Map<String,Object> psparam = new HashMap<String,Object>();
	    List<PortalServiceAllowIp> portalServiceAllowIpList = portalServiceAllowIpMapper.selectByExample(psparam);
	    if(portalServiceAllowIpList!=null&&portalServiceAllowIpList.size()>0) {
	    		for(PortalServiceAllowIp p:portalServiceAllowIpList) {
	    			if(p!=null&&p.getIp()!=null&&p.getIp().trim().equalsIgnoreCase(ip.trim())) {
	    				checkIpIn = true;
	    			}
	    		}
	    }
	    if(!checkIpIn) {
	    		*//**
	    		 * 401 Unauthorized
	    		 *//*
	    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    		return;
	    }*/
		String outStr = "";

        String client_id = "";
        String stime = "";
        String etime = "";
        List<String> txIdList = new ArrayList<String>();
        List<String> eventList = new ArrayList<String>();
        if (params.get("client_id") != null) {
        		client_id = params.get("client_id").toString();
        }
        if (params.get("stime") != null) {
        		stime = params.get("stime").toString();
        }
        if (params.get("etime") != null) {
        		etime = params.get("etime").toString();
	    }
        if (params.get("tx_id") != null) {
        		txIdList = (List<String>) params.get("tx_id");
	    }
        if (params.get("event") != null) {
        		eventList = (List<String>) params.get("event");
	    }
        if(params.get("stime") != null && params.get("etime") != null) {
        		if(params.get("client_id") != null && txIdList!=null) {
        			Map<String,Object> param = new HashMap<String,Object>();
        			param.put("client_id", client_id);
        			param.put("stime", sdf1.parse(stime+" 00:00:00"));
        			param.put("etime", sdf1.parse(etime+" 23:59:59"));
        			if(txIdList!=null&&txIdList.size()>0) {
        				param.put("tx_id", txIdList);
        			}
        			if(eventList!=null&&eventList.size()>0) {
        				param.put("eventList", eventList);
        			}
        			List<UlogApi> UlogApiList = ulogApiMapperExt.getLogByExample(param);
        			outStr = "{\"client_id\":\""+client_id+"\",";
        			outStr = outStr + "\"data\":[";
        			System.out.println("outStr1 =\n"+outStr);
        			if(UlogApiList!=null&&UlogApiList.size()>0) {
        				for(int i=0;i<UlogApiList.size();i++) {
        					if(i==(UlogApiList.size()-1)) {
        						UlogApi u = UlogApiList.get(i);
        						if(u.getAction()!=null) {
                					outStr = outStr + "{";
                					outStr = outStr + "\"tx_id\":\""+ValidatorHelper.removeSpecialCharacters(u.getTxId())+"\",";
                					outStr = outStr + "\"ctime\":\""+sdf1.format(u.getCtime())+"\",";
                					outStr = outStr + "\"event\":\""+(u.getAction()==null?"":ValidatorHelper.removeSpecialCharacters(u.getAction()))+"\",";
                					outStr = outStr + "\"ip\":\""+(u.getIp()==null?"":ValidatorHelper.removeSpecialCharacters(u.getIp()))+"\",";
                					outStr = outStr + "\"resource_id\":\""+(u.getResourceId()==null?"":ValidatorHelper.removeSpecialCharacters(u.getResourceId()))+"\"";
                					outStr = outStr + "}";
        						}            					
        					}else {
        						UlogApi u = UlogApiList.get(i);
        						if(u.getAction()!=null) {
                					outStr = outStr + "{";
                					outStr = outStr + "\"tx_id\":\""+ValidatorHelper.removeSpecialCharacters(u.getTxId())+"\",";
                					outStr = outStr + "\"ctime\":\""+sdf1.format(u.getCtime())+"\",";
                					outStr = outStr + "\"event\":\""+(u.getAction()==null?"":ValidatorHelper.removeSpecialCharacters(u.getAction()))+"\",";
                					outStr = outStr + "\"ip\":\""+(u.getIp()==null?"":ValidatorHelper.removeSpecialCharacters(u.getIp()))+"\",";
                					outStr = outStr + "\"resource_id\":\""+(u.getResourceId()==null?"":ValidatorHelper.removeSpecialCharacters(u.getResourceId()))+"\"";
                					outStr = outStr + "},";
        						}
        					}
        				}
        				
        			}
        			outStr = outStr + "]}";
	    	    		response.setStatus(HttpServletResponse.SC_OK);
	    	    		response.setCharacterEncoding("UTF-8");
	    	    		response.setContentType("application/jwe; charset=UTF-8");
	    	    		PrintWriter out = response.getWriter();
	    	    		out.print(outStr);
	    	    		out.close();	
        		}else {
	    	    		/**
	    	    		 * 拒絕存取。參數（tx_id, client_id）不存在。
	    	    		 */
	    	    		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        		}
        }else {
	    		/**
	    		 * 400 必要參數不存在
	    		 */
	    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
	}
	
	@PostMapping("/dp")
	public void postDpLog(
			@RequestBody Map<String, Object> params,
            HttpServletRequest request,
			HttpServletResponse response) throws IOException, ParseException {

		String resource_id = "";
		String stime = "";
		String etime = "";
		List<String> transactionUidList = new ArrayList<String>();
		List<String> eventList = new ArrayList<String>();
		if (params.get("resource_id") != null) {
			resource_id = params.get("resource_id").toString();
		}
		if (params.get("stime") != null) {
			stime = params.get("stime").toString();
		}
		if (params.get("etime") != null) {
			etime = params.get("etime").toString();
		}
		if (params.get("transaction_uid") != null) {
			transactionUidList = (List<String>) params.get("transaction_uid");
		}
		if (params.get("event") != null) {
			eventList = (List<String>) params.get("event");
		}
		if(params.get("stime") != null && params.get("etime") != null) {
			if(params.get("resource_id") != null && transactionUidList!=null) {
				Map<String,Object> param = new HashMap<String,Object>();
				param.put("resource_id", resource_id);
				param.put("stime", sdf1.parse(stime+" 00:00:00"));
				param.put("etime", sdf1.parse(etime+" 23:59:59"));
				if(transactionUidList!=null&&transactionUidList.size()>0) {
					param.put("transaction_uid", transactionUidList);
				}
				if(eventList!=null&&eventList.size()>0) {
					param.put("eventList", eventList);
				} else {
					param.put("eventIsNotNull", true);
				}
				List<UlogApi> ulogApiList = ulogApiMapperExt.getLogByExample(param);

				DpLogResult result = new DpLogResult();
				result.setResource_id(resource_id);

				List<DpLog> list = new ArrayList<>();
				for(UlogApi logApi : ulogApiList) {
					DpLog dpLog = new DpLog();
					dpLog.setTransaction_uid(ValidatorHelper.removeSpecialCharacters(logApi.getTransactionUid()));
					dpLog.setCtime(sdf1.format(logApi.getCtime()));
					dpLog.setEvent(logApi.getAction()==null?"":ValidatorHelper.removeSpecialCharacters(logApi.getAction()));
					dpLog.setIp(logApi.getIp()==null?"":ValidatorHelper.removeSpecialCharacters(logApi.getIp()));

					list.add(dpLog);
				}
				result.setData(list);

				response.setStatus(HttpServletResponse.SC_OK);
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/jwe; charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.print(new ObjectMapper().writeValueAsString(result));
				out.close();
			}else {
				/**
				 * 拒絕存取。參數（tx_id, client_id）不存在。
				 */
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			}
		}else {
			/**
			 * 400 必要參數不存在
			 */
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		
	}

	class DpLogResult {
		String resource_id;
		List<DpLog> data = Collections.emptyList();

		public String getResource_id() {
			return resource_id;
		}

		public void setResource_id(String resource_id) {
			this.resource_id = resource_id;
		}

		public List<DpLog> getData() {
			return data;
		}

		public void setData(List<DpLog> data) {
			this.data = data;
		}
	}

	class DpLog {
		String transaction_uid;
		String ctime;
		String event;
		String ip;

		public String getTransaction_uid() {
			return transaction_uid;
		}

		public void setTransaction_uid(String transaction_uid) {
			this.transaction_uid = transaction_uid;
		}

		public String getCtime() {
			return ctime;
		}

		public void setCtime(String ctime) {
			this.ctime = ctime;
		}

		public String getEvent() {
			return event;
		}

		public void setEvent(String event) {
			this.event = event;
		}

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}
	}
}
