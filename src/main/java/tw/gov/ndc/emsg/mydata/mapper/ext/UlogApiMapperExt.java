package tw.gov.ndc.emsg.mydata.mapper.ext;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import tw.gov.ndc.emsg.mydata.entity.UlogApi;

public interface UlogApiMapperExt {
	
	public List<UlogApi> getLogByExample(Map<String,Object> param);
	/**
	 * 
	 * @param account
	 * @return
	 */
	public List<UlogApi> getLogByAccount(String account);
	public List<UlogApi> getLogByAccountAndAuditEvent(String account);
	/**
	 * 
	 * @param account
	 * @param clientId
	 * @return
	 */
	public List<UlogApi> getLogByAccountAndClientId(@Param("account") String account,@Param("clientId") String clientId);
	public List<UlogApi> getLogByAccountAndClientIdAndAuditEventForClient(@Param("account") String account,@Param("clientId") String clientId);
	public List<UlogApi> getLogByAccountAndResourceIdAndAuditEventForResource(@Param("account") String account,@Param("resourceId") String resourceId);
	
	public List<UlogApi> getLogByAccountAndResourceIdAndAuditEventForRow(@Param("account") String account,@Param("resourceId") String resourceId);
	public List<UlogApi> getLogByAccountAndResourceIdAndAuditEventAndTransactionUidForResource(@Param("account") String account,@Param("resourceId") String resourceId,@Param("transactionUid") String transactionUid);
	public Integer countLogByAccountAndResourceIdAndAuditEventForResource(@Param("account") String account,@Param("resourceId") String resourceId);
	public List<UlogApi> getLogByAccountAndClientIdAndGsp(@Param("account") String account,@Param("clientId") String clientId);
	public List<UlogApi> getLogByAccountAndClientIdAndAuditEventForService(@Param("account") String account,@Param("clientId") String clientId);
}
