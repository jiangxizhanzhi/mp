package cn.cyberpeace.mp.flow.integration.cloud.topocloud;

import cn.cyberpeace.mp.flow.integration.cloud.base.AbstractCreateVirtualMachineParams;
import cn.cyberpeace.mp.flow.integration.cloud.base.VirtualNic;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TopoCreateVirtualMachineParams extends AbstractCreateVirtualMachineParams {
    private String name;
    private String image;
    private Integer cpu;
    private Integer ram;
    private Integer disk;
    private List<VirtualNic> networks;
}
