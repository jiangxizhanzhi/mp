package cn.cyberpeace.mp.flow.enums;

import com.fasterxml.jackson.annotation.JsonValue;


public enum TaskTaskStatusEnum {

    INIT("INIT"),

    RUNNING("RUNNING"),

    ERROR("ERROR"),

    STOPPED("STOPPED"),

    PAUSE("PAUSE"),

    PREPARING("PREPARING"),

    
    INITING("INITING");

    private final String status;

    TaskTaskStatusEnum(String status) {
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
