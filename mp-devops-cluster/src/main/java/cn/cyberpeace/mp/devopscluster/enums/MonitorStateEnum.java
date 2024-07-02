package cn.cyberpeace.mp.devopscluster.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @ClassName MonitorStateEnum
 * @Description 监控服务状态：0 ERROR, 1 DOWN 2 RUNNING
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Enum
 */
public enum MonitorStateEnum {

    RESTARTING("RESTARTING"),

    ERROR("ERROR"),

    DOWN("DOWN"),

    RUNNING("RUNNING");


    private String desc;

    /**
     * MonitorStateEnum
     *
     * @param desc
     */
    MonitorStateEnum(String desc) {
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
