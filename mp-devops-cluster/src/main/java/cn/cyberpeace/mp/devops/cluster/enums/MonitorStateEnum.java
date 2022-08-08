package cn.cyberpeace.mp.devops.cluster.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 监控服务状态：0 ERROR, 1 DOWN 2 RUNNING
 */
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
