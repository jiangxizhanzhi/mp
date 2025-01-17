 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NoticeTypeEnum.java.
 // File size 851.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.enums;

import cn.cyberpeace.mp.frontadmin.constant.ControllerConstant;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/21
 */
public enum NoticeTypeEnum {

    /**
     * 系统公告类型
     */
    SYSTEM(1, "system"),
    COURSE(2, "course"),
    EXERCISE(3, "exercise"),
    EXAM(4, "exam"),
    ;

    private int code;
    private String name;

    NoticeTypeEnum(int code, String name) {
        this.code = code;
        this.name = ControllerConstant.SYS_NOTICE_TYPE_PREFIX + "." + name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
