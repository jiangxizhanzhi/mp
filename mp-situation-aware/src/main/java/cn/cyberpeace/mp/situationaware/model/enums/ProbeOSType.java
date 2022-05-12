package cn.cyberpeace.mp.situationaware.model.enums;

import java.util.Objects;

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

    public static String getByCode(int code) {
        for (ProbeOSType type : ProbeOSType.values()) {
            if (Objects.equals(type.getCode(), code)) {
                return type.name();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String s = ProbeOSType.getByCode(1);
        System.out.println(s);
    }
}
