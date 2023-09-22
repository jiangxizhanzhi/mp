package cn.cyberpeace.mp.situationaware.constant;

/**
 * @ClassName WarningMovement
 * @Description
 * @Author zhanzhi
 * @Date 2023/9/21 14:54
 **/
public enum WarningMovement implements BaseKeyAndName {
    ADD(1, "新增"),
    UPDATE(2, "变更"),
    DELETE(3, "删除"),
    ;

    WarningMovement(Integer key, String name) {
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
