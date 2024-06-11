package cn.cyberpeace.mp.common.constant.codeEnum;

/**
 * 方法code对应定义
 */
public enum CommonFunctionCodeEnum {

    LOAD_RESULT_ACTION_CODE("MP-acceptLoadResult-by-outService", "接收结果日志方法"),
    SYNCHRONOUS_DATA_ACTION_CODE("MP-synchronousData-by-outService", "同步载荷数据方法");

    private String code;
    private String description;

    CommonFunctionCodeEnum(String code, String description) {
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
