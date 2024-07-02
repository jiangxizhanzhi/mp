package cn.cyberpeace.mp.situationaware.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * launch status
 */
public enum LaunchStatusEnum {

    ERROR("error"),

    SUCCESS("success");

    private String value;

    /**
     * LaunchStatusEnum
     *
     * @param value
     */
    LaunchStatusEnum(String value) {
        this.value = value;
    }

    /**
     * getValue
     *
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     * toValue
     *
     * @return int
     */
    @JsonValue
    public int toValue(){
       return this.ordinal();
    }
}
