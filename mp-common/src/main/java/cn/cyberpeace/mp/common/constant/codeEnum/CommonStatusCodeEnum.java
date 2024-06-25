package cn.cyberpeace.mp.common.constant.codeEnum;

/**
 * 状态code对应定义
 */
public enum CommonStatusCodeEnum {

    /**
     * 状态码
     */
    CODE("1005","状态码"),


    /**
     * 载荷执行结果页面code码
     */
    CODE_LOAD_RESULT("100501", "载荷执行结果页面code码"),
    RESULT_STATUS("10050101", "执行结果"),
    RESULT_TYPE("10050102", "结果类型"),


    /**
     * 载荷页面code码
     */
    CODE_LOAD("100502", "载荷页面code码"),
    ACTION_TYPE("10050201", "作用类型"),
    RUN_TABLE("10050202", "运行平台"),
    CONTROL_TYPE("10050203", "控制类型"),


    /**
     * 载荷同步设置code码
     */
    CODE_LOAD_SYNCHRONOUS("100503", "载荷同步设置code码"),


    /**
     * 云靶标镜像页面code码
     */
    CODE_TARGET("100504", "云靶标镜像页面code码");


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
