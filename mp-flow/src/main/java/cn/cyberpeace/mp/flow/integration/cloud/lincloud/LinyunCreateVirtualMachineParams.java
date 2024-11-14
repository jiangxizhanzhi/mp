package cn.cyberpeace.mp.flow.integration.cloud.lincloud;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.flow.integration.cloud.base.AbstractCreateVirtualMachineParams;
import cn.cyberpeace.mp.flow.integration.cloud.base.VirtualNic;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LinyunCreateVirtualMachineParams extends AbstractCreateVirtualMachineParams {
    private String name;
    private String templateId;
    private String image;
    private Integer cpu;
    private Integer ram;
    private Integer disk;
    private Boolean mainCloud;
    private String osType;
    private List<VirtualNic> networks;

    public static LinyunCreateVirtualMachineParams getDefault() {
        int trexCpu = 4;
        int trexRam = 4096;
        int trexDisk = 80;
        String networkId = "5489d262-8d06-4909-b893-4191ef036f46";
        String trexImage = "tgn-trex";
        String name = "Trex-" + TextUtils.uuid();

        LinyunCreateVirtualMachineParams params = new LinyunCreateVirtualMachineParams();
        params.setOsType("linux");
        params.setMainCloud(true);
        params.setTemplateId("");
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
