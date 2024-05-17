package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpParam;
import lombok.Data;

@Data
public class GetTemplateIdParam implements CloudHttpParam {
    private String name;
    private Boolean defaultInit;
}
