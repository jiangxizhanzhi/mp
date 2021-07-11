package cn.cyberpeace.mp.frontadmin.enums;


public enum NoticeTopTypeEnum {

    
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
