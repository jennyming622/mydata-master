package tw.gov.ndc.emsg.mydata.entity;

import java.io.Serializable;

public class TfidoRecord implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tfido_record.id
     *
     * @mbg.generated Thu Mar 12 13:50:06 CST 2020
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tfido_record.uid
     *
     * @mbg.generated Thu Mar 12 13:50:06 CST 2020
     */
    private String uid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tfido_record.qrid
     *
     * @mbg.generated Thu Mar 12 13:50:06 CST 2020
     */
    private String qrid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table tfido_record
     *
     * @mbg.generated Thu Mar 12 13:50:06 CST 2020
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tfido_record.id
     *
     * @return the value of tfido_record.id
     *
     * @mbg.generated Thu Mar 12 13:50:06 CST 2020
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tfido_record.id
     *
     * @param id the value for tfido_record.id
     *
     * @mbg.generated Thu Mar 12 13:50:06 CST 2020
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tfido_record.uid
     *
     * @return the value of tfido_record.uid
     *
     * @mbg.generated Thu Mar 12 13:50:06 CST 2020
     */
    public String getUid() {
        return uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tfido_record.uid
     *
     * @param uid the value for tfido_record.uid
     *
     * @mbg.generated Thu Mar 12 13:50:06 CST 2020
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tfido_record.qrid
     *
     * @return the value of tfido_record.qrid
     *
     * @mbg.generated Thu Mar 12 13:50:06 CST 2020
     */
    public String getQrid() {
        return qrid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tfido_record.qrid
     *
     * @param qrid the value for tfido_record.qrid
     *
     * @mbg.generated Thu Mar 12 13:50:06 CST 2020
     */
    public void setQrid(String qrid) {
        this.qrid = qrid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tfido_record
     *
     * @mbg.generated Thu Mar 12 13:50:06 CST 2020
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", uid=").append(uid);
        sb.append(", qrid=").append(qrid);
        sb.append("]");
        return sb.toString();
    }
}