package cn.cyberpeace.mp.flow.integration.cloud.topocloud;

import cn.cyberpeace.mp.flow.integration.cloud.base.AbstractDeleteVirtualMachineParams;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopoDeleteVirtualMachineParams extends AbstractDeleteVirtualMachineParams {
    private String csceneId;
}
