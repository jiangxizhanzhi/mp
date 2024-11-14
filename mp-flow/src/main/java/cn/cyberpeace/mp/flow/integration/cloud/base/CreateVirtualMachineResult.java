package cn.cyberpeace.mp.flow.integration.cloud.base;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateVirtualMachineResult {
    private String id ;
    private List<VirtualNic> networks;
}
