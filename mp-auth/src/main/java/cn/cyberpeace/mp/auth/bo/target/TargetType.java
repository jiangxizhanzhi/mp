 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TargetType.java.
 // File size 345.
 // File create date Fri Jan 17 09:57:14 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.bo.target;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
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
