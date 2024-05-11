package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpParam;
import lombok.Data;

@Data
public class GetConnectionUrlParam implements CloudHttpParam {
    private String vmId;
    private String consoleType;
}