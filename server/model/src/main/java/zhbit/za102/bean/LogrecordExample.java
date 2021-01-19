package zhbit.za102.bean;

import java.util.ArrayList;
import java.util.List;

public class LogrecordExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public LogrecordExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

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

        public Criteria andLogidIsNull() {
            addCriterion("logid is null");
            return (Criteria) this;
        }

        public Criteria andLogidIsNotNull() {
            addCriterion("logid is not null");
            return (Criteria) this;
        }

        public Criteria andLogidEqualTo(Integer value) {
            addCriterion("logid =", value, "logid");
            return (Criteria) this;
        }

        public Criteria andLogidNotEqualTo(Integer value) {
            addCriterion("logid <>", value, "logid");
            return (Criteria) this;
        }

        public Criteria andLogidGreaterThan(Integer value) {
            addCriterion("logid >", value, "logid");
            return (Criteria) this;
        }

        public Criteria andLogidGreaterThanOrEqualTo(Integer value) {
            addCriterion("logid >=", value, "logid");
            return (Criteria) this;
        }

        public Criteria andLogidLessThan(Integer value) {
            addCriterion("logid <", value, "logid");
            return (Criteria) this;
        }

        public Criteria andLogidLessThanOrEqualTo(Integer value) {
            addCriterion("logid <=", value, "logid");
            return (Criteria) this;
        }

        public Criteria andLogidIn(List<Integer> values) {
            addCriterion("logid in", values, "logid");
            return (Criteria) this;
        }

        public Criteria andLogidNotIn(List<Integer> values) {
            addCriterion("logid not in", values, "logid");
            return (Criteria) this;
        }

        public Criteria andLogidBetween(Integer value1, Integer value2) {
            addCriterion("logid between", value1, value2, "logid");
            return (Criteria) this;
        }

        public Criteria andLogidNotBetween(Integer value1, Integer value2) {
            addCriterion("logid not between", value1, value2, "logid");
            return (Criteria) this;
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(String value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(String value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(String value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(String value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(String value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(String value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLike(String value) {
            addCriterion("id like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotLike(String value) {
            addCriterion("id not like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<String> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<String> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(String value1, String value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(String value1, String value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andCardidIsNull() {
            addCriterion("cardid is null");
            return (Criteria) this;
        }

        public Criteria andCardidIsNotNull() {
            addCriterion("cardid is not null");
            return (Criteria) this;
        }

        public Criteria andCardidEqualTo(String value) {
            addCriterion("cardid =", value, "cardid");
            return (Criteria) this;
        }

        public Criteria andCardidNotEqualTo(String value) {
            addCriterion("cardid <>", value, "cardid");
            return (Criteria) this;
        }

        public Criteria andCardidGreaterThan(String value) {
            addCriterion("cardid >", value, "cardid");
            return (Criteria) this;
        }

        public Criteria andCardidGreaterThanOrEqualTo(String value) {
            addCriterion("cardid >=", value, "cardid");
            return (Criteria) this;
        }

        public Criteria andCardidLessThan(String value) {
            addCriterion("cardid <", value, "cardid");
            return (Criteria) this;
        }

        public Criteria andCardidLessThanOrEqualTo(String value) {
            addCriterion("cardid <=", value, "cardid");
            return (Criteria) this;
        }

        public Criteria andCardidLike(String value) {
            addCriterion("cardid like", value, "cardid");
            return (Criteria) this;
        }

        public Criteria andCardidNotLike(String value) {
            addCriterion("cardid not like", value, "cardid");
            return (Criteria) this;
        }

        public Criteria andCardidIn(List<String> values) {
            addCriterion("cardid in", values, "cardid");
            return (Criteria) this;
        }

        public Criteria andCardidNotIn(List<String> values) {
            addCriterion("cardid not in", values, "cardid");
            return (Criteria) this;
        }

        public Criteria andCardidBetween(String value1, String value2) {
            addCriterion("cardid between", value1, value2, "cardid");
            return (Criteria) this;
        }

        public Criteria andCardidNotBetween(String value1, String value2) {
            addCriterion("cardid not between", value1, value2, "cardid");
            return (Criteria) this;
        }

        public Criteria andChangevalueIsNull() {
            addCriterion("changevalue is null");
            return (Criteria) this;
        }

        public Criteria andChangevalueIsNotNull() {
            addCriterion("changevalue is not null");
            return (Criteria) this;
        }

        public Criteria andChangevalueEqualTo(String value) {
            addCriterion("changevalue =", value, "changevalue");
            return (Criteria) this;
        }

        public Criteria andChangevalueNotEqualTo(String value) {
            addCriterion("changevalue <>", value, "changevalue");
            return (Criteria) this;
        }

        public Criteria andChangevalueGreaterThan(String value) {
            addCriterion("changevalue >", value, "changevalue");
            return (Criteria) this;
        }

        public Criteria andChangevalueGreaterThanOrEqualTo(String value) {
            addCriterion("changevalue >=", value, "changevalue");
            return (Criteria) this;
        }

        public Criteria andChangevalueLessThan(String value) {
            addCriterion("changevalue <", value, "changevalue");
            return (Criteria) this;
        }

        public Criteria andChangevalueLessThanOrEqualTo(String value) {
            addCriterion("changevalue <=", value, "changevalue");
            return (Criteria) this;
        }

        public Criteria andChangevalueLike(String value) {
            addCriterion("changevalue like", value, "changevalue");
            return (Criteria) this;
        }

        public Criteria andChangevalueNotLike(String value) {
            addCriterion("changevalue not like", value, "changevalue");
            return (Criteria) this;
        }

        public Criteria andChangevalueIn(List<String> values) {
            addCriterion("changevalue in", values, "changevalue");
            return (Criteria) this;
        }

        public Criteria andChangevalueNotIn(List<String> values) {
            addCriterion("changevalue not in", values, "changevalue");
            return (Criteria) this;
        }

        public Criteria andChangevalueBetween(String value1, String value2) {
            addCriterion("changevalue between", value1, value2, "changevalue");
            return (Criteria) this;
        }

        public Criteria andChangevalueNotBetween(String value1, String value2) {
            addCriterion("changevalue not between", value1, value2, "changevalue");
            return (Criteria) this;
        }

        public Criteria andGentimeIsNull() {
            addCriterion("gentime is null");
            return (Criteria) this;
        }

        public Criteria andGentimeIsNotNull() {
            addCriterion("gentime is not null");
            return (Criteria) this;
        }

        public Criteria andGentimeEqualTo(String value) {
            addCriterion("gentime =", value, "gentime");
            return (Criteria) this;
        }

        public Criteria andGentimeNotEqualTo(String value) {
            addCriterion("gentime <>", value, "gentime");
            return (Criteria) this;
        }

        public Criteria andGentimeGreaterThan(String value) {
            addCriterion("gentime >", value, "gentime");
            return (Criteria) this;
        }

        public Criteria andGentimeGreaterThanOrEqualTo(String value) {
            addCriterion("gentime >=", value, "gentime");
            return (Criteria) this;
        }

        public Criteria andGentimeLessThan(String value) {
            addCriterion("gentime <", value, "gentime");
            return (Criteria) this;
        }

        public Criteria andGentimeLessThanOrEqualTo(String value) {
            addCriterion("gentime <=", value, "gentime");
            return (Criteria) this;
        }

        public Criteria andGentimeLike(String value) {
            addCriterion("gentime like", value, "gentime");
            return (Criteria) this;
        }

        public Criteria andGentimeNotLike(String value) {
            addCriterion("gentime not like", value, "gentime");
            return (Criteria) this;
        }

        public Criteria andGentimeIn(List<String> values) {
            addCriterion("gentime in", values, "gentime");
            return (Criteria) this;
        }

        public Criteria andGentimeNotIn(List<String> values) {
            addCriterion("gentime not in", values, "gentime");
            return (Criteria) this;
        }

        public Criteria andGentimeBetween(String value1, String value2) {
            addCriterion("gentime between", value1, value2, "gentime");
            return (Criteria) this;
        }

        public Criteria andGentimeNotBetween(String value1, String value2) {
            addCriterion("gentime not between", value1, value2, "gentime");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

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