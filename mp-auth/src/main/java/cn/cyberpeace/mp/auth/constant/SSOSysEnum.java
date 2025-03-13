package cn.cyberpeace.mp.auth.constant;
/*
 * @BelongsPackage: cn.cyberpeace.mp.auth.constant
 * @Author: hhuang
 * @CreateTime: 2025/3/13 14:58
 * @Description: 单点系统标识
 * @Version: 1.0
 */
public enum SSOSysEnum {


    FOUR_A("4a", "4A系统"),

    SOC("soc", "SOC涿鹿攻防平台");

    /**
     * 标识
     */
    private String code;

    /**
     * 单点系统
     */
    private String name;

    SSOSysEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
