package tw.gov.ndc.emsg.mydata.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class PortalProviderExample {
    /**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table portal_provider
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	protected String orderByClause;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table portal_provider
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	protected boolean distinct;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table portal_provider
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */protected List<Criteria> oredCriteria;

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table portal_provider
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */public PortalProviderExample(){oredCriteria=new ArrayList<Criteria>();}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table portal_provider
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table portal_provider
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	public String getOrderByClause() {
		return orderByClause;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table portal_provider
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table portal_provider
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	public boolean isDistinct() {
		return distinct;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table portal_provider
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */public List<Criteria> getOredCriteria(){return oredCriteria;}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table portal_provider
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	public void or(Criteria criteria) {
		oredCriteria.add(criteria);
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table portal_provider
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	public Criteria or() {
		Criteria criteria = createCriteriaInternal();
		oredCriteria.add(criteria);
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table portal_provider
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	public Criteria createCriteria() {
		Criteria criteria = createCriteriaInternal();
		if (oredCriteria.size() == 0) {
			oredCriteria.add(criteria);
		}
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table portal_provider
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	protected Criteria createCriteriaInternal() {
		Criteria criteria = new Criteria();
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table portal_provider
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */
	public void clear() {
		oredCriteria.clear();
		orderByClause = null;
		distinct = false;
	}

	/**
	 * This class was generated by MyBatis Generator. This class corresponds to the database table portal_provider
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */protected abstract static class GeneratedCriteria {protected List<Criterion> criteria;protected GeneratedCriteria(){super();criteria=new ArrayList<Criterion>();}public boolean isValid(){return criteria.size() > 0;}public List<Criterion> getAllCriteria(){return criteria;}public List<Criterion> getCriteria(){return criteria;}protected void addCriterion(String condition){if (condition == null){throw new RuntimeException("Value for condition cannot be null");}criteria.add(new Criterion(condition));}protected void addCriterion(String condition,Object value,String property){if (value == null){throw new RuntimeException("Value for " + property + " cannot be null");}criteria.add(new Criterion(condition,value));}protected void addCriterion(String condition,Object value1,Object value2,String property){if (value1 == null || value2 == null){throw new RuntimeException("Between values for " + property + " cannot be null");}criteria.add(new Criterion(condition,value1,value2));}public Criteria andProviderIdIsNull(){addCriterion("provider_id is null");return (Criteria)this;}public Criteria andProviderIdIsNotNull(){addCriterion("provider_id is not null");return (Criteria)this;}public Criteria andProviderIdEqualTo(Integer value){addCriterion("provider_id =",value,"providerId");return (Criteria)this;}public Criteria andProviderIdNotEqualTo(Integer value){addCriterion("provider_id <>",value,"providerId");return (Criteria)this;}public Criteria andProviderIdGreaterThan(Integer value){addCriterion("provider_id >",value,"providerId");return (Criteria)this;}public Criteria andProviderIdGreaterThanOrEqualTo(Integer value){addCriterion("provider_id >=",value,"providerId");return (Criteria)this;}public Criteria andProviderIdLessThan(Integer value){addCriterion("provider_id <",value,"providerId");return (Criteria)this;}public Criteria andProviderIdLessThanOrEqualTo(Integer value){addCriterion("provider_id <=",value,"providerId");return (Criteria)this;}public Criteria andProviderIdIn(List<Integer> values){addCriterion("provider_id in",values,"providerId");return (Criteria)this;}public Criteria andProviderIdNotIn(List<Integer> values){addCriterion("provider_id not in",values,"providerId");return (Criteria)this;}public Criteria andProviderIdBetween(Integer value1,Integer value2){addCriterion("provider_id between",value1,value2,"providerId");return (Criteria)this;}public Criteria andProviderIdNotBetween(Integer value1,Integer value2){addCriterion("provider_id not between",value1,value2,"providerId");return (Criteria)this;}public Criteria andNameIsNull(){addCriterion("name is null");return (Criteria)this;}public Criteria andNameIsNotNull(){addCriterion("name is not null");return (Criteria)this;}public Criteria andNameEqualTo(String value){addCriterion("name =",value,"name");return (Criteria)this;}public Criteria andNameNotEqualTo(String value){addCriterion("name <>",value,"name");return (Criteria)this;}public Criteria andNameGreaterThan(String value){addCriterion("name >",value,"name");return (Criteria)this;}public Criteria andNameGreaterThanOrEqualTo(String value){addCriterion("name >=",value,"name");return (Criteria)this;}public Criteria andNameLessThan(String value){addCriterion("name <",value,"name");return (Criteria)this;}public Criteria andNameLessThanOrEqualTo(String value){addCriterion("name <=",value,"name");return (Criteria)this;}public Criteria andNameLike(String value){addCriterion("name like",value,"name");return (Criteria)this;}public Criteria andNameNotLike(String value){addCriterion("name not like",value,"name");return (Criteria)this;}public Criteria andNameIn(List<String> values){addCriterion("name in",values,"name");return (Criteria)this;}public Criteria andNameNotIn(List<String> values){addCriterion("name not in",values,"name");return (Criteria)this;}public Criteria andNameBetween(String value1,String value2){addCriterion("name between",value1,value2,"name");return (Criteria)this;}public Criteria andNameNotBetween(String value1,String value2){addCriterion("name not between",value1,value2,"name");return (Criteria)this;}public Criteria andAddressIsNull(){addCriterion("address is null");return (Criteria)this;}public Criteria andAddressIsNotNull(){addCriterion("address is not null");return (Criteria)this;}public Criteria andAddressEqualTo(String value){addCriterion("address =",value,"address");return (Criteria)this;}public Criteria andAddressNotEqualTo(String value){addCriterion("address <>",value,"address");return (Criteria)this;}public Criteria andAddressGreaterThan(String value){addCriterion("address >",value,"address");return (Criteria)this;}public Criteria andAddressGreaterThanOrEqualTo(String value){addCriterion("address >=",value,"address");return (Criteria)this;}public Criteria andAddressLessThan(String value){addCriterion("address <",value,"address");return (Criteria)this;}public Criteria andAddressLessThanOrEqualTo(String value){addCriterion("address <=",value,"address");return (Criteria)this;}public Criteria andAddressLike(String value){addCriterion("address like",value,"address");return (Criteria)this;}public Criteria andAddressNotLike(String value){addCriterion("address not like",value,"address");return (Criteria)this;}public Criteria andAddressIn(List<String> values){addCriterion("address in",values,"address");return (Criteria)this;}public Criteria andAddressNotIn(List<String> values){addCriterion("address not in",values,"address");return (Criteria)this;}public Criteria andAddressBetween(String value1,String value2){addCriterion("address between",value1,value2,"address");return (Criteria)this;}public Criteria andAddressNotBetween(String value1,String value2){addCriterion("address not between",value1,value2,"address");return (Criteria)this;}public Criteria andTelphoneIsNull(){addCriterion("telphone is null");return (Criteria)this;}public Criteria andTelphoneIsNotNull(){addCriterion("telphone is not null");return (Criteria)this;}public Criteria andTelphoneEqualTo(String value){addCriterion("telphone =",value,"telphone");return (Criteria)this;}public Criteria andTelphoneNotEqualTo(String value){addCriterion("telphone <>",value,"telphone");return (Criteria)this;}public Criteria andTelphoneGreaterThan(String value){addCriterion("telphone >",value,"telphone");return (Criteria)this;}public Criteria andTelphoneGreaterThanOrEqualTo(String value){addCriterion("telphone >=",value,"telphone");return (Criteria)this;}public Criteria andTelphoneLessThan(String value){addCriterion("telphone <",value,"telphone");return (Criteria)this;}public Criteria andTelphoneLessThanOrEqualTo(String value){addCriterion("telphone <=",value,"telphone");return (Criteria)this;}public Criteria andTelphoneLike(String value){addCriterion("telphone like",value,"telphone");return (Criteria)this;}public Criteria andTelphoneNotLike(String value){addCriterion("telphone not like",value,"telphone");return (Criteria)this;}public Criteria andTelphoneIn(List<String> values){addCriterion("telphone in",values,"telphone");return (Criteria)this;}public Criteria andTelphoneNotIn(List<String> values){addCriterion("telphone not in",values,"telphone");return (Criteria)this;}public Criteria andTelphoneBetween(String value1,String value2){addCriterion("telphone between",value1,value2,"telphone");return (Criteria)this;}public Criteria andTelphoneNotBetween(String value1,String value2){addCriterion("telphone not between",value1,value2,"telphone");return (Criteria)this;}public Criteria andEmailIsNull(){addCriterion("email is null");return (Criteria)this;}public Criteria andEmailIsNotNull(){addCriterion("email is not null");return (Criteria)this;}public Criteria andEmailEqualTo(String value){addCriterion("email =",value,"email");return (Criteria)this;}public Criteria andEmailNotEqualTo(String value){addCriterion("email <>",value,"email");return (Criteria)this;}public Criteria andEmailGreaterThan(String value){addCriterion("email >",value,"email");return (Criteria)this;}public Criteria andEmailGreaterThanOrEqualTo(String value){addCriterion("email >=",value,"email");return (Criteria)this;}public Criteria andEmailLessThan(String value){addCriterion("email <",value,"email");return (Criteria)this;}public Criteria andEmailLessThanOrEqualTo(String value){addCriterion("email <=",value,"email");return (Criteria)this;}public Criteria andEmailLike(String value){addCriterion("email like",value,"email");return (Criteria)this;}public Criteria andEmailNotLike(String value){addCriterion("email not like",value,"email");return (Criteria)this;}public Criteria andEmailIn(List<String> values){addCriterion("email in",values,"email");return (Criteria)this;}public Criteria andEmailNotIn(List<String> values){addCriterion("email not in",values,"email");return (Criteria)this;}public Criteria andEmailBetween(String value1,String value2){addCriterion("email between",value1,value2,"email");return (Criteria)this;}public Criteria andEmailNotBetween(String value1,String value2){addCriterion("email not between",value1,value2,"email");return (Criteria)this;}public Criteria andContactInfoIsNull(){addCriterion("contact_info is null");return (Criteria)this;}public Criteria andContactInfoIsNotNull(){addCriterion("contact_info is not null");return (Criteria)this;}public Criteria andContactInfoEqualTo(String value){addCriterion("contact_info =",value,"contactInfo");return (Criteria)this;}public Criteria andContactInfoNotEqualTo(String value){addCriterion("contact_info <>",value,"contactInfo");return (Criteria)this;}public Criteria andContactInfoGreaterThan(String value){addCriterion("contact_info >",value,"contactInfo");return (Criteria)this;}public Criteria andContactInfoGreaterThanOrEqualTo(String value){addCriterion("contact_info >=",value,"contactInfo");return (Criteria)this;}public Criteria andContactInfoLessThan(String value){addCriterion("contact_info <",value,"contactInfo");return (Criteria)this;}public Criteria andContactInfoLessThanOrEqualTo(String value){addCriterion("contact_info <=",value,"contactInfo");return (Criteria)this;}public Criteria andContactInfoLike(String value){addCriterion("contact_info like",value,"contactInfo");return (Criteria)this;}public Criteria andContactInfoNotLike(String value){addCriterion("contact_info not like",value,"contactInfo");return (Criteria)this;}public Criteria andContactInfoIn(List<String> values){addCriterion("contact_info in",values,"contactInfo");return (Criteria)this;}public Criteria andContactInfoNotIn(List<String> values){addCriterion("contact_info not in",values,"contactInfo");return (Criteria)this;}public Criteria andContactInfoBetween(String value1,String value2){addCriterion("contact_info between",value1,value2,"contactInfo");return (Criteria)this;}public Criteria andContactInfoNotBetween(String value1,String value2){addCriterion("contact_info not between",value1,value2,"contactInfo");return (Criteria)this;}public Criteria andStatusIsNull(){addCriterion("status is null");return (Criteria)this;}public Criteria andStatusIsNotNull(){addCriterion("status is not null");return (Criteria)this;}public Criteria andStatusEqualTo(Integer value){addCriterion("status =",value,"status");return (Criteria)this;}public Criteria andStatusNotEqualTo(Integer value){addCriterion("status <>",value,"status");return (Criteria)this;}public Criteria andStatusGreaterThan(Integer value){addCriterion("status >",value,"status");return (Criteria)this;}public Criteria andStatusGreaterThanOrEqualTo(Integer value){addCriterion("status >=",value,"status");return (Criteria)this;}public Criteria andStatusLessThan(Integer value){addCriterion("status <",value,"status");return (Criteria)this;}public Criteria andStatusLessThanOrEqualTo(Integer value){addCriterion("status <=",value,"status");return (Criteria)this;}public Criteria andStatusIn(List<Integer> values){addCriterion("status in",values,"status");return (Criteria)this;}public Criteria andStatusNotIn(List<Integer> values){addCriterion("status not in",values,"status");return (Criteria)this;}public Criteria andStatusBetween(Integer value1,Integer value2){addCriterion("status between",value1,value2,"status");return (Criteria)this;}public Criteria andStatusNotBetween(Integer value1,Integer value2){addCriterion("status not between",value1,value2,"status");return (Criteria)this;}public Criteria andEgovAccIsNull(){addCriterion("egov_acc is null");return (Criteria)this;}public Criteria andEgovAccIsNotNull(){addCriterion("egov_acc is not null");return (Criteria)this;}public Criteria andEgovAccEqualTo(String value){addCriterion("egov_acc =",value,"egovAcc");return (Criteria)this;}public Criteria andEgovAccNotEqualTo(String value){addCriterion("egov_acc <>",value,"egovAcc");return (Criteria)this;}public Criteria andEgovAccGreaterThan(String value){addCriterion("egov_acc >",value,"egovAcc");return (Criteria)this;}public Criteria andEgovAccGreaterThanOrEqualTo(String value){addCriterion("egov_acc >=",value,"egovAcc");return (Criteria)this;}public Criteria andEgovAccLessThan(String value){addCriterion("egov_acc <",value,"egovAcc");return (Criteria)this;}public Criteria andEgovAccLessThanOrEqualTo(String value){addCriterion("egov_acc <=",value,"egovAcc");return (Criteria)this;}public Criteria andEgovAccLike(String value){addCriterion("egov_acc like",value,"egovAcc");return (Criteria)this;}public Criteria andEgovAccNotLike(String value){addCriterion("egov_acc not like",value,"egovAcc");return (Criteria)this;}public Criteria andEgovAccIn(List<String> values){addCriterion("egov_acc in",values,"egovAcc");return (Criteria)this;}public Criteria andEgovAccNotIn(List<String> values){addCriterion("egov_acc not in",values,"egovAcc");return (Criteria)this;}public Criteria andEgovAccBetween(String value1,String value2){addCriterion("egov_acc between",value1,value2,"egovAcc");return (Criteria)this;}public Criteria andEgovAccNotBetween(String value1,String value2){addCriterion("egov_acc not between",value1,value2,"egovAcc");return (Criteria)this;}public Criteria andApplyNameIsNull(){addCriterion("apply_name is null");return (Criteria)this;}public Criteria andApplyNameIsNotNull(){addCriterion("apply_name is not null");return (Criteria)this;}public Criteria andApplyNameEqualTo(String value){addCriterion("apply_name =",value,"applyName");return (Criteria)this;}public Criteria andApplyNameNotEqualTo(String value){addCriterion("apply_name <>",value,"applyName");return (Criteria)this;}public Criteria andApplyNameGreaterThan(String value){addCriterion("apply_name >",value,"applyName");return (Criteria)this;}public Criteria andApplyNameGreaterThanOrEqualTo(String value){addCriterion("apply_name >=",value,"applyName");return (Criteria)this;}public Criteria andApplyNameLessThan(String value){addCriterion("apply_name <",value,"applyName");return (Criteria)this;}public Criteria andApplyNameLessThanOrEqualTo(String value){addCriterion("apply_name <=",value,"applyName");return (Criteria)this;}public Criteria andApplyNameLike(String value){addCriterion("apply_name like",value,"applyName");return (Criteria)this;}public Criteria andApplyNameNotLike(String value){addCriterion("apply_name not like",value,"applyName");return (Criteria)this;}public Criteria andApplyNameIn(List<String> values){addCriterion("apply_name in",values,"applyName");return (Criteria)this;}public Criteria andApplyNameNotIn(List<String> values){addCriterion("apply_name not in",values,"applyName");return (Criteria)this;}public Criteria andApplyNameBetween(String value1,String value2){addCriterion("apply_name between",value1,value2,"applyName");return (Criteria)this;}public Criteria andApplyNameNotBetween(String value1,String value2){addCriterion("apply_name not between",value1,value2,"applyName");return (Criteria)this;}public Criteria andShowAllIsNull(){addCriterion("show_all is null");return (Criteria)this;}public Criteria andShowAllIsNotNull(){addCriterion("show_all is not null");return (Criteria)this;}public Criteria andShowAllEqualTo(Integer value){addCriterion("show_all =",value,"showAll");return (Criteria)this;}public Criteria andShowAllNotEqualTo(Integer value){addCriterion("show_all <>",value,"showAll");return (Criteria)this;}public Criteria andShowAllGreaterThan(Integer value){addCriterion("show_all >",value,"showAll");return (Criteria)this;}public Criteria andShowAllGreaterThanOrEqualTo(Integer value){addCriterion("show_all >=",value,"showAll");return (Criteria)this;}public Criteria andShowAllLessThan(Integer value){addCriterion("show_all <",value,"showAll");return (Criteria)this;}public Criteria andShowAllLessThanOrEqualTo(Integer value){addCriterion("show_all <=",value,"showAll");return (Criteria)this;}public Criteria andShowAllIn(List<Integer> values){addCriterion("show_all in",values,"showAll");return (Criteria)this;}public Criteria andShowAllNotIn(List<Integer> values){addCriterion("show_all not in",values,"showAll");return (Criteria)this;}public Criteria andShowAllBetween(Integer value1,Integer value2){addCriterion("show_all between",value1,value2,"showAll");return (Criteria)this;}public Criteria andShowAllNotBetween(Integer value1,Integer value2){addCriterion("show_all not between",value1,value2,"showAll");return (Criteria)this;}public Criteria andCtimeIsNull(){addCriterion("ctime is null");return (Criteria)this;}public Criteria andCtimeIsNotNull(){addCriterion("ctime is not null");return (Criteria)this;}public Criteria andCtimeEqualTo(Date value){addCriterion("ctime =",value,"ctime");return (Criteria)this;}public Criteria andCtimeNotEqualTo(Date value){addCriterion("ctime <>",value,"ctime");return (Criteria)this;}public Criteria andCtimeGreaterThan(Date value){addCriterion("ctime >",value,"ctime");return (Criteria)this;}public Criteria andCtimeGreaterThanOrEqualTo(Date value){addCriterion("ctime >=",value,"ctime");return (Criteria)this;}public Criteria andCtimeLessThan(Date value){addCriterion("ctime <",value,"ctime");return (Criteria)this;}public Criteria andCtimeLessThanOrEqualTo(Date value){addCriterion("ctime <=",value,"ctime");return (Criteria)this;}public Criteria andCtimeIn(List<Date> values){addCriterion("ctime in",values,"ctime");return (Criteria)this;}public Criteria andCtimeNotIn(List<Date> values){addCriterion("ctime not in",values,"ctime");return (Criteria)this;}public Criteria andCtimeBetween(Date value1,Date value2){addCriterion("ctime between",value1,value2,"ctime");return (Criteria)this;}public Criteria andCtimeNotBetween(Date value1,Date value2){addCriterion("ctime not between",value1,value2,"ctime");return (Criteria)this;}public Criteria andUtimeIsNull(){addCriterion("utime is null");return (Criteria)this;}public Criteria andUtimeIsNotNull(){addCriterion("utime is not null");return (Criteria)this;}public Criteria andUtimeEqualTo(Date value){addCriterion("utime =",value,"utime");return (Criteria)this;}public Criteria andUtimeNotEqualTo(Date value){addCriterion("utime <>",value,"utime");return (Criteria)this;}public Criteria andUtimeGreaterThan(Date value){addCriterion("utime >",value,"utime");return (Criteria)this;}public Criteria andUtimeGreaterThanOrEqualTo(Date value){addCriterion("utime >=",value,"utime");return (Criteria)this;}public Criteria andUtimeLessThan(Date value){addCriterion("utime <",value,"utime");return (Criteria)this;}public Criteria andUtimeLessThanOrEqualTo(Date value){addCriterion("utime <=",value,"utime");return (Criteria)this;}public Criteria andUtimeIn(List<Date> values){addCriterion("utime in",values,"utime");return (Criteria)this;}public Criteria andUtimeNotIn(List<Date> values){addCriterion("utime not in",values,"utime");return (Criteria)this;}public Criteria andUtimeBetween(Date value1,Date value2){addCriterion("utime between",value1,value2,"utime");return (Criteria)this;}public Criteria andUtimeNotBetween(Date value1,Date value2){addCriterion("utime not between",value1,value2,"utime");return (Criteria)this;}public Criteria andCuserIsNull(){addCriterion("cuser is null");return (Criteria)this;}public Criteria andCuserIsNotNull(){addCriterion("cuser is not null");return (Criteria)this;}public Criteria andCuserEqualTo(String value){addCriterion("cuser =",value,"cuser");return (Criteria)this;}public Criteria andCuserNotEqualTo(String value){addCriterion("cuser <>",value,"cuser");return (Criteria)this;}public Criteria andCuserGreaterThan(String value){addCriterion("cuser >",value,"cuser");return (Criteria)this;}public Criteria andCuserGreaterThanOrEqualTo(String value){addCriterion("cuser >=",value,"cuser");return (Criteria)this;}public Criteria andCuserLessThan(String value){addCriterion("cuser <",value,"cuser");return (Criteria)this;}public Criteria andCuserLessThanOrEqualTo(String value){addCriterion("cuser <=",value,"cuser");return (Criteria)this;}public Criteria andCuserLike(String value){addCriterion("cuser like",value,"cuser");return (Criteria)this;}public Criteria andCuserNotLike(String value){addCriterion("cuser not like",value,"cuser");return (Criteria)this;}public Criteria andCuserIn(List<String> values){addCriterion("cuser in",values,"cuser");return (Criteria)this;}public Criteria andCuserNotIn(List<String> values){addCriterion("cuser not in",values,"cuser");return (Criteria)this;}public Criteria andCuserBetween(String value1,String value2){addCriterion("cuser between",value1,value2,"cuser");return (Criteria)this;}public Criteria andCuserNotBetween(String value1,String value2){addCriterion("cuser not between",value1,value2,"cuser");return (Criteria)this;}public Criteria andUuserIsNull(){addCriterion("uuser is null");return (Criteria)this;}public Criteria andUuserIsNotNull(){addCriterion("uuser is not null");return (Criteria)this;}public Criteria andUuserEqualTo(String value){addCriterion("uuser =",value,"uuser");return (Criteria)this;}public Criteria andUuserNotEqualTo(String value){addCriterion("uuser <>",value,"uuser");return (Criteria)this;}public Criteria andUuserGreaterThan(String value){addCriterion("uuser >",value,"uuser");return (Criteria)this;}public Criteria andUuserGreaterThanOrEqualTo(String value){addCriterion("uuser >=",value,"uuser");return (Criteria)this;}public Criteria andUuserLessThan(String value){addCriterion("uuser <",value,"uuser");return (Criteria)this;}public Criteria andUuserLessThanOrEqualTo(String value){addCriterion("uuser <=",value,"uuser");return (Criteria)this;}public Criteria andUuserLike(String value){addCriterion("uuser like",value,"uuser");return (Criteria)this;}public Criteria andUuserNotLike(String value){addCriterion("uuser not like",value,"uuser");return (Criteria)this;}public Criteria andUuserIn(List<String> values){addCriterion("uuser in",values,"uuser");return (Criteria)this;}public Criteria andUuserNotIn(List<String> values){addCriterion("uuser not in",values,"uuser");return (Criteria)this;}public Criteria andUuserBetween(String value1,String value2){addCriterion("uuser between",value1,value2,"uuser");return (Criteria)this;}public Criteria andUuserNotBetween(String value1,String value2){addCriterion("uuser not between",value1,value2,"uuser");return (Criteria)this;}public Criteria andSpAgreeTimeIsNull(){addCriterion("sp_agree_time is null");return (Criteria)this;}public Criteria andSpAgreeTimeIsNotNull(){addCriterion("sp_agree_time is not null");return (Criteria)this;}public Criteria andSpAgreeTimeEqualTo(Date value){addCriterion("sp_agree_time =",value,"spAgreeTime");return (Criteria)this;}public Criteria andSpAgreeTimeNotEqualTo(Date value){addCriterion("sp_agree_time <>",value,"spAgreeTime");return (Criteria)this;}public Criteria andSpAgreeTimeGreaterThan(Date value){addCriterion("sp_agree_time >",value,"spAgreeTime");return (Criteria)this;}public Criteria andSpAgreeTimeGreaterThanOrEqualTo(Date value){addCriterion("sp_agree_time >=",value,"spAgreeTime");return (Criteria)this;}public Criteria andSpAgreeTimeLessThan(Date value){addCriterion("sp_agree_time <",value,"spAgreeTime");return (Criteria)this;}public Criteria andSpAgreeTimeLessThanOrEqualTo(Date value){addCriterion("sp_agree_time <=",value,"spAgreeTime");return (Criteria)this;}public Criteria andSpAgreeTimeIn(List<Date> values){addCriterion("sp_agree_time in",values,"spAgreeTime");return (Criteria)this;}public Criteria andSpAgreeTimeNotIn(List<Date> values){addCriterion("sp_agree_time not in",values,"spAgreeTime");return (Criteria)this;}public Criteria andSpAgreeTimeBetween(Date value1,Date value2){addCriterion("sp_agree_time between",value1,value2,"spAgreeTime");return (Criteria)this;}public Criteria andSpAgreeTimeNotBetween(Date value1,Date value2){addCriterion("sp_agree_time not between",value1,value2,"spAgreeTime");return (Criteria)this;}public Criteria andDpAgreeTimeIsNull(){addCriterion("dp_agree_time is null");return (Criteria)this;}public Criteria andDpAgreeTimeIsNotNull(){addCriterion("dp_agree_time is not null");return (Criteria)this;}public Criteria andDpAgreeTimeEqualTo(Date value){addCriterion("dp_agree_time =",value,"dpAgreeTime");return (Criteria)this;}public Criteria andDpAgreeTimeNotEqualTo(Date value){addCriterion("dp_agree_time <>",value,"dpAgreeTime");return (Criteria)this;}public Criteria andDpAgreeTimeGreaterThan(Date value){addCriterion("dp_agree_time >",value,"dpAgreeTime");return (Criteria)this;}public Criteria andDpAgreeTimeGreaterThanOrEqualTo(Date value){addCriterion("dp_agree_time >=",value,"dpAgreeTime");return (Criteria)this;}public Criteria andDpAgreeTimeLessThan(Date value){addCriterion("dp_agree_time <",value,"dpAgreeTime");return (Criteria)this;}public Criteria andDpAgreeTimeLessThanOrEqualTo(Date value){addCriterion("dp_agree_time <=",value,"dpAgreeTime");return (Criteria)this;}public Criteria andDpAgreeTimeIn(List<Date> values){addCriterion("dp_agree_time in",values,"dpAgreeTime");return (Criteria)this;}public Criteria andDpAgreeTimeNotIn(List<Date> values){addCriterion("dp_agree_time not in",values,"dpAgreeTime");return (Criteria)this;}public Criteria andDpAgreeTimeBetween(Date value1,Date value2){addCriterion("dp_agree_time between",value1,value2,"dpAgreeTime");return (Criteria)this;}public Criteria andDpAgreeTimeNotBetween(Date value1,Date value2){addCriterion("dp_agree_time not between",value1,value2,"dpAgreeTime");return (Criteria)this;}public Criteria andOidIsNull(){addCriterion("oid is null");return (Criteria)this;}public Criteria andOidIsNotNull(){addCriterion("oid is not null");return (Criteria)this;}public Criteria andOidEqualTo(String value){addCriterion("oid =",value,"oid");return (Criteria)this;}public Criteria andOidNotEqualTo(String value){addCriterion("oid <>",value,"oid");return (Criteria)this;}public Criteria andOidGreaterThan(String value){addCriterion("oid >",value,"oid");return (Criteria)this;}public Criteria andOidGreaterThanOrEqualTo(String value){addCriterion("oid >=",value,"oid");return (Criteria)this;}public Criteria andOidLessThan(String value){addCriterion("oid <",value,"oid");return (Criteria)this;}public Criteria andOidLessThanOrEqualTo(String value){addCriterion("oid <=",value,"oid");return (Criteria)this;}public Criteria andOidLike(String value){addCriterion("oid like",value,"oid");return (Criteria)this;}public Criteria andOidNotLike(String value){addCriterion("oid not like",value,"oid");return (Criteria)this;}public Criteria andOidIn(List<String> values){addCriterion("oid in",values,"oid");return (Criteria)this;}public Criteria andOidNotIn(List<String> values){addCriterion("oid not in",values,"oid");return (Criteria)this;}public Criteria andOidBetween(String value1,String value2){addCriterion("oid between",value1,value2,"oid");return (Criteria)this;}public Criteria andOidNotBetween(String value1,String value2){addCriterion("oid not between",value1,value2,"oid");return (Criteria)this;}}

	/**
	 * This class was generated by MyBatis Generator. This class corresponds to the database table portal_provider
	 * @mbg.generated  Thu Jul 05 11:36:56 CST 2018
	 */public static class Criterion {private String condition;private Object value;private Object secondValue;private boolean noValue;private boolean singleValue;private boolean betweenValue;private boolean listValue;private String typeHandler;public String getCondition(){return condition;}public Object getValue(){return value;}public Object getSecondValue(){return secondValue;}public boolean isNoValue(){return noValue;}public boolean isSingleValue(){return singleValue;}public boolean isBetweenValue(){return betweenValue;}public boolean isListValue(){return listValue;}public String getTypeHandler(){return typeHandler;}protected Criterion(String condition){super();this.condition=condition;this.typeHandler=null;this.noValue=true;}protected Criterion(String condition,Object value,String typeHandler){super();this.condition=condition;this.value=value;this.typeHandler=typeHandler;if (value instanceof List<?>){this.listValue=true;} else {this.singleValue=true;}}protected Criterion(String condition,Object value){this(condition,value,null);}protected Criterion(String condition,Object value,Object secondValue,String typeHandler){super();this.condition=condition;this.value=value;this.secondValue=secondValue;this.typeHandler=typeHandler;this.betweenValue=true;}protected Criterion(String condition,Object value,Object secondValue){this(condition,value,secondValue,null);}}

	/**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table portal_provider
     *
     * @mbg.generated do_not_delete_during_merge Tue Oct 03 16:17:10 CST 2017
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }
}