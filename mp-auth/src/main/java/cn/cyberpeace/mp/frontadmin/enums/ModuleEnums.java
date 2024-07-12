 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ModuleEnums.java.
 // File size 544.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.enums;

/**
 * @ClassName ModuleEnums
 * @Description
 * @Author zhouxinghuai
 * @Date 2023/1/17 17:15
 * @Version 1.0.0
 * @see Enum
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
