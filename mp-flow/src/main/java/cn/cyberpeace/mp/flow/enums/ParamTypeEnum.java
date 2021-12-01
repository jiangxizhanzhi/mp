package cn.cyberpeace.mp.flow.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 参数类型枚举类：0 String, 1 IP, 2 Number, 3 Bool
 */
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
