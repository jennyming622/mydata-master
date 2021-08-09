package tw.gov.ndc.emsg.mydata.entity;

import java.io.Serializable;

public class Category implements Serializable {

	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column category.cate_id
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	private Integer cateId;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column category.cate_name
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	private String cateName;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column category.cate_desc
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	private String cateDesc;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column category.parent_id
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	private Integer parentId;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column category.status
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	private Integer status;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column category.seq
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	private Integer seq;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table category
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column category.cate_id
	 * @return  the value of category.cate_id
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	public Integer getCateId() {
		return cateId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column category.cate_id
	 * @param cateId  the value for category.cate_id
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	public void setCateId(Integer cateId) {
		this.cateId = cateId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column category.cate_name
	 * @return  the value of category.cate_name
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	public String getCateName() {
		return cateName;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column category.cate_name
	 * @param cateName  the value for category.cate_name
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	public void setCateName(String cateName) {
		this.cateName = cateName;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column category.cate_desc
	 * @return  the value of category.cate_desc
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	public String getCateDesc() {
		return cateDesc;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column category.cate_desc
	 * @param cateDesc  the value for category.cate_desc
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	public void setCateDesc(String cateDesc) {
		this.cateDesc = cateDesc;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column category.parent_id
	 * @return  the value of category.parent_id
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	public Integer getParentId() {
		return parentId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column category.parent_id
	 * @param parentId  the value for category.parent_id
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column category.status
	 * @return  the value of category.status
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column category.status
	 * @param status  the value for category.status
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column category.seq
	 * @return  the value of category.seq
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	public Integer getSeq() {
		return seq;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column category.seq
	 * @param seq  the value for category.seq
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table category
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */@Override public String toString(){StringBuilder sb=new StringBuilder();sb.append(getClass().getSimpleName());sb.append(" [");sb.append("Hash = ").append(hashCode());sb.append(", cateId=").append(cateId);sb.append(", cateName=").append(cateName);sb.append(", cateDesc=").append(cateDesc);sb.append(", parentId=").append(parentId);sb.append(", status=").append(status);sb.append(", seq=").append(seq);sb.append("]");return sb.toString();}
}