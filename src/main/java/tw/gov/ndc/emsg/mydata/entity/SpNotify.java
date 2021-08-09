package tw.gov.ndc.emsg.mydata.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpNotify {

    @JsonProperty("tx_id")
    private String txId;

    @JsonProperty("apiKey")
    private String apiKey;

    @JsonProperty("permission_ticket")
    private String permissionTicket;

    @JsonProperty("secret_key")
    private String secretKey;

    @JsonProperty("unable_to_deliver")
    private List<String> unableToDeliver;

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getPermissionTicket() {
        return permissionTicket;
    }

    public void setPermissionTicket(String permissionTicket) {
        this.permissionTicket = permissionTicket;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public List<String> getUnableToDeliver() {
        return unableToDeliver;
    }

    public void setUnableToDeliver(List<String> unableToDeliver) {
        this.unableToDeliver = unableToDeliver;
    }
}
