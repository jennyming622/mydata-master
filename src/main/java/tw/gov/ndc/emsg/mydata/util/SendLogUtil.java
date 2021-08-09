package tw.gov.ndc.emsg.mydata.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.riease.common.helper.ValidatorHelper;

import tw.gov.ndc.emsg.mydata.entity.SendLog;
import tw.gov.ndc.emsg.mydata.mapper.SendLogMapper;
import tw.gov.ndc.emsg.mydata.type.SendType;

import java.util.Date;

@Component
public class SendLogUtil {


    @Autowired
    private SendLogMapper sendLogMapper;

    public void writeSendLog(SendType type, String account, String info, String content) {
        writeSendLog(type, account, info, null, content);
    }

    public void writeSendLog(SendType type, String account, String info, String title, String content) {
        SendLog slog = new SendLog();
        slog.setType(type.name());
        slog.setInfo(ValidatorHelper.removeSpecialCharacters(info));
        slog.setCtime(new Date());
        slog.setAccount(ValidatorHelper.removeSpecialCharacters(account));
        slog.setTitle(ValidatorHelper.removeSpecialCharacters(title));
        slog.setContent(ValidatorHelper.removeSpecialCharacters(content));
        sendLogMapper.insertSelective(slog);
    }
}
