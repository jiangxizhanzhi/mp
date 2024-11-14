package cn.cyberpeace.mp.flow.integration.cloud.topocloud;

import cn.cyberpeace.mp.flow.integration.cloud.base.AbstractCreateVirtualMachineParams;
import cn.cyberpeace.mp.flow.integration.cloud.base.AbstractGetVirtualMachineParams;
import cn.cyberpeace.mp.flow.integration.cloud.base.VirtualNic;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TopoGetVirtualMachineParams extends AbstractGetVirtualMachineParams {
    private String id;
}
