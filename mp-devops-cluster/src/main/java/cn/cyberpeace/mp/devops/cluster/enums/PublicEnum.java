package cn.cyberpeace.mp.devops.cluster.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PublicEnum {

    //自定义
    CUSTOMIZE,

    //授权所有
    ALL_PUBLIC;


    @JsonValue
    public int toValue(){
        return this.ordinal();
    }

}
