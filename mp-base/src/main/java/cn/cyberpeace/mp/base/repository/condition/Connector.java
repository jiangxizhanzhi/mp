package cn.cyberpeace.mp.base.repository.condition;

public enum Connector {
    LINK("link"),
    IN("in"),
    NOT_IN("notIn"),
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
    I_ENDS_WITH("iEndsWith"),
    NOT_CONTAINS("notContains"),
    NOT_I_CONTAINS("notIContains"),
    NOT_STARTS_WITH("notStartsWith"),
    NOT_I_STARTS_WITH("notIStartsWith"),
    NOT_ENDS_WITH("notEndsWith"),
    NOT_I_ENDS_WITH("notIEndsWith");

    private final String operator;

    Connector(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }
}
