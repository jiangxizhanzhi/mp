package cn.cyberpeace.mp.situationaware.model.enums;

/**
 * 探针脚本系统类型
 */
public enum OsType {
    LINUX(0),
    WINDOWS(1)
    ;

    private int typeVal;

    OsType(int typeVal) {
        this.typeVal = typeVal;
    }

    public int getTypeVal() {
        return typeVal;
    }
}
