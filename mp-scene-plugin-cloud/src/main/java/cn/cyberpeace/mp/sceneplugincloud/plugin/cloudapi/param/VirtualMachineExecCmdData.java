package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

import java.util.List;

@Data
public class VirtualMachineExecCmdData implements CloudHttpData {
    private String vmId;
    private String execCmd;
    private List<String> args;
    private List<String> envs;
}
