package tw.gov.ndc.emsg.mydata.entity;

import java.io.Serializable;
import java.util.Date;

public class SendLog implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column send_log.id
     *
     * @mbg.generated Thu May 28 10:41:34 CST 2020
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column send_log.type
     *
     * @mbg.generated Thu May 28 10:41:34 CST 2020
     */
    private String type;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column send_log.info
     *
     * @mbg.generated Thu May 28 10:41:34 CST 2020
     */
    private String info;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column send_log.ctime
     *
     * @mbg.generated Thu May 28 10:41:34 CST 2020
     */
    private Date ctime;

    private String account;

    private String title;

    private String content;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table send_log
     *
     * @mbg.generated Thu May 28 10:41:34 CST 2020
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column send_log.id
     *
     * @return the value of send_log.id
     *
     * @mbg.generated Thu May 28 10:41:34 CST 2020
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column send_log.id
     *
     * @param id the value for send_log.id
     *
     * @mbg.generated Thu May 28 10:41:34 CST 2020
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column send_log.type
     *
     * @return the value of send_log.type
     *
     * @mbg.generated Thu May 28 10:41:34 CST 2020
     */
    public String getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column send_log.type
     *
     * @param type the value for send_log.type
     *
     * @mbg.generated Thu May 28 10:41:34 CST 2020
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column send_log.info
     *
     * @return the value of send_log.info
     *
     * @mbg.generated Thu May 28 10:41:34 CST 2020
     */
    public String getInfo() {
        return info;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column send_log.info
     *
     * @param info the value for send_log.info
     *
     * @mbg.generated Thu May 28 10:41:34 CST 2020
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column send_log.ctime
     *
     * @return the value of send_log.ctime
     *
     * @mbg.generated Thu May 28 10:41:34 CST 2020
     */
    public Date getCtime() {
        return ctime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column send_log.ctime
     *
     * @param ctime the value for send_log.ctime
     *
     * @mbg.generated Thu May 28 10:41:34 CST 2020
     */
    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table send_log
     *
     * @mbg.generated Thu May 28 10:41:34 CST 2020
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", type=").append(type);
        sb.append(", info=").append(info);
        sb.append(", ctime=").append(ctime);
        sb.append("]");
        return sb.toString();
    }
}