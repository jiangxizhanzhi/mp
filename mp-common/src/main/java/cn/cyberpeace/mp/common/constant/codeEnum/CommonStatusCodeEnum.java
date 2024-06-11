package cn.cyberpeace.mp.common.constant.codeEnum;

/**
 * 状态code对应定义
 */
public enum CommonStatusCodeEnum {

    //载荷执行结果
    RESULT_STATUS("10050101", "执行结果"),
    RESULT_TYPE("10050102", "结果类型"),

    //载荷
    ACTION_TYPE("10050201", "作用类型"),
    RUN_TABLE("10050202", "运行类型"),
    CONTROL_TYPE("10050203", "控制类型");


    private String code;
    private String description;

    CommonStatusCodeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

}
