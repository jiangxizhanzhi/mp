package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpParam;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName GetConnectionUrlParam
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpParam
 */
@Data
@Accessors(chain = true)
public class GetConnectionUrlParam implements CloudHttpParam {
    private String vmId;
    private String consoleType;
}