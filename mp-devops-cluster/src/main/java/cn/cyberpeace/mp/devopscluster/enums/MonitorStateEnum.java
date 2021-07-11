package cn.cyberpeace.mp.devopscluster.enums;

import com.fasterxml.jackson.annotation.JsonValue;


public enum MonitorStateEnum {

    RESTARTING("RESTARTING"),

    ERROR("ERROR"),

    DOWN("DOWN"),

    RUNNING("RUNNING");


    private String desc;

    MonitorStateEnum(String desc) {
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
