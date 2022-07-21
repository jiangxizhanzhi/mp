package cn.cyberpeace.mp.cscene.enums;


import cn.cyberpeace.mp.cscene.constant.ControllerConstant;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/21
 */
public enum CsceneStatusEnum {
    /**
     * 靶场监控的场景实例状态
     */
    CREATING(1, "creating"),
    RUNNING(3, "running"),
    SUSPENDED(4, "suspended"),
    DELETED(0, "deleted"),
    ERROR(5, "error"),
    HANG(6, "hang"),
    ;

    private int code;
    private String name;

    CsceneStatusEnum(int code, String name) {
        this.code = code;
        this.name = ControllerConstant.CSCENE_INSTANCE_STATUS_PREFIX + "." +name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
