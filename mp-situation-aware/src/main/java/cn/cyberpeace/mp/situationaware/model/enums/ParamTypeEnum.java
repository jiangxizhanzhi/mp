package cn.cyberpeace.mp.situationaware.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 参数类型枚举类：0:number 1:string 2:bool 3:ip
 * 跟流量那边定义的顺序不一致
 */
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
