package cn.cyberpeace.mp.flow.enums;

import com.fasterxml.jackson.annotation.JsonValue;


public enum TaskStatusEnum {

    INIT("INIT"),

    RUNNING("RUNNING"),

    STOPING("STOPING"),

    STOPED("STOPED"),

    FAILED("FAILED");

    private final String status;

    TaskStatusEnum(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }

    @JsonValue
    public int toValue() {
        return this.ordinal();
    }

}
