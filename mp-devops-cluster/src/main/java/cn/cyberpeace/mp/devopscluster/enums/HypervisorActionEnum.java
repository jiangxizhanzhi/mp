package cn.cyberpeace.mp.devopscluster.enums;

/**
 * Hypervisor操作
 * @author wanggang
 */
public enum HypervisorActionEnum {

    REBOOT("reboot"),

    POWEROFF("poweroff");

    private String action;

    /**
     * HypervisorActionEnum
     *
     * @param action
     */
    HypervisorActionEnum(String action) {
        this.action = action;
    }

    /**
     * getAction
     *
     * @return
     */
    public String getAction() {
        return action;
    }
}
