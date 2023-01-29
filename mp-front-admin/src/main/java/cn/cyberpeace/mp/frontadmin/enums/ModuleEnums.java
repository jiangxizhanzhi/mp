package cn.cyberpeace.mp.frontadmin.enums;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2023/1/17 17:15
 */
public enum ModuleEnums {

    // 竞赛
    AD("AD"),

    // 实训
    OJ("OJ"),

    // 靶场
    CR("CR");

    private String code;

    ModuleEnums(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
