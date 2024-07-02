package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @ClassName VirtualPortCreateResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VirtualPortCreateResult {
    private String id;
    private String description;
    private String networkId;
    private String fixed_ip;
    private String mac_address;
    private String qos_policy_id;
    private String device_id;
    private String device_owner;
    private Boolean port_security_enabled;
}
