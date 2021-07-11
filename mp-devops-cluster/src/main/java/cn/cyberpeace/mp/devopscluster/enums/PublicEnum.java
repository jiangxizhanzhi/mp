package cn.cyberpeace.mp.devopscluster.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PublicEnum {

    
    PRIVATE,

    
    CUSTOMIZE,

    
    ALL_PUBLIC;


    @JsonValue
    public int toValue(){
        return this.ordinal();
    }

}
