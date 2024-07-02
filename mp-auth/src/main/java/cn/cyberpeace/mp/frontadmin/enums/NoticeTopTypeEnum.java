package cn.cyberpeace.mp.frontadmin.enums;

/**
 * @ClassName NoticeTopTypeEnum
 * @Description
 * @Author maojun
 * @Date 2022/9/23
 * @Version 1.0.0
 * @see Enum
 */
public enum NoticeTopTypeEnum {

    /**
     * 系统公告类型
     */
    UNTOP(0),
    TOP(1);

    private Integer code;

    NoticeTopTypeEnum(int code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
