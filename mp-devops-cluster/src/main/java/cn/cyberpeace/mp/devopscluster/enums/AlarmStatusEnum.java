package cn.cyberpeace.mp.devopscluster.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 告警状态：0 生效， 1失效
 */
public enum AlarmStatusEnum {

    ENABLED("ENABLED"),

    DISABLED("DISABLED");

    private String desc;

    /**
     * AlarmStatusEnum
     *
     * @param desc
     */
    AlarmStatusEnum(String desc) {
        this.desc = desc;
    }

    /**
     * getDesc
     *
     * @return
     */
    public String getDesc() {
        return desc;
    }

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
