package cn.cyberpeace.mp.situationaware.model.enums;

/**
 * 探针osType 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other
 */
public enum ProbeOSType {
    WINDOWS(0),
    UNIX(1),
    LINUX(2),
    BSD(3),
    OTHER(4)
    ;

    private int code;

    ProbeOSType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
