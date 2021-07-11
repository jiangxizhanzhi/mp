package cn.cyberpeace.mp.auth.bo.target;


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
