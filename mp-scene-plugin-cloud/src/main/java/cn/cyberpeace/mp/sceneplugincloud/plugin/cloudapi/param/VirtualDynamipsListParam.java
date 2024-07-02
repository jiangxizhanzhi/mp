package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpParam;
import lombok.Data;

/**
 * @ClassName VirtualDynamipsListParam
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpParam
 */
@Data
public class VirtualDynamipsListParam implements CloudHttpParam {
    private Integer limit;
    private Integer offset;
    private String sortKey;
    private String sortDir;
    private String ip;
}
