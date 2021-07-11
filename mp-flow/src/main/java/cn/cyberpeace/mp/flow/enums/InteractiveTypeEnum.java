package cn.cyberpeace.mp.flow.enums;

import com.fasterxml.jackson.annotation.JsonValue;


public enum InteractiveTypeEnum {

    BACKGROUND("BACKGROUND"),

    ATTACK("ATTACK"),

    FULL("FULL");

    private final String interactive;

    InteractiveTypeEnum(String interactive) {
        this.interactive = interactive;
    }

    @Override
    public String toString() {
        return interactive;
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
