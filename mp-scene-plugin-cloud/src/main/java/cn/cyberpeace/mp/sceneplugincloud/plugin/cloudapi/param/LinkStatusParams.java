package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpParam;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName LinkStatusParams
 * @Description
 * @Author maojun
 * @Date 2022/7/8
 * @Version 1.0.0
 * @see CloudHttpParam
 */
@Getter
@Setter
public class LinkStatusParams implements CloudHttpParam {
    private String networkId;
    private String instanceId;
}
