package cn.cyberpeace.mp.flow.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @Description: 流量任务状态枚举类
 * @Author: guquan
 * @Date：2023/6/2 18:06
 */
public enum TaskTaskStatusEnum {

    INIT("INIT"),

    RUNNING("RUNNING"),

    ERROR("ERROR"),

    STOPPED("STOPPED"),

    PAUSE("PAUSE"),

    PREPARING("PREPARING");

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
