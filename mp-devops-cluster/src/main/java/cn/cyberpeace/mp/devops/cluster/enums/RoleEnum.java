package cn.cyberpeace.mp.devops.cluster.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RoleEnum {

    //默认：无
    NO_ROLE,

    //网络
    NETWORK,

    //网关
    GATEWAY,

    //终端
    TERMINAL;


    @JsonValue
    public int toValue(){
        return this.ordinal();
    }

}
