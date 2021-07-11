package cn.cyberpeace.mp.situationaware.enums;

import com.fasterxml.jackson.annotation.JsonValue;


public enum LaunchStatusEnum {

    ERROR("error"),

    SUCCESS("success");

    private String value;

    LaunchStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonValue
    public int toValue(){
       return this.ordinal();
    }
}
