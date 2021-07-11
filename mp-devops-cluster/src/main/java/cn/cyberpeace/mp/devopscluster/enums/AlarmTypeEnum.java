package cn.cyberpeace.mp.devopscluster.enums;

import com.fasterxml.jackson.annotation.JsonValue;


public enum AlarmTypeEnum {

    CP("CP"),

    CR("CR"),

    SERVER("SERVER");

    private String desc;

    AlarmTypeEnum(String desc) {
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
