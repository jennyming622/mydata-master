package tw.gov.ndc.emsg.mydata.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PortalBoxLogExample {
    /**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table portal_box_log
	 * @mbg.generated  Tue Jul 30 17:30:05 CST 2019
	 */
	protected String orderByClause;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table portal_box_log
	 * @mbg.generated  Tue Jul 30 17:30:05 CST 2019
	 */
	protected boolean distinct;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table portal_box_log
	 * @mbg.generated  Tue Jul 30 17:30:05 CST 2019
	 */
	protected List<Criteria> oredCriteria;

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table portal_box_log
	 * @mbg.generated  Tue Jul 30 17:30:05 CST 2019
	 */
	public PortalBoxLogExample() {
		oredCriteria = new ArrayList<Criteria>();
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table portal_box_log
	 * @mbg.generated  Tue Jul 30 17:30:05 CST 2019
	 */
	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table portal_box_log
	 * @mbg.generated  Tue Jul 30 17:30:05 CST 2019
	 */
	public String getOrderByClause() {
		return orderByClause;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table portal_box_log
	 * @mbg.generated  Tue Jul 30 17:30:05 CST 2019
	 */
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table portal_box_log
	 * @mbg.generated  Tue Jul 30 17:30:05 CST 2019
	 */
	public boolean isDistinct() {
		return distinct;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table portal_box_log
	 * @mbg.generated  Tue Jul 30 17:30:05 CST 2019
	 */
	public List<Criteria> getOredCriteria() {
		return oredCriteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table portal_box_log
	 * @mbg.generated  Tue Jul 30 17:30:05 CST 2019
	 */
	public void or(Criteria criteria) {
		oredCriteria.add(criteria);
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table portal_box_log
	 * @mbg.generated  Tue Jul 30 17:30:05 CST 2019
	 */
	public Criteria or() {
		Criteria criteria = createCriteriaInternal();
		oredCriteria.add(criteria);
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table portal_box_log
	 * @mbg.generated  Tue Jul 30 17:30:05 CST 2019
	 */
	public Criteria createCriteria() {
		Criteria criteria = createCriteriaInternal();
		if (oredCriteria.size() == 0) {
			oredCriteria.add(criteria);
		}
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table portal_box_log
	 * @mbg.generated  Tue Jul 30 17:30:05 CST 2019
	 */
	protected Criteria createCriteriaInternal() {
		Criteria criteria = new Criteria();
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table portal_box_log
	 * @mbg.generated  Tue Jul 30 17:30:05 CST 2019
	 */
	public void clear() {
		oredCriteria.clear();
		orderByClause = null;
		distinct = false;
	}

	/**
	 * This class was generated by MyBatis Generator. This class corresponds to the database table portal_box_log
	 * @mbg.generated  Tue Jul 30 17:30:05 CST 2019
	 */
	protected abstract static class GeneratedCriteria {
		protected List<Criterion> criteria;

		protected GeneratedCriteria() {
			super();
			criteria = new ArrayList<Criterion>();
		}

		public boolean isValid() {
			return criteria.size() > 0;
		}

		public List<Criterion> getAllCriteria() {
			return criteria;
		}

		public List<Criterion> getCriteria() {
			return criteria;
		}

		protected void addCriterion(String condition) {
			if (condition == null) {
				throw new RuntimeException("Value for condition cannot be null");
			}
			criteria.add(new Criterion(condition));
		}

		protected void addCriterion(String condition, Object value, String property) {
			if (value == null) {
				throw new RuntimeException("Value for " + property + " cannot be null");
			}
			criteria.add(new Criterion(condition, value));
		}

		protected void addCriterion(String condition, Object value1, Object value2, String property) {
			if (value1 == null || value2 == null) {
				throw new RuntimeException("Between values for " + property + " cannot be null");
			}
			criteria.add(new Criterion(condition, value1, value2));
		}

		public Criteria andIdIsNull() {
			addCriterion("id is null");
			return (Criteria) this;
		}

		public Criteria andIdIsNotNull() {
			addCriterion("id is not null");
			return (Criteria) this;
		}

		public Criteria andIdEqualTo(Integer value) {
			addCriterion("id =", value, "id");
			return (Criteria) this;
		}

		public Criteria andIdNotEqualTo(Integer value) {
			addCriterion("id <>", value, "id");
			return (Criteria) this;
		}

		public Criteria andIdGreaterThan(Integer value) {
			addCriterion("id >", value, "id");
			return (Criteria) this;
		}

		public Criteria andIdGreaterThanOrEqualTo(Integer value) {
			addCriterion("id >=", value, "id");
			return (Criteria) this;
		}

		public Criteria andIdLessThan(Integer value) {
			addCriterion("id <", value, "id");
			return (Criteria) this;
		}

		public Criteria andIdLessThanOrEqualTo(Integer value) {
			addCriterion("id <=", value, "id");
			return (Criteria) this;
		}

		public Criteria andIdIn(List<Integer> values) {
			addCriterion("id in", values, "id");
			return (Criteria) this;
		}

		public Criteria andIdNotIn(List<Integer> values) {
			addCriterion("id not in", values, "id");
			return (Criteria) this;
		}

		public Criteria andIdBetween(Integer value1, Integer value2) {
			addCriterion("id between", value1, value2, "id");
			return (Criteria) this;
		}

		public Criteria andIdNotBetween(Integer value1, Integer value2) {
			addCriterion("id not between", value1, value2, "id");
			return (Criteria) this;
		}

		public Criteria andBoxIdIsNull() {
			addCriterion("box_id is null");
			return (Criteria) this;
		}

		public Criteria andBoxIdIsNotNull() {
			addCriterion("box_id is not null");
			return (Criteria) this;
		}

		public Criteria andBoxIdEqualTo(Integer value) {
			addCriterion("box_id =", value, "boxId");
			return (Criteria) this;
		}

		public Criteria andBoxIdNotEqualTo(Integer value) {
			addCriterion("box_id <>", value, "boxId");
			return (Criteria) this;
		}

		public Criteria andBoxIdGreaterThan(Integer value) {
			addCriterion("box_id >", value, "boxId");
			return (Criteria) this;
		}

		public Criteria andBoxIdGreaterThanOrEqualTo(Integer value) {
			addCriterion("box_id >=", value, "boxId");
			return (Criteria) this;
		}

		public Criteria andBoxIdLessThan(Integer value) {
			addCriterion("box_id <", value, "boxId");
			return (Criteria) this;
		}

		public Criteria andBoxIdLessThanOrEqualTo(Integer value) {
			addCriterion("box_id <=", value, "boxId");
			return (Criteria) this;
		}

		public Criteria andBoxIdIn(List<Integer> values) {
			addCriterion("box_id in", values, "boxId");
			return (Criteria) this;
		}

		public Criteria andBoxIdNotIn(List<Integer> values) {
			addCriterion("box_id not in", values, "boxId");
			return (Criteria) this;
		}

		public Criteria andBoxIdBetween(Integer value1, Integer value2) {
			addCriterion("box_id between", value1, value2, "boxId");
			return (Criteria) this;
		}

		public Criteria andBoxIdNotBetween(Integer value1, Integer value2) {
			addCriterion("box_id not between", value1, value2, "boxId");
			return (Criteria) this;
		}

		public Criteria andDownloadSnIsNull() {
			addCriterion("download_sn is null");
			return (Criteria) this;
		}

		public Criteria andDownloadSnIsNotNull() {
			addCriterion("download_sn is not null");
			return (Criteria) this;
		}

		public Criteria andDownloadSnEqualTo(String value) {
			addCriterion("download_sn =", value, "downloadSn");
			return (Criteria) this;
		}

		public Criteria andDownloadSnNotEqualTo(String value) {
			addCriterion("download_sn <>", value, "downloadSn");
			return (Criteria) this;
		}

		public Criteria andDownloadSnGreaterThan(String value) {
			addCriterion("download_sn >", value, "downloadSn");
			return (Criteria) this;
		}

		public Criteria andDownloadSnGreaterThanOrEqualTo(String value) {
			addCriterion("download_sn >=", value, "downloadSn");
			return (Criteria) this;
		}

		public Criteria andDownloadSnLessThan(String value) {
			addCriterion("download_sn <", value, "downloadSn");
			return (Criteria) this;
		}

		public Criteria andDownloadSnLessThanOrEqualTo(String value) {
			addCriterion("download_sn <=", value, "downloadSn");
			return (Criteria) this;
		}

		public Criteria andDownloadSnLike(String value) {
			addCriterion("download_sn like", value, "downloadSn");
			return (Criteria) this;
		}

		public Criteria andDownloadSnNotLike(String value) {
			addCriterion("download_sn not like", value, "downloadSn");
			return (Criteria) this;
		}

		public Criteria andDownloadSnIn(List<String> values) {
			addCriterion("download_sn in", values, "downloadSn");
			return (Criteria) this;
		}

		public Criteria andDownloadSnNotIn(List<String> values) {
			addCriterion("download_sn not in", values, "downloadSn");
			return (Criteria) this;
		}

		public Criteria andDownloadSnBetween(String value1, String value2) {
			addCriterion("download_sn between", value1, value2, "downloadSn");
			return (Criteria) this;
		}

		public Criteria andDownloadSnNotBetween(String value1, String value2) {
			addCriterion("download_sn not between", value1, value2, "downloadSn");
			return (Criteria) this;
		}

		public Criteria andVerifyPwdIsNull() {
			addCriterion("verify_pwd is null");
			return (Criteria) this;
		}

		public Criteria andVerifyPwdIsNotNull() {
			addCriterion("verify_pwd is not null");
			return (Criteria) this;
		}

		public Criteria andVerifyPwdEqualTo(String value) {
			addCriterion("verify_pwd =", value, "verifyPwd");
			return (Criteria) this;
		}

		public Criteria andVerifyPwdNotEqualTo(String value) {
			addCriterion("verify_pwd <>", value, "verifyPwd");
			return (Criteria) this;
		}

		public Criteria andVerifyPwdGreaterThan(String value) {
			addCriterion("verify_pwd >", value, "verifyPwd");
			return (Criteria) this;
		}

		public Criteria andVerifyPwdGreaterThanOrEqualTo(String value) {
			addCriterion("verify_pwd >=", value, "verifyPwd");
			return (Criteria) this;
		}

		public Criteria andVerifyPwdLessThan(String value) {
			addCriterion("verify_pwd <", value, "verifyPwd");
			return (Criteria) this;
		}

		public Criteria andVerifyPwdLessThanOrEqualTo(String value) {
			addCriterion("verify_pwd <=", value, "verifyPwd");
			return (Criteria) this;
		}

		public Criteria andVerifyPwdLike(String value) {
			addCriterion("verify_pwd like", value, "verifyPwd");
			return (Criteria) this;
		}

		public Criteria andVerifyPwdNotLike(String value) {
			addCriterion("verify_pwd not like", value, "verifyPwd");
			return (Criteria) this;
		}

		public Criteria andVerifyPwdIn(List<String> values) {
			addCriterion("verify_pwd in", values, "verifyPwd");
			return (Criteria) this;
		}

		public Criteria andVerifyPwdNotIn(List<String> values) {
			addCriterion("verify_pwd not in", values, "verifyPwd");
			return (Criteria) this;
		}

		public Criteria andVerifyPwdBetween(String value1, String value2) {
			addCriterion("verify_pwd between", value1, value2, "verifyPwd");
			return (Criteria) this;
		}

		public Criteria andVerifyPwdNotBetween(String value1, String value2) {
			addCriterion("verify_pwd not between", value1, value2, "verifyPwd");
			return (Criteria) this;
		}

		public Criteria andDownloadVerifyIsNull() {
			addCriterion("download_verify is null");
			return (Criteria) this;
		}

		public Criteria andDownloadVerifyIsNotNull() {
			addCriterion("download_verify is not null");
			return (Criteria) this;
		}

		public Criteria andDownloadVerifyEqualTo(String value) {
			addCriterion("download_verify =", value, "downloadVerify");
			return (Criteria) this;
		}

		public Criteria andDownloadVerifyNotEqualTo(String value) {
			addCriterion("download_verify <>", value, "downloadVerify");
			return (Criteria) this;
		}

		public Criteria andDownloadVerifyGreaterThan(String value) {
			addCriterion("download_verify >", value, "downloadVerify");
			return (Criteria) this;
		}

		public Criteria andDownloadVerifyGreaterThanOrEqualTo(String value) {
			addCriterion("download_verify >=", value, "downloadVerify");
			return (Criteria) this;
		}

		public Criteria andDownloadVerifyLessThan(String value) {
			addCriterion("download_verify <", value, "downloadVerify");
			return (Criteria) this;
		}

		public Criteria andDownloadVerifyLessThanOrEqualTo(String value) {
			addCriterion("download_verify <=", value, "downloadVerify");
			return (Criteria) this;
		}

		public Criteria andDownloadVerifyLike(String value) {
			addCriterion("download_verify like", value, "downloadVerify");
			return (Criteria) this;
		}

		public Criteria andDownloadVerifyNotLike(String value) {
			addCriterion("download_verify not like", value, "downloadVerify");
			return (Criteria) this;
		}

		public Criteria andDownloadVerifyIn(List<String> values) {
			addCriterion("download_verify in", values, "downloadVerify");
			return (Criteria) this;
		}

		public Criteria andDownloadVerifyNotIn(List<String> values) {
			addCriterion("download_verify not in", values, "downloadVerify");
			return (Criteria) this;
		}

		public Criteria andDownloadVerifyBetween(String value1, String value2) {
			addCriterion("download_verify between", value1, value2, "downloadVerify");
			return (Criteria) this;
		}

		public Criteria andDownloadVerifyNotBetween(String value1, String value2) {
			addCriterion("download_verify not between", value1, value2, "downloadVerify");
			return (Criteria) this;
		}

		public Criteria andProviderKeyIsNull() {
			addCriterion("provider_key is null");
			return (Criteria) this;
		}

		public Criteria andProviderKeyIsNotNull() {
			addCriterion("provider_key is not null");
			return (Criteria) this;
		}

		public Criteria andProviderKeyEqualTo(String value) {
			addCriterion("provider_key =", value, "providerKey");
			return (Criteria) this;
		}

		public Criteria andProviderKeyNotEqualTo(String value) {
			addCriterion("provider_key <>", value, "providerKey");
			return (Criteria) this;
		}

		public Criteria andProviderKeyGreaterThan(String value) {
			addCriterion("provider_key >", value, "providerKey");
			return (Criteria) this;
		}

		public Criteria andProviderKeyGreaterThanOrEqualTo(String value) {
			addCriterion("provider_key >=", value, "providerKey");
			return (Criteria) this;
		}

		public Criteria andProviderKeyLessThan(String value) {
			addCriterion("provider_key <", value, "providerKey");
			return (Criteria) this;
		}

		public Criteria andProviderKeyLessThanOrEqualTo(String value) {
			addCriterion("provider_key <=", value, "providerKey");
			return (Criteria) this;
		}

		public Criteria andProviderKeyLike(String value) {
			addCriterion("provider_key like", value, "providerKey");
			return (Criteria) this;
		}

		public Criteria andProviderKeyNotLike(String value) {
			addCriterion("provider_key not like", value, "providerKey");
			return (Criteria) this;
		}

		public Criteria andProviderKeyIn(List<String> values) {
			addCriterion("provider_key in", values, "providerKey");
			return (Criteria) this;
		}

		public Criteria andProviderKeyNotIn(List<String> values) {
			addCriterion("provider_key not in", values, "providerKey");
			return (Criteria) this;
		}

		public Criteria andProviderKeyBetween(String value1, String value2) {
			addCriterion("provider_key between", value1, value2, "providerKey");
			return (Criteria) this;
		}

		public Criteria andProviderKeyNotBetween(String value1, String value2) {
			addCriterion("provider_key not between", value1, value2, "providerKey");
			return (Criteria) this;
		}

		public Criteria andDownloadKeyIsNull() {
			addCriterion("download_key is null");
			return (Criteria) this;
		}

		public Criteria andDownloadKeyIsNotNull() {
			addCriterion("download_key is not null");
			return (Criteria) this;
		}

		public Criteria andDownloadKeyEqualTo(String value) {
			addCriterion("download_key =", value, "downloadKey");
			return (Criteria) this;
		}

		public Criteria andDownloadKeyNotEqualTo(String value) {
			addCriterion("download_key <>", value, "downloadKey");
			return (Criteria) this;
		}

		public Criteria andDownloadKeyGreaterThan(String value) {
			addCriterion("download_key >", value, "downloadKey");
			return (Criteria) this;
		}

		public Criteria andDownloadKeyGreaterThanOrEqualTo(String value) {
			addCriterion("download_key >=", value, "downloadKey");
			return (Criteria) this;
		}

		public Criteria andDownloadKeyLessThan(String value) {
			addCriterion("download_key <", value, "downloadKey");
			return (Criteria) this;
		}

		public Criteria andDownloadKeyLessThanOrEqualTo(String value) {
			addCriterion("download_key <=", value, "downloadKey");
			return (Criteria) this;
		}

		public Criteria andDownloadKeyLike(String value) {
			addCriterion("download_key like", value, "downloadKey");
			return (Criteria) this;
		}

		public Criteria andDownloadKeyNotLike(String value) {
			addCriterion("download_key not like", value, "downloadKey");
			return (Criteria) this;
		}

		public Criteria andDownloadKeyIn(List<String> values) {
			addCriterion("download_key in", values, "downloadKey");
			return (Criteria) this;
		}

		public Criteria andDownloadKeyNotIn(List<String> values) {
			addCriterion("download_key not in", values, "downloadKey");
			return (Criteria) this;
		}

		public Criteria andDownloadKeyBetween(String value1, String value2) {
			addCriterion("download_key between", value1, value2, "downloadKey");
			return (Criteria) this;
		}

		public Criteria andDownloadKeyNotBetween(String value1, String value2) {
			addCriterion("download_key not between", value1, value2, "downloadKey");
			return (Criteria) this;
		}

		public Criteria andCtimeIsNull() {
			addCriterion("ctime is null");
			return (Criteria) this;
		}

		public Criteria andCtimeIsNotNull() {
			addCriterion("ctime is not null");
			return (Criteria) this;
		}

		public Criteria andCtimeEqualTo(Date value) {
			addCriterion("ctime =", value, "ctime");
			return (Criteria) this;
		}

		public Criteria andCtimeNotEqualTo(Date value) {
			addCriterion("ctime <>", value, "ctime");
			return (Criteria) this;
		}

		public Criteria andCtimeGreaterThan(Date value) {
			addCriterion("ctime >", value, "ctime");
			return (Criteria) this;
		}

		public Criteria andCtimeGreaterThanOrEqualTo(Date value) {
			addCriterion("ctime >=", value, "ctime");
			return (Criteria) this;
		}

		public Criteria andCtimeLessThan(Date value) {
			addCriterion("ctime <", value, "ctime");
			return (Criteria) this;
		}

		public Criteria andCtimeLessThanOrEqualTo(Date value) {
			addCriterion("ctime <=", value, "ctime");
			return (Criteria) this;
		}

		public Criteria andCtimeIn(List<Date> values) {
			addCriterion("ctime in", values, "ctime");
			return (Criteria) this;
		}

		public Criteria andCtimeNotIn(List<Date> values) {
			addCriterion("ctime not in", values, "ctime");
			return (Criteria) this;
		}

		public Criteria andCtimeBetween(Date value1, Date value2) {
			addCriterion("ctime between", value1, value2, "ctime");
			return (Criteria) this;
		}

		public Criteria andCtimeNotBetween(Date value1, Date value2) {
			addCriterion("ctime not between", value1, value2, "ctime");
			return (Criteria) this;
		}

		public Criteria andStatIsNull() {
			addCriterion("stat is null");
			return (Criteria) this;
		}

		public Criteria andStatIsNotNull() {
			addCriterion("stat is not null");
			return (Criteria) this;
		}

		public Criteria andStatEqualTo(Integer value) {
			addCriterion("stat =", value, "stat");
			return (Criteria) this;
		}

		public Criteria andStatNotEqualTo(Integer value) {
			addCriterion("stat <>", value, "stat");
			return (Criteria) this;
		}

		public Criteria andStatGreaterThan(Integer value) {
			addCriterion("stat >", value, "stat");
			return (Criteria) this;
		}

		public Criteria andStatGreaterThanOrEqualTo(Integer value) {
			addCriterion("stat >=", value, "stat");
			return (Criteria) this;
		}

		public Criteria andStatLessThan(Integer value) {
			addCriterion("stat <", value, "stat");
			return (Criteria) this;
		}

		public Criteria andStatLessThanOrEqualTo(Integer value) {
			addCriterion("stat <=", value, "stat");
			return (Criteria) this;
		}

		public Criteria andStatIn(List<Integer> values) {
			addCriterion("stat in", values, "stat");
			return (Criteria) this;
		}

		public Criteria andStatNotIn(List<Integer> values) {
			addCriterion("stat not in", values, "stat");
			return (Criteria) this;
		}

		public Criteria andStatBetween(Integer value1, Integer value2) {
			addCriterion("stat between", value1, value2, "stat");
			return (Criteria) this;
		}

		public Criteria andStatNotBetween(Integer value1, Integer value2) {
			addCriterion("stat not between", value1, value2, "stat");
			return (Criteria) this;
		}

		public Criteria andIpIsNull() {
			addCriterion("ip is null");
			return (Criteria) this;
		}

		public Criteria andIpIsNotNull() {
			addCriterion("ip is not null");
			return (Criteria) this;
		}

		public Criteria andIpEqualTo(String value) {
			addCriterion("ip =", value, "ip");
			return (Criteria) this;
		}

		public Criteria andIpNotEqualTo(String value) {
			addCriterion("ip <>", value, "ip");
			return (Criteria) this;
		}

		public Criteria andIpGreaterThan(String value) {
			addCriterion("ip >", value, "ip");
			return (Criteria) this;
		}

		public Criteria andIpGreaterThanOrEqualTo(String value) {
			addCriterion("ip >=", value, "ip");
			return (Criteria) this;
		}

		public Criteria andIpLessThan(String value) {
			addCriterion("ip <", value, "ip");
			return (Criteria) this;
		}

		public Criteria andIpLessThanOrEqualTo(String value) {
			addCriterion("ip <=", value, "ip");
			return (Criteria) this;
		}

		public Criteria andIpLike(String value) {
			addCriterion("ip like", value, "ip");
			return (Criteria) this;
		}

		public Criteria andIpNotLike(String value) {
			addCriterion("ip not like", value, "ip");
			return (Criteria) this;
		}

		public Criteria andIpIn(List<String> values) {
			addCriterion("ip in", values, "ip");
			return (Criteria) this;
		}

		public Criteria andIpNotIn(List<String> values) {
			addCriterion("ip not in", values, "ip");
			return (Criteria) this;
		}

		public Criteria andIpBetween(String value1, String value2) {
			addCriterion("ip between", value1, value2, "ip");
			return (Criteria) this;
		}

		public Criteria andIpNotBetween(String value1, String value2) {
			addCriterion("ip not between", value1, value2, "ip");
			return (Criteria) this;
		}

		public Criteria andBatchIdIsNull() {
			addCriterion("batch_id is null");
			return (Criteria) this;
		}

		public Criteria andBatchIdIsNotNull() {
			addCriterion("batch_id is not null");
			return (Criteria) this;
		}

		public Criteria andBatchIdEqualTo(Integer value) {
			addCriterion("batch_id =", value, "batchId");
			return (Criteria) this;
		}

		public Criteria andBatchIdNotEqualTo(Integer value) {
			addCriterion("batch_id <>", value, "batchId");
			return (Criteria) this;
		}

		public Criteria andBatchIdGreaterThan(Integer value) {
			addCriterion("batch_id >", value, "batchId");
			return (Criteria) this;
		}

		public Criteria andBatchIdGreaterThanOrEqualTo(Integer value) {
			addCriterion("batch_id >=", value, "batchId");
			return (Criteria) this;
		}

		public Criteria andBatchIdLessThan(Integer value) {
			addCriterion("batch_id <", value, "batchId");
			return (Criteria) this;
		}

		public Criteria andBatchIdLessThanOrEqualTo(Integer value) {
			addCriterion("batch_id <=", value, "batchId");
			return (Criteria) this;
		}

		public Criteria andBatchIdIn(List<Integer> values) {
			addCriterion("batch_id in", values, "batchId");
			return (Criteria) this;
		}

		public Criteria andBatchIdNotIn(List<Integer> values) {
			addCriterion("batch_id not in", values, "batchId");
			return (Criteria) this;
		}

		public Criteria andBatchIdBetween(Integer value1, Integer value2) {
			addCriterion("batch_id between", value1, value2, "batchId");
			return (Criteria) this;
		}

		public Criteria andBatchIdNotBetween(Integer value1, Integer value2) {
			addCriterion("batch_id not between", value1, value2, "batchId");
			return (Criteria) this;
		}
	}

	/**
	 * This class was generated by MyBatis Generator. This class corresponds to the database table portal_box_log
	 * @mbg.generated  Tue Jul 30 17:30:05 CST 2019
	 */
	public static class Criterion {
		private String condition;
		private Object value;
		private Object secondValue;
		private boolean noValue;
		private boolean singleValue;
		private boolean betweenValue;
		private boolean listValue;
		private String typeHandler;

		public String getCondition() {
			return condition;
		}

		public Object getValue() {
			return value;
		}

		public Object getSecondValue() {
			return secondValue;
		}

		public boolean isNoValue() {
			return noValue;
		}

		public boolean isSingleValue() {
			return singleValue;
		}

		public boolean isBetweenValue() {
			return betweenValue;
		}

		public boolean isListValue() {
			return listValue;
		}

		public String getTypeHandler() {
			return typeHandler;
		}

		protected Criterion(String condition) {
			super();
			this.condition = condition;
			this.typeHandler = null;
			this.noValue = true;
		}

		protected Criterion(String condition, Object value, String typeHandler) {
			super();
			this.condition = condition;
			this.value = value;
			this.typeHandler = typeHandler;
			if (value instanceof List<?>) {
				this.listValue = true;
			} else {
				this.singleValue = true;
			}
		}

		protected Criterion(String condition, Object value) {
			this(condition, value, null);
		}

		protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
			super();
			this.condition = condition;
			this.value = value;
			this.secondValue = secondValue;
			this.typeHandler = typeHandler;
			this.betweenValue = true;
		}

		protected Criterion(String condition, Object value, Object secondValue) {
			this(condition, value, secondValue, null);
		}
	}

	/**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table portal_box_log
     *
     * @mbg.generated do_not_delete_during_merge Mon Aug 27 10:59:34 CST 2018
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }
}