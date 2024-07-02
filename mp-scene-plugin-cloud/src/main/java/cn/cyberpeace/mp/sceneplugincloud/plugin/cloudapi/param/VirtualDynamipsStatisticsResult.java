package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName VirtualDynamipsStatisticsResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
public class VirtualDynamipsStatisticsResult implements CloudHttpData {
    private Map<String, Integer> statics;
    private Boolean success;
}
