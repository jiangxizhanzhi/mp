package cn.cyberpeace.mp.front.admin.enums;

import cn.cyberpeace.mp.front.admin.constant.ControllerConstant;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/21
 */
public enum NoticeTypeEnum {

    /**
     * 系统公告类型
     */
    SYSTEM(1, "system"),
    COURSE(2, "course"),
    EXERCISE(3, "exercise"),
    EXAM(4, "exam"),
    ;

    private int code;
    private String name;

    NoticeTypeEnum(int code, String name) {
        this.code = code;
        this.name = ControllerConstant.SYS_NOTICE_TYPE_PREFIX + "." + name;
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
