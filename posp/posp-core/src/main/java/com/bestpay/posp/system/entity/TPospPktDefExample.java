package com.bestpay.posp.system.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class TPospPktDefExample {
	

	protected String orderByClause;
    
    protected String groupByClause;
    
	protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public TPospPktDefExample() {
        oredCriteria = new ArrayList<Criteria>();
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
        groupByClause = null;
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

        public Criteria andPktIdIsNull() {
            addCriterion("PKT_ID is null");
            return (Criteria) this;
        }

        public Criteria andPktIdIsNotNull() {
            addCriterion("PKT_ID is not null");
            return (Criteria) this;
        }

        public Criteria andPktIdEqualTo(Long value) {
            addCriterion("PKT_ID =", value, "pktId");
            return (Criteria) this;
        }

        public Criteria andPktIdNotEqualTo(Long value) {
            addCriterion("PKT_ID <>", value, "pktId");
            return (Criteria) this;
        }

        public Criteria andPktIdGreaterThan(Long value) {
            addCriterion("PKT_ID >", value, "pktId");
            return (Criteria) this;
        }

        public Criteria andPktIdGreaterThanOrEqualTo(Long value) {
            addCriterion("PKT_ID >=", value, "pktId");
            return (Criteria) this;
        }

        public Criteria andPktIdLessThan(Long value) {
            addCriterion("PKT_ID <", value, "pktId");
            return (Criteria) this;
        }

        public Criteria andPktIdLessThanOrEqualTo(Long value) {
            addCriterion("PKT_ID <=", value, "pktId");
            return (Criteria) this;
        }

        public Criteria andPktIdIn(List<Long> values) {
            addCriterion("PKT_ID in", values, "pktId");
            return (Criteria) this;
        }

        public Criteria andPktIdNotIn(List<Long> values) {
            addCriterion("PKT_ID not in", values, "pktId");
            return (Criteria) this;
        }

        public Criteria andPktIdBetween(Long value1, Long value2) {
            addCriterion("PKT_ID between", value1, value2, "pktId");
            return (Criteria) this;
        }

        public Criteria andPktIdNotBetween(Long value1, Long value2) {
            addCriterion("PKT_ID not between", value1, value2, "pktId");
            return (Criteria) this;
        }

        public Criteria andChannelCodeIsNull() {
            addCriterion("CHANNEL_CODE is null");
            return (Criteria) this;
        }

        public Criteria andChannelCodeIsNotNull() {
            addCriterion("CHANNEL_CODE is not null");
            return (Criteria) this;
        }

        public Criteria andChannelCodeEqualTo(String value) {
            addCriterion("CHANNEL_CODE =", value, "channelCode");
            return (Criteria) this;
        }

        public Criteria andChannelCodeNotEqualTo(String value) {
            addCriterion("CHANNEL_CODE <>", value, "channelCode");
            return (Criteria) this;
        }

        public Criteria andChannelCodeGreaterThan(String value) {
            addCriterion("CHANNEL_CODE >", value, "channelCode");
            return (Criteria) this;
        }

        public Criteria andChannelCodeGreaterThanOrEqualTo(String value) {
            addCriterion("CHANNEL_CODE >=", value, "channelCode");
            return (Criteria) this;
        }

        public Criteria andChannelCodeLessThan(String value) {
            addCriterion("CHANNEL_CODE <", value, "channelCode");
            return (Criteria) this;
        }

        public Criteria andChannelCodeLessThanOrEqualTo(String value) {
            addCriterion("CHANNEL_CODE <=", value, "channelCode");
            return (Criteria) this;
        }

        public Criteria andChannelCodeLike(String value) {
            addCriterion("CHANNEL_CODE like", value, "channelCode");
            return (Criteria) this;
        }

        public Criteria andChannelCodeNotLike(String value) {
            addCriterion("CHANNEL_CODE not like", value, "channelCode");
            return (Criteria) this;
        }

        public Criteria andChannelCodeIn(List<String> values) {
            addCriterion("CHANNEL_CODE in", values, "channelCode");
            return (Criteria) this;
        }

        public Criteria andChannelCodeNotIn(List<String> values) {
            addCriterion("CHANNEL_CODE not in", values, "channelCode");
            return (Criteria) this;
        }

        public Criteria andChannelCodeBetween(String value1, String value2) {
            addCriterion("CHANNEL_CODE between", value1, value2, "channelCode");
            return (Criteria) this;
        }

        public Criteria andChannelCodeNotBetween(String value1, String value2) {
            addCriterion("CHANNEL_CODE not between", value1, value2, "channelCode");
            return (Criteria) this;
        }

        public Criteria andMsgTypeIsNull() {
            addCriterion("MSG_TYPE is null");
            return (Criteria) this;
        }

        public Criteria andMsgTypeIsNotNull() {
            addCriterion("MSG_TYPE is not null");
            return (Criteria) this;
        }

        public Criteria andMsgTypeEqualTo(String value) {
            addCriterion("MSG_TYPE =", value, "msgType");
            return (Criteria) this;
        }

        public Criteria andMsgTypeNotEqualTo(String value) {
            addCriterion("MSG_TYPE <>", value, "msgType");
            return (Criteria) this;
        }

        public Criteria andMsgTypeGreaterThan(String value) {
            addCriterion("MSG_TYPE >", value, "msgType");
            return (Criteria) this;
        }

        public Criteria andMsgTypeGreaterThanOrEqualTo(String value) {
            addCriterion("MSG_TYPE >=", value, "msgType");
            return (Criteria) this;
        }

        public Criteria andMsgTypeLessThan(String value) {
            addCriterion("MSG_TYPE <", value, "msgType");
            return (Criteria) this;
        }

        public Criteria andMsgTypeLessThanOrEqualTo(String value) {
            addCriterion("MSG_TYPE <=", value, "msgType");
            return (Criteria) this;
        }

        public Criteria andMsgTypeLike(String value) {
            addCriterion("MSG_TYPE like", value, "msgType");
            return (Criteria) this;
        }

        public Criteria andMsgTypeNotLike(String value) {
            addCriterion("MSG_TYPE not like", value, "msgType");
            return (Criteria) this;
        }

        public Criteria andMsgTypeIn(List<String> values) {
            addCriterion("MSG_TYPE in", values, "msgType");
            return (Criteria) this;
        }

        public Criteria andMsgTypeNotIn(List<String> values) {
            addCriterion("MSG_TYPE not in", values, "msgType");
            return (Criteria) this;
        }

        public Criteria andMsgTypeBetween(String value1, String value2) {
            addCriterion("MSG_TYPE between", value1, value2, "msgType");
            return (Criteria) this;
        }

        public Criteria andMsgTypeNotBetween(String value1, String value2) {
            addCriterion("MSG_TYPE not between", value1, value2, "msgType");
            return (Criteria) this;
        }

        public Criteria andBitIsNull() {
            addCriterion("BIT is null");
            return (Criteria) this;
        }

        public Criteria andBitIsNotNull() {
            addCriterion("BIT is not null");
            return (Criteria) this;
        }

        public Criteria andBitEqualTo(Short value) {
            addCriterion("BIT =", value, "bit");
            return (Criteria) this;
        }

        public Criteria andBitNotEqualTo(Short value) {
            addCriterion("BIT <>", value, "bit");
            return (Criteria) this;
        }

        public Criteria andBitGreaterThan(Short value) {
            addCriterion("BIT >", value, "bit");
            return (Criteria) this;
        }

        public Criteria andBitGreaterThanOrEqualTo(Short value) {
            addCriterion("BIT >=", value, "bit");
            return (Criteria) this;
        }

        public Criteria andBitLessThan(Short value) {
            addCriterion("BIT <", value, "bit");
            return (Criteria) this;
        }

        public Criteria andBitLessThanOrEqualTo(Short value) {
            addCriterion("BIT <=", value, "bit");
            return (Criteria) this;
        }

        public Criteria andBitIn(List<Short> values) {
            addCriterion("BIT in", values, "bit");
            return (Criteria) this;
        }

        public Criteria andBitNotIn(List<Short> values) {
            addCriterion("BIT not in", values, "bit");
            return (Criteria) this;
        }

        public Criteria andBitBetween(Short value1, Short value2) {
            addCriterion("BIT between", value1, value2, "bit");
            return (Criteria) this;
        }

        public Criteria andBitNotBetween(Short value1, Short value2) {
            addCriterion("BIT not between", value1, value2, "bit");
            return (Criteria) this;
        }

        public Criteria andTypeIsNull() {
            addCriterion("TYPE is null");
            return (Criteria) this;
        }

        public Criteria andTypeIsNotNull() {
            addCriterion("TYPE is not null");
            return (Criteria) this;
        }

        public Criteria andTypeEqualTo(String value) {
            addCriterion("TYPE =", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotEqualTo(String value) {
            addCriterion("TYPE <>", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThan(String value) {
            addCriterion("TYPE >", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThanOrEqualTo(String value) {
            addCriterion("TYPE >=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThan(String value) {
            addCriterion("TYPE <", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThanOrEqualTo(String value) {
            addCriterion("TYPE <=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLike(String value) {
            addCriterion("TYPE like", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotLike(String value) {
            addCriterion("TYPE not like", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeIn(List<String> values) {
            addCriterion("TYPE in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotIn(List<String> values) {
            addCriterion("TYPE not in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeBetween(String value1, String value2) {
            addCriterion("TYPE between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotBetween(String value1, String value2) {
            addCriterion("TYPE not between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andFormatIsNull() {
            addCriterion("FORMAT is null");
            return (Criteria) this;
        }

        public Criteria andFormatIsNotNull() {
            addCriterion("FORMAT is not null");
            return (Criteria) this;
        }

        public Criteria andFormatEqualTo(String value) {
            addCriterion("FORMAT =", value, "format");
            return (Criteria) this;
        }

        public Criteria andFormatNotEqualTo(String value) {
            addCriterion("FORMAT <>", value, "format");
            return (Criteria) this;
        }

        public Criteria andFormatGreaterThan(String value) {
            addCriterion("FORMAT >", value, "format");
            return (Criteria) this;
        }

        public Criteria andFormatGreaterThanOrEqualTo(String value) {
            addCriterion("FORMAT >=", value, "format");
            return (Criteria) this;
        }

        public Criteria andFormatLessThan(String value) {
            addCriterion("FORMAT <", value, "format");
            return (Criteria) this;
        }

        public Criteria andFormatLessThanOrEqualTo(String value) {
            addCriterion("FORMAT <=", value, "format");
            return (Criteria) this;
        }

        public Criteria andFormatLike(String value) {
            addCriterion("FORMAT like", value, "format");
            return (Criteria) this;
        }

        public Criteria andFormatNotLike(String value) {
            addCriterion("FORMAT not like", value, "format");
            return (Criteria) this;
        }

        public Criteria andFormatIn(List<String> values) {
            addCriterion("FORMAT in", values, "format");
            return (Criteria) this;
        }

        public Criteria andFormatNotIn(List<String> values) {
            addCriterion("FORMAT not in", values, "format");
            return (Criteria) this;
        }

        public Criteria andFormatBetween(String value1, String value2) {
            addCriterion("FORMAT between", value1, value2, "format");
            return (Criteria) this;
        }

        public Criteria andFormatNotBetween(String value1, String value2) {
            addCriterion("FORMAT not between", value1, value2, "format");
            return (Criteria) this;
        }

        public Criteria andSpecIsNull() {
            addCriterion("SPEC is null");
            return (Criteria) this;
        }

        public Criteria andSpecIsNotNull() {
            addCriterion("SPEC is not null");
            return (Criteria) this;
        }

        public Criteria andSpecEqualTo(String value) {
            addCriterion("SPEC =", value, "spec");
            return (Criteria) this;
        }

        public Criteria andSpecNotEqualTo(String value) {
            addCriterion("SPEC <>", value, "spec");
            return (Criteria) this;
        }

        public Criteria andSpecGreaterThan(String value) {
            addCriterion("SPEC >", value, "spec");
            return (Criteria) this;
        }

        public Criteria andSpecGreaterThanOrEqualTo(String value) {
            addCriterion("SPEC >=", value, "spec");
            return (Criteria) this;
        }

        public Criteria andSpecLessThan(String value) {
            addCriterion("SPEC <", value, "spec");
            return (Criteria) this;
        }

        public Criteria andSpecLessThanOrEqualTo(String value) {
            addCriterion("SPEC <=", value, "spec");
            return (Criteria) this;
        }

        public Criteria andSpecLike(String value) {
            addCriterion("SPEC like", value, "spec");
            return (Criteria) this;
        }

        public Criteria andSpecNotLike(String value) {
            addCriterion("SPEC not like", value, "spec");
            return (Criteria) this;
        }

        public Criteria andSpecIn(List<String> values) {
            addCriterion("SPEC in", values, "spec");
            return (Criteria) this;
        }

        public Criteria andSpecNotIn(List<String> values) {
            addCriterion("SPEC not in", values, "spec");
            return (Criteria) this;
        }

        public Criteria andSpecBetween(String value1, String value2) {
            addCriterion("SPEC between", value1, value2, "spec");
            return (Criteria) this;
        }

        public Criteria andSpecNotBetween(String value1, String value2) {
            addCriterion("SPEC not between", value1, value2, "spec");
            return (Criteria) this;
        }

        public Criteria andLengthIsNull() {
            addCriterion("LENGTH is null");
            return (Criteria) this;
        }

        public Criteria andLengthIsNotNull() {
            addCriterion("LENGTH is not null");
            return (Criteria) this;
        }

        public Criteria andLengthEqualTo(Short value) {
            addCriterion("LENGTH =", value, "length");
            return (Criteria) this;
        }

        public Criteria andLengthNotEqualTo(Short value) {
            addCriterion("LENGTH <>", value, "length");
            return (Criteria) this;
        }

        public Criteria andLengthGreaterThan(Short value) {
            addCriterion("LENGTH >", value, "length");
            return (Criteria) this;
        }

        public Criteria andLengthGreaterThanOrEqualTo(Short value) {
            addCriterion("LENGTH >=", value, "length");
            return (Criteria) this;
        }

        public Criteria andLengthLessThan(Short value) {
            addCriterion("LENGTH <", value, "length");
            return (Criteria) this;
        }

        public Criteria andLengthLessThanOrEqualTo(Short value) {
            addCriterion("LENGTH <=", value, "length");
            return (Criteria) this;
        }

        public Criteria andLengthIn(List<Short> values) {
            addCriterion("LENGTH in", values, "length");
            return (Criteria) this;
        }

        public Criteria andLengthNotIn(List<Short> values) {
            addCriterion("LENGTH not in", values, "length");
            return (Criteria) this;
        }

        public Criteria andLengthBetween(Short value1, Short value2) {
            addCriterion("LENGTH between", value1, value2, "length");
            return (Criteria) this;
        }

        public Criteria andLengthNotBetween(Short value1, Short value2) {
            addCriterion("LENGTH not between", value1, value2, "length");
            return (Criteria) this;
        }

        public Criteria andStatIsNull() {
            addCriterion("STAT is null");
            return (Criteria) this;
        }

        public Criteria andStatIsNotNull() {
            addCriterion("STAT is not null");
            return (Criteria) this;
        }

        public Criteria andStatEqualTo(String value) {
            addCriterion("STAT =", value, "stat");
            return (Criteria) this;
        }

        public Criteria andStatNotEqualTo(String value) {
            addCriterion("STAT <>", value, "stat");
            return (Criteria) this;
        }

        public Criteria andStatGreaterThan(String value) {
            addCriterion("STAT >", value, "stat");
            return (Criteria) this;
        }

        public Criteria andStatGreaterThanOrEqualTo(String value) {
            addCriterion("STAT >=", value, "stat");
            return (Criteria) this;
        }

        public Criteria andStatLessThan(String value) {
            addCriterion("STAT <", value, "stat");
            return (Criteria) this;
        }

        public Criteria andStatLessThanOrEqualTo(String value) {
            addCriterion("STAT <=", value, "stat");
            return (Criteria) this;
        }

        public Criteria andStatLike(String value) {
            addCriterion("STAT like", value, "stat");
            return (Criteria) this;
        }

        public Criteria andStatNotLike(String value) {
            addCriterion("STAT not like", value, "stat");
            return (Criteria) this;
        }

        public Criteria andStatIn(List<String> values) {
            addCriterion("STAT in", values, "stat");
            return (Criteria) this;
        }

        public Criteria andStatNotIn(List<String> values) {
            addCriterion("STAT not in", values, "stat");
            return (Criteria) this;
        }

        public Criteria andStatBetween(String value1, String value2) {
            addCriterion("STAT between", value1, value2, "stat");
            return (Criteria) this;
        }

        public Criteria andStatNotBetween(String value1, String value2) {
            addCriterion("STAT not between", value1, value2, "stat");
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