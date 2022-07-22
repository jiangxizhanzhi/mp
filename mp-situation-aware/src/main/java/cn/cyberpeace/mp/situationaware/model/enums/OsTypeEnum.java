package cn.cyberpeace.mp.situationaware.model.enums;

import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;

/**
 * @Description 探针系统类型
 * @author: shgang
 * @Date: 2022/7/20
 */
public enum OsTypeEnum {
    /**
     * 探针系统类型
     */
    WINDOWS(0, "Windows"),
    UNIX(1, "Unix"),
    LINUX(2, "Linux"),
    BSD(3, "BSD"),
    OTHER(4, "Other"),
    ;

    private int code;
    private String name;

    OsTypeEnum(int code, String name) {
        this.code = code;
        this.name = SituationalAwareConstant.OS_TYPE_PREFIX + name;
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
