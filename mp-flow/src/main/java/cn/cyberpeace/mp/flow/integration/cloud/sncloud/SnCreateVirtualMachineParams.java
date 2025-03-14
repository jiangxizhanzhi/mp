package cn.cyberpeace.mp.flow.integration.cloud.sncloud;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.flow.config.FlowConfig;
import cn.cyberpeace.mp.flow.integration.cloud.base.AbstractCreateVirtualMachineParams;
import cn.cyberpeace.mp.flow.integration.cloud.base.VirtualNic;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SnCreateVirtualMachineParams extends AbstractCreateVirtualMachineParams {
    private String name;
    private String image;
    private Integer cpu;
    private Integer ram;
    private Integer disk;
    private List<VirtualNic> networks;

    public static SnCreateVirtualMachineParams getDefault() {
        int trexCpu = 4;
        int trexRam = 4096;
        int trexDisk = 80;

        String networkId = BeanCommon.getBean(FlowConfig.class).getNetworkId();
        String trexImage = "tgn-trex";
        String name = "Trex-" + TextUtils.uuid();

        SnCreateVirtualMachineParams params = new SnCreateVirtualMachineParams();
        params.setCpu(trexCpu);
        params.setDisk(trexDisk);
        params.setRam(trexRam);
        params.setImage(trexImage);
        params.setName(name);
        params.setNetworks(new ArrayList<>());

        VirtualNic virtualNic = new VirtualNic();
        virtualNic.setNetworkId(networkId);
        virtualNic.setAccessModes(new ArrayList<>());
        VirtualNic.AccessMode accessMode = new VirtualNic.AccessMode();
        accessMode.setProtocol("ssh");
        accessMode.setUsername("root");
        accessMode.setPassword("111111");
        accessMode.setPort(22);
        virtualNic.getAccessModes().add(accessMode);
        params.getNetworks().add(virtualNic);

        return params;
    }
}
