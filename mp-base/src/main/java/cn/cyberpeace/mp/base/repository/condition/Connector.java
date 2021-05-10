package cn.cyberpeace.mp.base.repository.condition;

public enum Connector {
    IN("in"),
    EQUAL("="),
    NOT_EQUAL("<>"),
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUAL(">="),
    LESS_THAN("<"),
    LESS_THAN_OR_EQUAL("<="),
    CONTAINS("contains"),
    I_CONTAINS("iContains"),
    STARTS_WITH("startsWith"),
    I_STARTS_WITH("iStartsWith"),
    ENDS_WITH("endsWith"),
    I_ENDS_WITH("iEndsWith");

    private final String operator;

    Connector(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }
}
