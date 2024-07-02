package cn.cyberpeace.mp.flow.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @ClassName TaskTaskStatusEnum
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Enum
 */
public enum TaskTaskStatusEnum {

    INIT("INIT"),

    RUNNING("RUNNING"),

    ERROR("ERROR"),

    STOPPED("STOPPED"),

    PAUSE("PAUSE"),

    PREPARING("PREPARING"),

    /**
     * 初始化中，不能执行
     */
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
