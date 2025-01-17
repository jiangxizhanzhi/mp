 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NoticeTopTypeEnum.java.
 // File size 469.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.enums;

/**
 * @Description
 * @author: zhouxinghuai
 * @Date: 2022/9/23
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
