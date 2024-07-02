package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpParam;
import lombok.Data;

import java.util.List;

/**
 * @ClassName NetworkFlowRateParam
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpParam
 */
@Data
public class NetworkFlowRateParam implements CloudHttpParam {

    /**
     * 端口列表
     */
    private List<String> ports;

    /**
     * 统计时间，单位S
     */
    private Long time;
}
