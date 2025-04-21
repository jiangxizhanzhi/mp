package cn.cyberpeace.mp.flow.integration.cloud.base;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddVirtualNicParams {
    private String instanceId;
    private String networkId;
    private String addr;
}
