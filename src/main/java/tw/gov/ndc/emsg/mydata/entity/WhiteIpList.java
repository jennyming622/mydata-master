package tw.gov.ndc.emsg.mydata.entity;

import java.io.Serializable;

public class WhiteIpList implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column white_ip_list.ip
     *
     * @mbg.generated Thu May 14 11:12:11 CST 2020
     */
    private String ip;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column white_ip_list.mark
     *
     * @mbg.generated Thu May 14 11:12:11 CST 2020
     */
    private String mark;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table white_ip_list
     *
     * @mbg.generated Thu May 14 11:12:11 CST 2020
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column white_ip_list.ip
     *
     * @return the value of white_ip_list.ip
     *
     * @mbg.generated Thu May 14 11:12:11 CST 2020
     */
    public String getIp() {
        return ip;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column white_ip_list.ip
     *
     * @param ip the value for white_ip_list.ip
     *
     * @mbg.generated Thu May 14 11:12:11 CST 2020
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column white_ip_list.mark
     *
     * @return the value of white_ip_list.mark
     *
     * @mbg.generated Thu May 14 11:12:11 CST 2020
     */
    public String getMark() {
        return mark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column white_ip_list.mark
     *
     * @param mark the value for white_ip_list.mark
     *
     * @mbg.generated Thu May 14 11:12:11 CST 2020
     */
    public void setMark(String mark) {
        this.mark = mark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table white_ip_list
     *
     * @mbg.generated Thu May 14 11:12:11 CST 2020
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", ip=").append(ip);
        sb.append(", mark=").append(mark);
        sb.append("]");
        return sb.toString();
    }
}