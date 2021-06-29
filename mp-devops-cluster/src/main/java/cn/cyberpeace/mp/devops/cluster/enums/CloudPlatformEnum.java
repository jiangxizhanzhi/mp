package cn.cyberpeace.mp.devops.cluster.enums;

/**
 * 云平台枚举信息
 */
public enum CloudPlatformEnum{

    NORMAL("正常"),

    ABNORMAL("异常"),

    UP("up"),

    DOWN("down");

    private String desc;

    CloudPlatformEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
