package cn.cyberpeace.mp.flow.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 交互类型枚举类：0 背景流量，1 攻击流量，2 全流量
 */
public enum InteractiveTypeEnum {

    BACKGROUND("BACKGROUND"),

    ATTACK("ATTACK"),

    FULL("FULL");

    private final String group;

    InteractiveTypeEnum(String group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return group;
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
