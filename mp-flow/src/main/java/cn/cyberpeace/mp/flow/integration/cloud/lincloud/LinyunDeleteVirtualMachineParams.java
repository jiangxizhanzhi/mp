package cn.cyberpeace.mp.flow.integration.cloud.lincloud;

import cn.cyberpeace.mp.flow.integration.cloud.base.AbstractDeleteVirtualMachineParams;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LinyunDeleteVirtualMachineParams extends AbstractDeleteVirtualMachineParams {
    private List<String> vms;
}
