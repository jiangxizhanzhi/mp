package cn.cyberpeace.mp.devops.cluster.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 告警状态：0 生效， 1失效
 */
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
