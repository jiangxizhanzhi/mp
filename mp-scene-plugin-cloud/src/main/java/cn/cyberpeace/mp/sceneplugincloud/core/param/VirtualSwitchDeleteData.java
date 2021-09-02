package cn.cyberpeace.mp.sceneplugincloud.core.param;

import cn.cyberpeace.mp.sceneplugincloud.core.common.HttpData;
import lombok.Data;

import java.util.List;

@Data
public class VirtualSwitchDeleteData implements HttpData {
    private List<String> ids;
    private Boolean entityNetwork;
    private Boolean configSwitch;
}
