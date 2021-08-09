package tw.gov.ndc.emsg.mydata.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.riease.common.helper.ValidatorHelper;

import tw.gov.ndc.emsg.mydata.entity.Member;
import tw.gov.ndc.emsg.mydata.entity.MemberChangeLog;
import tw.gov.ndc.emsg.mydata.mapper.MemberChangeLogMapper;
import tw.gov.ndc.emsg.mydata.type.MemberChangeType;
import tw.gov.ndc.emsg.mydata.type.SendType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class MemberChangeUtil {

    private static SimpleDateFormat sdf8 = new SimpleDateFormat("yyyy/M/d HH:mm");
    @Autowired
    private MemberChangeLogMapper memberChangeLogMapper;
    @Autowired
    private SendLogUtil sendLogUtil;
    @Value("${mail.enable}")
    private String mailEnable;

    public void saveLog(Member member, String beforeData, String afterData, MemberChangeType type, String verificationType) {

        MemberChangeLog memberChangeLog = new MemberChangeLog();
        memberChangeLog.setMemberId(ValidatorHelper.limitNumber(member.getId()));
        memberChangeLog.setType(ValidatorHelper.removeSpecialCharacters(type.name()));
        memberChangeLog.setBeforeData(ValidatorHelper.removeSpecialCharacters(beforeData));
        memberChangeLog.setAfterData(ValidatorHelper.removeSpecialCharacters(afterData));
        memberChangeLog.setVerificationType(ValidatorHelper.removeSpecialCharacters(verificationType));
        memberChangeLog.setCtime(new Date());
        memberChangeLogMapper.insertSelective(memberChangeLog);

        String from = "mydata_system@ndc.gov.tw";
        String title = emailTitle();
        String emailContent = emailContent();
        String smsContent = smsContent();

        try {
            if(type == MemberChangeType.Email) {
                List<String> tmpReveicers = new ArrayList<String>();
                if(StringUtils.isNotBlank(beforeData)) {
                    tmpReveicers.add(beforeData);
                }
                if(StringUtils.isNotBlank(afterData)) {
                    tmpReveicers.add(afterData);
                }

                MailUtil.sendHtmlMail(tmpReveicers,from, title, emailContent, mailEnable);
                for(String email : tmpReveicers) {
                    sendLogUtil.writeSendLog(SendType.email, member.getAccount(), email, title, emailContent);
                }


                if(StringUtils.equalsIgnoreCase(member.getInformMethod(), "mobile")) {
                    SMSUtil.sendSms(member.getMobile(), smsContent);
                    sendLogUtil.writeSendLog(SendType.mobile, member.getAccount(), member.getMobile(), smsContent);
                }
            } else if (type == MemberChangeType.Mobile) {
                if(StringUtils.isNotBlank(beforeData)) {
                    SMSUtil.sendSms(beforeData, smsContent);
                    sendLogUtil.writeSendLog(SendType.mobile, member.getAccount(), beforeData, smsContent);
                }
                if(StringUtils.isNotBlank(afterData)) {
                    SMSUtil.sendSms(afterData, smsContent);
                    sendLogUtil.writeSendLog(SendType.mobile, member.getAccount(), afterData, smsContent);
                }

                if(StringUtils.equalsIgnoreCase(member.getInformMethod(), "email")) {
                    List<String> tmpReveicers = new ArrayList<String>();
                    tmpReveicers.add(member.getEmail());
                    MailUtil.sendHtmlMail(tmpReveicers,from, emailTitle(), emailContent(), mailEnable);
                    sendLogUtil.writeSendLog(SendType.email, member.getAccount(), member.getEmail(), title, emailContent);
                }
            }
        } catch (Exception ex) {
            System.out.println("--寄信失敗--:\n"+ex);
        }
    }

    /**
     * 主要聯絡方式變更通知
     * @param member
     */
    public void informMethodChangeSend(Member member) {

        String from = "mydata_system@ndc.gov.tw";
        String title = emailTitle();
        String emailContent = emailContent();
        String smsContent = smsContent();

        try {
                List<String> tmpReveicers = new ArrayList<String>();
                tmpReveicers.add(member.getEmail());

                MailUtil.sendHtmlMail(tmpReveicers,from, title, emailContent, mailEnable);
                for(String email : tmpReveicers) {
                    sendLogUtil.writeSendLog(SendType.email, member.getAccount(), email, title, emailContent);
                }

                SMSUtil.sendSms(member.getMobile(), smsContent);
                sendLogUtil.writeSendLog(SendType.mobile, member.getAccount(), member.getMobile(), smsContent);
        } catch (Exception ex) {
            System.out.println("--寄信失敗--:\n"+ex);
        }
    }

    public String emailTitle() {
        return "【 個人化資料自主運用(MyData)平臺】聯絡方式變更通知信（系統信件）";
    }

    public String emailContent() {
        String content = "您好：\n\n"
                + "您已於" + sdf8.format(new Date())+ "，變更您的聯絡方式。"
                + "此為系統信件，請勿回信。<br>"
		        + "如有任何疑問，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。"
		        + "<br><br>"
		        + "——-"
		        + "<strong>我為什麼會收到這封信？</strong><br>"
		        + "您會收到此封信件，是因為您有同意並授權國家發展委員會個人化資料自主運用(MyData)平臺提供您的個人資料給上述單位臨櫃核驗，當您服務單位完成您的個人資料核驗，系統會自動發此信通知您。"
		        + "<br><br>——-"
		        + "<strong>非本人？</strong><br>"
		        + "如非您本人同意傳輸或下載資料，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。";
        return content;
    };

    public String smsContent() {
        String content = "MyData通知-您已於" + sdf8.format(new Date())+ "，變更您的聯絡方式。" +
                "如非您本人修改資料，請洽客服電話：0800-009-868。";
        return content;
    };
}
