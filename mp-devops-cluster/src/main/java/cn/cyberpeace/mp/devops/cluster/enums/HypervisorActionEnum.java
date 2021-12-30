package cn.cyberpeace.mp.devops.cluster.enums;

/**
 * Hypervisor操作
 * @author wanggang
 */
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
