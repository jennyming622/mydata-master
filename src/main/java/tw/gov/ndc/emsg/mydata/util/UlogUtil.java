package tw.gov.ndc.emsg.mydata.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import com.riease.common.enums.ActionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.riease.common.enums.ActionStatus;
import com.riease.common.helper.ValidatorHelper;
import com.riease.common.sysinit.SessionRecord;

import tw.gov.ndc.emsg.mydata.entity.AuthToken;
import tw.gov.ndc.emsg.mydata.entity.Member;
import tw.gov.ndc.emsg.mydata.entity.PortalBox;
import tw.gov.ndc.emsg.mydata.entity.PortalResource;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceDownload;
import tw.gov.ndc.emsg.mydata.entity.PortalResourceScope;
import tw.gov.ndc.emsg.mydata.entity.PortalService;
import tw.gov.ndc.emsg.mydata.entity.PortalServiceDownload;
import tw.gov.ndc.emsg.mydata.entity.UlogApi;
import tw.gov.ndc.emsg.mydata.gspclient.bean.UserInfoEntity;
import tw.gov.ndc.emsg.mydata.mapper.AuthTokenMapper;
import tw.gov.ndc.emsg.mydata.mapper.MemberMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalBoxMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceDownloadMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalResourceScopeMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalServiceDownloadMapper;
import tw.gov.ndc.emsg.mydata.mapper.PortalServiceMapper;
import tw.gov.ndc.emsg.mydata.mapper.UlogApiMapper;
import tw.gov.ndc.emsg.mydata.web.SessionMember;

@Component
public class UlogUtil {
	@Autowired
	private UlogApiMapper ulogApiMapper;
	@Autowired 
	private MemberMapper memberMapper;
	@Autowired
	private PortalServiceMapper portalServiceMapper;
	@Autowired
    private PortalServiceDownloadMapper portalServiceDownloadMapper;
	@Autowired 
	private PortalResourceScopeMapper portalResourceScopeMapper;
	@Autowired 
	private PortalResourceMapper portalResourceMapper;
	@Autowired 
	private PortalResourceDownloadMapper portalResourceDownloadMapper;
	@Autowired 
	private PortalBoxMapper portalBoxMapper;
	
	/**
	 * 要求 insert ulog_api sp 授權狀態 
	 * 1.登入(AS) 
	 * 2.授權(AS) 
	 * 3.登出(AS) 
	 * 4. 要求(MyData) 
	 * 5. 傳送(DP) 
	 * 6.儲存(MyData) 
	 * 7.取消授權(AS) 
	 * 8.取用, 
	 * 11.申請(SP), 
	 * 12.申請完成(SP), 
	 * 13.收到(SP)
	 * 
	 * 資料集：
	 * 11 同意申請：您申請此筆資料/服務 
	 * 12 同意：您同意服務條款
	 * 13 驗證：您完成身分驗證
	 * 14 傳輸：您同意 MyData 傳送資料給服務提供者
	 * 15 自行儲存：您下載資料
	 * 16 臨櫃核驗-轉存：機關取用資料
	 * 17 線上服務：MyData 將資料傳送給服務提供者 or 
	 *    臨櫃核驗-轉存: 您完成提供資料給「嘉義縣 - 敬老愛心卡」
	 * 18 申請完成
	 * 19 申請失敗
	 * 20 申請失敗
	 * 
	 * 服務：
	 * 21 申請：您申請此筆資料/服務 
	 * 22 同意：您同意服務條款
	 * 23 驗證：您完成身分驗證
	 * 24 傳輸：您同意 MyData 傳送資料給服務提供者
	 * 25 儲存：您下載資料
	 * 26 條碼取用：機關取用資料
	 * 27 服務應用：MyData 將資料傳送給服務提供者
	 * 28 服務申請終止
	 * 29 服務申請終止
	 * 
	 * 31 臨櫃核驗-預覽 「臨櫃人原」使用「預覽」
	 * 32 臨櫃核驗-預覽 「臨櫃人原」使用「預覽」
	 * 
	 * 34 檔案刪除 
	 * 
	 * 委託人LOG
	 * 41 委託申請：您選擇此項臨櫃服務委託代辦
	 * 42 同意：您同意委託代辦項目
	 * 43 授權成功：已提供驗證碼給代辦人
	 * 44 授權失敗：您輸入的訊息，非 MyData 會員
	 * 45 委託終止：您終止此項委託代辦項目
	 * 46 委託逾時：已超過資料可取用時間
	 * 47 委託完成：代辦人已至臨櫃提供資料給「嘉義縣 - 敬老愛心卡」申辦完畢
	 * 
	 * 代辦人LOG
	 * 51 同意代辦：您同意項臨櫃服務代辦
	 * 52 委託終止：委託人已取消此代辦申請
	 * 53 委託逾時：已超過資料可取用時間
	 * 54 臨櫃核驗-預覽：您完成提供資料給臨櫃核驗機關
	 * 55 臨櫃核驗-轉存：您完成提供資料給「嘉義縣 - 敬老愛心卡」
	 */
	
	/**
	 * 資料集-記錄日誌
	 * @param sr
	 * @param ps
	 * @param txId
	 * @param pr
	 * @param transactionUid
	 * @param action
	 * @param auditEvent
	 * @throws IllegalBlockSizeException 
	 * @throws BadPaddingException 
	 */
	public void recordFullByPr(SessionRecord sr,
				PortalService ps,
				String txId,
				PortalResource pr,
				String transactionUid,
				ActionEvent action,
				String scope,
				Integer auditEvent,
				String ip) {
		UlogApi record = new UlogApi();
		if(sr!=null&&sr.getMember()!=null) {
			Member sMember = null;
			try {
				sMember = SessionMember.getSessionMemberToMember(sr.getMember());
			} catch (BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if(sMember!=null&&sMember.getAccount()!=null) {
				record.setProviderKey(ValidatorHelper.removeSpecialCharacters(sMember.getAccount()));
			}
			if(sMember!=null&&sMember.getName()!=null) {
				record.setUserName(ValidatorHelper.removeSpecialCharacters(sMember.getName()));
			}
			if(sMember!=null&&sMember.getUid()!=null) {
				record.setUid(ValidatorHelper.removeSpecialCharacters(sMember.getUid()));	
			}
			record.setToken(ValidatorHelper.removeSpecialCharacters(sr.getAuthToken().getToken()));
		}
		if(ps!=null&&ps.getClientId()!=null&&ps.getClientId().trim().length()>0) {
			record.setClientId(ValidatorHelper.removeSpecialCharacters(ps.getClientId()));
		}else {
			record.setClientId("CLI.mydata.portal");
		}
		if(txId!=null&&txId.trim().length()>0) {
			record.setTxId(ValidatorHelper.removeSpecialCharacters(txId));
		}
		if(pr!=null&&pr.getResourceId()!=null&&pr.getResourceId().trim().length()>0) {
			record.setResourceId(ValidatorHelper.removeSpecialCharacters(pr.getResourceId()));
		}
		if(transactionUid!=null&&transactionUid.trim().length()>0) {
			record.setTransactionUid(ValidatorHelper.removeSpecialCharacters(transactionUid));
		}
		if(action!=null) {
			record.setAction(ValidatorHelper.removeSpecialCharacters(action.getCode()));
			record.setActionText(ValidatorHelper.removeSpecialCharacters(action.getText()));
		}
		if(auditEvent!=null) {
			record.setAuditEvent(ValidatorHelper.limitNumber(auditEvent));
		}
		if(scope!=null&&scope.trim().length()>0) {
			record.setScope(ValidatorHelper.removeSpecialCharacters(scope));
		}
		record.setCtime(new Date());
		if(ip!=null&&ip.trim().length()>0) {
			record.setIp(ValidatorHelper.removeSpecialCharacters(ip));
		}else {
			record.setIp("117.56.91.59");
		}
		ulogApiMapper.insertSelective(record);
	}
	
	public void recordFullByPrUseMember(Member sMember,
				PortalService ps,
				String txId,
				PortalResource pr,
				String transactionUid,
				ActionEvent action,
				String scope,
				Integer auditEvent,
				String ip) {
		UlogApi record = new UlogApi();
		if(sMember!=null&&sMember.getAccount()!=null) {
			if(sMember!=null&&sMember.getAccount()!=null) {
				record.setProviderKey(ValidatorHelper.removeSpecialCharacters(sMember.getAccount()));
			}
			if(sMember!=null&&sMember.getName()!=null) {
				record.setUserName(ValidatorHelper.removeSpecialCharacters(sMember.getName()));
			}
			if(sMember!=null&&sMember.getUid()!=null) {
				record.setUid(ValidatorHelper.removeSpecialCharacters(sMember.getUid()));	
			}
		}
		if(ps!=null&&ps.getClientId()!=null&&ps.getClientId().trim().length()>0) {
			record.setClientId(ValidatorHelper.removeSpecialCharacters(ps.getClientId()));
		}else {
			record.setClientId("CLI.mydata.portal");
		}
		if(txId!=null&&txId.trim().length()>0) {
			record.setTxId(ValidatorHelper.removeSpecialCharacters(txId));
		}
		if(pr!=null&&pr.getResourceId()!=null&&pr.getResourceId().trim().length()>0) {
			record.setResourceId(ValidatorHelper.removeSpecialCharacters(pr.getResourceId()));
		}
		if(transactionUid!=null&&transactionUid.trim().length()>0) {
			record.setTransactionUid(ValidatorHelper.removeSpecialCharacters(transactionUid));
		}
		if(action!=null) {
			record.setAction(ValidatorHelper.removeSpecialCharacters(action.getCode()));
			record.setActionText(ValidatorHelper.removeSpecialCharacters(action.getText()));
		}
		if(auditEvent!=null) {
			record.setAuditEvent(ValidatorHelper.limitNumber(auditEvent));
		}
		if(scope!=null&&scope.trim().length()>0) {
			record.setScope(ValidatorHelper.removeSpecialCharacters(scope));
		}
		record.setCtime(new Date());
		if(ip!=null&&ip.trim().length()>0) {
			record.setIp(ValidatorHelper.removeSpecialCharacters(ip));
		}else {
			record.setIp("117.56.91.59");
		}
		ulogApiMapper.insertSelective(record);
	}	
	
	/**
	 * 服務-記錄日誌
	 * @param psd
	 * @param pr
	 * @param transactionUid
	 * @param action
	 * @param scope
	 * @param auditEvent
	 * @param ip
	 */
	public void recordFullByPs(
				PortalServiceDownload psd,
				PortalResource pr,
				String transactionUid,
				ActionEvent action,
				String scope,
				Integer auditEvent,
				String ip) {
		String[] downloadSnList = null;
		if(psd!=null) {
			downloadSnList = psd.getDownloadSnList().trim().split("[,]");
		}
		PortalResourceDownload prd = null;
		if(downloadSnList!=null&&downloadSnList.length>0) {
			prd = portalResourceDownloadMapper.selectByPrimaryKey(ValidatorHelper.removeSpecialCharacters(downloadSnList[0]));
		}
		Member member = null;
		if(prd!=null&&prd.getProviderKey()!=null) {
			Map<String,Object> mParam = new HashMap<>();
			mParam.put("account", ValidatorHelper.removeSpecialCharacters(prd.getProviderKey()));
			List<Member> mList = memberMapper.selectByExample(mParam);
			if(mList!=null&&mList.size()>0) {
				member = mList.get(0);
			}
		}
		UlogApi record = new UlogApi();
		if(member!=null) {
			record.setProviderKey(ValidatorHelper.removeSpecialCharacters(member.getAccount()));
			record.setUserName(ValidatorHelper.removeSpecialCharacters(member.getName()));
			
		}
		if(psd!=null) {
			record.setUid(ValidatorHelper.removeSpecialCharacters(psd.getUid()));
			record.setClientId(ValidatorHelper.removeSpecialCharacters(psd.getClientId()));
			record.setTxId(ValidatorHelper.removeSpecialCharacters(psd.getTxId()));
		}
		if(pr!=null&&pr.getResourceId()!=null&&pr.getResourceId().trim().length()>0) {
			record.setResourceId(ValidatorHelper.removeSpecialCharacters(pr.getResourceId()));
		}
		if(transactionUid!=null&&transactionUid.trim().length()>0) {
			record.setTransactionUid(ValidatorHelper.removeSpecialCharacters(transactionUid));
		}
		if(action!=null) {
			record.setAction(ValidatorHelper.removeSpecialCharacters(action.getCode()));
			record.setActionText(ValidatorHelper.removeSpecialCharacters(action.getText()));
		}
		if(auditEvent!=null) {
			record.setAuditEvent(ValidatorHelper.limitNumber(auditEvent));
		}
		if(scope!=null&&scope.trim().length()>0) {
			record.setScope(ValidatorHelper.removeSpecialCharacters(scope));
		}
		record.setCtime(new Date());
		if(ip!=null&&ip.trim().length()>0) {
			record.setIp(ValidatorHelper.removeSpecialCharacters(ip));
		}else {
			record.setIp("117.56.91.59");
		}
		ulogApiMapper.insertSelective(record);
	}	
	
	/**
	 * Introspection EndPoint & UserInfo EndPoint-記錄日誌
	 * @param resourceId
	 * @param auth
	 * @param action
	 * @param ip
	 */
	public void recordFullByConn(
				String resourceId,
				AuthToken auth,
				ActionEvent action,
				String ip) {
		Member member = null;
		List<Member> mList = null;
		if(auth!=null) {
			Map<String,Object> mParam = new HashMap<>();
			mParam.put("account", ValidatorHelper.removeSpecialCharacters(auth.getAccount()));
			mList = memberMapper.selectByExample(mParam);
		}
		if(mList!=null&&mList.size()>0) {
			member = mList.get(0);
			UlogApi record = new UlogApi();
			if(auth!=null&&auth.getAccount()!=null) {
				record.setProviderKey(ValidatorHelper.removeSpecialCharacters(auth.getAccount()));
			}			
			record.setUserName(ValidatorHelper.removeSpecialCharacters(member.getName()));
			record.setUid(ValidatorHelper.removeSpecialCharacters(member.getUid()));
			record.setClientId("CLI.mydata.portal");
			record.setToken(ValidatorHelper.removeSpecialCharacters(auth.getToken()));
			//record.setTxId(txId);
			if(resourceId!=null&&resourceId.trim().length()>0) {
				record.setResourceId(ValidatorHelper.removeSpecialCharacters(resourceId));
			}else {
				String[] scopeList = auth.getScope().split(" ");
				//僅一個「資料集」 會正確，多個「資料集」不一定對，可以改為不標註 resourceId
				Map<String,Object> sparam = new HashMap<String,Object>();
				sparam.put("scope", ValidatorHelper.removeSpecialCharacters(scopeList[0]));
				List<PortalResourceScope> prsList = portalResourceScopeMapper.selectByExample(sparam);
				PortalResource pr = null;
				if(prsList!=null&&prsList.size()>0) {
					pr = portalResourceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prsList.get(0).getPrId()));
					record.setResourceId(ValidatorHelper.removeSpecialCharacters(pr.getResourceId()));
				}
			}
			//record.setTransactionUid(transactionUid);
			if(action != null) {
				record.setAction(ValidatorHelper.removeSpecialCharacters(action.getCode()));
				record.setActionText(ValidatorHelper.removeSpecialCharacters(action.getText()));
			}
			if(auth!=null&&auth.getScope()!=null) {
				record.setScope(ValidatorHelper.removeSpecialCharacters(auth.getScope()));
			}
			record.setCtime(new Date());
			if(ip!=null&&ip.trim().length()>0) {
				record.setIp(ValidatorHelper.removeSpecialCharacters(ip));
			}else {
				record.setIp("117.56.91.59");
			}
			ulogApiMapper.insertSelective(record);
		}else {
			//UNDO
		}
	}
	
	/**
	 * 資料集下載-記錄日誌
	 * @param sr
	 * @param ps
	 * @param txId
	 * @param pr
	 * @param transactionUid
	 * @param action
	 * @param auditEvent
	 * @throws IllegalBlockSizeException 
	 * @throws BadPaddingException 
	 */
	public void recordFullByPrDownload(PortalResourceDownload prd,
				PortalResource pr,
				ActionEvent action,
				String scope,
				Integer auditEvent,
				String ip) {
		UlogApi record = new UlogApi();
		Member member = null;
		List<Member> mList = null;
		if(prd!=null) {
			Map<String,Object> mParam = new HashMap<>();
			mParam.put("account", ValidatorHelper.removeSpecialCharacters(prd.getProviderKey()));
			mList = memberMapper.selectByExample(mParam);
			if(mList!=null&&mList.size()>0) {
				member = mList.get(0);
			}
			if(member!=null&&member.getAccount()!=null) {
				record.setProviderKey(ValidatorHelper.removeSpecialCharacters(member.getAccount()));
			}
			if(member!=null&&member.getName()!=null) {
				record.setUserName(ValidatorHelper.removeSpecialCharacters(member.getName()));
			}
			if(member!=null&&member.getUid()!=null) {
				record.setUid(ValidatorHelper.removeSpecialCharacters(member.getUid()));	
			}
		}
		if(prd.getPsdId()!=null) {
			PortalServiceDownload portalServiceDownload = portalServiceDownloadMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(prd.getPsdId()));
			if(portalServiceDownload!=null) {
				PortalService ps = portalServiceMapper.selectByPrimaryKey(ValidatorHelper.limitNumber(portalServiceDownload.getPsId()));
				if(ps!=null) {
					if(ps!=null&&ps.getClientId()!=null&&ps.getClientId().trim().length()>0) {
						record.setClientId(ValidatorHelper.removeSpecialCharacters(ps.getClientId()));
					}else {
						record.setClientId("CLI.mydata.portal");
					}
				}else {
					record.setClientId("CLI.mydata.portal");
				}
			}else {
				record.setClientId("CLI.mydata.portal");
			}
		}else {
			record.setClientId("CLI.mydata.portal");
		}
		if(pr!=null&&pr.getResourceId()!=null&&pr.getResourceId().trim().length()>0) {
			record.setResourceId(ValidatorHelper.removeSpecialCharacters(pr.getResourceId()));
		}
		if(prd.getTransactionUid()!=null&&prd.getTransactionUid().trim().length()>0) {
			record.setTransactionUid(ValidatorHelper.removeSpecialCharacters(prd.getTransactionUid()));
		}
		if(action!=null) {
			record.setAction(ValidatorHelper.removeSpecialCharacters(action.getCode()));
			record.setActionText(ValidatorHelper.removeSpecialCharacters(action.getText()));
		}
		if(auditEvent!=null) {
			record.setAuditEvent(ValidatorHelper.limitNumber(auditEvent));
		}
		if(scope!=null&&scope.trim().length()>0) {
			record.setScope(ValidatorHelper.removeSpecialCharacters(scope));
		}
		record.setCtime(new Date());
		if(ip!=null&&ip.trim().length()>0) {
			record.setIp(ValidatorHelper.removeSpecialCharacters(ip));
		}else {
			record.setIp("117.56.91.59");
		}
		ulogApiMapper.insertSelective(record);
	}
	
	/**
	 * 臨櫃-記錄日誌
	 * @param box
	 * @param ps
	 * @param txId
	 * @param pr
	 * @param download
	 * @param action
	 * @param scope
	 * @param auditEvent
	 * @param ip
	 */
	public void recordFullByBoxAgent(
			PortalBox box,
			PortalService ps,
			String txId,
			PortalResource pr,
			PortalResourceDownload download,
			ActionEvent action,
			String scope,
			Integer auditEvent,
			String ip) {
		UlogApi record = new UlogApi();
		Member sMember = null;
		List<Member> mList = null;
		/**
		 * 委託人LOG
		 * 41 委託申請：您選擇此項臨櫃服務委託代辦
		 * 42 同意：您同意委託代辦項目
		 * 43 授權成功：已提供驗證碼給代辦人
		 * 44 授權失敗：您輸入的訊息，非 MyData 會員
		 * 45 委託終止：您終止此項委託代辦項目
		 * 46 委託逾時：已超過資料可取用時間
		 * 47 委託完成：代辦人已至臨櫃提供資料給「嘉義縣 - 敬老愛心卡」申辦完畢
		 * 48 不同意：代辦人終止此項委託代辦項目
		 * 
		 * 代辦人LOG
		 * 51 同意代辦：您同意項臨櫃服務代辦
		 * 52 委託終止：委託人已取消此代辦申請
		 * 53 委託逾時：已超過資料可取用時間
		 * 54 臨櫃核驗-預覽：您完成提供資料給臨櫃核驗機關
		 * 55 臨櫃核驗-轉存：您完成提供資料給「嘉義縣 - 敬老愛心卡」
		 * 56 不同意：您終止此項委託代辦項目
		 */
		List<Integer> memberAuditEventList = new ArrayList<Integer>();
		memberAuditEventList.add(41);
		memberAuditEventList.add(42);
		memberAuditEventList.add(43);
		memberAuditEventList.add(44);
		memberAuditEventList.add(45);
		memberAuditEventList.add(46);
		memberAuditEventList.add(47);
		memberAuditEventList.add(48);
		if(memberAuditEventList.contains(auditEvent)) {
			Map<String,Object> param1 = new HashMap<String,Object>();
			param1.put("psdId",ValidatorHelper.limitNumber(box.getPsdId()));
			List<PortalResourceDownload> portalResourceDownloadList = portalResourceDownloadMapper.selectByExample(param1);
			//委託人事件
			PortalResourceDownload prd = null;
			if(portalResourceDownloadList!=null&&portalResourceDownloadList.size()>0) {
				prd = portalResourceDownloadList.get(0);
			}
			if(prd!=null) {
				Map<String,Object> mParam = new HashMap<>();
				mParam.put("account", ValidatorHelper.removeSpecialCharacters(prd.getProviderKey()));
				mList = memberMapper.selectByExample(mParam);
				if(mList!=null&&mList.size()>0) {
					sMember = mList.get(0);
				}
			}
		}else {
			//代辦人事件
			if(box.getAgentUid()!=null&&box.getAgentBirthdate()!=null) {
		        Map<String,Object> mParam = new HashMap<>();
		        mParam.put("uid", ValidatorHelper.removeSpecialCharacters(box.getAgentUid().toUpperCase()));
		        mParam.put("birthdate", ValidatorHelper.limitDate(box.getAgentBirthdate()));
		        mParam.put("statIsNullorZero", true);
		        mList = memberMapper.selectByExample(mParam);
		        if(mList!=null&&mList.size()>0) {
		        	sMember = mList.get(0);
		        }			
			}else {
				Map<String,Object> param1 = new HashMap<String,Object>();
				param1.put("psdId", ValidatorHelper.limitNumber(download.getPsdId()));
				param1.put("ctimeDesc", true);
				List<PortalBox> portalBoxList = portalBoxMapper.selectByExample(param1);
				PortalBox tmpBox = null;
				if(portalBoxList!=null&&portalBoxList.size()>0) {
					tmpBox = portalBoxList.get(0);
				}
				if(tmpBox!=null&&tmpBox.getAgentUid()!=null&&tmpBox.getAgentBirthdate()!=null) {
			        Map<String,Object> mParam = new HashMap<>();
			        mParam.put("uid", ValidatorHelper.removeSpecialCharacters(tmpBox.getAgentUid().toUpperCase()));
			        mParam.put("birthdate", ValidatorHelper.limitDate(tmpBox.getAgentBirthdate()));
			        mParam.put("statIsNullorZero", true);
			        mList = memberMapper.selectByExample(mParam);
			        if(mList!=null&&mList.size()>0) {
			        	sMember = mList.get(0);
			        }
				}
			}
		}
		if(sMember!=null&&sMember.getAccount()!=null) {
			record.setProviderKey(ValidatorHelper.removeSpecialCharacters(sMember.getAccount()));
		}
		if(sMember!=null&&sMember.getName()!=null) {
			record.setUserName(ValidatorHelper.removeSpecialCharacters(sMember.getName()));
		}
		if(sMember!=null&&sMember.getUid()!=null) {
			record.setUid(ValidatorHelper.removeSpecialCharacters(sMember.getUid()));	
		}
		
		
		if(ps!=null&&ps.getClientId()!=null&&ps.getClientId().trim().length()>0) {
			record.setClientId(ValidatorHelper.removeSpecialCharacters(ps.getClientId()));
		}else {
			record.setClientId("CLI.mydata.portal");
		}
		if(txId!=null&&txId.trim().length()>0) {
			record.setTxId(ValidatorHelper.removeSpecialCharacters(txId));
		}
		if(pr!=null&&pr.getResourceId()!=null&&pr.getResourceId().trim().length()>0) {
			record.setResourceId(ValidatorHelper.removeSpecialCharacters(pr.getResourceId()));
		}
		if(download!=null&&download.getTransactionUid()!=null&&download.getTransactionUid().trim().length()>0) {
			record.setTransactionUid(ValidatorHelper.removeSpecialCharacters(download.getTransactionUid().trim()));
		}
		if(action!=null) {
			record.setAction(ValidatorHelper.removeSpecialCharacters(action.getCode()));
			record.setActionText(ValidatorHelper.removeSpecialCharacters(action.getText() ));
		}
		if(auditEvent!=null) {
			record.setAuditEvent(ValidatorHelper.limitNumber(auditEvent));
		}
		if(scope!=null&&scope.trim().length()>0) {
			record.setScope(ValidatorHelper.removeSpecialCharacters(scope));
		}
		record.setCtime(new Date());
		if(ip!=null&&ip.trim().length()>0) {
			record.setIp(ValidatorHelper.removeSpecialCharacters(ip));
		}else {
			record.setIp("117.56.91.59");
		}
		ulogApiMapper.insertSelective(record);
	}
	
	public Boolean hasUserReject(String txId, String transactionUid) {
		Map<String, Object> param = new HashMap<>();
		param.put("txId", ValidatorHelper.removeSpecialCharacters(txId));
		param.put("transactionUid", ValidatorHelper.removeSpecialCharacters(transactionUid));
		param.put("auditEvent", 28);

		List<UlogApi> list = ulogApiMapper.selectByExample(param);
		return list.size() > 0;
	}
	
}
