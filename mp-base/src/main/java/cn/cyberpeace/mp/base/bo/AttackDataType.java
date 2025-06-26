package cn.cyberpeace.mp.base.bo;

public enum AttackDataType {
    // 1 网络接口
    NETIFACE(1),

    // 2 硬件资源
    HOSTINFO(2),

    // 3 网络流量
    FLOW(3),

    //4 系统进程
    PROCESS(4);

    private int type;

    AttackDataType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static AttackDataType getByType(int type) {
        for (AttackDataType value : values()) {
            if (value.type == type) {
                return value;
            }
        }
        return null;
    }
}
