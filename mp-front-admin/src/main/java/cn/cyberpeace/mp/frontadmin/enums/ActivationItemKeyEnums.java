package cn.cyberpeace.mp.frontadmin.enums;


public enum ActivationItemKeyEnums {

    
    SHIXUN("LCS-FUNC-CR-SHIXUN"),

    
    JINGSAI("LCS-FUNC-CR-JINGSAI"),

    
    HUOTILOUDONG("LCS-FUNC-CR-HUOTILOUDONG"),

    
    ZHISHIKU("LCS-FUNC-CR-ZHISHIKU"),

    
    SHENTOUSAI("LCS-FUNC-CR-SHENTOUSAI"),

    
    YUNWEISAI("LCS-FUNC-CR-YUNWEISAI"),

    
    ZONGHEXUNLIAN("LCS-FUNC-CR-ZONGHEXUNLIAN"),

    
    HONGLANDUIKANG("LCS-FUNC-CR-HONGLANDUIKANG"),

    
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
