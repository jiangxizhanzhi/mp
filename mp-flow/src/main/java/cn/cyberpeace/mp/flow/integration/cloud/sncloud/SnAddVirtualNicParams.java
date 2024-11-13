package cn.cyberpeace.mp.flow.integration.cloud.sncloud;

import cn.cyberpeace.mp.flow.integration.cloud.base.AbstractAddVirtualNicParams;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SnAddVirtualNicParams extends AbstractAddVirtualNicParams {
    private String instanceId;
    private String networkId;
}
