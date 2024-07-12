 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name PublicEnum.java.
 // File size 512.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @ClassName PublicEnum
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Enum
 */
public enum PublicEnum {

    //私有
    PRIVATE,

    //自定义
    CUSTOMIZE,

    //授权所有
    ALL_PUBLIC;


    /**
     * toValue
     *
     * @return int
     */
    @JsonValue
    public int toValue(){
        return this.ordinal();
    }

}
