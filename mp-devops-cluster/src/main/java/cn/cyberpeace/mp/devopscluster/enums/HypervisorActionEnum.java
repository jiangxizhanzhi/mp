package cn.cyberpeace.mp.devopscluster.enums;


public enum HypervisorActionEnum {

    REBOOT("reboot"),

    POWEROFF("poweroff");

    private String action;

    HypervisorActionEnum(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}
