 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FileEventType.java.
 // File size 438.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model.enums;

 import lombok.Getter;

 /**
 * 文件预警类型
 */
public enum FileEventType {
    FILE("文件","FILE"), REGISTRY("注册表","REGISTRY"), PROCESS("进程","PROCESS"), PORT("端口","PORT"), INTERNET_CONNECTION("网络连接","INTERNET_CONNECTION"),
    USERS("用户","USERS");

    @Getter
    private final String desc;

    private final String type;

    FileEventType(String desc , String type) {
        this.desc = desc;
        this.type = type;
    }

    @Override public String toString() {
        return type;
    }
}
