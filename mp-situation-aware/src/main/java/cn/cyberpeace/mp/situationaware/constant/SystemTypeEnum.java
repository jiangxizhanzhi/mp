package cn.cyberpeace.mp.situationaware.constant;

/**
 * @ClassName SystemTypeEnum
 * @Description
 * @Author zhanzhi
 * @Date 2023/9/21 14:57
 **/
public enum SystemTypeEnum implements BaseKeyAndName {
    WINDOWS(0, "windows"),
    LINUX(1, "linux"),
    ;

    SystemTypeEnum(Integer key, String name) {
        this.key = key;
        this.name = name;
    }
    private final Integer key;
    private final String name;

    @Override
    public Integer getKey() {
        return key;
    }

    @Override
    public String getName() {
        return name;
    }
}
