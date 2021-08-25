package cn.cyberpeace.mp.devops.cluster.enums;

public enum ImageTypeEnum {

    VM("vm"),

    CONTAINER("container"),

    ENTITY("entity"),

    SIMPLE("simple");

    private String desc;

    ImageTypeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
