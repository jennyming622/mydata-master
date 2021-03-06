package tw.gov.ndc.emsg.mydata.entity;

import java.io.Serializable;
import java.util.Date;

public class PortalBoxLockCheck implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column portal_box_lock_check.ip
     *
     * @mbg.generated Wed Nov 13 15:02:09 CST 2019
     */
    private String ip;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column portal_box_lock_check.count
     *
     * @mbg.generated Wed Nov 13 15:02:09 CST 2019
     */
    private Integer count;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column portal_box_lock_check.ctime
     *
     * @mbg.generated Wed Nov 13 15:02:09 CST 2019
     */
    private Date ctime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column portal_box_lock_check.stat
     *
     * @mbg.generated Wed Nov 13 15:02:09 CST 2019
     */
    private Integer stat;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table portal_box_lock_check
     *
     * @mbg.generated Wed Nov 13 15:02:09 CST 2019
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column portal_box_lock_check.ip
     *
     * @return the value of portal_box_lock_check.ip
     *
     * @mbg.generated Wed Nov 13 15:02:09 CST 2019
     */
    public String getIp() {
        return ip;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column portal_box_lock_check.ip
     *
     * @param ip the value for portal_box_lock_check.ip
     *
     * @mbg.generated Wed Nov 13 15:02:09 CST 2019
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column portal_box_lock_check.count
     *
     * @return the value of portal_box_lock_check.count
     *
     * @mbg.generated Wed Nov 13 15:02:09 CST 2019
     */
    public Integer getCount() {
        return count;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column portal_box_lock_check.count
     *
     * @param count the value for portal_box_lock_check.count
     *
     * @mbg.generated Wed Nov 13 15:02:09 CST 2019
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column portal_box_lock_check.ctime
     *
     * @return the value of portal_box_lock_check.ctime
     *
     * @mbg.generated Wed Nov 13 15:02:09 CST 2019
     */
    public Date getCtime() {
        return ctime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column portal_box_lock_check.ctime
     *
     * @param ctime the value for portal_box_lock_check.ctime
     *
     * @mbg.generated Wed Nov 13 15:02:09 CST 2019
     */
    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column portal_box_lock_check.stat
     *
     * @return the value of portal_box_lock_check.stat
     *
     * @mbg.generated Wed Nov 13 15:02:09 CST 2019
     */
    public Integer getStat() {
        return stat;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column portal_box_lock_check.stat
     *
     * @param stat the value for portal_box_lock_check.stat
     *
     * @mbg.generated Wed Nov 13 15:02:09 CST 2019
     */
    public void setStat(Integer stat) {
        this.stat = stat;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table portal_box_lock_check
     *
     * @mbg.generated Wed Nov 13 15:02:09 CST 2019
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", ip=").append(ip);
        sb.append(", count=").append(count);
        sb.append(", ctime=").append(ctime);
        sb.append(", stat=").append(stat);
        sb.append("]");
        return sb.toString();
    }
}