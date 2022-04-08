package cn.cyberpeace.mp.flow.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 是否自定义：0 否， 1 是
 */
public enum CustomFlagEnum {

    NO("NO"),

    YES("YES");

    private final String custom;

    CustomFlagEnum(String resource) {
        this.custom = resource;
    }

    @Override
    public String toString() {
        return custom;
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
