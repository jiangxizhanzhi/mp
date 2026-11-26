package cn.cyberpeace.mp.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Provider accepted the VM; actual address fields are null until positively verified. */
@Getter
@AllArgsConstructor
public class ManagedVmCreationResult {
    private final String resourceId;
    private final String actualIp;
    private final String macAddress;
    private final String netmask;
    private final String gateway;
}
