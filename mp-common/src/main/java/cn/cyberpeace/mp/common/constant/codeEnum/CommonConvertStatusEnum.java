package cn.cyberpeace.mp.common.constant.codeEnum;

/**
 * 载荷执行结果code转换
 */
public enum CommonConvertStatusEnum {

    STATUS_SUCCESS_CODE("1005010101", "操作成功"),
    STATUS_FAIL_CODE("1005010102", "操作失败");

    private String code;
    private String description;

    CommonConvertStatusEnum(String code, String description) {
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
