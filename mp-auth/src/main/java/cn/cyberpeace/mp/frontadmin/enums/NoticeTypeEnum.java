 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NoticeTypeEnum.java.
 // File size 940.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.enums;

import cn.cyberpeace.mp.frontadmin.constant.ControllerConstant;

/**
 * @ClassName NoticeTypeEnum
 * @Description
 * @Author maojun
 * @Date 2022/7/21
 * @Version 1.0.0
 * @see Enum
 */
public enum NoticeTypeEnum {

    /**
     * 系统公告类型
     */
    SYSTEM(1, "system"),
    COURSE(2, "course"),
    EXERCISE(3, "exercise"),
    EXAM(4, "exam"),
    LICENSE(5, "license"),
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
