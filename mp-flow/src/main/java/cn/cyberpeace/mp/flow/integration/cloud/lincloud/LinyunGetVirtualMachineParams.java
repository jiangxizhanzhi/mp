package cn.cyberpeace.mp.flow.integration.cloud.lincloud;

import cn.cyberpeace.mp.flow.integration.cloud.base.AbstractGetVirtualMachineParams;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinyunGetVirtualMachineParams extends AbstractGetVirtualMachineParams {
    private String id;
}
