package cn.cyberpeace.mp.devopscluster.enums;

/**
 * @ClassName HypervisorActionEnum
 * @Description Hypervisor操作
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Enum
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
