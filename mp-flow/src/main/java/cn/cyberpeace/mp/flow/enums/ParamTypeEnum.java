package cn.cyberpeace.mp.flow.enums;

import com.fasterxml.jackson.annotation.JsonValue;


public enum ParamTypeEnum {

    STRING("String"),

    IP("IP"),

    NUMBER("Number"),

    BOOL("Bool");

    private final String paramType;

    ParamTypeEnum(String paramType) {
        this.paramType = paramType;
    }

    @Override
    public String toString() {
        return paramType;
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
