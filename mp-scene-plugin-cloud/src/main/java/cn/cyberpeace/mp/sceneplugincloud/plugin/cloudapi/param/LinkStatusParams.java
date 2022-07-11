package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpParam;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author maojun
 * @Date 2022/7/8
 */
@Getter
@Setter
public class LinkStatusParams implements CloudHttpParam {
    private String networkId;
    private String instanceId;
}
