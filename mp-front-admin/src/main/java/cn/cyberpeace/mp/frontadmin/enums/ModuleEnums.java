package cn.cyberpeace.mp.frontadmin.enums;


public enum ModuleEnums {

    
    AD("AD"),

    
    OJ("OJ"),

    
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
