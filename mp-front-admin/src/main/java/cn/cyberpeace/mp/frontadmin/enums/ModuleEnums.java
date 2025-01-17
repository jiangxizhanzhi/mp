 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ModuleEnums.java.
 // File size 487.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.enums;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2023/1/17 17:15
 */
public enum ModuleEnums {

    // 竞赛
    AD("AD"),

    // 实训
    OJ("OJ"),

    // 靶场
    CR("CR");

    private String code;

    ModuleEnums(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
