package cn.cyberpeace.mp.situationaware.model.dto.probe;

import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;

public enum ProbeTypeEnum {

    MESSAGE(0, "message"),

    EVENT(1, "event"),

    LOG(2, "log")
    ;

    private int code;

    private String name;


    ProbeTypeEnum(int code, String name) {
        this.code = code;
        this.name = SituationalAwareConstant.PROBE_TYPES_CODE_PREFIX + name;;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
