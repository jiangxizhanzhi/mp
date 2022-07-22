package cn.cyberpeace.mp.devops.log.enums;

import cn.cyberpeace.mp.devops.log.constant.LogConstant;

/**
 * @Description 审计日志操作类型
 * @author: shgang
 * @Date: 2022/7/20
 */
public enum OpTypeEnum {
    /**
     *审计日志操作类型
     */
    LOGIN(0, "login"),
    LOGOUT(1, "logout"),
    CREATE(2, "create"),
    UPDATE(3, "update"),
    DELETE(4, "delete"),
    LEARN(5, "learn"),
    EXERCISE(6, "exercise"),
    EXAM(7, "exam"),
    TRAIN(8, "train"),
    EVAL(9, "eval"),
    CONTEST(10, "contest"),
    STOP(11, "stop"),
    PAUSE(12, "pause"),
    START(13, "start"),
    RECOVER(14, "recover"),
    ;

    private int code;
    private String name;

    OpTypeEnum(int code, String name) {
        this.code = code;
        this.name = LogConstant.LOG_OP_TYPE_PREFIX + "." + name;
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
