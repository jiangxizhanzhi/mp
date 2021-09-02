package cn.cyberpeace.mp.sceneplugincloud.core.param;

import cn.cyberpeace.mp.sceneplugincloud.core.common.HttpData;
import lombok.Data;

import java.util.List;

@Data
public class VirtualMachineCreateData implements HttpData {
    private String name;
    private String image;
    private Integer cpu;
    private Integer ram;
    private Integer disk;
    private String requestUser;
    private String host;
    private List<VirtualNic> networks;
}
