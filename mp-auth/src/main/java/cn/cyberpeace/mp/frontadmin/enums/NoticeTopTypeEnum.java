 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NoticeTopTypeEnum.java.
 // File size 527.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.enums;

/**
 * @ClassName NoticeTopTypeEnum
 * @Description
 * @Author maojun
 * @Date 2022/9/23
 * @Version 1.0.0
 * @see Enum
 */
public enum NoticeTopTypeEnum {

    /**
     * 系统公告类型
     */
    UNTOP(0),
    TOP(1);

    private Integer code;

    NoticeTopTypeEnum(int code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
