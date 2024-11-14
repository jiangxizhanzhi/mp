package cn.cyberpeace.mp.flow.integration.cloud.sncloud;

import cn.cyberpeace.mp.flow.integration.cloud.base.AbstractGetVirtualMachineParams;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SnGetVirtualMachineParams extends AbstractGetVirtualMachineParams {
    private String id;
}
