package tw.gov.ndc.emsg.mydata.entity.ext;

public class NhiResultEntity {

    private boolean isValid;

    private String message;

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
