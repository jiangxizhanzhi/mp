package cn.cyberpeace.mp.auth.bo.target;

/**
 * @ClassName TargetType
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see Enum
 */
public enum TargetType {

    MENU(1),
    API(2),
    DATA(3);

    public int getType() {
        return type;
    }

    private int type;

    TargetType(int type) {
        this.type = type;
    }
}
