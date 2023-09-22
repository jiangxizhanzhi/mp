package cn.cyberpeace.mp.situationaware.constant;

/**
 * @ClassName ProcessStatusEnum
 * @Description
 * @Author zhanzhi
 * @Date 2023/9/21 15:36
 **/
public enum ProcessStatusEnum implements BaseKeyAndName {
    RUN(0, "running"),
    SLEEP(1, "sleeping"),

    ;

    ProcessStatusEnum(Integer key, String name) {
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
