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

    @Override
    public boolean isEmpty() {
        return conds.isEmpty();
    }

}
