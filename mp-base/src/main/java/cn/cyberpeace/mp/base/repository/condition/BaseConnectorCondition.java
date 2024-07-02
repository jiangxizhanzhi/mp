package cn.cyberpeace.mp.base.repository.condition;

import java.util.ArrayList;
import java.util.List;

public class BaseConnectorCondition implements BaseCondition {

    private final List<Object> conds = new ArrayList<>();

    /**
     * addCondition
     *
     * @param fieldName
     * @param connector
     * @param fieldValue
     * @return
     */
    public BaseConnectorCondition addCondition(String fieldName, Connector connector, Object fieldValue) {
        ConnectorCond cond = new ConnectorCond(fieldName, connector.getOperator(), fieldValue);
        conds.add(cond);
        return this;
    }

    /**
     * addCondition
     *
     * @param connectorCondition
     * @return
     */
    public BaseConnectorCondition addCondition(BaseConnectorCondition connectorCondition) {
        conds.add(connectorCondition);
        return this;
    }

    /**
     * addLink
     *
     * @param fieldName
     * @param targetFieldName
     * @return
     */
    public BaseConnectorCondition addLink(String fieldName, String targetFieldName) {
        return addCondition(fieldName, Connector.LINK, targetFieldName);
    }

    /**
     * addIn
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public BaseConnectorCondition addIn(String fieldName, Object[] fieldValue) {
        return addCondition(fieldName, Connector.IN, fieldValue);
    }

    /**
     * addNotIn
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public BaseConnectorCondition addNotIn(String fieldName, Object[] fieldValue) {
        return addCondition(fieldName, Connector.NOT_IN, fieldValue);
    }

    /**
     * addEqual
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public BaseConnectorCondition addEqual(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.EQUAL, fieldValue);
    }

    /**
     * addNotEqual
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public BaseConnectorCondition addNotEqual(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.NOT_EQUAL, fieldValue);
    }

    /**
     * addGreaterThan
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public BaseConnectorCondition addGreaterThan(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.GREATER_THAN, fieldValue);
    }

    /**
     * addGreaterThanOrEqual
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public BaseConnectorCondition addGreaterThanOrEqual(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.GREATER_THAN_OR_EQUAL, fieldValue);
    }

    /**
     * addLessThan
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public BaseConnectorCondition addLessThan(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.LESS_THAN, fieldValue);
    }

    /**
     * addLessThanOrEqual
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public BaseConnectorCondition addLessThanOrEqual(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.LESS_THAN_OR_EQUAL, fieldValue);
    }

    /**
     * addContains
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public BaseConnectorCondition addContains(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.CONTAINS, fieldValue);
    }

    /**
     * addIContains
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public BaseConnectorCondition addIContains(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.I_CONTAINS, fieldValue);
    }

    /**
     * addStartsWith
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public BaseConnectorCondition addStartsWith(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.STARTS_WITH, fieldValue);
    }

    /**
     * addIStartsWith
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public BaseConnectorCondition addIStartsWith(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.I_STARTS_WITH, fieldValue);
    }

    /**
     * addEndsWith
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public BaseConnectorCondition addEndsWith(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.ENDS_WITH, fieldValue);
    }

    /**
     * addIEndsWith
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public BaseConnectorCondition addIEndsWith(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.I_ENDS_WITH, fieldValue);
    }

    /**
     * addNotContains
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public BaseConnectorCondition addNotContains(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.NOT_CONTAINS, fieldValue);
    }

    /**
     * addNotIContains
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public BaseConnectorCondition addNotIContains(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.NOT_I_CONTAINS, fieldValue);
    }

    /**
     * addNotStartsWith
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public BaseConnectorCondition addNotStartsWith(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.NOT_STARTS_WITH, fieldValue);
    }

    /**
     * addNotIStartsWith
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public BaseConnectorCondition addNotIStartsWith(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.NOT_I_STARTS_WITH, fieldValue);
    }

    /**
     * addNotEndsWith
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public BaseConnectorCondition addNotEndsWith(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.NOT_ENDS_WITH, fieldValue);
    }

    /**
     * addNotIEndsWith
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public BaseConnectorCondition addNotIEndsWith(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.NOT_I_ENDS_WITH, fieldValue);
    }

    /**
     * isEmpty
     *
     * @return boolean
     */
    @Override
    public boolean isEmpty() {
        return conds.isEmpty();
    }

}
