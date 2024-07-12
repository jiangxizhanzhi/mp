 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HypervisorActionEnum.java.
 // File size 611.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
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
