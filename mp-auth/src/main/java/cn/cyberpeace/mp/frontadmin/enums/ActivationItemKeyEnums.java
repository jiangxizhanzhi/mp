package cn.cyberpeace.mp.frontadmin.enums;

/**
 * @Description 激活项枚举
 * @author: zhouxinghuai
 * @Date: 2023/01/17
 */
public enum ActivationItemKeyEnums {

    // 实训模块
    SHIXUN("LCS-FUNC-CR-SHIXUN"),

    // 竞赛模块
    JINGSAI("LCS-FUNC-CR-JINGSAI"),

    // 活体漏洞模块
    HUOTILOUDONG("LCS-FUNC-CR-HUOTILOUDONG"),

    // 实战知识库
    ZHISHIKU("LCS-FUNC-CR-ZHISHIKU"),

    // 渗透赛模块
    SHENTOUSAI("LCS-FUNC-CR-SHENTOUSAI"),

    // 运维赛模块
    YUNWEISAI("LCS-FUNC-CR-YUNWEISAI"),

    // 综合训练模块
    ZONGHEXUNLIAN("LCS-FUNC-CR-ZONGHEXUNLIAN"),

    // 红蓝对抗模块
    HONGLANDUIKANG("LCS-FUNC-CR-HONGLANDUIKANG"),

    // 测评模块
    CEPING("LCS-FUNC-CR-CEPING");

    private String code;

    ActivationItemKeyEnums(String key) {
        this.code = key;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
