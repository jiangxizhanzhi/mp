package cn.cyberpeace.mp.devopscluster.enums;

import com.fasterxml.jackson.annotation.JsonValue;


public enum AlarmStatusEnum {

    ENABLED("ENABLED"),

    DISABLED("DISABLED");

    private String desc;

    AlarmStatusEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    @JsonValue
    public int toValue(){
       return this.ordinal();
    }
}
