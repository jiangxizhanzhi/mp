package cn.cyberpeace.mp.base.repository.condition;

import java.util.ArrayList;
import java.util.List;

public class BaseConnectorCondition implements BaseCondition {

    private final List<Object> conds = new ArrayList<>();

    public BaseConnectorCondition addCondition(String fieldName, Connector connector, Object fieldValue) {
        var cond = new ConnectorCond(fieldName, connector.getOperator(), fieldValue);
        conds.add(cond);
        return this;
    }

    public BaseConnectorCondition addCondition(BaseConnectorCondition connectorCondition) {
        conds.add(connectorCondition);
        return this;
    }

    public BaseConnectorCondition addLink(String fieldName, String targetFieldName) {
        return addCondition(fieldName, Connector.LINK, targetFieldName);
    }

    public BaseConnectorCondition addIn(String fieldName, Object[] fieldValue) {
        return addCondition(fieldName, Connector.IN, fieldValue);
    }

    public BaseConnectorCondition addNotIn(String fieldName, Object[] fieldValue) {
        return addCondition(fieldName, Connector.NOT_IN, fieldValue);
    }

    public BaseConnectorCondition addEqual(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.EQUAL, fieldValue);
    }

    public BaseConnectorCondition addNotEqual(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.NOT_EQUAL, fieldValue);
    }

    public BaseConnectorCondition addGreaterThan(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.GREATER_THAN, fieldValue);
    }

    public BaseConnectorCondition addGreaterThanOrEqual(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.GREATER_THAN_OR_EQUAL, fieldValue);
    }

    public BaseConnectorCondition addLessThan(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.LESS_THAN, fieldValue);
    }

    public BaseConnectorCondition addLessThanOrEqual(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.LESS_THAN_OR_EQUAL, fieldValue);
    }

    public BaseConnectorCondition addContains(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.CONTAINS, fieldValue);
    }

    public BaseConnectorCondition addIContains(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.I_CONTAINS, fieldValue);
    }

    public BaseConnectorCondition addStartsWith(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.STARTS_WITH, fieldValue);
    }

    public BaseConnectorCondition addIStartsWith(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.I_STARTS_WITH, fieldValue);
    }

    public BaseConnectorCondition addEndsWith(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.ENDS_WITH, fieldValue);
    }

    public BaseConnectorCondition addIEndsWith(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.I_ENDS_WITH, fieldValue);
    }

    public BaseConnectorCondition addNotContains(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.NOT_CONTAINS, fieldValue);
    }

    public BaseConnectorCondition addNotIContains(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.NOT_I_CONTAINS, fieldValue);
    }

    public BaseConnectorCondition addNotStartsWith(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.NOT_STARTS_WITH, fieldValue);
    }

    public BaseConnectorCondition addNotIStartsWith(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.NOT_I_STARTS_WITH, fieldValue);
    }

    public BaseConnectorCondition addNotEndsWith(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.NOT_ENDS_WITH, fieldValue);
    }

    public BaseConnectorCondition addNotIEndsWith(String fieldName, Object fieldValue) {
        return addCondition(fieldName, Connector.NOT_I_ENDS_WITH, fieldValue);
    }

    @Override
    public boolean isEmpty() {
        return conds.isEmpty();
    }

}
