package cn.cyberpeace.mp.flow.enums;

import com.fasterxml.jackson.annotation.JsonValue;


public enum TaskActionEnum {

    INIT("INIT"),

    START("START"),

    STOP("STOP"),

    RESET("RESET");

    private final String action;

    TaskActionEnum(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return action;
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
