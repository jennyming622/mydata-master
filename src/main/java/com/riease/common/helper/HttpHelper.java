package com.riease.common.helper;

import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;


public final class HttpHelper
{
	/**
	 * Get parameter from request, throws ServletException if parameter value is null or empty.
	 * @param HttpServletRequest
	 * @param parameter name
	 * @return value of parameter
	 * @throws ServletException
	 */
	public static String getRequiredParameter(HttpServletRequest request, String name)
	throws ServletException
	{
    	String value = request.getParameter(name);
    	if (StringUtils.isEmpty(value)) {
    		throw new ServletException("parameter " + name + " is null or empty");
    	}
    	
    	return value;
	}
	
	public static String getAttribute(String key, HttpSession session) {
        return session.getAttribute(key)==null?"":session.getAttribute(key).toString();
    }
    
    public static String getParameter(String key, HttpServletRequest request) {
        return request.getParameter(key)==null?"":request.getParameter(key).trim();
    }
    
    public static String getParameter(String key, String defaultValue, HttpServletRequest request) {
        return request.getParameter(key)==null?defaultValue:request.getParameter(key).trim();
    }
    
    public static String[] getParameterValues(String key, HttpServletRequest request) {
        String r[] = request.getParameterValues(key)==null ? new String[0] : request.getParameterValues(key);
        for(int i=0; i<r.length; i++) {
            if(r[i]==null) r[i] = "";
        }
        return r;
    }
    
    public static HashMap<String,String> getParameter(String[] params, HttpServletRequest request) {
        HashMap<String,String> r = new HashMap<String, String>();
        for(String param : params) {
            String value = getParameter(param,request);
            r.put(param, value);
        }
        return r;
    }
    
    
    /**
     * 
     * @param param
     * @param request
     * @return
     */
    public static HashMap<String, String> getParameterValue(HashMap<String,String> param, HttpServletRequest request) {
        Iterator<String> iter = param.keySet().iterator();
        while(iter.hasNext()) {
            String key = iter.next();
            param.put(key, getParameter(key, request));
        }
        return param;
    }
    
    
    /**
	 * 先以getRemoteAddrByHeader取值，若取不到值再以getRemoteAddr取值
	 * @param request
	 * @return
	 */
	public static String getRemoteIp(HttpServletRequest request) {
		String rip = getRemoteAddr(request);
		if(rip == null || rip.isEmpty() || rip.startsWith("127.0.0.1")) {
			rip = getRemoteAddrByHeader(request);
		}
		return rip;
	}
	
	/**
	 * 取得逺端的IP位址
	 * @param request
	 * @return
	 */
	public static String getRemoteAddr(HttpServletRequest request) {
		if(request == null) {
			return "";
		}
		String remoteIp = request.getRemoteAddr();
		if (remoteIp.startsWith("0:0:0:0:0:0:0:1")) {
			remoteIp = "127.0.0.1";
		}
		return ValidatorHelper.removeSpecialCharacters(remoteIp);
	}
	
	/**
	 * 取得逺端的IP位址。從http header中取值。
	 * @param request
	 * @return
	 */
	public static String getRemoteAddrByHeader(HttpServletRequest request) {
		if(request != null) {
			String remoteIp = request.getHeader("X-Real-IP");
			if(remoteIp != null) {
				if(remoteIp.startsWith("0:0:0:0:0:0:0:1")) {
					remoteIp = "127.0.0.1";
				}
				return ValidatorHelper.removeSpecialCharacters(remoteIp);
			}else {
				String ip = request.getHeader("X-Forwarded-For");
				if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
					remoteIp = request.getRemoteAddr();
			    }else {
			    	String[] ips = ip.split(",");
			    	if(ips.length >= 1) {
			    		remoteIp = ips[0].trim();
			    	}
			    }
				return ValidatorHelper.removeSpecialCharacters(remoteIp);
			}
		}else {
			return null;
		}
	}
    
}
