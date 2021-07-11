package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpParam;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LinkStatusParams implements CloudHttpParam {
    private String networkId;
    private String instanceId;
}
