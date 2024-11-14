package cn.cyberpeace.mp.flow.integration.cloud.topocloud;

import cn.cyberpeace.mp.flow.integration.cloud.base.AbstractAddVirtualNicParams;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopoAddVirtualNicParams extends AbstractAddVirtualNicParams {
    private String instanceId;
    private String networkId;
}
