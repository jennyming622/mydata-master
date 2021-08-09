/**
 * 
 */
package com.riease.common.sysinit.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.google.common.base.CaseFormat;
import com.riease.common.bean.PagingInfo;
import com.riease.common.helper.HttpClientHelper;
import com.riease.common.sysinit.SysCode;
import com.riease.common.sysinit.rest.RestResponseBean;

/**
 * Spring RestController 基礎物件，定義基礎行為。
 * @author wesleyzhuang
 *
 */
public class BaseRestController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(BaseRestController.class);
	
	//若無指定參數時的預設參數。
	private static final String[] defaultArgs = {"",""};
	
	/**
	 * 回傳錯誤
	 * @param result
	 * @return
	 */
	public ResponseEntity<RestResponseBean> responseError(BindingResult result) {
		RestResponseBean rb = new RestResponseBean();
		ObjectError es = result.getAllErrors().get(0);
		String field = null;
		if(es instanceof FieldError) {
			field = ((FieldError)es).getField();
		}else {
			field = "global";
		}
		String key = new StringJoiner(".").add(es.getCode()).add(field).toString();
		String message = message(key,null,key);
		logger.debug("hasError ... {} , {}", key, message);
		rb.setCode(SysCode.OperationFailed.value());
		rb.setText(es.getDefaultMessage());
		return new ResponseEntity<RestResponseBean>(rb, HttpStatus.OK);
	}
	
	/**
	 * 回傳錯誤
	 * @param code
	 * @param field
	 * @return
	 */
	public ResponseEntity<RestResponseBean> responseError(SysCode code, String field, String defaultMessage) {
		return responseError(code,field,null,defaultMessage);
	}
	
	/**
	 * 回傳錯誤
	 * @param code
	 * @param field
	 * @return
	 */
	public ResponseEntity<RestResponseBean> responseError(SysCode code, String field, String[] args, String defaultMessage) {
		RestResponseBean rb = new RestResponseBean();
		if(field == null) {
			field = "global";
		}
		String key = new StringJoiner(".").add(code.stringValue()).add(field).toString();
		String message = message(key,null,key);
		logger.debug("hasError ... {} , {}", key, message);
		
		String text = null;
		if(defaultMessage == null) {
			defaultMessage = code.stringValue();
		}
		if(args == null || args.length == 0) {
			text = message(code,defaultArgs,defaultMessage);
		}else {
			text = message(code,args,defaultMessage);
		}
		rb.setCode(code.value());
		rb.setText(text);
		return new ResponseEntity<RestResponseBean>(rb, HttpStatus.OK);
	}

	/**
	 * 回傳錯誤
	 * @param code
	 * @param errorMessage
	 * @return
	 */
	public ResponseEntity<RestResponseBean> responseError(SysCode code,String errorMessage) {
		RestResponseBean rb = new RestResponseBean();
		rb.setCode(code.value());
		rb.setText(errorMessage);
		return new ResponseEntity<RestResponseBean>(rb, HttpStatus.OK);
	}


	/**
	 * 回傳錯誤
	 * @param code
	 * @param messages
	 * @param joiner
	 * @return
	 */
	public ResponseEntity<RestResponseBean> responseError(Integer code, List<String> messages, String joiner) {
		RestResponseBean rb = new RestResponseBean();
		rb.setCode(code);
		StringJoiner sj = new StringJoiner(joiner);
		messages.forEach(msg -> {
			sj.add(msg);
		});
		rb.setText(sj.toString());
		return new ResponseEntity<RestResponseBean>(rb, HttpStatus.OK);
	}
	
	
	/**
	 * 回傳呼叫成功，沒有資料被帶回。
	 * @return
	 */
	public ResponseEntity<RestResponseBean> responseOK() {
		RestResponseBean rb = new RestResponseBean();
		rb.setCode(SysCode.OK.value());
		rb.setText(SysCode.OK.name());
		return new ResponseEntity<RestResponseBean>(rb, HttpStatus.OK);
	}
	
//	/**
//	 * 回傳呼叫成功，有資料被帶回。
//	 * @param data
//	 * @return
//	 */
//	public ResponseEntity<RestResponseBean> responseOK(Map<String,Object> data) {
//		RestResponseBean rb = new RestResponseBean();
//		rb.setCode(SysCode.OK.value());
//		rb.setText(SysCode.OK.name());
//		if(data != null) {
//			rb.setData(data);
//		}
//		return new ResponseEntity<RestResponseBean>(rb, HttpStatus.OK);
//	}
	
	/**
	 * 回傳呼叫成功，有資料被帶回。
	 * @param data
	 * @return
	 */
	public <T> ResponseEntity<RestResponseBean> responseOK(T data) {
		RestResponseBean rb = new RestResponseBean();
		rb.setCode(SysCode.OK.value());
		rb.setText(SysCode.OK.name());
		if(data != null) {
			rb.setData(data);
		}
		return new ResponseEntity<RestResponseBean>(rb, HttpStatus.OK);
	}
		
	/**
	 * 從DataTables的請求參數中取值，並生成PagingInfo物件。
	 * @param params
	 * @return
	 */
	public PagingInfo pagingParamFromDataTables(Map<String,Object> params) {
		PagingInfo p = new PagingInfo();
		String sStart = (String)params.get("start");
		if(sStart == null) sStart = "0";
		String sLength = (String)params.get("length");
		if(sLength == null) sLength = "10";
		
		int start = Integer.parseInt(sStart);
		int length = Integer.parseInt(sLength);
		p.setPageIndex(start/length);
		p.setPageSize(length);
		return p;
	}
	
	/**
	 * 從DataTables的請求參數中取值，draw。
	 * @param params
	 * @return
	 */
	public Integer drawParamFromDataTables(Map<String,Object> params) {
		String draw = (String)params.get("draw");
		if(draw == null) return 1;
		return Integer.parseInt(draw);
	}
	
	/**
	 * 從DataTables的請求參數中取值，search[value]。
	 * @param params
	 * @return
	 */
	public List<String> searchValueFromDataTables(Map<String,Object> params) {
		String search = (String)params.get(String.format("search[%s]", "value"));
		if(search == null || search.length() == 0) {
			return null;
		}else {
			search = search.replaceAll(" ", ",");
			search = search.replaceAll(",{1,}", ",");
			return Arrays.asList(search.split(","));
		}
	}
	
	/**
	 * 從DataTables的請求參數中取值，search[key]。
	 * @param params	請求參數
	 * @param key		搜尋條件的key
	 * @return
	 */
	public String searchParamFromDataTables(Map<String,Object> params, String key) {
		return (String)params.get(String.format("search[%s]", key));
	}
	
	/**
	 * 從DataTables的請求參數中取值，order[0][key]。
	 * @param params	請求參數
	 * @param key		column or dir
	 * @return
	 */
	public String orderParamFromDataTables(Map<String,Object> params, String key) {
		return (String)params.get(String.format("order[0][%s]", key));
	}
	
	/**
	 * 從DataTables的請求參數中取值，columns[x][xxx]
	 * @param params
	 * @return
	 */
	public List<Map<String,String>> columnsFromDataTables(final Map<String,Object> params) {
		List<Map<String,String>> rs = new ArrayList<>();
		params.keySet().stream().forEach(key -> {
			if(key.startsWith("columns")) {
				int p0 = "columns[".length();
				int p1 = key.indexOf("]", p0);
				int p2 = key.indexOf("[",p1)+1;
				int p3 = key.indexOf("]",p2);
				String idx = key.substring(p0, p1);
				String prop = key.substring(p2, p3);
				String value = (String)params.get(key);
				//prop為search時暫不處理。
				if(!"search".contentEquals(prop)) {
					int index = Integer.parseInt(idx);
					Map<String,String> m = null;
					if(rs.size() > index) {
						m = rs.get(index);
					}else {
						m = new HashMap<>();
						rs.add(index, m);
					}
					m.put(prop, value);
				}
			}
		});
		return rs;
	}
	
	/**
	 * 生成查詢資料庫的orderby字句，從DataTables的請求參數中取值。
	 * @param params
	 */
	public void handleOrderbyFromDataTables(Map<String,Object> params, String prefix) {
		//放置用來判斷orderby條件的值
		//Map<String,String> om = new HashMap<>();
		StringBuilder sb = new StringBuilder();
		String column = orderParamFromDataTables(params, "column");
		String dir = orderParamFromDataTables(params, "dir");
		if(dir == null) dir = "asc";
		if(column != null) {
			int colIndex = Integer.parseInt(column);
			List<Map<String,String>> columns = columnsFromDataTables(params);
			if(columns.size() > colIndex) {
				Map<String,String> map = columns.get(colIndex);
				boolean orderable = Boolean.parseBoolean(map.get("orderable"));
				if(orderable) {
					String data = map.get("data");
					if(data != null) {
						//om.put(data, dir);
						data = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, data);
						if(prefix != null) {
							data = prefix+data;
						}
						if(sb.length() > 0) sb.append(", ");
						sb.append(data).append(" ").append(dir);
					}
				}
			}
			
			if(sb.length() > 0) {
				params.put("orderby", sb.toString());
				logger.trace("orderby -> {}", params.get("orderby"));
			}
		}
	}
	
	/**
	 * 生成查詢資料庫的orderby字句，從DataTables的請求參數中取值。
	 * @param params
	 */
	public void handleOrderbyFromDataTables(Map<String,Object> params) {
		handleOrderbyFromDataTables(params,null);
	}
	
	/**
	 * 把日期物件調整至當日的23:59:59
	 * @param date
	 */
	public Date adjustToEndOfDay(Date date) {
		if(date == null) {
			return null;
		}else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			return cal.getTime();
		}
	}
	
	public String getBody(HttpServletRequest request) throws IOException {
	    String body = null;
	    StringBuilder stringBuilder = new StringBuilder();
	    BufferedReader bufferedReader = null;
	    InputStream inputStream = null;
	    try {
	        inputStream = request.getInputStream();
	        if (inputStream != null) {
	            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	            char[] charBuffer = new char[128];
	            int bytesRead = -1;
	            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
	                stringBuilder.append(charBuffer, 0, bytesRead);
	            }
	        } else {
	            stringBuilder.append("");
	        }
	    } catch (IOException ex) {
	        throw ex;
	    } finally {
			if(inputStream!=null) {
				HttpClientHelper.safeClose(inputStream);
			}
	        if (bufferedReader != null) {
	        	HttpClientHelper.safeClose(bufferedReader);
	        }
	    }

	    body = stringBuilder.toString();
	    return body;
	}
	
	public String objToStr(Object obj) {
		if(obj == null) {
			return null;
		}
		return obj.toString();
	}
	
}
