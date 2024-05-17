package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

@Data
public class VirtualMachineModifyData implements CloudHttpData {
    private String name;
    private Integer memory;
    private Integer cpu;
    private Integer diskSize;
    private String displayType;
    private String diskId;
}
