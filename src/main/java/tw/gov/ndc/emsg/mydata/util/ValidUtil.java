package tw.gov.ndc.emsg.mydata.util;

import tw.gov.ndc.emsg.mydata.model.WebServiceJobId;

import java.util.Map;

public interface ValidUtil {

    static final String RETURN_FAIL = "Fail";

    String call(Map<String, Object> parameters, WebServiceJobId jobId) throws Exception;

    Boolean isIdentifyValid(String content) throws Exception;
}
