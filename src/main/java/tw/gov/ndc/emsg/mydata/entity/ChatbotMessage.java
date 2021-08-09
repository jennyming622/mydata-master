package tw.gov.ndc.emsg.mydata.entity;

import java.io.Serializable;
import java.util.Date;

public class ChatbotMessage implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column chatbot_message.id
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column chatbot_message.account
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    private String account;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column chatbot_message.type
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    private String type;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column chatbot_message.message
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    private String message;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column chatbot_message.intent_name
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    private String intentName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column chatbot_message.bot_response
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    private String botResponse;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column chatbot_message.ctime
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    private Date ctime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column chatbot_message.session
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    private String session;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column chatbot_message.feel
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    private String feel;

    private String value;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table chatbot_message
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column chatbot_message.id
     *
     * @return the value of chatbot_message.id
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column chatbot_message.id
     *
     * @param id the value for chatbot_message.id
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column chatbot_message.account
     *
     * @return the value of chatbot_message.account
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    public String getAccount() {
        return account;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column chatbot_message.account
     *
     * @param account the value for chatbot_message.account
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column chatbot_message.type
     *
     * @return the value of chatbot_message.type
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    public String getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column chatbot_message.type
     *
     * @param type the value for chatbot_message.type
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column chatbot_message.message
     *
     * @return the value of chatbot_message.message
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    public String getMessage() {
        return message;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column chatbot_message.message
     *
     * @param message the value for chatbot_message.message
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column chatbot_message.intent_name
     *
     * @return the value of chatbot_message.intent_name
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    public String getIntentName() {
        return intentName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column chatbot_message.intent_name
     *
     * @param intentName the value for chatbot_message.intent_name
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    public void setIntentName(String intentName) {
        this.intentName = intentName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column chatbot_message.bot_response
     *
     * @return the value of chatbot_message.bot_response
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    public String getBotResponse() {
        return botResponse;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column chatbot_message.bot_response
     *
     * @param botResponse the value for chatbot_message.bot_response
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    public void setBotResponse(String botResponse) {
        this.botResponse = botResponse;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column chatbot_message.ctime
     *
     * @return the value of chatbot_message.ctime
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    public Date getCtime() {
        return ctime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column chatbot_message.ctime
     *
     * @param ctime the value for chatbot_message.ctime
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column chatbot_message.session
     *
     * @return the value of chatbot_message.session
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    public String getSession() {
        return session;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column chatbot_message.session
     *
     * @param session the value for chatbot_message.session
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    public void setSession(String session) {
        this.session = session;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column chatbot_message.feel
     *
     * @return the value of chatbot_message.feel
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    public String getFeel() {
        return feel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column chatbot_message.feel
     *
     * @param feel the value for chatbot_message.feel
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    public void setFeel(String feel) {
        this.feel = feel;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table chatbot_message
     *
     * @mbg.generated Wed Oct 21 16:57:34 CST 2020
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", account=").append(account);
        sb.append(", type=").append(type);
        sb.append(", message=").append(message);
        sb.append(", intentName=").append(intentName);
        sb.append(", botResponse=").append(botResponse);
        sb.append(", ctime=").append(ctime);
        sb.append(", session=").append(session);
        sb.append(", feel=").append(feel);
        sb.append("]");
        return sb.toString();
    }
}