 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TaskStatusEnum.java.
 // File size 757.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.enums;

import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @ClassName TaskStatusEnum
 * @Description 任务操作枚举类：0 初始化，1 正在运行， 2 正在停止, 3 已停止, 4  失败
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Enum
 */
public enum TaskStatusEnum {

    INIT("INIT"),

    RUNNING("RUNNING"),

    @Deprecated
    STOPING("STOPING"),

    STOPED("STOPED"),

    @Deprecated
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
