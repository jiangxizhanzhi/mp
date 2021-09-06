package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.HttpData;
import lombok.Data;

import java.util.List;

@Data
public class VirtualSwitchDeleteData implements HttpData {
    private List<String> ids;
    private Boolean entityNetwork;
    private Boolean configSwitch;
}
