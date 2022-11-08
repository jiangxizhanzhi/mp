package cn.cyberpeace.mp.frontadmin.enums;

/**
 * @Description
 * @author: zhouxinghuai
 * @Date: 2022/9/23
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
