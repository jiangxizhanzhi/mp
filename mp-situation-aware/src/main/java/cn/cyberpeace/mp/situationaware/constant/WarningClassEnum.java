package cn.cyberpeace.mp.situationaware.constant;

/**
 * @ClassName WarningClassEnum
 * @Description
 * @Author zhanzhi
 * @Date 2023/9/21 14:50
 **/
public enum WarningClassEnum implements BaseKeyAndName {
    LOW(0, "低危"),
    MID(1, "中危"),
    HIGH(2, "高危"),
    ;


    WarningClassEnum(Integer key, String name) {
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
