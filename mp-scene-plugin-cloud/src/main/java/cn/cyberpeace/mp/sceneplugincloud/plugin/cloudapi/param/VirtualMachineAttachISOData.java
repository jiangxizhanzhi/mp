package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

@Data
public class VirtualMachineAttachISOData implements CloudHttpData {
    private String instanceUuid;
    private String tmpdirUuid;
    private String fileName;
}
