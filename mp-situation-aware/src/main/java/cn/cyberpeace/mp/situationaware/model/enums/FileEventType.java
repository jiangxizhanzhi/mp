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

/**
 * 文件预警类型
 */
public enum FileEventType {
    FILE("FILE"), REGISTRY("REGISTRY"), PROCESS("PROCESS"), PORT("PORT"), INTERNET_CONNECTION("INTERNET_CONNECTION"),
    USERS("USERS");

    private final String type;

    FileEventType(String type) {
        this.type = type;
    }

    @Override public String toString() {
        return type;
    }
}
