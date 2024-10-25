package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineAddPortData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VirtualMachineUpdatePortData implements CloudHttpData {
    private String nodeId;
    private boolean special;
    private List<VirtualMachineAddPortData> addNics;
    private List<String> deleteNics;
    private String hwVifModel;
    private String type;
}
