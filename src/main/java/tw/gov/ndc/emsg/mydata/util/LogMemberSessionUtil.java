package tw.gov.ndc.emsg.mydata.util;

import com.riease.common.util.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tw.gov.ndc.emsg.mydata.entity.LogMemberSession;
import tw.gov.ndc.emsg.mydata.entity.Member;
import tw.gov.ndc.emsg.mydata.type.SendType;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class LogMemberSessionUtil {

    @Autowired
    private SendLogUtil sendLogUtil;
    @Value("${mail.enable}")
    private String mailEnable;

    public void sendOTP(Member member, HttpSession session, LogMemberSession logMemberSession) {
        // Send Email or SMS

        String uuid = UUID.randomUUID().toString();
        String checkCode = RandomUtils.numericString(8);
        session.setAttribute("msuuidcheck", uuid);
        session.setAttribute("mscheckCode", checkCode);
        Long now = new Date().getTime();
        session.setAttribute("msuuidcheckTime", now);

        logMemberSession.setMsuuidcheck(uuid);
        logMemberSession.setMsuuidcheckTime(now);

        if (StringUtils.equals("email", member.getInformMethod())) {
            //傳送EMAIL
            System.out.println("checkCode=" + checkCode);
            /**
             * 寄通知信
             */
            try {
                String from = "mydata_system@ndc.gov.tw";
                String title = "【個人化資料自主運用(MyData)平臺】Email 驗證碼（系統信件）";
                String content = "您好：\n\n"
                        + "感謝您使用個人化資料自主運用(MyData)平臺，您本次的驗證碼如下，請於2分鐘內返回 MyData平臺進行驗證(逾期無效)，以完成驗證流程。\n"
                        + "\n"
                        + "<div style=\""
                        + "    display: block;"
                        + "    margin: 20px 0;"
                        + "    padding: 10px 20px;"
                        + "    font-size: 1rem;\n"
                        + "    background-color: #efefef;"
                        + "    border: none;\">"
                        + checkCode
                        + "</div>"
                        + "\n"
                        + "此為系統信件，請勿回信。\n"
                        + "如有任何疑問，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n"
                        + "<br><br>\n"
                        + "——-\n"
                        + "<strong>我為什麼會收到這封信？</strong><br>\n"
                        + "您會收到此封信件，是因為您於國家發展委員會個人化資料自主運用(MyData)平臺驗證身分，因此，系統會自動發此信通知您。\n"
                        + "<br><br>——-\n"
                        + "<strong>非本人？</strong><br>\n"
                        + "如非您本人同意傳輸或下載資料，請洽客服電話：0800-009-868，或寄信至客服信箱：mydata@ndc.gov.tw。\n";
                List<String> tmpReveicers = new ArrayList<String>();
                tmpReveicers.add(member.getEmail());
                /**
                 * 強制寄信 mailEnable == "true"
                 */
                //ulogUtil.recordFullByPr(sr, null, null, null, null, ActionEvent.EVENT_220, null, null, ip);
                MailUtil.sendHtmlMail(tmpReveicers, from, title, content, mailEnable);
                sendLogUtil.writeSendLog(SendType.email, member.getAccount(), member.getEmail(), title, content);
            } catch (Exception ex) {
                System.out.println("--寄信失敗--:\n" + ex);
            }
        } else {
            //傳送簡訊checkCode
            System.out.println("checkCode=" + checkCode);
            String smbody = "MyData 動態密碼訊息限當次有效，密碼：" + checkCode + "。請於網頁上輸入密碼。";
            try {
                //ulogUtil.recordFullByPr(sr, null, null, null, null, ActionEvent.EVENT_200, null, null, ip);
                SMSUtil.sendSms(member.getMobile(), smbody);
                sendLogUtil.writeSendLog(SendType.mobile, member.getAccount(), member.getMobile(), smbody);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
