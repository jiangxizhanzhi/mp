package cn.cyberpeace.mp.situationaware.model.enums;

/**
 * 默认图片文件夹
 * @author wanggang
 */
public enum DefaultImgDirEnum  {

    AVATAR("/avatar"),

    APPLICATION_SYSTEM("/application-system"),

    NETWORK_EQUIPMENT("/network-equipment"),

    OPERATION_SYSTEM("/operation-system"),

    SAFETY_EQUIPMENT("/safety-equipment"),

    STATUS("/status"),

    TARGET("");

    private final String endPathName;

    DefaultImgDirEnum(String endPathName) {
        this.endPathName = endPathName;
    }

    public String getEndPathName() {
        return endPathName;
    }
}
