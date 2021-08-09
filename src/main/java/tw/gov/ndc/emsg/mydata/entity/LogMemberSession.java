package tw.gov.ndc.emsg.mydata.entity;

import java.io.Serializable;
import java.util.Date;

public class LogMemberSession implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column log_member_session.id
     *
     * @mbg.generated Fri Feb 19 10:04:57 CST 2021
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column log_member_session.member_id
     *
     * @mbg.generated Fri Feb 19 10:04:57 CST 2021
     */
    private Integer memberId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column log_member_session.session_id
     *
     * @mbg.generated Fri Feb 19 10:04:57 CST 2021
     */
    private String sessionId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column log_member_session.state
     *
     * @mbg.generated Fri Feb 19 10:04:57 CST 2021
     */
    private String state;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column log_member_session.created_at
     *
     * @mbg.generated Fri Feb 19 10:04:57 CST 2021
     */
    private Date createdAt;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column log_member_session.update_at
     *
     * @mbg.generated Fri Feb 19 10:04:57 CST 2021
     */
    private Date updateAt;

    private String msuuidcheck;
    private Long msuuidcheckTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table log_member_session
     *
     * @mbg.generated Fri Feb 19 10:04:57 CST 2021
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column log_member_session.id
     *
     * @return the value of log_member_session.id
     *
     * @mbg.generated Fri Feb 19 10:04:57 CST 2021
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column log_member_session.id
     *
     * @param id the value for log_member_session.id
     *
     * @mbg.generated Fri Feb 19 10:04:57 CST 2021
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column log_member_session.member_id
     *
     * @return the value of log_member_session.member_id
     *
     * @mbg.generated Fri Feb 19 10:04:57 CST 2021
     */
    public Integer getMemberId() {
        return memberId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column log_member_session.member_id
     *
     * @param memberId the value for log_member_session.member_id
     *
     * @mbg.generated Fri Feb 19 10:04:57 CST 2021
     */
    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column log_member_session.session_id
     *
     * @return the value of log_member_session.session_id
     *
     * @mbg.generated Fri Feb 19 10:04:57 CST 2021
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column log_member_session.session_id
     *
     * @param sessionId the value for log_member_session.session_id
     *
     * @mbg.generated Fri Feb 19 10:04:57 CST 2021
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column log_member_session.state
     *
     * @return the value of log_member_session.state
     *
     * @mbg.generated Fri Feb 19 10:04:57 CST 2021
     */
    public String getState() {
        return state;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column log_member_session.state
     *
     * @param state the value for log_member_session.state
     *
     * @mbg.generated Fri Feb 19 10:04:57 CST 2021
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column log_member_session.created_at
     *
     * @return the value of log_member_session.created_at
     *
     * @mbg.generated Fri Feb 19 10:04:57 CST 2021
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column log_member_session.created_at
     *
     * @param createdAt the value for log_member_session.created_at
     *
     * @mbg.generated Fri Feb 19 10:04:57 CST 2021
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column log_member_session.update_at
     *
     * @return the value of log_member_session.update_at
     *
     * @mbg.generated Fri Feb 19 10:04:57 CST 2021
     */
    public Date getUpdateAt() {
        return updateAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column log_member_session.update_at
     *
     * @param updateAt the value for log_member_session.update_at
     *
     * @mbg.generated Fri Feb 19 10:04:57 CST 2021
     */
    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public String getMsuuidcheck() {
        return msuuidcheck;
    }

    public void setMsuuidcheck(String msuuidcheck) {
        this.msuuidcheck = msuuidcheck;
    }

    public Long getMsuuidcheckTime() {
        return msuuidcheckTime;
    }

    public void setMsuuidcheckTime(Long msuuidcheckTime) {
        this.msuuidcheckTime = msuuidcheckTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table log_member_session
     *
     * @mbg.generated Fri Feb 19 10:04:57 CST 2021
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", memberId=").append(memberId);
        sb.append(", sessionId=").append(sessionId);
        sb.append(", state=").append(state);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updateAt=").append(updateAt);
        sb.append("]");
        return sb.toString();
    }
}