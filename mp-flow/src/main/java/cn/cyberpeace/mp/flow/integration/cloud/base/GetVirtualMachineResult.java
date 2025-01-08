package cn.cyberpeace.mp.flow.integration.cloud.base;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetVirtualMachineResult {
    private String id;
    private List<VirtualNic> network;
    private List<VirtualNic> networks;

    public List<VirtualNic> getNetwork() {
        if (networks != null) {
            return networks;
        } else {
            return network;
        }
    }
}
