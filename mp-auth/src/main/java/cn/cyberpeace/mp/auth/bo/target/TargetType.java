 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TargetType.java.
 // File size 401.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.bo.target;

/**
 * @ClassName TargetType
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see Enum
 */
public enum TargetType {

    MENU(1),
    API(2),
    DATA(3);

    public int getType() {
        return type;
    }

    private int type;

    TargetType(int type) {
        this.type = type;
    }
}
