package tw.gov.ndc.emsg.mydata.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Named;

@Component
public class ValidFactory {

    @Autowired
    @Named("identifyUtil")
    private ValidUtil identifyUtil;

    @Autowired
    @Named("immigrationUtil")
    private ValidUtil immigrationUtil;

    public ValidUtil get(String uid) {
        if(ValidateHelper.isValidResidentPermit(uid)) {
            return immigrationUtil;
        }
        return identifyUtil;
    }

    public ValidUtil getIdentifyUtil() {
        return identifyUtil;
    }

    public ValidUtil getImmigrationUtil() {
        return immigrationUtil;
    }
}
