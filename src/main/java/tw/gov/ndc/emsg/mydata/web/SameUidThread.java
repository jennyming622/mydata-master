package tw.gov.ndc.emsg.mydata.web;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.riease.common.helper.ValidatorHelper;

import tw.gov.ndc.emsg.mydata.entity.Member;
import tw.gov.ndc.emsg.mydata.mapper.MemberMapper;
import tw.gov.ndc.emsg.mydata.model.WebServiceJobId;
import tw.gov.ndc.emsg.mydata.util.CDateUtil;
import tw.gov.ndc.emsg.mydata.util.ValidUtil;

public class SameUidThread extends Thread {
	private static SimpleDateFormat sdf6 = new SimpleDateFormat("yyyyMMdd");
	private Member member;
	private String uid;
	private String birthDate;
	private ValidUtil validUtil;
	private MemberMapper memberMapper;
	public SameUidThread(Member member,ValidUtil validUtil,MemberMapper memberMapper) {
		this.member = member;
		this.validUtil = validUtil;
		this.memberMapper = memberMapper;
	}
	
	public void run() {
        try {
            Map<String,Object> identifyMap = new HashMap<>();
            identifyMap.put("ck_personId",member.getUid());
            identifyMap.put("ck_birthDate", CDateUtil.ADDateToROCStr(member.getBirthdate()));
            String birthResult = null;
        	birthResult = validUtil.call(identifyMap, WebServiceJobId.BirthDate);
            if(StringUtils.equals(birthResult, ValidUtil.RETURN_FAIL)) {
            	System.out.println("SameUidThread 驗證系統異常");
            }
			if(!validUtil.isIdentifyValid(birthResult)) {
				Member member1 = new Member();
				member1.setId(ValidatorHelper.limitNumber(member.getId()));
				member1.setStat(1);
				memberMapper.updateByPrimaryKeySelective(member1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
