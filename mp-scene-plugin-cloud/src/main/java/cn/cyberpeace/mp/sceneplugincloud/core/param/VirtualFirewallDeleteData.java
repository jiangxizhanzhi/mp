package cn.cyberpeace.mp.sceneplugincloud.core.param;

import cn.cyberpeace.mp.sceneplugincloud.core.common.HttpData;
import lombok.Data;

import java.util.List;

@Data
public class VirtualFirewallDeleteData implements HttpData {
    private List<String> ids;
}
