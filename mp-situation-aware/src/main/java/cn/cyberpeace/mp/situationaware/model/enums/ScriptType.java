 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ScriptType.java.
 // File size 398.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model.enums;

/**
  *@Desciption 探针脚本类型
  *@Author zhangjin
  *@Date 8/31/21 11:51 上午
**/
public enum ScriptType {
    SHELL(0),
    PYTHON(1),
    BAT(2)
    ;

    private int typeVal;

    ScriptType(int typeVal) {
        this.typeVal = typeVal;
    }

    public int getTypeVal() {
        return typeVal;
    }
}
