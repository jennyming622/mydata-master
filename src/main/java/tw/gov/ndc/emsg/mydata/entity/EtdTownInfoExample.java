package tw.gov.ndc.emsg.mydata.entity;

import java.util.ArrayList;
import java.util.List;

public class EtdTownInfoExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table etd_town_info
     *
     * @mbg.generated Fri Dec 27 14:17:46 CST 2019
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table etd_town_info
     *
     * @mbg.generated Fri Dec 27 14:17:46 CST 2019
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table etd_town_info
     *
     * @mbg.generated Fri Dec 27 14:17:46 CST 2019
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table etd_town_info
     *
     * @mbg.generated Fri Dec 27 14:17:46 CST 2019
     */
    public EtdTownInfoExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table etd_town_info
     *
     * @mbg.generated Fri Dec 27 14:17:46 CST 2019
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table etd_town_info
     *
     * @mbg.generated Fri Dec 27 14:17:46 CST 2019
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table etd_town_info
     *
     * @mbg.generated Fri Dec 27 14:17:46 CST 2019
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table etd_town_info
     *
     * @mbg.generated Fri Dec 27 14:17:46 CST 2019
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table etd_town_info
     *
     * @mbg.generated Fri Dec 27 14:17:46 CST 2019
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table etd_town_info
     *
     * @mbg.generated Fri Dec 27 14:17:46 CST 2019
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table etd_town_info
     *
     * @mbg.generated Fri Dec 27 14:17:46 CST 2019
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table etd_town_info
     *
     * @mbg.generated Fri Dec 27 14:17:46 CST 2019
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table etd_town_info
     *
     * @mbg.generated Fri Dec 27 14:17:46 CST 2019
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table etd_town_info
     *
     * @mbg.generated Fri Dec 27 14:17:46 CST 2019
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table etd_town_info
     *
     * @mbg.generated Fri Dec 27 14:17:46 CST 2019
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

        public Criteria andHsnCdIsNull() {
            addCriterion("hsn_cd is null");
            return (Criteria) this;
        }

        public Criteria andHsnCdIsNotNull() {
            addCriterion("hsn_cd is not null");
            return (Criteria) this;
        }

        public Criteria andHsnCdEqualTo(String value) {
            addCriterion("hsn_cd =", value, "hsnCd");
            return (Criteria) this;
        }

        public Criteria andHsnCdNotEqualTo(String value) {
            addCriterion("hsn_cd <>", value, "hsnCd");
            return (Criteria) this;
        }

        public Criteria andHsnCdGreaterThan(String value) {
            addCriterion("hsn_cd >", value, "hsnCd");
            return (Criteria) this;
        }

        public Criteria andHsnCdGreaterThanOrEqualTo(String value) {
            addCriterion("hsn_cd >=", value, "hsnCd");
            return (Criteria) this;
        }

        public Criteria andHsnCdLessThan(String value) {
            addCriterion("hsn_cd <", value, "hsnCd");
            return (Criteria) this;
        }

        public Criteria andHsnCdLessThanOrEqualTo(String value) {
            addCriterion("hsn_cd <=", value, "hsnCd");
            return (Criteria) this;
        }

        public Criteria andHsnCdLike(String value) {
            addCriterion("hsn_cd like", value, "hsnCd");
            return (Criteria) this;
        }

        public Criteria andHsnCdNotLike(String value) {
            addCriterion("hsn_cd not like", value, "hsnCd");
            return (Criteria) this;
        }

        public Criteria andHsnCdIn(List<String> values) {
            addCriterion("hsn_cd in", values, "hsnCd");
            return (Criteria) this;
        }

        public Criteria andHsnCdNotIn(List<String> values) {
            addCriterion("hsn_cd not in", values, "hsnCd");
            return (Criteria) this;
        }

        public Criteria andHsnCdBetween(String value1, String value2) {
            addCriterion("hsn_cd between", value1, value2, "hsnCd");
            return (Criteria) this;
        }

        public Criteria andHsnCdNotBetween(String value1, String value2) {
            addCriterion("hsn_cd not between", value1, value2, "hsnCd");
            return (Criteria) this;
        }

        public Criteria andTownCdIsNull() {
            addCriterion("town_cd is null");
            return (Criteria) this;
        }

        public Criteria andTownCdIsNotNull() {
            addCriterion("town_cd is not null");
            return (Criteria) this;
        }

        public Criteria andTownCdEqualTo(String value) {
            addCriterion("town_cd =", value, "townCd");
            return (Criteria) this;
        }

        public Criteria andTownCdNotEqualTo(String value) {
            addCriterion("town_cd <>", value, "townCd");
            return (Criteria) this;
        }

        public Criteria andTownCdGreaterThan(String value) {
            addCriterion("town_cd >", value, "townCd");
            return (Criteria) this;
        }

        public Criteria andTownCdGreaterThanOrEqualTo(String value) {
            addCriterion("town_cd >=", value, "townCd");
            return (Criteria) this;
        }

        public Criteria andTownCdLessThan(String value) {
            addCriterion("town_cd <", value, "townCd");
            return (Criteria) this;
        }

        public Criteria andTownCdLessThanOrEqualTo(String value) {
            addCriterion("town_cd <=", value, "townCd");
            return (Criteria) this;
        }

        public Criteria andTownCdLike(String value) {
            addCriterion("town_cd like", value, "townCd");
            return (Criteria) this;
        }

        public Criteria andTownCdNotLike(String value) {
            addCriterion("town_cd not like", value, "townCd");
            return (Criteria) this;
        }

        public Criteria andTownCdIn(List<String> values) {
            addCriterion("town_cd in", values, "townCd");
            return (Criteria) this;
        }

        public Criteria andTownCdNotIn(List<String> values) {
            addCriterion("town_cd not in", values, "townCd");
            return (Criteria) this;
        }

        public Criteria andTownCdBetween(String value1, String value2) {
            addCriterion("town_cd between", value1, value2, "townCd");
            return (Criteria) this;
        }

        public Criteria andTownCdNotBetween(String value1, String value2) {
            addCriterion("town_cd not between", value1, value2, "townCd");
            return (Criteria) this;
        }

        public Criteria andTownNmIsNull() {
            addCriterion("town_nm is null");
            return (Criteria) this;
        }

        public Criteria andTownNmIsNotNull() {
            addCriterion("town_nm is not null");
            return (Criteria) this;
        }

        public Criteria andTownNmEqualTo(String value) {
            addCriterion("town_nm =", value, "townNm");
            return (Criteria) this;
        }

        public Criteria andTownNmNotEqualTo(String value) {
            addCriterion("town_nm <>", value, "townNm");
            return (Criteria) this;
        }

        public Criteria andTownNmGreaterThan(String value) {
            addCriterion("town_nm >", value, "townNm");
            return (Criteria) this;
        }

        public Criteria andTownNmGreaterThanOrEqualTo(String value) {
            addCriterion("town_nm >=", value, "townNm");
            return (Criteria) this;
        }

        public Criteria andTownNmLessThan(String value) {
            addCriterion("town_nm <", value, "townNm");
            return (Criteria) this;
        }

        public Criteria andTownNmLessThanOrEqualTo(String value) {
            addCriterion("town_nm <=", value, "townNm");
            return (Criteria) this;
        }

        public Criteria andTownNmLike(String value) {
            addCriterion("town_nm like", value, "townNm");
            return (Criteria) this;
        }

        public Criteria andTownNmNotLike(String value) {
            addCriterion("town_nm not like", value, "townNm");
            return (Criteria) this;
        }

        public Criteria andTownNmIn(List<String> values) {
            addCriterion("town_nm in", values, "townNm");
            return (Criteria) this;
        }

        public Criteria andTownNmNotIn(List<String> values) {
            addCriterion("town_nm not in", values, "townNm");
            return (Criteria) this;
        }

        public Criteria andTownNmBetween(String value1, String value2) {
            addCriterion("town_nm between", value1, value2, "townNm");
            return (Criteria) this;
        }

        public Criteria andTownNmNotBetween(String value1, String value2) {
            addCriterion("town_nm not between", value1, value2, "townNm");
            return (Criteria) this;
        }

        public Criteria andTownNmEnIsNull() {
            addCriterion("town_nm_en is null");
            return (Criteria) this;
        }

        public Criteria andTownNmEnIsNotNull() {
            addCriterion("town_nm_en is not null");
            return (Criteria) this;
        }

        public Criteria andTownNmEnEqualTo(String value) {
            addCriterion("town_nm_en =", value, "townNmEn");
            return (Criteria) this;
        }

        public Criteria andTownNmEnNotEqualTo(String value) {
            addCriterion("town_nm_en <>", value, "townNmEn");
            return (Criteria) this;
        }

        public Criteria andTownNmEnGreaterThan(String value) {
            addCriterion("town_nm_en >", value, "townNmEn");
            return (Criteria) this;
        }

        public Criteria andTownNmEnGreaterThanOrEqualTo(String value) {
            addCriterion("town_nm_en >=", value, "townNmEn");
            return (Criteria) this;
        }

        public Criteria andTownNmEnLessThan(String value) {
            addCriterion("town_nm_en <", value, "townNmEn");
            return (Criteria) this;
        }

        public Criteria andTownNmEnLessThanOrEqualTo(String value) {
            addCriterion("town_nm_en <=", value, "townNmEn");
            return (Criteria) this;
        }

        public Criteria andTownNmEnLike(String value) {
            addCriterion("town_nm_en like", value, "townNmEn");
            return (Criteria) this;
        }

        public Criteria andTownNmEnNotLike(String value) {
            addCriterion("town_nm_en not like", value, "townNmEn");
            return (Criteria) this;
        }

        public Criteria andTownNmEnIn(List<String> values) {
            addCriterion("town_nm_en in", values, "townNmEn");
            return (Criteria) this;
        }

        public Criteria andTownNmEnNotIn(List<String> values) {
            addCriterion("town_nm_en not in", values, "townNmEn");
            return (Criteria) this;
        }

        public Criteria andTownNmEnBetween(String value1, String value2) {
            addCriterion("town_nm_en between", value1, value2, "townNmEn");
            return (Criteria) this;
        }

        public Criteria andTownNmEnNotBetween(String value1, String value2) {
            addCriterion("town_nm_en not between", value1, value2, "townNmEn");
            return (Criteria) this;
        }

        public Criteria andTaxTypeIsNull() {
            addCriterion("tax_type is null");
            return (Criteria) this;
        }

        public Criteria andTaxTypeIsNotNull() {
            addCriterion("tax_type is not null");
            return (Criteria) this;
        }

        public Criteria andTaxTypeEqualTo(String value) {
            addCriterion("tax_type =", value, "taxType");
            return (Criteria) this;
        }

        public Criteria andTaxTypeNotEqualTo(String value) {
            addCriterion("tax_type <>", value, "taxType");
            return (Criteria) this;
        }

        public Criteria andTaxTypeGreaterThan(String value) {
            addCriterion("tax_type >", value, "taxType");
            return (Criteria) this;
        }

        public Criteria andTaxTypeGreaterThanOrEqualTo(String value) {
            addCriterion("tax_type >=", value, "taxType");
            return (Criteria) this;
        }

        public Criteria andTaxTypeLessThan(String value) {
            addCriterion("tax_type <", value, "taxType");
            return (Criteria) this;
        }

        public Criteria andTaxTypeLessThanOrEqualTo(String value) {
            addCriterion("tax_type <=", value, "taxType");
            return (Criteria) this;
        }

        public Criteria andTaxTypeLike(String value) {
            addCriterion("tax_type like", value, "taxType");
            return (Criteria) this;
        }

        public Criteria andTaxTypeNotLike(String value) {
            addCriterion("tax_type not like", value, "taxType");
            return (Criteria) this;
        }

        public Criteria andTaxTypeIn(List<String> values) {
            addCriterion("tax_type in", values, "taxType");
            return (Criteria) this;
        }

        public Criteria andTaxTypeNotIn(List<String> values) {
            addCriterion("tax_type not in", values, "taxType");
            return (Criteria) this;
        }

        public Criteria andTaxTypeBetween(String value1, String value2) {
            addCriterion("tax_type between", value1, value2, "taxType");
            return (Criteria) this;
        }

        public Criteria andTaxTypeNotBetween(String value1, String value2) {
            addCriterion("tax_type not between", value1, value2, "taxType");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table etd_town_info
     *
     * @mbg.generated do_not_delete_during_merge Fri Dec 27 14:17:46 CST 2019
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table etd_town_info
     *
     * @mbg.generated Fri Dec 27 14:17:46 CST 2019
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
}