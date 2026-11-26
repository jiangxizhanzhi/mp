package cn.cyberpeace.mp.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/** Provider accepted the VM; actual address fields are null until positively verified. */
@Getter
@AllArgsConstructor
public class ManagedVmCreationResult {
    private final String resourceId;
    private final String actualIp;
    private final String macAddress;
    private final String netmask;
    private final String gateway;
    private final String taskId;
    private final boolean runningConfirmed;
    private final int vendor;
    private final String stage;

    public ManagedVmCreationResult(String resourceId, String actualIp, String macAddress, String netmask,
                                   String gateway, String taskId, boolean runningConfirmed) {
        this(resourceId, actualIp, macAddress, netmask, gateway, taskId, runningConfirmed, 2, null);
    }

    public boolean isReady() {
        return runningConfirmed && StringUtils.isNoneBlank(resourceId, actualIp, macAddress);
    }

    public String pendingMessage() {
        return "厂商" + vendor + "创建结果待核查，uuid=" + resourceId + "，task_id=" + taskId
                + "，运行状态已确认=" + runningConfirmed + "，IP/MAC已确认="
                + StringUtils.isNoneBlank(actualIp, macAddress) + (stage == null ? "" : "，阶段=" + stage);
    }
}
