package cn.cyberpeace.mp.situationaware.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;


public enum ParamTypeEnum {

    NUMBER("Number"),
    STRING("String"),
    BOOL("Bool"),
    IP("IP");

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
