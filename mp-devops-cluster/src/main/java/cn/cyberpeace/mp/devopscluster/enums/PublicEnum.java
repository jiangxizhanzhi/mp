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
