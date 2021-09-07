package cn.cyberpeace.mp.flow.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 任务操作枚举类：0 初始化，1 正在运行， 2 正在停止, 3 已停止
 */
public enum TaskStatusEnum {

    INIT("INIT"),

    RUNNING("RUNNING"),

    STOPING("STOPING"),

    STOPED("STOPED");

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
